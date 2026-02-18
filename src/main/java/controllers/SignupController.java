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
            showError("Erreur lors de l'inscription: " + e.getMessage());
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
