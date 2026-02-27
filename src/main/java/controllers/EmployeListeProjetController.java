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
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeListeProjetController {
    @FXML private FlowPane flowPaneProjets;
    @FXML private TextField searchField;
    @FXML private Label lblBienvenue;

    private ProjetService projetService = new ProjetService();

    // TODO: Récupérer le nom de l'utilisateur réellement connecté via une classe Session
    private String nomEmployeConnecte = "Jean Dupont";

    @FXML
    public void initialize() {
        lblBienvenue.setText("Bonjour, " + nomEmployeConnecte);
        chargerProjetsEmploye();
    }

    private void chargerProjetsEmploye() {
        // On ne récupère que les projets où l'employé est présent dans la chaîne 'equipe_membres'
        List<Projet> mesProjets = projetService.listerTousLesProjets().stream()
                .filter(p -> p.getEquipeMembres() != null && p.getEquipeMembres().contains(nomEmployeConnecte))
                .filter(p -> !"Annulé".equals(p.getStatut())) // On cache les annulés
                .collect(Collectors.toList());

        afficherLesCartes(mesProjets);
    }

    private void afficherLesCartes(List<Projet> projets) {
        flowPaneProjets.getChildren().clear();
        for (Projet p : projets) {
            flowPaneProjets.getChildren().add(creerCarteSimple(p));
        }
    }

    private VBox creerCarteSimple(Projet p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("project-card");
        card.setPrefWidth(350);
        card.setPadding(new javafx.geometry.Insets(20));

        Label lblStatut = new Label(p.getStatut().toUpperCase());
        lblStatut.getStyleClass().addAll("statut-badge", "badge-" + p.getStatut().toLowerCase().replace(" ", "-"));

        Label title = new Label(p.getNom());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        title.setWrapText(true);

        // QR Code dynamique
        String qrContent = "PROJET: " + p.getNom() + "\nMa Progression: " + p.getProgression() + "%";
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=" + URLEncoder.encode(qrContent, StandardCharsets.UTF_8);
        ImageView qrView = new ImageView(new Image(qrUrl, true));
        qrView.setFitWidth(80);
        qrView.setPreserveRatio(true);

        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);

        Label lblProg = new Label("Progression: " + p.getProgression() + "%");
        lblProg.setStyle("-fx-font-size: 11px;");

        card.getChildren().addAll(lblStatut, title, new StackPane(qrView), new Separator(), lblProg, pb);
        return card;
    }

    @FXML
    private void ouvrirChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatProjet.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chat Collaboratif - Stratix");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.NONE);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rechercherProjet() {
        String search = searchField.getText().toLowerCase();
        List<Projet> filtrés = projetService.listerTousLesProjets().stream()
                .filter(p -> p.getEquipeMembres() != null && p.getEquipeMembres().contains(nomEmployeConnecte))
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
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}