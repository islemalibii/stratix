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
import java.util.List;

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

    private SERVICEPlanning service = new SERVICEPlanning();
    private EmployeeService employeService = new EmployeeService();
    private int selectedPlanningId = -1;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation PlanningController ===");

        // Charger les employés dans la ComboBox
        List<Employe> employes = employeService.getAllEmployes();
        cmbEmploye.setItems(FXCollections.observableArrayList(employes));

        // Afficher le nom dans la ComboBox
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    String poste = (emp.getPoste() != null && !emp.getPoste().isEmpty()) ? " - " + emp.getPoste() : "";
                    setText(emp.getUsername() + poste);
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
                    String poste = (emp.getPoste() != null && !emp.getPoste().isEmpty()) ? " - " + emp.getPoste() : "";
                    setText(emp.getUsername() + poste);
                }
            }
        });

        cbTypeShift.getItems().addAll("JOUR", "SOIR", "NUIT");

        // Charger les statistiques
        chargerStatistiques();

        System.out.println("✅ PlanningController initialisé");
        System.out.println("   Total employés: " + employes.size());
    }

    @FXML
    void ajouterPlanning() {
        try {
            if (!validerChamps()) return;

            Planning p = new Planning();
            Employe selectedEmploye = cmbEmploye.getValue();
            p.setEmployeId(selectedEmploye.getId());
            p.setDate(Date.valueOf(dpDate.getValue()));
            p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
            p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            p.setTypeShift(cbTypeShift.getValue());

            service.addPlanning(p);
            showAlert("Succès", "✅ Planning ajouté avec succès !");
            viderFormulaire();
            chargerStatistiques();

        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "Format d'heure incorrect ! Utilisez HH:MM (ex: 08:30)");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
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
            p.setHeureDebut(Time.valueOf(txtHeureDebut.getText() + ":00"));
            p.setHeureFin(Time.valueOf(txtHeureFin.getText() + ":00"));
            p.setTypeShift(cbTypeShift.getValue());

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
    private void openPlanningList() {
        try {
            System.out.println("Ouverture de la liste des plannings...");
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
        if (txtHeureDebut.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ L'heure de début est requise !");
            return false;
        }
        if (txtHeureFin.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ L'heure de fin est requise !");
            return false;
        }
        if (cbTypeShift.getValue() == null) {
            showAlert("Erreur", "❌ Le type de shift est requis !");
            return false;
        }

        // Validation du format d'heure (HH:MM)
        if (!txtHeureDebut.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert("Erreur", "❌ Format d'heure début invalide. Utilisez HH:MM (ex: 08:30)");
            return false;
        }
        if (!txtHeureFin.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert("Erreur", "❌ Format d'heure fin invalide. Utilisez HH:MM (ex: 17:30)");
            return false;
        }

        return true;
    }

    private void chargerStatistiques() {
        try {
            lblTotalEmployes.setText(String.valueOf(employeService.getAllEmployes().size()));
            lblEnPoste.setText(String.valueOf(service.compterEnPoste()));
            lblAbsents.setText(String.valueOf(service.compterAbsents()));
        } catch (Exception e) {
            System.err.println("Erreur chargement statistiques: " + e.getMessage());
        }
    }

    private void viderFormulaire() {
        cmbEmploye.setValue(null);
        dpDate.setValue(null);
        txtHeureDebut.clear();
        txtHeureFin.clear();
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