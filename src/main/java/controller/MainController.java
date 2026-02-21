package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.UserRole;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private VBox sidebar;
    @FXML private Button btnServices;
    @FXML private Button btnCategories;
    @FXML private Button btnResources;
    @FXML private Button btnResource;
    @FXML private Button btnPlanning;
    @FXML private Button btnTaches;
    @FXML private Button btnEvents;
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblUserAvatar;

    private Node servicesView;
    private Node categoriesView;
    private Node statistiquesView;
    private Node resourcesView;
    private Node planningView;
    private Node eventsView;

    private String userRole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            System.out.println("Initialisation MainController...");


            userRole = UserRole.getInstance().getRole();
            System.out.println("Rôle récupéré: " + userRole);


            updateUserInfo();


            System.out.println("Chargement des vues...");
            servicesView = FXMLLoader.load(getClass().getResource("/service-tab.fxml"));
            categoriesView = FXMLLoader.load(getClass().getResource("/categorie-tab.fxml"));
            statistiquesView = FXMLLoader.load(getClass().getResource("/statistiques-tab.fxml"));

            resourcesView = createPlaceholderView("Product Resources");
            planningView = createPlaceholderView("Projet Tâches Planning");
            eventsView = createPlaceholderView("Événements");

            applyRoleBasedVisibility();

            showServices();

            System.out.println("Initialisation terminée avec succès");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des vues: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUserRole(String role) {
        this.userRole = role;
        UserRole.getInstance().setRole(role);
        updateUserInfo();
        applyRoleBasedVisibility();
    }

    private void updateUserInfo() {
        try {
            if (lblUserName != null && lblUserRole != null) {
                if (UserRole.getInstance().isAdmin()) {
                    lblUserName.setText("Admin User");
                    lblUserRole.setText("Administrateur");
                    if (lblUserAvatar != null) lblUserAvatar.setText("👑");
                } else {
                    lblUserName.setText("Employé");
                    lblUserRole.setText("Consultation seule");
                    if (lblUserAvatar != null) lblUserAvatar.setText("👤");
                }
            } else {
                System.err.println("lblUserName ou lblUserRole est null");
            }
        } catch (Exception e) {
            System.err.println("Erreur dans updateUserInfo: " + e.getMessage());
        }
    }

    private void applyRoleBasedVisibility() {
        try {
            if (UserRole.getInstance().isEmployee()) {
                System.out.println("Mode employé: masquage des éléments");

                if (btnCategories != null) {
                    btnCategories.setVisible(false);
                    btnCategories.setManaged(false);
                }

                if (btnResources != null) {
                    btnResources.setVisible(false);
                    btnResources.setManaged(false);
                }

                if (btnResource != null) {
                    btnResource.setVisible(false);
                    btnResource.setManaged(false);
                }

                if (btnPlanning != null) {
                    btnPlanning.setVisible(false);
                    btnPlanning.setManaged(false);
                }

                if (btnTaches != null) {
                    btnTaches.setVisible(false);
                    btnTaches.setManaged(false);
                }

                if (btnEvents != null) {
                    btnEvents.setVisible(false);
                    btnEvents.setManaged(false);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur dans applyRoleBasedVisibility: " + e.getMessage());
        }
    }

    @FXML
    private void showServices() {
        contentArea.getChildren().setAll(servicesView);
    }

    @FXML
    private void showCategories() {
        if (UserRole.getInstance().isAdmin()) {
            contentArea.getChildren().setAll(categoriesView);
        }
    }

    @FXML
    private void showResources() {
        if (UserRole.getInstance().isAdmin()) {
            contentArea.getChildren().setAll(resourcesView);
        }
    }

    @FXML
    private void showPlanning() {
        if (UserRole.getInstance().isAdmin()) {
            contentArea.getChildren().setAll(planningView);
        }
    }

    @FXML
    private void showEvents() {
        if (UserRole.getInstance().isAdmin()) {
            contentArea.getChildren().setAll(eventsView);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/Accueil.fxml"));
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("stratiX - Accueil");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node createPlaceholderView(String text) {
        Label label = new Label(text + " - En cours de développement");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b; -fx-alignment: center;");
        VBox pane = new VBox(label);
        pane.setStyle("-fx-background-color: #f8fafc; -fx-alignment: center;");
        return pane;
    }
}