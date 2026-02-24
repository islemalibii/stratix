package controllers;

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

import models.CategorieService;
import Services.CategorieServiceService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CategorieController implements Initializable {

    @FXML private GridPane categoriesGrid;
    @FXML private Button btnArchives;
    @FXML private Button btnRetour;

    private CategorieServiceService categorieService;
    private List<CategorieService> allCategories;
    private boolean modeArchive = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            categorieService = new CategorieServiceService();
            chargerDonnees();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void chargerDonnees() {
        try {
            if (modeArchive) {
                allCategories = categorieService.afficherArchives();
                System.out.println("📦 Mode archives: " + allCategories.size() + " catégories");
            } else {
                allCategories = categorieService.afficherAll();
                System.out.println("📋 Mode normal: " + allCategories.size() + " catégories");
            }
            afficherCartes(allCategories);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherCartes(List<CategorieService> categories) {
        categoriesGrid.getChildren().clear();

        categoriesGrid.getColumnConstraints().clear();
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            categoriesGrid.getColumnConstraints().add(col);
        }

        if (categories.isEmpty()) {
            Label emptyLabel = new Label(modeArchive ? "Aucune catégorie archivée" : "Aucune catégorie trouvée");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b; -fx-padding: 50;");
            categoriesGrid.add(emptyLabel, 0, 0, 3, 1);
            return;
        }

        int col = 0;
        int row = 0;

        for (CategorieService c : categories) {
            VBox card = new VBox(15);
            card.getStyleClass().add("categorie-card");
            card.setMaxWidth(Double.MAX_VALUE);
            card.setPrefWidth(Region.USE_COMPUTED_SIZE);

            HBox header = new HBox(10);
            header.getStyleClass().add("categorie-header");
            header.setAlignment(Pos.CENTER_LEFT);

            Label nom = new Label(c.getNom());
            nom.getStyleClass().add("categorie-nom");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            header.getChildren().addAll(nom, spacer);

            VBox content = new VBox(8);
            content.getStyleClass().add("categorie-content");

            String desc = c.getDescription();
            if (desc == null || desc.isEmpty()) {
                desc = "Aucune description";
            }

            Label description = new Label(desc);
            description.getStyleClass().add("categorie-description");
            description.setWrapText(true);

            HBox dateRow = new HBox(10);
            dateRow.getStyleClass().add("categorie-row");
            dateRow.setAlignment(Pos.CENTER_LEFT);

            Label dateLabel = new Label("📅 Créée le:");
            dateLabel.getStyleClass().add("categorie-label");

            String dateCreation = c.getDateCreation();
            if (dateCreation == null) {
                dateCreation = "Date inconnue";
            }

            Label dateValue = new Label(dateCreation);
            dateValue.getStyleClass().add("categorie-value");

            dateRow.getChildren().addAll(dateLabel, dateValue);
            content.getChildren().addAll(description, dateRow);


            HBox actions = new HBox(10);
            actions.getStyleClass().add("categorie-actions");
            actions.setAlignment(Pos.CENTER_RIGHT);

            if (modeArchive) {
                Button btnDesarchiver = new Button("Restaurer");
                btnDesarchiver.getStyleClass().add("categorie-button-restore");
                btnDesarchiver.setOnAction(e -> {
                    desarchiverCategorie(c);
                });
                actions.getChildren().add(btnDesarchiver);
            } else {
                Button btnArchiver = new Button("Archiver");
                btnArchiver.getStyleClass().add("categorie-button-archive");
                btnArchiver.setOnAction(e -> {
                    archiverCategorie(c);
                });

                Button btnModifier = new Button("Modifier");
                btnModifier.getStyleClass().add("categorie-button-modify");
                btnModifier.setOnAction(e -> ouvrirModification(c));

                actions.getChildren().addAll(btnArchiver, btnModifier);
            }

            card.getChildren().addAll(header, content, actions);
            categoriesGrid.add(card, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void handleVoirArchives() {
        modeArchive = true;
        chargerDonnees();
        btnArchives.setVisible(false);
        btnRetour.setVisible(true);
    }

    @FXML
    private void handleRetour() {
        modeArchive = false;
        chargerDonnees();
        btnArchives.setVisible(true);
        btnRetour.setVisible(false);
    }

    private void archiverCategorie(CategorieService categorie) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous archiver la catégorie \"" + categorie.getNom() + "\" ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                categorieService.archiver(categorie.getId());
                chargerDonnees();
                showAlert("Succès", "Catégorie archivée avec succès!");
            } catch (SQLException e) {
                showAlert("Erreur", e.getMessage());
            }
        }
    }

    private void desarchiverCategorie(CategorieService categorie) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous restaurer la catégorie \"" + categorie.getNom() + "\" ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                categorieService.desarchiver(categorie.getId());
                chargerDonnees();
                showAlert("Succès", "Catégorie restaurée avec succès!");
            } catch (SQLException e) {
                showAlert("Erreur", e.getMessage());
            }
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}