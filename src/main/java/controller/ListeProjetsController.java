package controller;

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
import model.Projet;
import service.ProjetService;

import java.io.IOException;
import java.util.List;

public class ListeProjetsController {

    @FXML private FlowPane containerProjets;
    @FXML private Label lblTotal, lblEnCours, lblTermine, lblAnnule;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> comboFiltre;
    @FXML private ImageView logoImageView; // Pour gérer le logo dynamiquement si besoin

    private ProjetService projetService;
    private List<Projet> listeCompleteProjets;

    @FXML
    public void initialize() {
        projetService = new ProjetService();

        // Chargement du logo stratix.png
        if (logoImageView != null) {
            try {
                Image logo = new Image(getClass().getResourceAsStream("/stratix.png"));
                logoImageView.setImage(logo);
            } catch (Exception e) {
                System.out.println("Logo stratix.png introuvable dans /resources");
            }
        }

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
        if (lblTotal != null) lblTotal.setText(String.valueOf(projets.size()));
        if (lblEnCours != null) {
            lblEnCours.setText(String.valueOf(projets.stream().filter(p -> "En cours".equals(p.getStatut())).count()));
        }
        if (lblTermine != null) {
            lblTermine.setText(String.valueOf(projets.stream().filter(p -> "Terminé".equals(p.getStatut())).count()));
        }
        if (lblAnnule != null) {
            lblAnnule.setText(String.valueOf(projets.stream().filter(p -> "Annulé".equals(p.getStatut())).count()));
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
        VBox card = new VBox(15);
        card.setPrefWidth(320); // Légèrement plus large pour Stratix
        card.setPadding(new Insets(20));
        card.getStyleClass().add("project-card");

        Label statutBadge = new Label(p.getStatut());
        statutBadge.getStyleClass().addAll("statut-badge", getStatutClass(p.getStatut()));

        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("project-title"); // Utilise la classe CSS
        nom.setWrapText(true);

        Label desc = new Label(p.getDescription());
        desc.getStyleClass().add("project-desc"); // Utilise la classe CSS
        desc.setWrapText(true);
        desc.setMinHeight(50);

        VBox progBox = new VBox(8);
        Label lblProg = new Label("Progression: " + p.getProgression() + "%");
        lblProg.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #4a5568;");
        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        progBox.getChildren().addAll(lblProg, pb);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

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

    @FXML private void allerAjouterProjet() { chargerFenetre("/AjouterProjet.fxml", "Nouveau Projet - Stratix"); }
    @FXML private void voirArchives() { chargerFenetre("/ListeArchives.fxml", "Archives Stratix"); }

    private void ouvrirFenetreModification(Projet p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
            Parent root = loader.load();
            ModifierProjetController controller = loader.getController();
            controller.chargerDonnees(p.getId());
            Stage stage = new Stage();
            stage.setTitle("Modifier Projet - Stratix");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            rafraichirDonnees();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la modification.");
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
            afficherErreur("Erreur", "Fichier FXML non trouvé.");
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
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}