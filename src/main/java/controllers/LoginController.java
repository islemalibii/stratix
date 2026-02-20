package controllers;

import Services.AuthenticationService;
import Services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Utilisateur;
import models.Role;
import utils.PasswordValidator;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    
    @FXML
    private Hyperlink forgotPasswordLink;

    private UtilisateurService utilisateurService;
    private AuthenticationService authService;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        authService = AuthenticationService.getInstance();
        
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
            // Vérifier si le compte est verrouillé
            if (authService.isAccountLocked(email)) {
                showError("Compte verrouillé. Réessayez dans 15 minutes.");
                return;
            }

            // Hasher le mot de passe pour comparaison
            String hashedPassword = PasswordValidator.hashPassword(password);

            // Vérifier les credentials
            List<Utilisateur> utilisateurs = utilisateurService.getAll();
            Utilisateur user = utilisateurs.stream()
                    .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(hashedPassword))
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                // Réinitialiser les tentatives échouées
                authService.resetFailedAttempts(email);

                // Vérifier si 2FA est activé
                if (user.isTwoFactorEnabled()) {
                    show2FADialog(user);
                } else {
                    // Connexion réussie - Sauvegarder la session
                    SessionManager.getInstance().saveSession(user.getId(), user.getEmail(), user.getRole().name());
                    redirectToRoleDashboard(user);
                }
            } else {
                // Enregistrer tentative échouée
                authService.recordFailedLogin(email);
                showError("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showError("Erreur de connexion");
            e.printStackTrace();
        }
    }
    
    /**
     * Afficher le dialog 2FA
     */
    private void show2FADialog(Utilisateur user) {
        try {
            // Générer un nouveau code 2FA
            String code = authService.generate2FACode(user.getId());
            
            // Afficher le code (dans une vraie app, envoyer par SMS/Email)
            Alert codeAlert = new Alert(Alert.AlertType.INFORMATION);
            codeAlert.setTitle("Code 2FA");
            codeAlert.setHeaderText("Votre code de vérification");
            codeAlert.setContentText("Code: " + code + "\n\n(Dans une application réelle, ce code serait envoyé par SMS/Email)");
            codeAlert.showAndWait();
            
            // Dialog pour entrer le code
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Authentification à deux facteurs");
            dialog.setHeaderText("Entrez le code de vérification");
            dialog.setContentText("Code:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(enteredCode -> {
                try {
                    if (authService.verify2FACode(user.getId(), enteredCode)) {
                        // Code correct - Connexion réussie
                        SessionManager.getInstance().saveSession(user.getId(), user.getEmail(), user.getRole().name());
                        redirectToRoleDashboard(user);
                    } else {
                        showError("Code de vérification incorrect");
                    }
                } catch (SQLException e) {
                    showError("Erreur lors de la vérification du code");
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            showError("Erreur lors de la génération du code 2FA");
            e.printStackTrace();
        }
    }
    
    /**
     * Rediriger vers le dashboard selon le rôle
     */
    private void redirectToRoleDashboard(Utilisateur user) {
        switch (user.getRole()) {
            case ADMIN:
                openDashboard(user);
                break;
            case CEO:
                openCEODashboard(user);
                break;
            case RESPONSABLE_RH:
            case RESPONSABLE_PROJET:
            case RESPONSABLE_PRODUCTION:
                openResponsableDashboard(user);
                break;
            case EMPLOYE:
                openEmployeDashboard(user);
                break;
            default:
                showError("Rôle non reconnu");
        }
    }
    
    @FXML
    void handleForgotPassword(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mot de passe oublié");
        dialog.setHeaderText("Réinitialisation du mot de passe");
        dialog.setContentText("Entrez votre email:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            try {
                // Vérifier si l'email existe
                Utilisateur user = utilisateurService.getByEmail(email);
                if (user != null) {
                    // Générer un token de réinitialisation
                    String token = authService.generatePasswordResetToken(email);
                    
                    // Afficher le token (dans une vraie app, envoyer par email)
                    Alert tokenAlert = new Alert(Alert.AlertType.INFORMATION);
                    tokenAlert.setTitle("Token de réinitialisation");
                    tokenAlert.setHeaderText("Token généré");
                    tokenAlert.setContentText("Token: " + token + "\n\n(Dans une application réelle, ce token serait envoyé par email)\n\nValide pendant 1 heure.");
                    tokenAlert.showAndWait();
                    
                    // Ouvrir le dialog de réinitialisation
                    showResetPasswordDialog();
                } else {
                    showError("Email non trouvé");
                }
            } catch (SQLException e) {
                showError("Erreur lors de la génération du token");
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Dialog pour réinitialiser le mot de passe
     */
    private void showResetPasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Réinitialiser le mot de passe");
        dialog.setHeaderText("Entrez le token et votre nouveau mot de passe");
        
        ButtonType resetButtonType = new ButtonType("Réinitialiser", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField tokenField = new TextField();
        tokenField.setPromptText("Token");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer mot de passe");
        
        grid.add(new Label("Token:"), 0, 0);
        grid.add(tokenField, 1, 0);
        grid.add(new Label("Nouveau mot de passe:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirmer:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == resetButtonType) {
            String token = tokenField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            
            // Validation
            if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("Veuillez remplir tous les champs");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showError("Les mots de passe ne correspondent pas");
                return;
            }
            
            // Valider la force du mot de passe
            PasswordValidator.ValidationResult validation = PasswordValidator.validatePassword(newPassword);
            if (!validation.isValid()) {
                showError(validation.getMessage());
                return;
            }
            
            try {
                if (authService.resetPassword(token, newPassword)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Succès");
                    success.setHeaderText(null);
                    success.setContentText("Mot de passe réinitialisé avec succès!");
                    success.showAndWait();
                } else {
                    showError("Token invalide ou expiré");
                }
            } catch (SQLException e) {
                showError("Erreur lors de la réinitialisation");
                e.printStackTrace();
            }
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
    
    private void openCEODashboard(Utilisateur user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dashboard CEO");
        alert.setHeaderText("Bienvenue " + user.getNom());
        alert.setContentText("Le dashboard CEO sera disponible prochainement.");
        alert.showAndWait();
    }
    
    private void openResponsableDashboard(Utilisateur user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dashboard Responsable");
        alert.setHeaderText("Bienvenue " + user.getNom());
        alert.setContentText("Le dashboard Responsable sera disponible prochainement.");
        alert.showAndWait();
    }
    
    private void openEmployeDashboard(Utilisateur user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dashboard Employé");
        alert.setHeaderText("Bienvenue " + user.getNom());
        alert.setContentText("Le dashboard Employé sera disponible prochainement.");
        alert.showAndWait();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
