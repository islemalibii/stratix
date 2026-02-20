package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Employe;
import models.Planning;
import services.EmployeeService;
import services.SERVICEPlanning;

import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlanningController {

    @FXML private ComboBox<Employe> cmbEmploye;  // Changé de TextField à ComboBox
    @FXML private DatePicker dpDate;
    @FXML private TextField txtHeureDebut, txtHeureFin;
    @FXML private ComboBox<String> cbTypeShift;
    @FXML private ListView<String> listViewPlannings;

    // Statistiques
    @FXML private Label lblTotalEmployes;
    @FXML private Label lblEnPoste;
    @FXML private Label lblAbsents;

    private SERVICEPlanning service = new SERVICEPlanning();
    private EmployeeService employeService = new EmployeeService();
    private int selectedPlanningId = -1;

    @FXML
    public void initialize() {
        // Charger les employés dans la ComboBox
        List<Employe> employes = employeService.getAllEmployes();
        cmbEmploye.setItems(FXCollections.observableArrayList(employes));

        // Afficher le nom au lieu de l'objet
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getUsername() + " - " + emp.getPoste());
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
                    setText(emp.getUsername() + " - " + emp.getPoste());
                }
            }
        });

        cbTypeShift.getItems().addAll("JOUR", "SOIR", "NUIT");

        // Charger les données
        chargerPlannings();
        chargerStatistiques();

        // Sélection dans la liste
        listViewPlannings.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        String[] parts = newVal.split(" \\| ");
                        selectedPlanningId = Integer.parseInt(parts[0]);
                        remplirFormulaire(selectedPlanningId);
                    }
                }
        );
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
            showAlert("Succès", "Planning ajouté avec succès !");
            chargerPlannings();
            chargerStatistiques();
            viderFormulaire();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Vérifiez les champs !");
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
            showAlert("Succès", "Planning modifié avec succès !");
            chargerPlannings();
            chargerStatistiques();
            viderFormulaire();

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
            showAlert("Succès", "Planning supprimé avec succès !");
            chargerPlannings();
            chargerStatistiques();
            viderFormulaire();
        }
    }

    private void remplirFormulaire(int id) {
        // Ici tu dois implémenter une méthode getPlanningById dans SERVICEPlanning
        // Pour l'instant, on va juste sélectionner l'employé depuis la liste
    }

    private boolean validerChamps() {
        if (cmbEmploye.getValue() == null) {
            showAlert("Erreur", "Sélectionnez un employé !");
            return false;
        }
        if (dpDate.getValue() == null) {
            showAlert("Erreur", "La date est requise !");
            return false;
        }
        if (txtHeureDebut.getText().isEmpty()) {
            showAlert("Erreur", "L'heure de début est requise !");
            return false;
        }
        if (txtHeureFin.getText().isEmpty()) {
            showAlert("Erreur", "L'heure de fin est requise !");
            return false;
        }
        if (cbTypeShift.getValue() == null) {
            showAlert("Erreur", "Le type de shift est requis !");
            return false;
        }
        return true;
    }

    private void chargerPlannings() {
        listViewPlannings.getItems().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Planning p : service.getAllPlannings()) {
            Employe emp = employeService.getEmployeById(p.getEmployeId());
            String empName = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();

            listViewPlannings.getItems().add(
                    p.getId() + " | " + empName +
                            " | " + p.getDate().toLocalDate().format(formatter) +
                            " | " + p.getHeureDebut() + "-" + p.getHeureFin() +
                            " | " + p.getTypeShift()
            );
        }
    }

    private void chargerStatistiques() {
        lblTotalEmployes.setText(String.valueOf(employeService.getAllEmployes().size()));
        lblEnPoste.setText(String.valueOf(service.compterEnPoste()));
        lblAbsents.setText(String.valueOf(service.compterAbsents()));
    }

    private void viderFormulaire() {
        cmbEmploye.setValue(null);
        dpDate.setValue(null);
        txtHeureDebut.clear();
        txtHeureFin.clear();
        cbTypeShift.setValue(null);
        selectedPlanningId = -1;
        listViewPlannings.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}