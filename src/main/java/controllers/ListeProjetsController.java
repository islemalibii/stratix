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
            } catch (Exception e) {
                System.out.println("Logo introuvable.");
            }
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
        card.getStyleClass().add("project-card");

        Label statutBadge = new Label(p.getStatut());
        statutBadge.getStyleClass().addAll("statut-badge", getStatutClass(p.getStatut()));

        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("project-title");
        nom.setWrapText(true);

        String qrContent = String.format(
                "PROJET : %s\nDESCRIPTION : %s\nCHEF : %s\nÉQUIPE : %s\nPROGRESSION : %d%%",
                p.getNom(), p.getDescription(),
                (p.getResponsableId() > 0 ? "Responsable Assigné" : "Non défini"),
                (p.getEquipeMembres() != null ? p.getEquipeMembres() : "Aucun membre"),
                p.getProgression()
        );

        String encodedData = URLEncoder.encode(qrContent, StandardCharsets.UTF_8);
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + encodedData;

        ImageView qrCodeView = new ImageView(new Image(qrUrl, true));
        qrCodeView.setFitWidth(100);
        qrCodeView.setPreserveRatio(true);

        VBox qrContainer = new VBox(qrCodeView);
        qrContainer.setAlignment(Pos.CENTER);
        qrContainer.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label desc = new Label(p.getDescription());
        desc.getStyleClass().add("project-desc");
        desc.setWrapText(true);
        desc.setMaxHeight(60);

        VBox progBox = new VBox(8);
        Label lblProg = new Label("Progression: " + p.getProgression() + "%");
        lblProg.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        progBox.getChildren().addAll(lblProg, pb);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button btnMod = new Button("Modifier");
        btnMod.getStyleClass().add("btn-secondary");
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnPdf = new Button("PDF");
        btnPdf.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPdf.setOnAction(e -> exporterEnPDF(p, qrUrl)); // On passe l'URL du QR Code

        Button btnArch = new Button("Archiver");
        btnArch.getStyleClass().add("btn-primary");
        btnArch.setStyle("-fx-background-color: #f59e0b;");
        btnArch.setOnAction(e -> {
            projetService.archiverUnProjet(p.getId());
            rafraichirDonnees();
        });

        actions.getChildren().addAll(btnMod, btnPdf, btnArch);

        card.getChildren().addAll(statutBadge, nom, qrContainer, desc, progBox, new Separator(), actions);
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

    private void exporterEnPDF(Projet p, String qrCodeUrl) {
        Document document = new Document();
        try {
            String fileName = "Rapport_" + p.getNom().replaceAll("\\s+", "_") + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            Paragraph header = new Paragraph("STRATIX - FICHE TECHNIQUE DU PROJET");
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph(" ")); // Espace
            document.add(new Paragraph("============================================"));

            document.add(new Paragraph("NOM DU PROJET : " + p.getNom()));
            document.add(new Paragraph("STATUT : " + p.getStatut()));
            document.add(new Paragraph("PROGRESSION : " + p.getProgression() + "%"));
            document.add(new Paragraph("CHEF DE PROJET (ID) : " + p.getResponsableId()));
            document.add(new Paragraph("ÉQUIPE : " + (p.getEquipeMembres() != null ? p.getEquipeMembres() : "Non définie")));
            document.add(new Paragraph("DESCRIPTION : " + p.getDescription()));
            document.add(new Paragraph(" "));

            try {
                com.lowagie.text.Image qrImage = com.lowagie.text.Image.getInstance(new java.net.URL(qrCodeUrl));
                qrImage.scaleAbsolute(120, 120);
                qrImage.setAlignment(Element.ALIGN_CENTER);
                document.add(qrImage);
                Paragraph qrLabel = new Paragraph("(Scannez pour suivre l'évolution en direct)");
                qrLabel.setAlignment(Element.ALIGN_CENTER);
                document.add(qrLabel);
            } catch (Exception ex) {
                document.add(new Paragraph("[Image QR Code indisponible sans connexion internet]"));
            }

            document.close();

            File file = new File(fileName);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            afficherErreur("Erreur PDF", "Impossible de générer le fichier.");
            e.printStackTrace();
        }
    }

    @FXML private void allerAjouterProjet() { chargerFenetre("/AjouterProjet.fxml", "Nouveau Projet"); }
    @FXML private void voirArchives() { chargerFenetre("/ListeArchives.fxml", "Archives"); }

    private void ouvrirFenetreModification(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
            Parent root = loader.load();
            ModifierProjetController controller = loader.getController();
            controller.chargerDonnees(p.getId());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible de modifier.");
        }
    }

    private void chargerFenetre(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) {
            afficherErreur("Erreur", "Fichier FXML non trouvé.");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void allerAuFront() { changerEspace("/EmployeListeProjets.fxml", "Espace Employé"); }

    private void changerEspace(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) containerProjets.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}