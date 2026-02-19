package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListeProjetsController {

    @FXML private VBox containerProjets;
    @FXML private Label lblTotal, lblEnCours, lblTermine, lblAnnule, lblPlanifie;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> comboFiltre;
    @FXML private VBox projetsContainer;

    private ProjetService projetService;
    private List<Projet> listeCompleteProjets;

    @FXML
    public void initialize() {
        projetService = new ProjetService();

        if (comboFiltre != null) {
            comboFiltre.setItems(FXCollections.observableArrayList(
                    "Tous les projets", "Planifié", "En cours", "Terminé", "Annulé"
            ));
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
        if (lblTotal == null) return;

        lblTotal.setText(String.valueOf(projets.size()));
        lblEnCours.setText(String.valueOf(projets.stream().filter(p -> "En cours".equals(p.getStatut())).count()));
        lblTermine.setText(String.valueOf(projets.stream().filter(p -> "Terminé".equals(p.getStatut())).count()));
        lblAnnule.setText(String.valueOf(projets.stream().filter(p -> "Annulé".equals(p.getStatut())).count()));

        if (lblPlanifie != null) {
            lblPlanifie.setText(String.valueOf(projets.stream().filter(p -> "Planifié".equals(p.getStatut())).count()));
        }
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

    private HBox creerCardProjet(Projet p) {
        HBox card = new HBox(15);
        card.getStyleClass().add("project-card");

        switch (p.getStatut()) {
            case "Terminé" -> card.getStyleClass().add("card-termine");
            case "En cours" -> card.getStyleClass().add("card-en-cours");
            case "Annulé" -> card.getStyleClass().add("card-annule");
            default -> card.getStyleClass().add("card-planifie");
        }

        VBox info = new VBox(5);
        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label desc = new Label(p.getDescription());
        desc.setStyle("-fx-text-fill: #718096; -fx-font-size: 13px;");
        desc.setWrapText(true);

        Label footer = new Label("Budget: " + p.getBudget() + " DT | Progression: " + p.getProgression() + "%");
        footer.setStyle("-fx-font-size: 11px; -fx-text-fill: #a0aec0;");

        info.getChildren().addAll(nom, desc, footer);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnMod = new Button("Modifier");
        btnMod.getStyleClass().add("btn-secondary");
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnArch = new Button("Archiver");
        btnArch.getStyleClass().add("btn-primary");
        btnArch.setStyle("-fx-background-color: #f59e0b;"); // Orange pour l'archive
        btnArch.setOnAction(e -> {
            projetService.archiverUnProjet(p.getId());
            rafraichirDonnees();
        });

        VBox actions = new VBox(8);
        actions.getChildren().addAll(btnMod, btnArch);

        card.getChildren().addAll(info, actions);
        return card;
    }

    @FXML
    private void allerAjouterProjet() {
        chargerFenetre("/AjouterProjet.fxml", "Nouveau Projet");
    }

    @FXML
    private void voirArchives() {
        chargerFenetre("/ListeArchives.fxml", "Projets Archivés");
    }

    private void ouvrirFenetreModification(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
            Parent root = loader.load();

            ModifierProjetController controller = loader.getController();
            controller.chargerDonnees(p.getId());

            Stage stage = new Stage();
            stage.setTitle("Modifier Projet");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            rafraichirDonnees();
        } catch (IOException e) {
            afficherErreur("Fichier introuvable", "Impossible de charger ModifierProjet.fxml");
            e.printStackTrace();
        }
    }

    private void chargerFenetre(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void allerFrontOffice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontOffice.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) containerProjets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Front Office");

        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le Front Office");
            e.printStackTrace();
        }
    }

}