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
    @FXML private javafx.scene.layout.HBox userProfileSection;
    public static StackPane staticContentArea;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        staticContentArea = contentArea;
    }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
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
        utils.SessionManager.getInstance().logout();
        
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root, 1000, 700);
            
            if (getClass().getResource("/css/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            }
            
            stage.setScene(scene);
            stage.setTitle("Stratix - Connexion");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick() {
        Utilisateur user = UserRole.getInstance().getUser();
        if (user == null) return;
        
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Profil Utilisateur");
        dialog.setHeaderText(null);
        
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20);
        content.setPadding(new javafx.geometry.Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #f7fafc 0%, #ffffff 100%); -fx-background-radius: 10px;");
        
        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        javafx.scene.layout.StackPane avatarContainer = new javafx.scene.layout.StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(40);
        avatarCircle.setFill(javafx.scene.paint.Color.web("#4299E1"));
        avatarCircle.setStroke(javafx.scene.paint.Color.web("#3182CE"));
        avatarCircle.setStrokeWidth(3);
        avatarCircle.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(66, 153, 225, 0.3)));
        
        javafx.scene.control.Label avatarText = new javafx.scene.control.Label(
            user.getNom().substring(0, 1).toUpperCase() + user.getPrenom().substring(0, 1).toUpperCase()
        );
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");
        avatarContainer.getChildren().addAll(avatarCircle, avatarText);
        
        javafx.scene.layout.VBox nameBox = new javafx.scene.layout.VBox(8);
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(user.getNom() + " " + user.getPrenom());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        javafx.scene.control.Label roleLabel = new javafx.scene.control.Label(getRoleDisplayName(user.getRole()));
        roleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-background-color: #4299E1; -fx-background-radius: 12px; -fx-padding: 4px 12px;");
        nameBox.getChildren().addAll(nameLabel, roleLabel);
        
        header.getChildren().addAll(avatarContainer, nameBox);
        
        javafx.scene.layout.VBox infoCard = new javafx.scene.layout.VBox(15);
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        addProfileRow(grid, 0, "📧 Email:", user.getEmail());
        addProfileRow(grid, 1, "📱 Téléphone:", user.getTel());
        addProfileRow(grid, 2, "🆔 CIN:", String.valueOf(user.getCin()));
        addProfileRow(grid, 3, "📊 Statut:", user.getStatut() != null ? user.getStatut().toUpperCase() : "ACTIF");
        
        int rowIndex = 4;
        if (user.getRole() == Role.EMPLOYE || user.getRole() == Role.RESPONSABLE_RH || 
            user.getRole() == Role.RESPONSABLE_PROJET || user.getRole() == Role.RESPONSABLE_PRODUCTION) {
            if (user.getDepartment() != null && !user.getDepartment().isEmpty()) {
                addProfileRow(grid, rowIndex++, "🏢 Département:", user.getDepartment());
            }
            if (user.getPoste() != null && !user.getPoste().isEmpty()) {
                addProfileRow(grid, rowIndex++, "💼 Poste:", user.getPoste());
            }
            if (user.getSalaire() > 0) {
                addProfileRow(grid, rowIndex++, "💰 Salaire:", String.format("%.2f DT", user.getSalaire()));
            }
            if (user.getCompetences() != null && !user.getCompetences().isEmpty()) {
                addProfileRow(grid, rowIndex++, "🎯 Compétences:", user.getCompetences());
            }
        }
        
        infoCard.getChildren().add(grid);
        content.getChildren().addAll(header, infoCard);
        
        dialog.getDialogPane().setContent(content);
        
        javafx.scene.control.ButtonType closeButtonType = new javafx.scene.control.ButtonType("Fermer", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        dialog.getDialogPane().setStyle("-fx-background-color: #f7fafc; -fx-border-color: #E2E8F0; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        dialog.getDialogPane().setPrefWidth(500);
        
        javafx.scene.Node closeButton = dialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 30px; -fx-cursor: hand;");
        
        dialog.showAndWait();
    }
    
    private void addProfileRow(javafx.scene.layout.GridPane grid, int row, String label, String value) {
        javafx.scene.control.Label labelNode = new javafx.scene.control.Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #4A5568; -fx-font-size: 14px;");
        
        javafx.scene.control.Label valueNode = new javafx.scene.control.Label(value);
        valueNode.setStyle("-fx-text-fill: #2D3748; -fx-font-size: 14px; -fx-background-color: #F7FAFC; -fx-background-radius: 6px; -fx-padding: 8px 12px;");
        valueNode.setWrapText(true);
        valueNode.setMaxWidth(280);
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    
    private String getRoleDisplayName(Role role) {
        switch (role) {
            case ADMIN: return "Administrateur";
            case CEO: return "Directeur Général";
            case EMPLOYE: return "Employé";
            case RESPONSABLE_RH: return "Responsable RH";
            case RESPONSABLE_PROJET: return "Responsable Projet";
            case RESPONSABLE_PRODUCTION: return "Responsable Production";
            default: return role.name();
        }
    }
}
