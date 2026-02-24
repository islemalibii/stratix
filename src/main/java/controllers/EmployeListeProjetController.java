package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Projet;
import Services.ProjetService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeListeProjetController {
    @FXML private FlowPane flowPaneProjets;
    @FXML private TextField searchField;

    private ProjetService projetService = new ProjetService();

    @FXML
    public void initialize() {
        chargerProjetsEmploye();
    }

    private void chargerProjetsEmploye() {
        List<Projet> projetsActifs = projetService.listerTousLesProjets().stream()
                .filter(p -> "En cours".equals(p.getStatut()) || "Planifié".equals(p.getStatut()))
                .collect(Collectors.toList());

        afficherLesCartes(projetsActifs);
    }

    private void afficherLesCartes(List<Projet> projets) {
        if (flowPaneProjets != null) {
            flowPaneProjets.getChildren().clear();
            for (Projet p : projets) {
                flowPaneProjets.getChildren().add(creerCarteSimple(p));
            }
        }
    }


    private VBox creerCarteSimple(Projet p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("project-card");
        card.setPrefWidth(350); // Taille harmonisée pour la grille

        Label lblStatut = new Label(p.getStatut().toUpperCase());
        lblStatut.getStyleClass().addAll("statut-badge", "badge-" + p.getStatut().toLowerCase().replace(" ", "-"));

        Label title = new Label(p.getNom());
        title.getStyleClass().add("project-title");
        title.setWrapText(true);

        String qrData = "Stratix Collab\nProjet: " + p.getNom() + "\nAvancement: " + p.getProgression() + "%";
        String encodedData = URLEncoder.encode(qrData, StandardCharsets.UTF_8);
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=" + encodedData;

        ImageView qrView = new ImageView(new Image(qrUrl, true));
        qrView.setFitWidth(80);
        qrView.setPreserveRatio(true);

        VBox qrContainer = new VBox(qrView);
        qrContainer.setAlignment(Pos.CENTER);

        Label desc = new Label(p.getDescription());
        desc.setWrapText(true);
        desc.getStyleClass().add("project-desc");
        desc.setMinHeight(60);

        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);

        card.getChildren().addAll(lblStatut, title, qrContainer, desc, pb);
        return card;
    }

    @FXML
    private void rechercherProjet() {
        String search = searchField.getText().toLowerCase();
        List<Projet> filtrés = projetService.listerTousLesProjets().stream()
                .filter(p -> "En cours".equals(p.getStatut()) || "Planifié".equals(p.getStatut()))
                .filter(p -> p.getNom().toLowerCase().contains(search))
                .collect(Collectors.toList());
        afficherLesCartes(filtrés);
    }


    @FXML
    private void versBackOffice() {
        changerEspace("/ListeProjets.fxml", "Stratix - Administration");
    }

    private void changerEspace(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) searchField.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);

            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle(titre);
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Erreur lors du retour au Back-Office : " + e.getMessage());
            e.printStackTrace();
        }
    }
}