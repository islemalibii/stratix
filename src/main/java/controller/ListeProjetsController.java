package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import model.Projet;
import service.ProjetService;
import java.util.List;

public class ListeProjetsController {

    @FXML private VBox containerProjets;
    @FXML private Label lblTotal, lblEnCours, lblTermine, lblAnnule, lblPlanifie;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> comboFiltre;

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
        if (lblTotal == null) {
            System.err.println("ERREUR : Les fx:id ne sont pas reconnus. Vérifiez le FXML.");
            return;
        }

        lblTotal.setText(String.valueOf(projets.size()));
        lblEnCours.setText(String.valueOf(projets.stream().filter(p -> "En cours".equals(p.getStatut())).count()));
        lblTermine.setText(String.valueOf(projets.stream().filter(p -> "Terminé".equals(p.getStatut())).count()));
        lblAnnule.setText(String.valueOf(projets.stream().filter(p -> "Annulé".equals(p.getStatut())).count()));

        if (lblPlanifie != null)
            lblPlanifie.setText(String.valueOf(projets.stream().filter(p -> "Planifié".equals(p.getStatut())).count()));
    }

    private void filtrerEtAfficher() {
        if (containerProjets == null) return;
        containerProjets.getChildren().clear();

        String statut = (comboFiltre != null) ? comboFiltre.getValue() : "Tous les projets";
        String recherche = (searchField != null) ? searchField.getText().toLowerCase() : "";

        for (Projet p : listeCompleteProjets) {
            boolean matchStatut = statut.equals("Tous les projets") || p.getStatut().equals(statut);
            boolean matchRecherche = p.getNom().toLowerCase().contains(recherche);

            if (matchStatut && matchRecherche) {
                containerProjets.getChildren().add(creerCardProjet(p));
            }
        }
    }
    private HBox creerCardProjet(Projet p) {
        HBox card = new HBox(15);
        card.getStyleClass().add("project-card");

        // Application du style de bordure latérale selon le statut
        String s = p.getStatut();
        if ("Terminé".equals(s)) card.getStyleClass().add("card-termine");
        else if ("En cours".equals(s)) card.getStyleClass().add("card-en-cours");
        else if ("Annulé".equals(s)) card.getStyleClass().add("card-annule");
        else card.getStyleClass().add("card-planifie");

        VBox info = new VBox(5);
        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label desc = new Label(p.getDescription());
        desc.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        Label footer = new Label("Statut: " + s + " | Budget: " + p.getBudget() + "€ | " + p.getProgression() + "%");
        footer.setStyle("-fx-text-fill: #718096; -fx-font-size: 12px;");

        info.getChildren().addAll(nom, desc, footer);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnMod = new Button("Modifier");
        btnMod.getStyleClass().add("btn-modify");
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnArch = new Button("Archiver");
        btnArch.getStyleClass().add("btn-archive");
        btnArch.setOnAction(e -> {
            projetService.archiverUnProjet(p.getId());
            rafraichirDonnees(); // Rafraîchissement immédiat
        });

        VBox actions = new VBox(8);
        actions.getChildren().addAll(btnMod, btnArch);

        card.getChildren().addAll(info, actions);
        return card;
    }

    @FXML
    private void allerAjouterProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjet.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nouveau Projet");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();

            rafraichirDonnees();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void voirArchives() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeArchives.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Projets Archivés");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}