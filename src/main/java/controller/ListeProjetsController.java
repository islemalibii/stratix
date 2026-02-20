package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class ListeProjetsController {

    @FXML private FlowPane containerProjets; // Changé de VBox en FlowPane
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
        // On vérifie si l'objet injecté par FXML n'est pas null
        if (lblTotal != null) {
            lblTotal.setText(String.valueOf(projets.size()));
        }

        if (lblEnCours != null) {
            long count = projets.stream().filter(p -> "En cours".equals(p.getStatut())).count();
            lblEnCours.setText(String.valueOf(count));
        }

        if (lblTermine != null) {
            long count = projets.stream().filter(p -> "Terminé".equals(p.getStatut())).count();
            lblTermine.setText(String.valueOf(count));
        }

        if (lblAnnule != null) {
            long count = projets.stream().filter(p -> "Annulé".equals(p.getStatut())).count();
            lblAnnule.setText(String.valueOf(count));
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

    private VBox creerCardProjet(Projet p) {
        // Création de la carte verticale
        VBox card = new VBox(15);
        card.setPrefWidth(300);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("project-card"); // Utilise le style avec ombre portée

        // Badge de Statut
        Label statutBadge = new Label(p.getStatut());
        statutBadge.getStyleClass().addAll("statut-badge", getStatutClass(p.getStatut()));

        // Titre
        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        nom.setWrapText(true);

        // Description (limitée en hauteur)
        Label desc = new Label(p.getDescription());
        desc.setStyle("-fx-text-fill: #718096; -fx-font-size: 13px;");
        desc.setWrapText(true);
        desc.setMinHeight(40);

        // Progression
        VBox progBox = new VBox(5);
        Label lblProg = new Label("Progression: " + p.getProgression() + "%");
        lblProg.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        progBox.getChildren().addAll(lblProg, pb);

        // Boutons d'actions
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button btnMod = new Button("Modifier");
        btnMod.getStyleClass().add("btn-secondary");
        btnMod.setPrefWidth(100);
        btnMod.setOnAction(e -> ouvrirFenetreModification(p));

        Button btnArch = new Button("Archiver");
        btnArch.getStyleClass().add("btn-primary");
        btnArch.setStyle("-fx-background-color: #f59e0b;");
        btnArch.setPrefWidth(100);
        btnArch.setOnAction(e -> {
            projetService.archiverUnProjet(p.getId());
            rafraichirDonnees();
        });

        actions.getChildren().addAll(btnMod, btnArch);

        card.getChildren().addAll(statutBadge, nom, desc, progBox, new Separator(), actions);
        return card;
    }

    private String getStatutClass(String statut) {
        return switch (statut) {
            case "Terminé" -> "badge-termine";
            case "En cours" -> "badge-en-cours";
            case "Annulé" -> "badge-annule";
            default -> "badge-planifie";
        };
    }

    @FXML private void allerAjouterProjet() { chargerFenetre("/AjouterProjet.fxml", "Nouveau Projet"); }
    @FXML private void voirArchives() { chargerFenetre("/ListeArchives.fxml", "Projets Archivés"); }

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
            afficherErreur("Fichier introuvable", "Erreur de chargement de ModifierProjet.fxml");
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
            afficherErreur("Erreur de chargement", "Impossible de charger : " + fxmlPath);
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void allerAuFront() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EmployeListeProjets.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) containerProjets.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 800);

            // FORCER le CSS sur la nouvelle scène
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}