package controllers;

import Services.UtilisateurService;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardAdminController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private VBox cardsContainer;

    @FXML
    private Button logoutButton;

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
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void loadEmployes() {
        try {
            currentData = utilisateurService.getByRole(Role.EMPLOYE);
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void loadAdmins() {
        try {
            currentData = utilisateurService.getByRole(Role.ADMIN);
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void loadCEOs() {
        try {
            currentData = utilisateurService.getByRole(Role.CEO);
            displayCards(currentData);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
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
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
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

        for (Utilisateur user : users) {
            cardsContainer.getChildren().add(createUserCard(user));
        }
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

        // ID Label
        Label idLabel = new Label("ID: " + user.getId());
        idLabel.getStyleClass().add("card-id-label");

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
        rightBox.getChildren().addAll(roleLabel, idLabel);

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
                showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage());
            }
        }
    }

    private void handleSupprimer(Utilisateur user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer l'utilisateur");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + 
                                   user.getNom() + " " + user.getPrenom() + " ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                utilisateurService.supprimer(user.getId());
                refreshCurrentView();
                showAlert("Succès", "Utilisateur supprimé avec succès");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
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
                utilisateurService.ajouter(newUser);
                refreshCurrentView();
                showAlert("Succès", "Utilisateur ajouté avec succès");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
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

        // Role
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Role.values());
        roleCombo.setValue(user != null ? user.getRole() : Role.EMPLOYE);

        // Champs employé (optionnels)
        TextField departmentField = new TextField(user != null ? user.getDepartment() : "");
        TextField posteField = new TextField(user != null ? user.getPoste() : "");
        TextField salaireField = new TextField(user != null ? String.valueOf(user.getSalaire()) : "0");
        TextField competencesField = new TextField(user != null ? user.getCompetences() : "");

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
        grid.add(new Label("Département:"), 0, row);
        grid.add(departmentField, 1, row++);
        grid.add(new Label("Poste:"), 0, row);
        grid.add(posteField, 1, row++);
        grid.add(new Label("Salaire:"), 0, row);
        grid.add(salaireField, 1, row++);
        grid.add(new Label("Compétences:"), 0, row);
        grid.add(competencesField, 1, row++);

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
        String department = getFieldValue(grid, "department");
        String poste = getFieldValue(grid, "poste");
        double salaire = Double.parseDouble(getFieldValue(grid, "salaire"));
        String competences = getFieldValue(grid, "competences");

        return new Utilisateur(nom, prenom, email, tel, cin, password, role, department, poste, salaire, competences);
    }

    private void updateUserFromForm(Utilisateur user, GridPane grid) {
        user.setNom(getFieldValue(grid, "nom"));
        user.setPrenom(getFieldValue(grid, "prenom"));
        user.setEmail(getFieldValue(grid, "email"));
        user.setTel(getFieldValue(grid, "tel"));
        user.setCin(Integer.parseInt(getFieldValue(grid, "cin")));
        user.setPassword(getFieldValue(grid, "password"));
        user.setRole(getComboValue(grid, "role"));
        user.setDepartment(getFieldValue(grid, "department"));
        user.setPoste(getFieldValue(grid, "poste"));
        user.setSalaire(Double.parseDouble(getFieldValue(grid, "salaire")));
        user.setCompetences(getFieldValue(grid, "competences"));
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
}
