package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;
import utils.MyDataBase;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Element;

import java.io.FileOutputStream;
import java.io.File;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListeProjetsController {

    @FXML private FlowPane containerProjets;
    @FXML private Label lblTotal, lblEnCours, lblTermine, lblAnnule;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> comboFiltre;
    @FXML private ImageView logoImageView;

    private ProjetService projetService;
    private List<Projet> listeCompleteProjets;

    @FXML
    public void initialize() {
        projetService = new ProjetService();

        if (logoImageView != null) {
            try {
                Image logo = new Image(getClass().getResourceAsStream("/stratix.png"));
                logoImageView.setImage(logo);
            } catch (Exception e) { System.out.println("Logo introuvable."); }
        }

        if (comboFiltre != null) {
            comboFiltre.setItems(FXCollections.observableArrayList("Tous les projets", "Planifié", "En cours", "Terminé", "Annulé"));
            comboFiltre.setValue("Tous les projets");
            comboFiltre.setOnAction(e -> filtrerEtAfficher());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrerEtAfficher());
        }

        rafraichirDonnees();
    }

    public void rafraichirDonnees() {
        listeCompleteProjets = projetService.listerTousLesProjets();
        updateStatistics(listeCompleteProjets);
        filtrerEtAfficher();
    }

    private void updateStatistics(List<Projet> projets) {
        if (lblTotal != null) lblTotal.setText(String.valueOf(projets.size()));
        if (lblEnCours != null) lblEnCours.setText(String.valueOf(projets.stream().filter(p -> "En cours".equals(p.getStatut())).count()));
        if (lblTermine != null) lblTermine.setText(String.valueOf(projets.stream().filter(p -> "Terminé".equals(p.getStatut())).count()));
        if (lblAnnule != null) lblAnnule.setText(String.valueOf(projets.stream().filter(p -> "Annulé".equals(p.getStatut())).count()));
    }

    private void filtrerEtAfficher() {
        if (containerProjets == null) return;
        containerProjets.getChildren().clear();
        String statut = (comboFiltre != null) ? comboFiltre.getValue() : "Tous les projets";
        String recherche = (searchField != null) ? searchField.getText() : "";
        List<Projet> filtree = projetService.rechercherProjets(recherche, statut);
        for (Projet p : filtree) {
            containerProjets.getChildren().add(creerCardProjet(p));
        }
    }

    private VBox creerCardProjet(Projet p) {
        VBox card = new VBox(15);
        card.setPrefWidth(320);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #e2e8f0; -fx-border-radius: 15;");

        String nomChef = recupererNomChef(p.getResponsableId());

        Label statutBadge = new Label(p.getStatut());
        statutBadge.setStyle("-fx-background-color: " + getStatusHexColor(p.getStatut()) + "; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 10;");

        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        nom.setWrapText(true);

        VBox progBox = new VBox(5, new Label("Progression: " + p.getProgression() + "%"), new ProgressBar(p.getProgression() / 100.0));

        // --- SECTION ACTIONS ---
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);

        Button btnMod = new Button("Modifier");
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnPdf = new Button("PDF");
        btnPdf.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white;");
        btnPdf.setOnAction(e -> {
            String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + URLEncoder.encode(p.getNom(), StandardCharsets.UTF_8);
            exporterEnPDF(p, qrUrl);
        });

        // NOUVEAU : Bouton Chat spécifique au projet
        Button btnChat = new Button("Chat");
        btnChat.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnChat.setOnAction(e -> ouvrirChatSpecifique(p));

        actions.getChildren().addAll(btnMod, btnPdf, btnChat);

        // Bouton Archiver en dessous (plus large)
        Button btnArch = new Button("Archiver le projet");
        btnArch.setMaxWidth(Double.MAX_VALUE);
        btnArch.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white;");
        btnArch.setOnAction(e -> handleArchiver(p));

        card.getChildren().addAll(statutBadge, nom, progBox, actions, btnArch);
        return card;
    }

    /**
     * Ouvre le chat pour un projet précis (ADMIN)
     */
    private void ouvrirChatSpecifique(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatProjet.fxml"));
            Parent root = loader.load();

            ChatProjetController chatCtrl = loader.getController();
            // L'ID du projet lie l'admin et l'employé sur la même discussion
            chatCtrl.initChat(p.getId(), p.getNom());

            Stage stage = new Stage();
            stage.setTitle("Discussion Admin : " + p.getNom());
            stage.setScene(new Scene(root));

            // On stoppe le rafraîchissement auto quand l'admin ferme la fenêtre
            stage.setOnCloseRequest(e -> chatCtrl.stopChat());
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur Chat", "Impossible de charger la discussion pour " + p.getNom());
        }
    }

    /**
     * Méthode liée au bouton "Espace Chat" de la Sidebar
     */
    @FXML
    private void ouvrirChat() {
        if (listeCompleteProjets != null && !listeCompleteProjets.isEmpty()) {
            // Ouvre par défaut le chat du premier projet de la liste
            ouvrirChatSpecifique(listeCompleteProjets.get(0));
        } else {
            afficherErreur("Chat", "Aucun projet actif pour ouvrir une discussion.");
        }
    }

    private String getStatusHexColor(String statut) {
        return switch (statut != null ? statut : "") {
            case "Terminé" -> "#6366f1";
            case "En cours" -> "#10b981";
            case "Annulé" -> "#ef4444";
            default -> "#3b82f6";
        };
    }

    private String recupererNomChef(int id) {
        String nomComplet = "Non assigné";
        if (id <= 0) return nomComplet;
        String sql = "SELECT nom, prenom FROM utilisateur WHERE id = ?";
        try (PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) nomComplet = rs.getString("nom") + " " + rs.getString("prenom");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return nomComplet;
    }

    private void handleArchiver(Projet p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Archiver '" + p.getNom() + "' ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                projetService.archiverUnProjet(p.getId());
                rafraichirDonnees();
            }
        });
    }

    private void exporterEnPDF(Projet p, String qrCodeUrl) {
        Document document = new Document();
        try {
            String fileName = "Rapport_" + p.getNom().replace(" ", "_") + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.add(new Paragraph("STRATIX - RAPPORT PROJET"));
            document.add(new Paragraph("Projet : " + p.getNom()));
            document.add(new Paragraph("Responsable : " + recupererNomChef(p.getResponsableId())));
            document.add(new Paragraph("Description : " + p.getDescription()));
            document.close();
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void ouvrirFenetreModification(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
            Parent root = loader.load();
            ModifierProjetController ctrl = loader.getController();
            ctrl.chargerDonnees(p.getId());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void voirArchives() { chargerFenetre("/ListeArchives.fxml", "Archives"); }
    @FXML private void allerAjouterProjet() { chargerFenetre("/AjouterProjet.fxml", "Nouveau Projet"); }

    private void chargerFenetre(String fxmlPath, String titre) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void afficherErreur(String titre, String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}