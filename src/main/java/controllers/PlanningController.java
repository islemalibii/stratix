package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Employe;
import models.Planning;
import Services.EmployeeService;
import Services.SERVICEPlanning;

import java.sql.Date;
import java.sql.Time;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PlanningController {

    @FXML private ComboBox<Employe> cmbEmploye;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtHeureDebut;
    @FXML private TextField txtHeureFin;
    @FXML private ComboBox<String> cbTypeShift;

    // Statistiques
    @FXML private Label lblTotalEmployes;
    @FXML private Label lblEnPoste;
    @FXML private Label lblAbsents;
    @FXML private Label lblConges;      // Nouveau
    @FXML private Label lblMaladie;      // Nouveau

    private SERVICEPlanning service = new SERVICEPlanning();
    private EmployeeService employeService = new EmployeeService();
    private int selectedPlanningId = -1;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation PlanningController ===");

        // Charger les employés
        List<Employe> employes = employeService.getAllEmployes();
        cmbEmploye.setItems(FXCollections.observableArrayList(employes));

        // Configuration de la ComboBox
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getDisplayName());
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
                    setText(emp.getDisplayName());
                }
            }
        });

        // Types de shift incluant les congés
        cbTypeShift.getItems().addAll(
                "JOUR", "SOIR", "NUIT",
                "CONGE", "MALADIE", "FORMATION", "AUTRE"
        );

        // Ajouter un listener pour désactiver les heures quand on choisit un congé
        cbTypeShift.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && (newVal.equals("CONGE") || newVal.equals("MALADIE"))) {
                txtHeureDebut.setText("00:00");
                txtHeureFin.setText("23:59");
                txtHeureDebut.setDisable(true);
                txtHeureFin.setDisable(true);
            } else if (newVal != null && newVal.equals("FORMATION")) {
                txtHeureDebut.setText("09:00");
                txtHeureFin.setText("17:00");
                txtHeureDebut.setDisable(false);
                txtHeureFin.setDisable(false);
            } else {
                txtHeureDebut.setDisable(false);
                txtHeureFin.setDisable(false);
            }
        });

        // Charger les statistiques
        chargerStatistiques();

        System.out.println("✅ PlanningController initialisé");
    }

    @FXML
    void ajouterPlanning() {
        try {
            if (!validerChamps()) return;

            Planning p = new Planning();
            Employe selectedEmploye = cmbEmploye.getValue();
            p.setEmployeId(selectedEmploye.getId());
            p.setDate(Date.valueOf(dpDate.getValue()));

            String type = cbTypeShift.getValue();

            // Gestion spéciale pour les congés
            if (type.equals("CONGE") || type.equals("MALADIE")) {
                p.setHeureDebut(Time.valueOf("00:00:00"));
                p.setHeureFin(Time.valueOf("23:59:00"));
            } else {
                p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
                p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            }

            p.setTypeShift(type);

            service.addPlanning(p);
            showAlert("Succès", "✅ " + getTypeLabel(type) + " ajouté avec succès !");
            viderFormulaire();
            chargerStatistiques();

        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "Format d'heure incorrect ! Utilisez HH:MM (ex: 08:30)");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTypeLabel(String type) {
        switch(type) {
            case "CONGE": return "Congé";
            case "MALADIE": return "Arrêt maladie";
            case "FORMATION": return "Formation";
            case "AUTRE": return "Absence";
            default: return "Planning";
        }
    }

    @FXML
    void modifierPlanning() {
        if (selectedPlanningId == -1) {
            showAlert("Erreur", "Sélectionnez d'abord un planning dans la liste !");
            return;
        }

        try {
            if (!validerChamps()) return;

            Planning p = new Planning();
            p.setId(selectedPlanningId);
            Employe selectedEmploye = cmbEmploye.getValue();
            p.setEmployeId(selectedEmploye.getId());
            p.setDate(Date.valueOf(dpDate.getValue()));

            String type = cbTypeShift.getValue();

            if (type.equals("CONGE") || type.equals("MALADIE")) {
                p.setHeureDebut(Time.valueOf("00:00:00"));
                p.setHeureFin(Time.valueOf("23:59:00"));
            } else {
                p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
                p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            }

            p.setTypeShift(type);

            service.updatePlanning(p);
            showAlert("Succès", "✅ Planning modifié avec succès !");
            viderFormulaire();
            chargerStatistiques();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    void supprimerPlanning() {
        if (selectedPlanningId == -1) {
            showAlert("Erreur", "Sélectionnez d'abord un planning dans la liste !");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer ce planning ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            service.deletePlanning(selectedPlanningId);
            showAlert("Succès", "✅ Planning supprimé avec succès !");
            viderFormulaire();
            chargerStatistiques();
        }
    }

    @FXML
    private void demandeConge() {
        cbTypeShift.setValue("CONGE");
        txtHeureDebut.setText("00:00");
        txtHeureFin.setText("23:59");
        txtHeureDebut.setDisable(true);
        txtHeureFin.setDisable(true);
    }

    @FXML
    private void demandeMaladie() {
        cbTypeShift.setValue("MALADIE");
        txtHeureDebut.setText("00:00");
        txtHeureFin.setText("23:59");
        txtHeureDebut.setDisable(true);
        txtHeureFin.setDisable(true);
    }

    @FXML
    private void demandeFormation() {
        cbTypeShift.setValue("FORMATION");
        txtHeureDebut.setText("09:00");
        txtHeureFin.setText("17:00");
        txtHeureDebut.setDisable(false);
        txtHeureFin.setDisable(false);
    }

    @FXML
    private void openPlanningList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanningListeView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("📋 Liste des Plannings");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste des plannings");
        }
    }

    private boolean validerChamps() {
        if (cmbEmploye.getValue() == null) {
            showAlert("Erreur", "❌ Sélectionnez un employé !");
            return false;
        }
        if (dpDate.getValue() == null) {
            showAlert("Erreur", "❌ La date est requise !");
            return false;
        }
        if (cbTypeShift.getValue() == null) {
            showAlert("Erreur", "❌ Le type est requis !");
            return false;
        }

        String type = cbTypeShift.getValue();
        if (!type.equals("CONGE") && !type.equals("MALADIE") && !type.equals("FORMATION") && !type.equals("AUTRE")) {
            if (txtHeureDebut.getText().trim().isEmpty()) {
                showAlert("Erreur", "❌ L'heure de début est requise !");
                return false;
            }
            if (txtHeureFin.getText().trim().isEmpty()) {
                showAlert("Erreur", "❌ L'heure de fin est requise !");
                return false;
            }
            if (!txtHeureDebut.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showAlert("Erreur", "❌ Format d'heure début invalide. Utilisez HH:MM");
                return false;
            }
            if (!txtHeureFin.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showAlert("Erreur", "❌ Format d'heure fin invalide. Utilisez HH:MM");
                return false;
            }
        }

        return true;
    }

    private void chargerStatistiques() {
        try {
            lblTotalEmployes.setText(String.valueOf(employeService.getAllEmployes().size()));
            lblEnPoste.setText(String.valueOf(service.compterEnPoste()));
            lblAbsents.setText(String.valueOf(service.compterAbsents()));

            // Compter les congés et maladies du mois
            int conges = 0, maladies = 0, formations = 0;
            List<Planning> plannings = service.getAllPlannings();
            LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
            LocalDate finMois = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

            for (Planning p : plannings) {
                LocalDate date = p.getDate().toLocalDate();
                if (date.isAfter(debutMois.minusDays(1)) && date.isBefore(finMois.plusDays(1))) {
                    if ("CONGE".equals(p.getTypeShift())) conges++;
                    if ("MALADIE".equals(p.getTypeShift())) maladies++;
                    if ("FORMATION".equals(p.getTypeShift())) formations++;
                }
            }

            if (lblConges != null) lblConges.setText(String.valueOf(conges));
            if (lblMaladie != null) lblMaladie.setText(String.valueOf(maladies));

        } catch (Exception e) {
            System.err.println("Erreur chargement statistiques: " + e.getMessage());
        }
    }

    private void viderFormulaire() {
        cmbEmploye.setValue(null);
        dpDate.setValue(null);
        txtHeureDebut.clear();
        txtHeureDebut.setDisable(false);
        txtHeureFin.clear();
        txtHeureFin.setDisable(false);
        cbTypeShift.setValue(null);
        selectedPlanningId = -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    // Add this method to allow the List view to send data to this form
    public void setPlanningToEdit(Planning planning) {
        if (planning == null) return;

        this.selectedPlanningId = planning.getId();

        // 1. Find and set the employee in the ComboBox
        for (Employe emp : cmbEmploye.getItems()) {
            if (emp.getId() == planning.getEmployeId()) {
                cmbEmploye.setValue(emp);
                break;
            }
        }

        // 2. Set the Date
        dpDate.setValue(planning.getDate().toLocalDate());

        // 3. Set the Type
        cbTypeShift.setValue(planning.getTypeShift());

        // 4. Set the Hours (formatted as HH:mm)
        String start = planning.getHeureDebut().toString();
        String end = planning.getHeureFin().toString();

        txtHeureDebut.setText(start.substring(0, 5));
        txtHeureFin.setText(end.substring(0, 5));

        // 5. Adjust UI state based on type
        if ("CONGE".equals(planning.getTypeShift()) || "MALADIE".equals(planning.getTypeShift())) {
            txtHeureDebut.setDisable(true);
            txtHeureFin.setDisable(true);
        } else {
            txtHeureDebut.setDisable(false);
            txtHeureFin.setDisable(false);
        }
    }
}