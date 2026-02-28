package controllers;

import models.Role;
import models.UserRole;
import services.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import utils.SessionManager;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private UtilisateurService utilisateurService;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        errorLabel.setVisible(false);

        // Remplir automatiquement pour les tests
        emailField.setText("sol@gmail.com");
        passwordField.setText("RYry123*");
    }

    @FXML
    void handleLogin() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Veuillez entrer votre email");
            return;
        }

        try {
            System.out.println("🔓 MODE TEST - Connexion automatique avec email: " + email);

            // Chercher l'utilisateur par email
            Utilisateur user = utilisateurService.getByEmail(email);

            if (user == null) {
                // Créer un utilisateur factice
                user = new Utilisateur();
                user.setId(1);
                user.setEmail(email);
                user.setRole(Role.EMPLOYE);
                user.setNom("Test");
                user.setPrenom("User");
            }

            System.out.println("✅ Connexion réussie pour: " + user.getEmail() + " (" + user.getRole() + ")");

            // Sauvegarder la session
            SessionManager.getInstance().saveSession(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name()
            );

            UserRole.getInstance().setUser(user);

            // Rediriger
            if (user.getRole() == Role.ADMIN) {
                openDashboard(user);
            } else {
                openStandardUserDashboard(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur: " + e.getMessage());
        }
    }

    private void openDashboard(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard_admin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStandardUserDashboard(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/service-view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.initData(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1300, 750);
            stage.setScene(scene);
            stage.setTitle("Stratix - " + user.getNom() + " " + user.getPrenom());
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleForgotPassword() {
        // Désactivé en mode test
    }

    @FXML
    void handleSignup() {
        // Désactivé en mode test
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}