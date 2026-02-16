package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;
import java.util.List;

public class ListeArchivesController {

    @FXML private VBox containerArchives;
    private ProjetService projetService;

    @FXML
    public void initialize() {
        projetService = new ProjetService();
        chargerArchives();
    }

    private void chargerArchives() {
        if (containerArchives == null) return;
        containerArchives.getChildren().clear();

        List<Projet> archives = projetService.listerArchives();

        if (archives.isEmpty()) {
            Label emptyLabel = new Label("Aucun projet archivé.");
            emptyLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-style: italic; -fx-padding: 20;");
            containerArchives.getChildren().add(emptyLabel);
            return;
        }

        for (Projet p : archives) {
            containerArchives.getChildren().add(creerCardArchive(p));
        }
    }

    private HBox creerCardArchive(Projet p) {
        HBox card = new HBox(15);
        card.getStyleClass().add("project-card");

        String s = p.getStatut();
        if ("Terminé".equals(s)) card.getStyleClass().add("card-termine");
        else if ("En cours".equals(s)) card.getStyleClass().add("card-en-cours");
        else if ("Annulé".equals(s)) card.getStyleClass().add("card-annule");
        else card.getStyleClass().add("card-planifie");

        VBox info = new VBox(5);
        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #4a5568;");

        Label details = new Label("Archivé - Statut final : " + s + " | Budget : " + p.getBudget() + "€");
        details.setStyle("-fx-text-fill: #718096; -fx-font-size: 12px;");

        info.getChildren().addAll(nom, details);
        HBox.setHgrow(info, Priority.ALWAYS);

        card.getChildren().add(info);

        card.setOpacity(0.85);

        return card;
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) containerArchives.getScene().getWindow();
        stage.close();
    }
}