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

// Bibliothèques PDF (OpenPDF / iText)
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

        // Chargement du logo
        if (logoImageView != null) {
            try {
                Image logo = new Image(getClass().getResourceAsStream("/stratix.png"));
                logoImageView.setImage(logo);
            } catch (Exception e) {
                System.out.println("Logo introuvable.");
            }
        }

        // Configuration du filtre
        if (comboFiltre != null) {
            comboFiltre.setItems(FXCollections.observableArrayList("Tous les projets", "Planifié", "En cours", "Terminé", "Annulé"));
            comboFiltre.setValue("Tous les projets");
            comboFiltre.setOnAction(e -> filtrerEtAfficher());
        }

        // Configuration de la recherche
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

    private String recupererNomChef(int id) {
        String nomComplet = "Non assigné";
        if (id <= 0) return nomComplet;
        String sql = "SELECT nom, prenom FROM utilisateur WHERE id = ?";
        Connection conn = MyDataBase.getInstance().getCnx();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nomComplet = rs.getString("nom") + " " + rs.getString("prenom");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomComplet;
    }

    private VBox creerCardProjet(Projet p) {
        VBox card = new VBox(15);
        card.setPrefWidth(320);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("project-card");

        String nomChef = recupererNomChef(p.getResponsableId());

        Label statutBadge = new Label(p.getStatut());
        statutBadge.getStyleClass().addAll("statut-badge", getStatutClass(p.getStatut()));

        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("project-title");
        nom.setWrapText(true);

        // QR CODE
        String qrContent = "PROJET: " + p.getNom() + "\nCHEF: " + nomChef + "\nPROG: " + p.getProgression() + "%";
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + URLEncoder.encode(qrContent, StandardCharsets.UTF_8);
        ImageView qrView = new ImageView(new Image(qrUrl, true));
        qrView.setFitWidth(80);
        qrView.setPreserveRatio(true);

        VBox progBox = new VBox(5, new Label("Progression: " + p.getProgression() + "%"), new ProgressBar(p.getProgression() / 100.0));

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button btnMod = new Button("Modifier");
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnPdf = new Button("PDF");
        btnPdf.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white;");
        btnPdf.setOnAction(e -> exporterEnPDF(p, qrUrl));

        Button btnArch = new Button("Archiver");
        btnArch.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white;");
        btnArch.setOnAction(e -> handleArchiver(p));

        actions.getChildren().addAll(btnMod, btnPdf, btnArch);

        card.getChildren().addAll(statutBadge, nom, qrView, progBox, actions);
        return card;
    }

    private String getStatutClass(String statut) {
        return switch (statut != null ? statut : "") {
            case "Terminé" -> "badge-termine";
            case "En cours" -> "badge-en-cours";
            case "Annulé" -> "badge-annule";
            default -> "badge-planifie";
        };
    }

    private void handleArchiver(Projet p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Archiver le projet '" + p.getNom() + "' ?", ButtonType.YES, ButtonType.NO);
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

            Paragraph title = new Paragraph("STRATIX - RAPPORT TECHNIQUE");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Nom du Projet : " + p.getNom()));
            document.add(new Paragraph("Responsable : " + recupererNomChef(p.getResponsableId())));
            document.add(new Paragraph("Statut actuel : " + p.getStatut()));
            document.add(new Paragraph("Progression : " + p.getProgression() + "%"));
            document.add(new Paragraph("Membres : " + (p.getEquipeMembres() != null ? p.getEquipeMembres() : "Aucun")));
            document.add(new Paragraph("Description : " + p.getDescription()));

            document.add(new Paragraph(" "));
            com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(new java.net.URL(qrCodeUrl));
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleAbsolute(100, 100);
            document.add(img);

            document.close();
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            afficherErreur("Erreur PDF", "Échec de génération du rapport.");
        }
    }

    @FXML
    private void ouvrirChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatProjet.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Espace Chat - Stratix");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur Chat", "Impossible d'ouvrir le chat.");
        }
    }

    @FXML private void voirArchives() { chargerFenetre("/ListeArchives.fxml", "Archives"); }
    @FXML private void allerAjouterProjet() { chargerFenetre("/AjouterProjet.fxml", "Nouveau Projet"); }

    private void ouvrirFenetreModification(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
            Parent root = loader.load();
            ModifierProjetController controller = loader.getController();
            controller.chargerDonnees(p.getId());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerFenetre(String fxmlPath, String titre) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) {
            afficherErreur("Erreur Navigation", "Fichier introuvable.");
        }
    }

    private void afficherErreur(String titre, String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}