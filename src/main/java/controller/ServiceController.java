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
import model.Service;
import service.ServiceService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ServiceController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private VBox servicesContainer;

    private ServiceService serviceService;
    private List<Service> allServices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            serviceService = new ServiceService();

            filterType.getItems().addAll("Tous", "Développement", "Formation", "Maintenance", "Conseil", "Support");
            filterType.setValue("Tous");

            chargerDonnees();

        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void chargerDonnees() {
        try {
            allServices = serviceService.afficherAll();
            afficherLignes(allServices);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void afficherLignes(List<Service> services) {
        servicesContainer.getChildren().clear();

        for (Service s : services) {
            HBox row = new HBox(10);
            row.getStyleClass().add("table-row");
            row.setPrefHeight(50);

            Label titre = new Label(s.getTitre());
            titre.getStyleClass().add("titre-cell");
            titre.setPrefWidth(200);

            String catName = (s.getCategorie() != null) ? s.getCategorie().getNom() : "Non catégorisé";
            Label categorie = new Label(catName);
            categorie.getStyleClass().add("categorie-cell");
            categorie.setPrefWidth(120);

            Label budget = new Label(String.format("%,.0f DT", s.getBudget()));
            budget.getStyleClass().add("budget-cell");
            budget.setPrefWidth(120);

            Label dateDebut = new Label(s.getDateDebut());
            dateDebut.getStyleClass().add("dates-cell");
            dateDebut.setPrefWidth(120);

            Label dateFin = new Label(s.getDateFin());
            dateFin.getStyleClass().add("dates-cell");
            dateFin.setPrefWidth(120);

            Label resp = new Label(String.valueOf(s.getResponsableId()));
            resp.getStyleClass().add("resp-cell");
            resp.setPrefWidth(80);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox actions = new HBox(5);
            actions.getStyleClass().add("action-buttons");
            actions.setPrefWidth(200);

            Button btnModifier = new Button("Modifier");
            btnModifier.getStyleClass().add("btn-modifier");
            btnModifier.setPrefWidth(80);
            btnModifier.setOnAction(e -> ouvrirModification(s));

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.getStyleClass().add("btn-supprimer");
            btnSupprimer.setPrefWidth(80);
            btnSupprimer.setOnAction(e -> supprimerService(s));

            actions.getChildren().addAll(btnModifier, btnSupprimer);

            row.getChildren().addAll(titre, categorie, budget, dateDebut, dateFin, resp, spacer, actions);
            servicesContainer.getChildren().add(row);
        }
    }

    private void ouvrirModification(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-service.fxml"));
            Parent root = loader.load();

            AjoutController controller = loader.getController();
            controller.setServiceService(serviceService);
            controller.setServiceAModifier(service);

            Stage stage = new Stage();
            stage.setTitle("Modifier Service");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerDonnees();

        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void supprimerService(Service service) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer ce service ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                serviceService.delete(service.getId());
                chargerDonnees();
                showAlert("Succès", "Service supprimé!");
            } catch (SQLException e) {
                showAlert("Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-service.fxml"));
            Parent root = loader.load();

            AjoutController controller = loader.getController();
            controller.setServiceService(serviceService);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Service");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerDonnees();

        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = filterType.getValue();

        List<Service> filtered = allServices.stream()
                .filter(s -> searchText.isEmpty() ||
                        s.getTitre().toLowerCase().contains(searchText))
                .filter(s -> selectedType.equals("Tous") ||
                        (s.getCategorie() != null &&
                                s.getCategorie().getNom().equals(selectedType)))
                .toList();

        afficherLignes(filtered);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}