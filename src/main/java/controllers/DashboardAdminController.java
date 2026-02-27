package controllers;

import services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.*;
import utils.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardAdminController {

    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label avatarLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private VBox cardsContainer;

    @FXML
    private Button logoutButton;

    @FXML
    private Button btnEvenements;

    @FXML
    private HBox userProfileSection;

    private Utilisateur currentUser;
    private UtilisateurService utilisateurService;

    private String currentView = "utilisateurs";
    private List<Utilisateur> currentData;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        loadUtilisateurs();
        
        // Recherche en temps réel
        searchField.textProperty().addListener((obs, old, newVal) -> handleSearch(null));
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
        
        // Mettre à jour l'avatar avec la première lettre du prénom
        if (user.getPrenom() != null && !user.getPrenom().isEmpty()) {
            avatarLabel.setText(user.getPrenom().substring(0, 1).toUpperCase());
        }
    }

    @FXML
    void showUtilisateurs(ActionEvent event) {
        currentView = "utilisateurs";
        titleLabel.setText("Gestion des Utilisateurs");
        loadUtilisateurs();
    }

    @FXML
    void showEmployes(ActionEvent event) {
        currentView = "employes";
        titleLabel.setText("Gestion des Employés");
        loadEmployes();
    }

    @FXML
    void showAdmins(ActionEvent event) {
        currentView = "admins";
        titleLabel.setText("Gestion des Admins");
        loadAdmins();
    }

    @FXML
    void showCEOs(ActionEvent event) {
        currentView = "ceos";
        titleLabel.setText("Gestion des CEOs");
        loadCEOs();
    }

    @FXML
    void showResponsables(ActionEvent event) {
        currentView = "responsables";
        titleLabel.setText("Gestion des Responsables");
        loadResponsables();
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        showAddDialog();
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            displayCards(currentData);
            return;
        }

        List<Utilisateur> filteredData = currentData.stream()
                .filter(user -> user.getNom().toLowerCase().contains(searchText) ||
                               user.getPrenom().toLowerCase().contains(searchText) ||
                               user.getEmail().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        displayCards(filteredData);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        // Supprimer la session
        SessionManager.getInstance().logout();
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setMaximized(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleProfileClick() {
        if (currentUser == null) return;

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
            currentUser.getNom().substring(0, 1).toUpperCase() + currentUser.getPrenom().substring(0, 1).toUpperCase()
        );
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");
        avatarContainer.getChildren().addAll(avatarCircle, avatarText);

        javafx.scene.layout.VBox nameBox = new javafx.scene.layout.VBox(8);
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(currentUser.getNom() + " " + currentUser.getPrenom());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");

        javafx.scene.control.Label roleLabel = new javafx.scene.control.Label("Administrateur");
        roleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-background-color: #4299E1; -fx-background-radius: 12px; -fx-padding: 4px 12px;");
        nameBox.getChildren().addAll(nameLabel, roleLabel);

        header.getChildren().addAll(avatarContainer, nameBox);

        javafx.scene.layout.VBox infoCard = new javafx.scene.layout.VBox(15);
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        addProfileRow(grid, 0, "📧 Email:", currentUser.getEmail());
        addProfileRow(grid, 1, "📱 Téléphone:", currentUser.getTel());
        addProfileRow(grid, 2, "🆔 CIN:", String.valueOf(currentUser.getCin()));
        addProfileRow(grid, 3, "📊 Statut:", currentUser.getStatut() != null ? currentUser.getStatut().toUpperCase() : "ACTIF");

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


    private void loadUtilisateurs() {
        try {
            currentData = utilisateurService.getAll();
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les utilisateurs");
            e.printStackTrace();
        }
    }

    private void loadEmployes() {
        try {
            currentData = utilisateurService.getByRole(Role.EMPLOYE);
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les employés");
            e.printStackTrace();
        }
    }

    private void loadAdmins() {
        try {
            currentData = utilisateurService.getByRole(Role.ADMIN);
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les administrateurs");
            e.printStackTrace();
        }
    }

    private void loadCEOs() {
        try {
            currentData = utilisateurService.getByRole(Role.CEO);
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les CEOs");
            e.printStackTrace();
        }
    }

    private void loadResponsables() {
        try {
            List<Utilisateur> allResponsables = new java.util.ArrayList<>();
            allResponsables.addAll(utilisateurService.getByRole(Role.RESPONSABLE_RH));
            allResponsables.addAll(utilisateurService.getByRole(Role.RESPONSABLE_PROJET));
            allResponsables.addAll(utilisateurService.getByRole(Role.RESPONSABLE_PRODUCTION));
            currentData = allResponsables;
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les responsables");
            e.printStackTrace();
        }
    }

    private void displayCards(List<Utilisateur> users) {
        cardsContainer.getChildren().clear();

        if (users.isEmpty()) {
            Label emptyLabel = new Label("Aucun utilisateur trouvé");
            emptyLabel.getStyleClass().add("empty-label");
            cardsContainer.getChildren().add(emptyLabel);
            return;
        }

        // Créer une grille avec 3 colonnes
        int columns = 3;
        int row = 0;
        HBox currentRow = null;

        for (int i = 0; i < users.size(); i++) {
            if (i % columns == 0) {
                currentRow = new HBox(20);
                currentRow.setAlignment(Pos.TOP_LEFT);
                cardsContainer.getChildren().add(currentRow);
            }

            VBox card = createUserCardGrid(users.get(i));
            HBox.setHgrow(card, Priority.ALWAYS);
            currentRow.getChildren().add(card);
        }
    }

    private VBox createUserCardGrid(Utilisateur user) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("user-card-grid");
        card.setPrefWidth(300);
        card.setMaxWidth(350);

        // Avatar/Icon section
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(80, 80);
        avatarPane.getStyleClass().add("avatar-large");
        
        Label avatarLabel = new Label(user.getNom().substring(0, 1).toUpperCase());
        avatarLabel.setFont(Font.font("System Bold", 32));
        avatarLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        avatarPane.getChildren().add(avatarLabel);

        // Name
        Label nameLabel = new Label(user.getNom() + " " + user.getPrenom());
        nameLabel.setFont(Font.font("System Bold", 16));
        nameLabel.getStyleClass().add("card-name-label");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setAlignment(Pos.CENTER);

        // Role badge
        Label roleLabel = new Label(user.getRole().toString());
        roleLabel.setPadding(new Insets(6, 16, 6, 16));
        roleLabel.getStyleClass().addAll("role-badge", getRoleBadgeClass(user.getRole()));
        
        // Statut badge
        Label statutLabel = new Label(user.isActif() ? "✓ Actif" : "✗ Inactif");
        statutLabel.setPadding(new Insets(4, 12, 4, 12));
        statutLabel.setStyle(user.isActif() 
            ? "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;"
            : "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;"
        );
        
        HBox badgesBox = new HBox(8);
        badgesBox.setAlignment(Pos.CENTER);
        badgesBox.getChildren().addAll(roleLabel, statutLabel);

        // Details
        VBox detailsBox = new VBox(6);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.setPrefWidth(Double.MAX_VALUE);

        Label emailLabel = new Label("📧 " + user.getEmail());
        emailLabel.getStyleClass().add("card-detail-label-small");
        emailLabel.setMaxWidth(Double.MAX_VALUE);

        Label telLabel = new Label("📞 " + user.getTel());
        telLabel.getStyleClass().add("card-detail-label-small");

        detailsBox.getChildren().addAll(emailLabel, telLabel);

        // Si employé, afficher poste et salaire
        if (user.isEmploye() && user.getPoste() != null) {
            Label posteLabel = new Label("💼 " + user.getPoste());
            posteLabel.getStyleClass().add("card-detail-label-small");
            detailsBox.getChildren().add(posteLabel);

            if (user.getSalaire() > 0) {
                Label salaireLabel = new Label("💰 " + user.getSalaire() + " €");
                salaireLabel.getStyleClass().add("card-detail-label-small");
                detailsBox.getChildren().add(salaireLabel);
            }
        }

        // Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setPrefWidth(Double.MAX_VALUE);

        Button btnModifier = new Button("Modifier");
        btnModifier.getStyleClass().addAll("button-warning", "card-action-button-small");
        btnModifier.setOnAction(e -> handleModifier(user));
        HBox.setHgrow(btnModifier, Priority.ALWAYS);
        btnModifier.setMaxWidth(Double.MAX_VALUE);

        Button btnToggleStatut = new Button(user.isActif() ? "Désactiver" : "Activer");
        btnToggleStatut.getStyleClass().addAll(
            user.isActif() ? "button-danger" : "button-success", 
            "card-action-button-small"
        );
        btnToggleStatut.setOnAction(e -> handleToggleStatut(user));
        HBox.setHgrow(btnToggleStatut, Priority.ALWAYS);
        btnToggleStatut.setMaxWidth(Double.MAX_VALUE);

        actionsBox.getChildren().addAll(btnModifier, btnToggleStatut);

        card.getChildren().addAll(avatarPane, nameLabel, badgesBox, detailsBox, actionsBox);

        return card;
    }

    private HBox createUserCard(Utilisateur user) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("user-card");
        card.setPrefHeight(100);

        // Info section
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(user.getNom() + " " + user.getPrenom());
        nameLabel.setFont(Font.font("System Bold", 16));
        nameLabel.getStyleClass().add("card-name-label");

        Label emailLabel = new Label("📧 " + user.getEmail());
        emailLabel.getStyleClass().add("card-detail-label");

        Label telLabel = new Label("📞 " + user.getTel());
        telLabel.getStyleClass().add("card-detail-label");

        HBox detailsBox = new HBox(20);
        detailsBox.getChildren().addAll(emailLabel, telLabel);

        infoBox.getChildren().addAll(nameLabel, detailsBox);

        // Role badge
        Label roleLabel = new Label(user.getRole().toString());
        roleLabel.setPadding(new Insets(6, 16, 6, 16));
        roleLabel.getStyleClass().addAll("role-badge", getRoleBadgeClass(user.getRole()));

        // Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnModifier = new Button("Modifier");
        btnModifier.getStyleClass().addAll("button-warning", "card-action-button");
        btnModifier.setOnAction(e -> handleModifier(user));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.getStyleClass().addAll("button-danger", "card-action-button");
        btnSupprimer.setOnAction(e -> handleSupprimer(user));

        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);

        // Assemble card
        VBox rightBox = new VBox(10);
        rightBox.setAlignment(Pos.TOP_RIGHT);
        rightBox.getChildren().add(roleLabel);

        card.getChildren().addAll(infoBox, rightBox, actionsBox);

        return card;
    }

    private String getRoleBadgeClass(Role role) {
        switch (role) {
            case ADMIN:
                return "role-badge-admin";
            case CEO:
                return "role-badge-ceo";
            case EMPLOYE:
                return "role-badge-employe";
            case RESPONSABLE_RH:
                return "role-badge-rh";
            case RESPONSABLE_PROJET:
                return "role-badge-projet";
            case RESPONSABLE_PRODUCTION:
                return "role-badge-production";
            default:
                return "role-badge-default";
        }
    }

    private void handleModifier(Utilisateur user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Utilisateur");
        dialog.setHeaderText("Modifier les informations de " + user.getNom() + " " + user.getPrenom());

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = createUserFormGrid(user);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            try {
                String oldPassword = user.getPassword(); // Sauvegarder l'ancien mot de passe
                updateUserFromForm(user, grid);
                
                // Si le mot de passe a été modifié, le hasher
                if (!user.getPassword().equals(oldPassword)) {
                    String hashedPassword = utils.PasswordValidator.hashPassword(user.getPassword());
                    user.setPassword(hashedPassword);
                }
                
                utilisateurService.modifier(user);
                refreshCurrentView();
                showAlert("Succès", "Utilisateur modifié avec succès");
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modifier l'utilisateur");
                e.printStackTrace();
            }
        }
    }

    private void handleSupprimer(Utilisateur user) {
        // Désactiver au lieu de supprimer
        handleToggleStatut(user);
    }
    
    private void handleToggleStatut(Utilisateur user) {
        try {
            if (user.isActif()) {
                // Confirmation avant désactivation
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText("Désactiver l'utilisateur");
                confirmation.setContentText("Êtes-vous sûr de vouloir désactiver " + 
                                           user.getNom() + " " + user.getPrenom() + " ?\n\n" +
                                           "L'utilisateur ne pourra plus se connecter.");

                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    utilisateurService.desactiver(user.getId());
                    refreshCurrentView();
                    showAlert("Succès", "Utilisateur désactivé avec succès");
                }
            } else {
                // Activer directement sans confirmation
                utilisateurService.activer(user.getId());
                refreshCurrentView();
                showAlert("Succès", "Utilisateur activé avec succès");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de modifier le statut de l'utilisateur");
            e.printStackTrace();
        }
    }

    private void refreshCurrentView() {
        switch (currentView) {
            case "utilisateurs":
                loadUtilisateurs();
                break;
            case "employes":
                loadEmployes();
                break;
            case "admins":
                loadAdmins();
                break;
            case "ceos":
                loadCEOs();
                break;
            case "responsables":
                loadResponsables();
                break;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter Utilisateur");
        dialog.setHeaderText("Créer un nouvel utilisateur");

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        ButtonType saveButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = createUserFormGrid(null);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            try {
                Utilisateur newUser = createUserFromForm(grid);
                
                // DEBUG: Afficher les informations de l'utilisateur créé
                System.out.println("=== DEBUG AJOUT UTILISATEUR ===");
                System.out.println("Nom: " + newUser.getNom());
                System.out.println("Email: " + newUser.getEmail());
                System.out.println("Rôle récupéré: " + newUser.getRole());
                System.out.println("Mot de passe (avant hash): " + newUser.getPassword());
                
                // Vérifier l'unicité de l'email et du CIN
                if (utilisateurService.emailExists(newUser.getEmail())) {
                    showAlert("Erreur", "Cet email est déjà utilisé");
                    return;
                }
                
                if (utilisateurService.cinExists(newUser.getCin())) {
                    showAlert("Erreur", "Ce CIN est déjà enregistré");
                    return;
                }
                
                // Hasher le mot de passe avant d'ajouter
                String hashedPassword = utils.PasswordValidator.hashPassword(newUser.getPassword());
                newUser.setPassword(hashedPassword);
                
                System.out.println("Mot de passe (après hash): " + newUser.getPassword());
                System.out.println("Rôle final: " + newUser.getRole());
                System.out.println("=== FIN DEBUG ===");
                
                utilisateurService.ajouter(newUser);
                refreshCurrentView();
                showAlert("Succès", "Utilisateur ajouté avec succès");
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'ajouter l'utilisateur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private GridPane createUserFormGrid(Utilisateur user) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Champs de base
        TextField nomField = new TextField(user != null ? user.getNom() : "");
        TextField prenomField = new TextField(user != null ? user.getPrenom() : "");
        TextField emailField = new TextField(user != null ? user.getEmail() : "");
        TextField telField = new TextField(user != null ? user.getTel() : "");
        TextField cinField = new TextField(user != null ? String.valueOf(user.getCin()) : "");
        PasswordField passwordField = new PasswordField();
        if (user != null) passwordField.setText(user.getPassword());

        // Contrôles de saisie pour nom (lettres uniquement)
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                nomField.setText(oldValue);
            }
        });

        // Contrôles de saisie pour prénom (lettres uniquement)
        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                prenomField.setText(oldValue);
            }
        });

        // Validation email
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !emailField.getText().isEmpty()) {
                if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    emailField.setStyle("-fx-border-color: #EF4444; -fx-border-width: 2;");
                } else {
                    emailField.setStyle("");
                }
            }
        });

        // Contrôle téléphone (chiffres et + uniquement, max 12)
        telField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9+\\s]*")) {
                telField.setText(oldValue);
            }
            if (newValue.length() > 12) {
                telField.setText(oldValue);
            }
        });

        // Contrôle CIN (8 chiffres uniquement)
        cinField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cinField.setText(oldValue);
            }
            if (newValue.length() > 8) {
                cinField.setText(oldValue);
            }
        });

        // Validation mot de passe (minimum 6 caractères)
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !passwordField.getText().isEmpty()) {
                if (passwordField.getText().length() < 6) {
                    passwordField.setStyle("-fx-border-color: #EF4444; -fx-border-width: 2;");
                } else {
                    passwordField.setStyle("-fx-border-color: #10B981; -fx-border-width: 2;");
                }
            }
        });

        // Role
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Role.values());
        roleCombo.setValue(user != null ? user.getRole() : Role.EMPLOYE);

        // Champs employé (optionnels)
        Label departmentLabel = new Label("Département:");
        TextField departmentField = new TextField(user != null ? user.getDepartment() : "");
        
        Label posteLabel = new Label("Poste:");
        TextField posteField = new TextField(user != null ? user.getPoste() : "");
        
        Label salaireLabel = new Label("Salaire:");
        TextField salaireField = new TextField(user != null && user.getSalaire() > 0 ? String.valueOf(user.getSalaire()) : "");
        
        Label competencesLabel = new Label("Compétences:");
        TextField competencesField = new TextField(user != null ? user.getCompetences() : "");

        // Contrôle salaire (chiffres et point décimal uniquement)
        salaireField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                salaireField.setText(oldValue);
            }
        });

        // Ajouter les champs de base
        int row = 0;
        grid.add(new Label("Nom:"), 0, row);
        grid.add(nomField, 1, row++);
        grid.add(new Label("Prénom:"), 0, row);
        grid.add(prenomField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("Téléphone:"), 0, row);
        grid.add(telField, 1, row++);
        grid.add(new Label("CIN:"), 0, row);
        grid.add(cinField, 1, row++);
        grid.add(new Label("Mot de passe:"), 0, row);
        grid.add(passwordField, 1, row++);
        grid.add(new Label("Rôle:"), 0, row);
        grid.add(roleCombo, 1, row++);

        // Lignes pour les champs employé (initialement cachés si ADMIN/CEO)
        int employeFieldsStartRow = row;
        grid.add(departmentLabel, 0, row);
        grid.add(departmentField, 1, row++);
        grid.add(posteLabel, 0, row);
        grid.add(posteField, 1, row++);
        grid.add(salaireLabel, 0, row);
        grid.add(salaireField, 1, row++);
        grid.add(competencesLabel, 0, row);
        grid.add(competencesField, 1, row++);

        // Fonction pour afficher/cacher les champs employé
        Runnable updateFieldsVisibility = () -> {
            Role selectedRole = roleCombo.getValue();
            boolean isEmployeOrResponsable = selectedRole == Role.EMPLOYE || 
                                            selectedRole == Role.RESPONSABLE_RH ||
                                            selectedRole == Role.RESPONSABLE_PROJET ||
                                            selectedRole == Role.RESPONSABLE_PRODUCTION;
            
            departmentLabel.setVisible(isEmployeOrResponsable);
            departmentLabel.setManaged(isEmployeOrResponsable);
            departmentField.setVisible(isEmployeOrResponsable);
            departmentField.setManaged(isEmployeOrResponsable);
            
            posteLabel.setVisible(isEmployeOrResponsable);
            posteLabel.setManaged(isEmployeOrResponsable);
            posteField.setVisible(isEmployeOrResponsable);
            posteField.setManaged(isEmployeOrResponsable);
            
            salaireLabel.setVisible(isEmployeOrResponsable);
            salaireLabel.setManaged(isEmployeOrResponsable);
            salaireField.setVisible(isEmployeOrResponsable);
            salaireField.setManaged(isEmployeOrResponsable);
            
            competencesLabel.setVisible(isEmployeOrResponsable);
            competencesLabel.setManaged(isEmployeOrResponsable);
            competencesField.setVisible(isEmployeOrResponsable);
            competencesField.setManaged(isEmployeOrResponsable);
        };

        // Initialiser la visibilité
        updateFieldsVisibility.run();

        // Écouter les changements de rôle
        roleCombo.setOnAction(e -> updateFieldsVisibility.run());

        // Stocker les références pour récupération ultérieure
        nomField.setUserData("nom");
        prenomField.setUserData("prenom");
        emailField.setUserData("email");
        telField.setUserData("tel");
        cinField.setUserData("cin");
        passwordField.setUserData("password");
        roleCombo.setUserData("role");
        departmentField.setUserData("department");
        posteField.setUserData("poste");
        salaireField.setUserData("salaire");
        competencesField.setUserData("competences");

        return grid;
    }

    private Utilisateur createUserFromForm(GridPane grid) {
        String nom = getFieldValue(grid, "nom");
        String prenom = getFieldValue(grid, "prenom");
        String email = getFieldValue(grid, "email");
        String tel = getFieldValue(grid, "tel");
        int cin = Integer.parseInt(getFieldValue(grid, "cin"));
        String password = getFieldValue(grid, "password");
        Role role = getComboValue(grid, "role");
        
        // Si le rôle est ADMIN ou CEO, créer sans les champs employé
        if (role == Role.ADMIN || role == Role.CEO) {
            return new Utilisateur(nom, prenom, email, tel, cin, password, role);
        } else {
            // Pour EMPLOYE et RESPONSABLES, inclure les champs employé
            String department = getFieldValue(grid, "department");
            String poste = getFieldValue(grid, "poste");
            String salaireStr = getFieldValue(grid, "salaire");
            double salaire = salaireStr.isEmpty() ? 0.0 : Double.parseDouble(salaireStr);
            String competences = getFieldValue(grid, "competences");
            
            return new Utilisateur(nom, prenom, email, tel, cin, password, role, department, poste, salaire, competences);
        }
    }

    private void updateUserFromForm(Utilisateur user, GridPane grid) {
        user.setNom(getFieldValue(grid, "nom"));
        user.setPrenom(getFieldValue(grid, "prenom"));
        user.setEmail(getFieldValue(grid, "email"));
        user.setTel(getFieldValue(grid, "tel"));
        user.setCin(Integer.parseInt(getFieldValue(grid, "cin")));
        user.setPassword(getFieldValue(grid, "password"));
        user.setRole(getComboValue(grid, "role"));
        
        // Si le rôle est ADMIN ou CEO, mettre NULL pour les champs employé
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.CEO) {
            user.setDepartment(null);
            user.setPoste(null);
            user.setSalaire(0.0);
            user.setCompetences(null);
        } else {
            // Pour EMPLOYE et RESPONSABLES, récupérer les champs employé
            user.setDepartment(getFieldValue(grid, "department"));
            user.setPoste(getFieldValue(grid, "poste"));
            String salaireStr = getFieldValue(grid, "salaire");
            user.setSalaire(salaireStr.isEmpty() ? 0.0 : Double.parseDouble(salaireStr));
            user.setCompetences(getFieldValue(grid, "competences"));
        }
    }

    private String getFieldValue(GridPane grid, String userData) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node.getUserData() != null && node.getUserData().equals(userData)) {
                if (node instanceof TextField) {
                    return ((TextField) node).getText();
                } else if (node instanceof PasswordField) {
                    return ((PasswordField) node).getText();
                }
            }
        }
        return "";
    }

    private Role getComboValue(GridPane grid, String userData) {
        System.out.println("=== DEBUG getComboValue ===");
        System.out.println("Recherche userData: " + userData);
        
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node.getUserData() != null) {
                System.out.println("Node trouvé avec userData: " + node.getUserData() + " (Type: " + node.getClass().getSimpleName() + ")");
                
                if (node.getUserData().equals(userData)) {
                    System.out.println("Match trouvé!");
                    if (node instanceof ComboBox) {
                        Role selectedRole = (Role) ((ComboBox<?>) node).getValue();
                        System.out.println("Rôle sélectionné dans ComboBox: " + selectedRole);
                        return selectedRole;
                    }
                }
            }
        }
        
        System.out.println("ATTENTION: Aucun ComboBox trouvé avec userData '" + userData + "', retour EMPLOYE par défaut");
        return Role.EMPLOYE;
    }

    @FXML
    private void handleEvenements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnEvenements.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Erreur de chargement du Dashboard Événements : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleServices(ActionEvent event) {
        navigateToModule("/service-tab.fxml", "/style/global.css");
    }

    @FXML
    private void handleCategories(ActionEvent event) {
        navigateToModule("/categorie-tab.fxml", "/style/global.css");
    }

    @FXML
    private void handleProduits(ActionEvent event) {
        navigateToModule("/produit.fxml", "/style/global.css");
    }

    @FXML
    private void handleRessources(ActionEvent event) {
        navigateToModule("/ressource.fxml", "/style/global.css");
    }

    @FXML
    private void handleProjets(ActionEvent event) {
        navigateToModule("/ListeProjets.fxml", "/style/global.css");
    }

    @FXML
    private void handlePlanning(ActionEvent event) {
        navigateToModule("/dashboard-view.fxml", "/style/global.css");
    }

    private void navigateToModule(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Passer l'utilisateur actuel au contrôleur si c'est MainController
            Object controller = loader.getController();
            if (controller instanceof MainController && currentUser != null) {
                ((MainController) controller).initData(currentUser);
            }
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            if (cssPath != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            }
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Erreur de chargement de la vue " + fxmlPath + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
