package controllers;

import Services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Role;
import models.Utilisateur;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telField;

    @FXML
    private TextField cinField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button signupButton;

    @FXML
    private Hyperlink loginLink;

    private UtilisateurService utilisateurService;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        
        // Contrôle de saisie pour le nom (lettres uniquement)
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                nomField.setText(oldValue);
            }
        });
        
        // Contrôle de saisie pour le prénom (lettres uniquement)
        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                prenomField.setText(oldValue);
            }
        });
        
        // Contrôle de saisie pour l'email (format email)
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !emailField.getText().isEmpty()) {
                if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    emailField.setStyle("-fx-border-color: #EF4444; -fx-border-width: 2;");
                } else {
                    emailField.setStyle("");
                }
            }
        });
        
        // Contrôle de saisie pour le téléphone (chiffres et + uniquement, max 12 caractères)
        telField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9+\\s]*")) {
                telField.setText(oldValue);
            }
            if (newValue.length() > 12) {
                telField.setText(oldValue);
            }
        });
        
        // Contrôle de saisie pour le CIN (8 chiffres uniquement)
        cinField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cinField.setText(oldValue);
            }
            if (newValue.length() > 8) {
                cinField.setText(oldValue);
            }
        });
        
        // Contrôle de saisie pour la confirmation du mot de passe
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !passwordField.getText().isEmpty()) {
                if (!newValue.equals(passwordField.getText())) {
                    confirmPasswordField.setStyle("-fx-border-color: #EF4444; -fx-border-width: 2;");
                } else {
                    confirmPasswordField.setStyle("-fx-border-color: #10B981; -fx-border-width: 2;");
                }
            } else {
                confirmPasswordField.setStyle("");
            }
        });
        
        // Validation du mot de passe (minimum 6 caractères)
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !passwordField.getText().isEmpty()) {
                if (passwordField.getText().length() < 6) {
                    passwordField.setStyle("-fx-border-color: #EF4444; -fx-border-width: 2;");
                } else {
                    passwordField.setStyle("-fx-border-color: #10B981; -fx-border-width: 2;");
                }
            }
        });
    }

    @FXML
    void handleSignup(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String tel = telField.getText().trim();
        String cinStr = cinField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || tel.isEmpty() || 
            cinStr.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }

        if (password.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        int cin;
        try {
            cin = Integer.parseInt(cinStr);
            if (cinStr.length() != 8) {
                showError("Le CIN doit contenir exactement 8 chiffres");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Le CIN doit être un nombre valide");
            return;
        }

        // Créer l'utilisateur
        try {
            Utilisateur user = new Utilisateur(nom, prenom, email, tel, cin, password, Role.EMPLOYE);
            utilisateurService.ajouter(user);

            // Afficher message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inscription réussie");
            alert.setHeaderText(null);
            alert.setContentText("Votre compte a été créé avec succès !");
            alert.showAndWait();

            // Retour au login
            handleBackToLogin(event);
        } catch (Exception e) {
            showError("Erreur lors de l'inscription");
            e.printStackTrace();
        }
    }

    @FXML
    void handleBackToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
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
