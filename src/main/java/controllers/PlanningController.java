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
import services.EmployeeService;
import services.SERVICEPlanning;

import java.sql.Date;
import java.sql.Time;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PlanningController {

    @FXML private ComboBox<Employe> cmbEmploye;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtHeureDebut;
    @FXML private TextField txtHeureFin;
    @FXML private ComboBox<String> cbTypeShift;
    @FXML private Label lblTotalEmployes;
    @FXML private Label lblEnPoste;
    @FXML private Label lblAbsents;

    private SERVICEPlanning service = new SERVICEPlanning();
    private EmployeeService employeService = new EmployeeService();
    private int selectedPlanningId = -1;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation PlanningController ===");

        // Charger les employés
        List<Employe> employes = employeService.getAllEmployes();
        cmbEmploye.setItems(FXCollections.observableArrayList(employes));

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

        // Types de shift
        cbTypeShift.getItems().addAll("JOUR", "SOIR", "NUIT", "CONGE", "MALADIE", "FORMATION", "AUTRE");

        // Listener pour les congés
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

        chargerStatistiques();
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
            if (type.equals("CONGE") || type.equals("MALADIE")) {
                p.setHeureDebut(Time.valueOf("00:00:00"));
                p.setHeureFin(Time.valueOf("23:59:00"));
            } else {
                p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
                p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            }

            p.setTypeShift(type);

            service.addPlanning(p);
            showAlert("Succès", "✅ Planning ajouté !");
            viderFormulaire();
            chargerStatistiques();

        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "Format d'heure incorrect ! Utilisez HH:MM");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void modifierPlanning() {
        if (selectedPlanningId == -1) {
            showAlert("Erreur", "❌ Aucun planning sélectionné");
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
            showAlert("Succès", "✅ Planning modifié !");
            viderFormulaire();
            chargerStatistiques();
            selectedPlanningId = -1;

        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage());
        }
    }

    @FXML
    void supprimerPlanning() {
        if (selectedPlanningId == -1) {
            showAlert("Erreur", "❌ Aucun planning sélectionné");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer ce planning ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            service.deletePlanning(selectedPlanningId);
            showAlert("Succès", "✅ Planning supprimé !");
            viderFormulaire();
            chargerStatistiques();
            selectedPlanningId = -1;
        }
    }

    public void setPlanningToEdit(Planning planning) {
        if (planning != null) {
            this.selectedPlanningId = planning.getId();

            Employe emp = employeService.getEmployeById(planning.getEmployeId());
            if (emp != null) {
                cmbEmploye.setValue(emp);
            }

            dpDate.setValue(planning.getDate().toLocalDate());

            String type = planning.getTypeShift();
            cbTypeShift.setValue(type);

            if (type.equals("CONGE") || type.equals("MALADIE")) {
                txtHeureDebut.setText("00:00");
                txtHeureFin.setText("23:59");
                txtHeureDebut.setDisable(true);
                txtHeureFin.setDisable(true);
            } else {
                String debut = planning.getHeureDebut().toString();
                String fin = planning.getHeureFin().toString();
                txtHeureDebut.setText(debut.substring(0, 5));
                txtHeureFin.setText(fin.substring(0, 5));
                txtHeureDebut.setDisable(false);
                txtHeureFin.setDisable(false);
            }

            System.out.println("🔄 Formulaire pré-rempli pour ID: " + selectedPlanningId);
        }
    }

    @FXML
    private void demandeConge() {
        cbTypeShift.setValue("CONGE");
        txtHeureDebut.setText("00:00");
        txtHeureFin.setText("23:59");
        txtHeureDebut.setDisable(true);
        txtHeureFin.setDisable(true);
        selectedPlanningId = -1;
    }

    @FXML
    private void demandeMaladie() {
        cbTypeShift.setValue("MALADIE");
        txtHeureDebut.setText("00:00");
        txtHeureFin.setText("23:59");
        txtHeureDebut.setDisable(true);
        txtHeureFin.setDisable(true);
        selectedPlanningId = -1;
    }

    @FXML
    private void demandeFormation() {
        cbTypeShift.setValue("FORMATION");
        txtHeureDebut.setText("09:00");
        txtHeureFin.setText("17:00");
        txtHeureDebut.setDisable(false);
        txtHeureFin.setDisable(false);
        selectedPlanningId = -1;
    }

    @FXML
    private void openPlanningList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanningListeView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cmbEmploye.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste");
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
        if (!type.equals("CONGE") && !type.equals("MALADIE")) {
            if (txtHeureDebut.getText().trim().isEmpty() || txtHeureFin.getText().trim().isEmpty()) {
                showAlert("Erreur", "❌ Les heures sont requises !");
                return false;
            }
            if (!txtHeureDebut.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") ||
                    !txtHeureFin.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showAlert("Erreur", "❌ Format HH:MM requis");
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
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void viderFormulaire() {
        cmbEmploye.setValue(null);
        dpDate.setValue(null);
        txtHeureDebut.clear();
        txtHeureFin.clear();
        txtHeureDebut.setDisable(false);
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
}