package controllers;

import Services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink signupLink;

    private UtilisateurService utilisateurService;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        
        // Contrôle de saisie pour l'email
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });
        
        // Validation email au focus perdu
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !emailField.getText().isEmpty()) {
                if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    emailField.setStyle("-fx-border-color: #EF4444; -fx-border-width: 2;");
                } else {
                    emailField.setStyle("");
                }
            }
        });
        
        // Masquer l'erreur quand on tape le mot de passe
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            // Vérifier les credentials
            List<Utilisateur> utilisateurs = utilisateurService.getAll();
            Utilisateur user = utilisateurs.stream()
                    .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                // Connexion réussie
                if (user.getRole().name().equals("ADMIN")) {
                    openDashboard(user);
                } else {
                    showError("Accès réservé aux administrateurs");
                }
            } else {
                showError("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showError("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/signup.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDashboard(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard_admin.fxml"));
            Parent root = loader.load();
            
            DashboardAdminController controller = loader.getController();
            controller.setCurrentUser(user);
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setMaximized(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
