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
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-border-color: #e2e8f0; -fx-border-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label statutBadge = new Label(p.getStatut().toUpperCase());
        statutBadge.setStyle("-fx-background-color: " + getStatusHexColor(p.getStatut()) +
                "; -fx-text-fill: white; -fx-padding: 5 12; -fx-background-radius: 20; " +
                "-fx-font-size: 11px; -fx-font-weight: bold;");

        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-font-weight: 900; -fx-font-size: 20px; -fx-text-fill: #1a202c;");
        nom.setWrapText(true);
        nom.setMinHeight(60);
        nom.setAlignment(Pos.CENTER_LEFT);

        VBox progBox = new VBox(8);
        Label lblProg = new Label("Progression: " + p.getProgression() + "%");
        lblProg.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 13px; -fx-font-weight: bold;");

        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        pb.setPrefHeight(12);
        pb.setStyle("-fx-accent: #3182ce;");
        progBox.getChildren().addAll(lblProg, pb);

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);

        Button btnMod = new Button("Modifier");
        btnMod.setStyle("-fx-cursor: hand;");
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnPdf = new Button("PDF");
        btnPdf.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-cursor: hand;");
        btnPdf.setOnAction(e -> exporterEnPDF(p));

        Button btnChat = new Button("Chat");
        btnChat.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnChat.setOnAction(e -> ouvrirChatSpecifique(p));

        actions.getChildren().addAll(btnMod, btnPdf, btnChat);

        Button btnArch = new Button("Archiver le projet");
        btnArch.setMaxWidth(Double.MAX_VALUE);
        btnArch.setPrefHeight(40);
        btnArch.setStyle("-fx-background-color: #ed8936; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        btnArch.setOnAction(e -> handleArchiver(p));

        card.getChildren().addAll(statutBadge, nom, new Separator(), progBox, actions, btnArch);
        return card;
    }

    private void exporterEnPDF(Projet p) {
        Document document = new Document();
        try {
            String fileName = "Rapport_" + p.getNom().replace(" ", "_") + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // 1. Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph titre = new Paragraph("STRATIX - RAPPORT DÉTAILLÉ DU PROJET", titleFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph(" "));

            // 2. Informations détaillées (Sauf ID)
            document.add(new Paragraph("Nom du Projet : " + p.getNom()));
            document.add(new Paragraph("Responsable : " + recupererNomChef(p.getResponsableId())));
            document.add(new Paragraph("Statut : " + p.getStatut()));
            document.add(new Paragraph("Budget : " + p.getBudget() + " DT"));
            document.add(new Paragraph("Date de Début : " + p.getDateDebut()));
            document.add(new Paragraph("Date de Fin : " + p.getDateFin()));
            document.add(new Paragraph("Progression : " + p.getProgression() + "%"));
            document.add(new Paragraph("Description : " + p.getDescription()));
            document.add(new Paragraph(" "));

            // 3. Génération du QR Code complet (Toutes les infos sauf ID)
            String qrData = String.format(
                    "STRATIX - FICHE PROJET\nNom: %s\nResponsable: %s\nStatut: %s\nBudget: %s DT\nProgression: %d%%\nDescription: %s",
                    p.getNom(), recupererNomChef(p.getResponsableId()), p.getStatut(), p.getBudget(), p.getProgression(), p.getDescription()
            );

            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + URLEncoder.encode(qrData, StandardCharsets.UTF_8);

            // Insertion de l'image QR Code dans le PDF
            com.lowagie.text.Image qrImage = com.lowagie.text.Image.getInstance(new java.net.URL(qrCodeUrl));
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scalePercent(80);
            document.add(qrImage);

            document.close();
            Desktop.getDesktop().open(new File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur PDF", "Erreur lors de la génération du PDF.");
        }
    }

    private void ouvrirChatSpecifique(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatProjet.fxml"));
            Parent root = loader.load();
            ChatProjetController chatCtrl = loader.getController();
            chatCtrl.initChat(p.getId(), p.getNom());
            Stage stage = new Stage();
            stage.setTitle("Discussion Admin : " + p.getNom());
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> chatCtrl.stopChat());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur Chat", "Impossible d'ouvrir le chat.");
        }
    }

    private String getStatusHexColor(String statut) {
        return switch (statut != null ? statut : "") {
            case "Terminé" -> "#48bb78";
            case "En cours" -> "#38b2ac";
            case "Annulé" -> "#f56565";
            default -> "#4299e1";
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