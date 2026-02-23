package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import models.Employe;
import models.Tache;
import services.EmployeeService;
import services.SERVICETache;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WhiteboardController {

    @FXML private VBox columnAFaire;
    @FXML private VBox columnEnCours;
    @FXML private VBox columnTerminee;

    @FXML private Label lblCountAFaire;
    @FXML private Label lblCountEnCours;
    @FXML private Label lblCountTerminee;

    private SERVICETache tacheService;
    private EmployeeService employeService;

    private Tache selectedTache;
    private ContextMenu contextMenu;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation WhiteboardController ===");

        tacheService = new SERVICETache();
        employeService = new EmployeeService();

        // Vérifier que les composants ne sont pas null
        System.out.println("   columnAFaire: " + (columnAFaire != null ? "✅" : "❌"));
        System.out.println("   columnEnCours: " + (columnEnCours != null ? "✅" : "❌"));
        System.out.println("   columnTerminee: " + (columnTerminee != null ? "✅" : "❌"));

        // Vider les colonnes
        columnAFaire.getChildren().clear();
        columnEnCours.getChildren().clear();
        columnTerminee.getChildren().clear();

        // Charger les tâches
        chargerTaches();

        // Créer le menu contextuel
        contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("✏️ Modifier");
        MenuItem deleteItem = new MenuItem("🗑️ Supprimer");
        MenuItem moveAFaire = new MenuItem("📌 Déplacer vers À FAIRE");
        MenuItem moveEnCours = new MenuItem("⚡ Déplacer vers EN COURS");
        MenuItem moveTerminee = new MenuItem("✅ Déplacer vers TERMINÉE");

        editItem.setOnAction(e -> editSelectedTache());
        deleteItem.setOnAction(e -> deleteSelectedTache());
        moveAFaire.setOnAction(e -> moveTacheTo("A_FAIRE"));
        moveEnCours.setOnAction(e -> moveTacheTo("EN_COURS"));
        moveTerminee.setOnAction(e -> moveTacheTo("TERMINEE"));

        contextMenu.getItems().addAll(editItem, deleteItem,
                new SeparatorMenuItem(),
                moveAFaire, moveEnCours, moveTerminee);
    }

    private void chargerTaches() {
        columnAFaire.getChildren().clear();
        columnEnCours.getChildren().clear();
        columnTerminee.getChildren().clear();

        int countAFaire = 0;
        int countEnCours = 0;
        int countTerminee = 0;

        List<Tache> toutesTaches = tacheService.getAllTaches();
        System.out.println("   Nombre de tâches chargées: " + toutesTaches.size());

        for (Tache t : toutesTaches) {
            VBox card = createTaskCard(t);

            switch(t.getStatut()) {
                case "A_FAIRE":
                    columnAFaire.getChildren().add(card);
                    countAFaire++;
                    break;
                case "EN_COURS":
                    columnEnCours.getChildren().add(card);
                    countEnCours++;
                    break;
                case "TERMINEE":
                    columnTerminee.getChildren().add(card);
                    countTerminee++;
                    break;
                default:
                    System.out.println("   Statut inconnu: " + t.getStatut());
            }
        }

        lblCountAFaire.setText(String.valueOf(countAFaire));
        lblCountEnCours.setText(String.valueOf(countEnCours));
        lblCountTerminee.setText(String.valueOf(countTerminee));

        System.out.println("   À faire: " + countAFaire + ", En cours: " + countEnCours + ", Terminées: " + countTerminee);
    }

    private VBox createTaskCard(Tache t) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 8;");

        // Effet hover
        card.setOnMouseEntered(e ->
                card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 0); " +
                        "-fx-border-color: #3498db; -fx-border-radius: 8;")
        );
        card.setOnMouseExited(e ->
                card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); " +
                        "-fx-border-color: #e0e0e0; -fx-border-radius: 8;")
        );

        // Priorité (bandeau coloré en haut)
        String prioriteColor = "";
        String prioriteEmoji = "";
        switch(t.getPriorite()) {
            case "HAUTE":
                prioriteColor = "#ef4444";
                prioriteEmoji = "🔴";
                break;
            case "MOYENNE":
                prioriteColor = "#f59e0b";
                prioriteEmoji = "🟡";
                break;
            case "BASSE":
                prioriteColor = "#10b981";
                prioriteEmoji = "🟢";
                break;
            default:
                prioriteColor = "#6b7280";
                prioriteEmoji = "⚪";
        }

        HBox prioriteBar = new HBox();
        prioriteBar.setPrefHeight(4);
        prioriteBar.setStyle("-fx-background-color: " + prioriteColor + "; -fx-background-radius: 4;");

        // Titre avec priorité
        HBox titleBox = new HBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label lblPrioriteEmoji = new Label(prioriteEmoji);
        lblPrioriteEmoji.setFont(Font.font("Arial", 16));

        Label lblTitre = new Label(t.getTitre());
        lblTitre.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTitre.setWrapText(true);

        titleBox.getChildren().addAll(lblPrioriteEmoji, lblTitre);

        // Description (tronquée)
        Label lblDescription = new Label(t.getDescription() != null ? t.getDescription() : "");
        lblDescription.setFont(Font.font("Arial", 12));
        lblDescription.setWrapText(true);
        lblDescription.setStyle("-fx-text-fill: #6b7280;");
        if (t.getDescription() != null && t.getDescription().length() > 50) {
            lblDescription.setText(t.getDescription().substring(0, 50) + "...");
        }

        // Informations employé
        Employe emp = employeService.getEmployeById(t.getEmployeId());
        String empInfo;
        if (emp != null) {
            String poste = (emp.getPoste() != null && !emp.getPoste().isEmpty()) ? " - " + emp.getPoste() : "";
            empInfo = emp.getUsername() + poste;
        } else {
            empInfo = "Employé " + t.getEmployeId();
        }

        HBox empBox = new HBox(5);
        empBox.setAlignment(Pos.CENTER_LEFT);
        Label lblEmpIcon = new Label("👤");
        Label lblEmp = new Label(empInfo);
        lblEmp.setFont(Font.font("Arial", 11));
        lblEmp.setStyle("-fx-text-fill: #4b5563;");
        empBox.getChildren().addAll(lblEmpIcon, lblEmp);

        // Deadline
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        HBox dateBox = new HBox(5);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label lblDateIcon = new Label("📅");
        Label lblDate = new Label(t.getDeadline().toLocalDate().format(formatter));
        lblDate.setFont(Font.font("Arial", 11));

        // Colorer si deadline est proche ou dépassée
        if (t.getDeadline().toLocalDate().isBefore(java.time.LocalDate.now())
                && !"TERMINEE".equals(t.getStatut())) {
            lblDate.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        } else if (t.getDeadline().toLocalDate().minusDays(2).isBefore(java.time.LocalDate.now())
                && !"TERMINEE".equals(t.getStatut())) {
            lblDate.setStyle("-fx-text-fill: #f59e0b;");
        }

        dateBox.getChildren().addAll(lblDateIcon, lblDate);

        // Séparateur
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e5e7eb;");

        // Boutons d'action
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-font-size: 16; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> {
            selectedTache = t;
            editSelectedTache();
        });

        Button btnDelete = new Button("🗑️");
        btnDelete.setStyle("-fx-background-color: transparent; -fx-font-size: 16; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            selectedTache = t;
            deleteSelectedTache();
        });

        actionBox.getChildren().addAll(btnEdit, btnDelete);

        // Assembler la carte
        card.getChildren().addAll(prioriteBar, titleBox, lblDescription, empBox, dateBox, separator, actionBox);

        // Rendre la carte cliquable pour le menu contextuel
        card.setOnContextMenuRequested(e -> {
            selectedTache = t;
            contextMenu.show(card, e.getScreenX(), e.getScreenY());
        });

        return card;
    }

    private void editSelectedTache() {
        if (selectedTache != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modifier");
            alert.setHeaderText("Modifier la tâche: " + selectedTache.getTitre());
            alert.setContentText("Redirection vers l'interface de modification...");
            alert.showAndWait();

            // Ici tu peux appeler TacheController avec la tâche sélectionnée
            // Par exemple: MainController.showTachesWithTache(selectedTache);
        }
    }

    private void deleteSelectedTache() {
        if (selectedTache != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la tâche");
            confirm.setContentText("Voulez-vous vraiment supprimer \"" + selectedTache.getTitre() + "\" ?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                tacheService.deleteTache(selectedTache.getId());
                chargerTaches();
                selectedTache = null;
            }
        }
    }

    private void moveTacheTo(String newStatut) {
        if (selectedTache != null) {
            selectedTache.setStatut(newStatut);
            tacheService.updateTache(selectedTache);
            chargerTaches();
            selectedTache = null;
        }
    }

    @FXML
    private void showDashboardFromButton() { loadView("/dashboard-view.fxml"); }

    @FXML
    private void showPlanningFromButton() { loadView("/PlanningListeView.fxml"); }

    @FXML
    private void showTachesFromButton() { loadView("/TacheListeView.fxml"); }

    @FXML
    private void showCalendarFromButton() { loadView("/calendar-view.fxml"); }

    @FXML
    private void showWhiteboardFromButton() { loadView("/WhiteboardView.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
}