package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;

    private Node servicesView;
    private Node categoriesView;
    private Node resourcesView;
    private Node planningView;
    private Node eventsView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Load all views (your existing FXML files with sub-navigation)
            servicesView = FXMLLoader.load(getClass().getResource("/service-tab.fxml"));
            categoriesView = FXMLLoader.load(getClass().getResource("/categorie-tab.fxml"));

            // Placeholder for other tabs (you can replace these with actual FXML later)
            resourcesView = createPlaceholderView("Product Resources");
            planningView = createPlaceholderView("Projet Tâches Planning");
            eventsView = createPlaceholderView("Événements");

            // Show services by default
            showServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showServices() {
        contentArea.getChildren().setAll(servicesView);
    }

    @FXML
    private void showCategories() {
        contentArea.getChildren().setAll(categoriesView);
    }

    @FXML
    private void showResources() {
        contentArea.getChildren().setAll(resourcesView);
    }

    @FXML
    private void showPlanning() {
        contentArea.getChildren().setAll(planningView);
    }

    @FXML
    private void showEvents() {
        contentArea.getChildren().setAll(eventsView);
    }

    private Node createPlaceholderView(String text) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text + " - En cours de développement");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b; -fx-alignment: center;");
        javafx.scene.layout.VBox pane = new javafx.scene.layout.VBox(label);
        pane.setStyle("-fx-background-color: #f8fafc; -fx-alignment: center;");
        return pane;
    }
}