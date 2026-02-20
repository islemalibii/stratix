package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.Planning;
import models.Employe;
import services.SERVICEPlanning;
import services.EmployeeService;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class PlanningListeController implements Initializable {

    @FXML private TableView<Planning> tablePlannings;
    @FXML private TableColumn<Planning, Integer> colId;
    @FXML private TableColumn<Planning, String> colEmploye;
    @FXML private TableColumn<Planning, String> colDate;
    @FXML private TableColumn<Planning, String> colHeureDebut;
    @FXML private TableColumn<Planning, String> colHeureFin;
    @FXML private TableColumn<Planning, String> colTypeShift;

    @FXML private Label lblTotalPlannings;
    @FXML private Label lblTotalJour;
    @FXML private Label lblTotalSoir;
    @FXML private Label lblTotalNuit;

    private SERVICEPlanning planningService;
    private EmployeeService employeService;
    private ObservableList<Planning> planningList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planningService = new SERVICEPlanning();
        employeService = new EmployeeService();
        planningList = FXCollections.observableArrayList();

        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmploye.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            Employe emp = employeService.getEmployeById(p.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDate.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    p.getDate().toLocalDate().format(dateFormatter)
            );
        });

        colHeureDebut.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    p.getHeureDebut().toString().substring(0, 5)
            );
        });

        colHeureFin.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    p.getHeureFin().toString().substring(0, 5)
            );
        });

        colTypeShift.setCellValueFactory(new PropertyValueFactory<>("typeShift"));

        // Charger les données
        chargerPlannings();
    }

    private void chargerPlannings() {
        planningList.clear();
        List<Planning> plannings = planningService.getAllPlannings();
        planningList.addAll(plannings);
        tablePlannings.setItems(planningList);

        // Mettre à jour les statistiques
        int total = plannings.size();
        int jour = 0, soir = 0, nuit = 0;

        for (Planning p : plannings) {
            switch(p.getTypeShift()) {
                case "JOUR": jour++; break;
                case "SOIR": soir++; break;
                case "NUIT": nuit++; break;
            }
        }

        lblTotalPlannings.setText(String.valueOf(total));
        lblTotalJour.setText(String.valueOf(jour));
        lblTotalSoir.setText(String.valueOf(soir));
        lblTotalNuit.setText(String.valueOf(nuit));
    }

    @FXML
    private void retourFormulaire() {
        MainController.showPlanning();
    }
}