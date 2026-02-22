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

    private Utilisateur currentUser;
    private UtilisateurService utilisateurService;

    private String currentView = "utilisateurs";
    private List<Utilisateur> currentData;

    public void initialize() {
        utilisateurService = UtilisateurService.getInstance();
        loadUtilisateurs();
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
                updateUserFromForm(user, grid);
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
                
                // Vérifier l'unicité de l'email et du CIN
                if (utilisateurService.emailExists(newUser.getEmail())) {
                    showAlert("Erreur", "Cet email est déjà utilisé");
                    return;
                }
                
                if (utilisateurService.cinExists(newUser.getCin())) {
                    showAlert("Erreur", "Ce CIN est déjà enregistré");
                    return;
                }
                
                utilisateurService.ajouter(newUser);
                refreshCurrentView();
                showAlert("Succès", "Utilisateur ajouté avec succès");
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'ajouter l'utilisateur");
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
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node.getUserData() != null && node.getUserData().equals(userData)) {
                if (node instanceof ComboBox) {
                    return (Role) ((ComboBox<?>) node).getValue();
                }
            }
        }
        return Role.EMPLOYE;
    }

    @FXML
    private void handleEvenements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnEvenements.getScene().getWindow();
            stage.getScene().setRoot(root);

            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Erreur de chargement du Dashboard Événements : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
