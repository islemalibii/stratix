package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CategorieService;
import service.CategorieServiceService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CategorieController implements Initializable {

    @FXML private VBox categoriesContainer;

    private CategorieServiceService categorieService;
    private List<CategorieService> allCategories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            categorieService = new CategorieServiceService();
            chargerDonnees();
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void chargerDonnees() {
        try {
            allCategories = categorieService.afficherAll();
            afficherLignes(allCategories);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void afficherLignes(List<CategorieService> categories) {
        categoriesContainer.getChildren().clear();

        for (CategorieService c : categories) {
            HBox row = new HBox(15);
            row.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-alignment: center-left;");
            row.setPrefHeight(50);

            // PLUS DE COLONNE ID !!!

            Label nom = new Label(c.getNom());
            nom.setStyle("-fx-font-weight: bold; -fx-min-width: 200; -fx-text-fill: #2c3e50;");
            nom.setPrefWidth(200);

            String desc = c.getDescription();
            if (desc != null && desc.length() > 50) {
                desc = desc.substring(0, 47) + "...";
            }
            Label description = new Label(desc != null ? desc : "");
            description.setStyle("-fx-text-fill: #7f8c8d; -fx-min-width: 300;");
            description.setPrefWidth(300);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox actions = new HBox(5);
            actions.setAlignment(Pos.CENTER_RIGHT);

            Button btnModifier = new Button("Modifier");
            btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
            btnModifier.setOnAction(e -> ouvrirModification(c));

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
            btnSupprimer.setOnAction(e -> supprimerCategorie(c));

            actions.getChildren().addAll(btnModifier, btnSupprimer);

            row.getChildren().addAll(nom, description, spacer, actions);
            categoriesContainer.getChildren().add(row);
        }
    }

    private void ouvrirModification(CategorieService categorie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-categorie.fxml"));
            Parent root = loader.load();

            AjoutCategorieController controller = loader.getController();
            controller.setCategorieService(categorieService);
            controller.setCategorieAModifier(categorie);

            Stage stage = new Stage();
            stage.setTitle("Modifier Catégorie");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerDonnees();

        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void supprimerCategorie(CategorieService categorie) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer cette catégorie ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                categorieService.delete(categorie.getId());
                chargerDonnees();
                showAlert("Succès", "Catégorie supprimée!");
            } catch (SQLException e) {
                showAlert("Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-categorie.fxml"));
            Parent root = loader.load();

            AjoutCategorieController controller = loader.getController();
            controller.setCategorieService(categorieService);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Catégorie");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerDonnees();

        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}