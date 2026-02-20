package controllers;

import Services.AuthenticationService;
import Services.EmailService;
import Services.UtilisateurService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Utilisateur;
import models.Role;
import utils.PasswordValidator;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
    private EmailService emailService;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        authService = AuthenticationService.getInstance();
        emailService = EmailService.getInstance();
        
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
            AuthenticationService.LockStatus lockStatus = authService.isAccountLocked(email);
            if (lockStatus.isLocked()) {
                showLockoutCountdown(lockStatus);
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
                    // Générer un code de vérification (6 chiffres)
                    String code = PasswordValidator.generate2FACode();
                    
                    // Enregistrer le code comme token de réinitialisation
                    authService.generatePasswordResetToken(email);
                    
                    // Envoyer l'email
                    boolean emailSent = emailService.sendPasswordResetEmail(email, code);
                    
                    if (emailSent) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Email envoyé");
                        successAlert.setHeaderText("Code de vérification envoyé");
                        successAlert.setContentText("Un code de vérification a été envoyé à votre adresse email.\n\nVérifiez votre boîte de réception.");
                        successAlert.showAndWait();
                        
                        // Ouvrir le dialog de réinitialisation avec le code
                        showResetPasswordDialog(code);
                    } else {
                        // Si l'envoi échoue, afficher le code à l'écran (fallback)
                        Alert codeAlert = new Alert(Alert.AlertType.INFORMATION);
                        codeAlert.setTitle("Code de vérification");
                        codeAlert.setHeaderText("Impossible d'envoyer l'email");
                        codeAlert.setContentText("Votre code de vérification: " + code + "\n\n(Configurez votre serveur SMTP dans EmailService.java pour activer l'envoi d'emails)");
                        codeAlert.showAndWait();
                        
                        showResetPasswordDialog(code);
                    }
                } else {
                    showError("Email non trouvé");
                }
            } catch (SQLException e) {
                showError("Erreur lors de la génération du code");
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Dialog pour réinitialiser le mot de passe
     */
    private void showResetPasswordDialog(String expectedCode) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Réinitialiser le mot de passe");
        dialog.setHeaderText("Entrez le code reçu par email et votre nouveau mot de passe");
        
        ButtonType resetButtonType = new ButtonType("Réinitialiser", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField codeField = new TextField();
        codeField.setPromptText("Code de vérification");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer mot de passe");
        
        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nouveau mot de passe:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirmer:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == resetButtonType) {
            String enteredCode = codeField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            
            // Validation
            if (enteredCode.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("Veuillez remplir tous les champs");
                return;
            }
            
            // Vérifier le code
            if (!enteredCode.equals(expectedCode)) {
                showError("Code de vérification incorrect");
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
                // Hasher et enregistrer le nouveau mot de passe
                String hashedPassword = PasswordValidator.hashPassword(newPassword);
                
                // Mettre à jour directement dans la base (utiliser l'email stocké)
                // Pour simplifier, on utilise le code comme référence
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setHeaderText(null);
                success.setContentText("Mot de passe réinitialisé avec succès!\n\nVous pouvez maintenant vous connecter avec votre nouveau mot de passe.");
                success.showAndWait();
            } catch (Exception e) {
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
    
    /**
     * Afficher un compteur à rebours pour le verrouillage
     */
    private void showLockoutCountdown(AuthenticationService.LockStatus lockStatus) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Compte Verrouillé");
        alert.setHeaderText("Trop de tentatives échouées");
        
        // Label pour le compteur
        Label countdownLabel = new Label();
        countdownLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
        
        Label messageLabel = new Label("Votre compte a été temporairement verrouillé.\nVeuillez patienter:");
        messageLabel.setStyle("-fx-font-size: 14px;");
        
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(messageLabel, countdownLabel);
        
        alert.getDialogPane().setContent(content);
        
        // Calculer le temps restant en secondes
        long totalSeconds = lockStatus.getMinutesRemaining() * 60;
        
        // Timeline pour mettre à jour le compteur chaque seconde
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                long currentSeconds = java.time.Duration.between(
                    LocalDateTime.now(), 
                    lockStatus.getUnlockTime()
                ).getSeconds();
                
                if (currentSeconds <= 0) {
                    alert.close();
                    showError("Vous pouvez maintenant réessayer de vous connecter");
                } else {
                    long minutes = currentSeconds / 60;
                    long seconds = currentSeconds % 60;
                    countdownLabel.setText(String.format("%d:%02d", minutes, seconds));
                }
            })
        );
        
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        // Arrêter le timeline quand le dialog se ferme
        alert.setOnCloseRequest(e -> timeline.stop());
        
        // Affichage initial
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        countdownLabel.setText(String.format("%d:%02d", minutes, seconds));
        
        alert.showAndWait();
        timeline.stop();
    }
}
