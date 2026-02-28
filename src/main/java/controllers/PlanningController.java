package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.Planning;
import models.Employe;
import services.SERVICEPlanning;
import services.EmployeeService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class PlanningController implements Initializable {

    @FXML private ComboBox<Employe> cmbEmploye;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtHeureDebut;
    @FXML private TextField txtHeureFin;
    @FXML private ComboBox<String> cbTypeShift;

    @FXML private Label lblTotalEmployes;
    @FXML private Label lblEnPoste;
    @FXML private Label lblAbsents;
    @FXML private Label lblMessage;

    private SERVICEPlanning planningService;
    private EmployeeService employeService;
    private int selectedPlanningId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation PlanningController (Formulaire moderne) ===");

        planningService = new SERVICEPlanning();
        employeService = new EmployeeService();

        // Charger les employés
        List<Employe> employes = employeService.getAllEmployes();
        cmbEmploye.setItems(FXCollections.observableArrayList(employes));

        // Configuration de l'affichage des employés
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getUsername() + " (" + emp.getEmail() + ")");
                }
            }
        });

        cmbEmploye.setButtonCell(new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getUsername());
                }
            }
        });

        // Types de shift avec icônes
        cbTypeShift.setItems(FXCollections.observableArrayList(
                "☀️ JOUR", "🌆 SOIR", "🌙 NUIT", "🌴 CONGÉ", "🤒 MALADIE", "🎓 FORMATION", "📋 AUTRE"
        ));

        // Charger les statistiques
        chargerStatistiques();

        // Valeurs par défaut
        dpDate.setValue(LocalDate.now());
    }

    private void chargerStatistiques() {
        try {
            List<Employe> employes = employeService.getAllEmployes();
            lblTotalEmployes.setText(String.valueOf(employes.size()));

            long enPoste = employes.stream()
                    .filter(e -> e.getStatut() != null && e.getStatut().equals("actif"))
                    .count();

            lblEnPoste.setText(String.valueOf(enPoste));
            lblAbsents.setText(String.valueOf(employes.size() - enPoste));
        } catch (Exception e) {
            lblTotalEmployes.setText("0");
            lblEnPoste.setText("0");
            lblAbsents.setText("0");
        }
    }

    @FXML
    private void ajouterPlanning() {
        if (!validerChamps()) return;

        try {
            Planning p = new Planning();
            Employe selectedEmploye = cmbEmploye.getValue();
            p.setEmployeId(selectedEmploye.getId());
            p.setDate(Date.valueOf(dpDate.getValue()));

            String type = getTypeWithoutIcon(cbTypeShift.getValue());

            if (type.equals("CONGÉ") || type.equals("MALADIE")) {
                p.setHeureDebut(Time.valueOf("00:00:00"));
                p.setHeureFin(Time.valueOf("23:59:00"));
            } else {
                p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
                p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            }

            p.setTypeShift(type);

            planningService.addPlanning(p);
            showMessage("✅ Planning ajouté avec succès !", "success");
            viderFormulaire();

        } catch (IllegalArgumentException e) {
            showMessage("❌ Format d'heure incorrect ! Utilisez HH:MM", "error");
        } catch (Exception e) {
            showMessage("❌ Erreur : " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierPlanning() {
        if (selectedPlanningId == -1) {
            showMessage("❌ Aucun planning sélectionné", "error");
            return;
        }

        if (!validerChamps()) return;

        try {
            Planning p = new Planning();
            p.setId(selectedPlanningId);
            Employe selectedEmploye = cmbEmploye.getValue();
            p.setEmployeId(selectedEmploye.getId());
            p.setDate(Date.valueOf(dpDate.getValue()));

            String type = getTypeWithoutIcon(cbTypeShift.getValue());

            if (type.equals("CONGÉ") || type.equals("MALADIE")) {
                p.setHeureDebut(Time.valueOf("00:00:00"));
                p.setHeureFin(Time.valueOf("23:59:00"));
            } else {
                p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
                p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            }

            p.setTypeShift(type);

            planningService.updatePlanning(p);
            showMessage("✏️ Planning modifié avec succès !", "success");
            viderFormulaire();
            selectedPlanningId = -1;

        } catch (Exception e) {
            showMessage("❌ Erreur : " + e.getMessage(), "error");
        }
    }

    @FXML
    private void supprimerPlanning() {
        if (selectedPlanningId == -1) {
            showMessage("❌ Aucun planning sélectionné", "error");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer ce planning ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            planningService.deletePlanning(selectedPlanningId);
            showMessage("🗑️ Planning supprimé !", "success");
            viderFormulaire();
            selectedPlanningId = -1;
        }
    }

    public void setPlanningToEdit(Planning planning) {
        if (planning == null) return;

        this.selectedPlanningId = planning.getId();

        // Trouver l'employé
        for (Employe emp : cmbEmploye.getItems()) {
            if (emp.getId() == planning.getEmployeId()) {
                cmbEmploye.setValue(emp);
                break;
            }
        }

        dpDate.setValue(planning.getDate().toLocalDate());

        String type = planning.getTypeShift();
        String typeWithIcon = switch(type) {
            case "JOUR" -> "☀️ JOUR";
            case "SOIR" -> "🌆 SOIR";
            case "NUIT" -> "🌙 NUIT";
            case "CONGE" -> "🌴 CONGÉ";
            case "MALADIE" -> "🤒 MALADIE";
            case "FORMATION" -> "🎓 FORMATION";
            default -> "📋 AUTRE";
        };
        cbTypeShift.setValue(typeWithIcon);

        if (planning.getHeureDebut() != null && planning.getHeureFin() != null) {
            String debut = planning.getHeureDebut().toString();
            String fin = planning.getHeureFin().toString();
            txtHeureDebut.setText(debut.substring(0, 5));
            txtHeureFin.setText(fin.substring(0, 5));
        }
    }

    @FXML
    private void demandeConge() {
        cbTypeShift.setValue("🌴 CONGÉ");
    }

    @FXML
    private void demandeMaladie() {
        cbTypeShift.setValue("🤒 MALADIE");
    }

    @FXML
    private void demandeFormation() {
        cbTypeShift.setValue("🎓 FORMATION");
    }

    @FXML
    private void openPlanningListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PlanningListeView.fxml"));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validerChamps() {
        if (cmbEmploye.getValue() == null) {
            showMessage("❌ Sélectionnez un employé !", "error");
            return false;
        }
        if (dpDate.getValue() == null) {
            showMessage("❌ La date est requise !", "error");
            return false;
        }
        if (cbTypeShift.getValue() == null) {
            showMessage("❌ Le type est requis !", "error");
            return false;
        }

        String type = getTypeWithoutIcon(cbTypeShift.getValue());
        if (!type.equals("CONGÉ") && !type.equals("MALADIE")) {
            if (txtHeureDebut.getText().trim().isEmpty() || txtHeureFin.getText().trim().isEmpty()) {
                showMessage("❌ Les heures sont requises !", "error");
                return false;
            }
            if (!txtHeureDebut.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") ||
                    !txtHeureFin.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showMessage("❌ Format HH:MM requis", "error");
                return false;
            }
        }
        return true;
    }

    private String getTypeWithoutIcon(String typeWithIcon) {
        if (typeWithIcon == null) return null;
        return typeWithIcon.replaceAll("[^A-Za-zéèàùôîûÄËÏÖÜäëïöüÀ-ÿ]", "").trim();
    }

    private void viderFormulaire() {
        cmbEmploye.setValue(null);
        dpDate.setValue(LocalDate.now());
        txtHeureDebut.clear();
        txtHeureFin.clear();
        cbTypeShift.setValue(null);
    }

    private void showMessage(String message, String type) {
        lblMessage.setText(message);
        if (type.equals("success")) {
            lblMessage.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        } else {
            lblMessage.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
        lblMessage.setVisible(true);

        // Faire disparaître le message après 3 secondes
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    lblMessage.setVisible(false);
                });
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML private void showDashboardFromButton() { loadView("/dashboard-view.fxml"); }
    @FXML private void showPlanningFromButton() { loadView("/PlanningListeView.fxml"); }
    @FXML private void showTachesFromButton() { loadView("/TacheListeView.fxml"); }
    @FXML private void showCalendarFromButton() { loadView("/calendar-view.fxml"); }
    @FXML private void showWhiteboardFromButton() { loadView("/WhiteboardView.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            System.err.println("Navigation error: " + fxmlPath);
        }
    }
}