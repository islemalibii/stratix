package controllers;

import javafx.event.ActionEvent;
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
import models.Role;
import models.UserRole;
import models.Utilisateur;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Button btnCategories;
    @FXML private Button btnResources;
    @FXML private Button btnProjet;
    @FXML private Button btnTaches;
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblUserAvatar;
    public static StackPane staticContentArea;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        staticContentArea = contentArea;    }

    public void initData(Utilisateur user) {
        updateUserInfo(user);

        applyRoleBasedVisibility(user);

        if (user.getRole() == Role.EMPLOYE) {
            loadView("/EventEmployeeDashboard.fxml");
        } else {
            loadView("/EventDashboard.fxml");
        }
    }


    private void updateUserInfo(Utilisateur user) {
        if (lblUserName == null) return;

        lblUserName.setText(user.getNom() + " " + user.getPrenom());

        if (user.getRole() == Role.EMPLOYE) {
            lblUserRole.setText("Session Employé");
            if (lblUserAvatar != null) lblUserAvatar.setText("👤");
        } else {
            lblUserRole.setText(user.getRole().name());
            if (lblUserAvatar != null) lblUserAvatar.setText("👑");
        }
    }

    private void applyRoleBasedVisibility(Utilisateur user) {
        if (user.getRole() == Role.EMPLOYE) {
            Button[] toHide = {btnCategories};
            for (Button btn : toHide) {
                if (btn != null) {
                    btn.setVisible(false);
                    btn.setManaged(false);
                }
            }
        }
    }


    @FXML
    private void showServices(ActionEvent event) {
        loadView("/service-tab.fxml");
    }

    @FXML
    private void showCategories(ActionEvent event) {
        loadView("/categorie-tab.fxml");
    }

    @FXML
    private void handleEvenements(ActionEvent event) {
        Utilisateur user = UserRole.getInstance().getUser();
        String fxml = (user != null && user.getRole() == Role.EMPLOYE) ?
                "/EventEmployeeDashboard.fxml" : "/EventDashboard.fxml";
        loadView(fxml);
    }

    @FXML
    private void showProjet(ActionEvent event) {
        Utilisateur user = UserRole.getInstance().getUser();
        String fxml = (user != null && user.getRole() == Role.EMPLOYE) ?
                "/EmployeListeProjets.fxml" : "/ListeProjets.fxml";
        loadView(fxml);
    }
    private void loadView(String fxmlPath) {
        try {
            // Load the FXML (which is now just the VBox content)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Clear the StackPane and inject only the new content
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void showResources() {
        Utilisateur user = UserRole.getInstance().getUser();
        String fxml = (user != null && user.getRole() == Role.EMPLOYE) ?
                "/FrontRessources.fxml" : "/ressource.fxml";
        loadView(fxml);
    }

    @FXML
    private void showProduits() {
        Utilisateur user = UserRole.getInstance().getUser();
        String fxml = (user != null && user.getRole() == Role.EMPLOYE) ?
                "/FrontProduits.fxml" : "/produit.fxml";
        loadView(fxml);
    }

    @FXML
    private void showPlanning() {
        Utilisateur user = UserRole.getInstance().getUser();
        String fxml = (user != null && user.getRole() == Role.EMPLOYE) ?
                "/EmpTacheView.fxml" : "/dashboard-view.fxml";
        loadView(fxml);
    }



    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("stratiX - Accueil");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}