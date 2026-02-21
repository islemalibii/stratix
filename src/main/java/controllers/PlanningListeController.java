package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.Planning;
import models.Employe;
import services.SERVICEPlanning;
import services.EmployeeService;
import api.WeatherAPI;

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
    @FXML private Label lblConges;
    @FXML private Label lblMaladie;
    @FXML private Label lblFormation;
    @FXML private Label lblAutre;
    @FXML private Label lblMeteo;

    private SERVICEPlanning planningService;
    private EmployeeService employeService;
    private ObservableList<Planning> planningList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planningService = new SERVICEPlanning();
        employeService = new EmployeeService();
        planningList = FXCollections.observableArrayList();

        configurerColonnes();
        colorerLignes();
        chargerPlannings();

        tablePlannings.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) afficherMeteo(newVal);
                }
        );
    }

    private void configurerColonnes() {
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
    }

    private void colorerLignes() {
        tablePlannings.setRowFactory(tv -> new TableRow<Planning>() {
            protected void updateItem(Planning p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) {
                    setStyle("");
                } else if ("CONGE".equals(p.getTypeShift())) {
                    setStyle("-fx-background-color: #fef3c7;");
                } else if ("MALADIE".equals(p.getTypeShift())) {
                    setStyle("-fx-background-color: #fee2e2;");
                } else if ("FORMATION".equals(p.getTypeShift())) {
                    setStyle("-fx-background-color: #dbeafe;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void chargerPlannings() {
        planningList.clear();
        List<Planning> plannings = planningService.getAllPlannings();
        planningList.addAll(plannings);
        tablePlannings.setItems(planningList);

        int jour = 0, soir = 0, nuit = 0, conge = 0, maladie = 0, formation = 0, autre = 0;

        for (Planning p : plannings) {
            switch(p.getTypeShift()) {
                case "JOUR": jour++; break;
                case "SOIR": soir++; break;
                case "NUIT": nuit++; break;
                case "CONGE": conge++; break;
                case "MALADIE": maladie++; break;
                case "FORMATION": formation++; break;
                default: autre++;
            }
        }

        lblTotalPlannings.setText(String.valueOf(plannings.size()));
        lblTotalJour.setText(String.valueOf(jour));
        lblTotalSoir.setText(String.valueOf(soir));
        lblTotalNuit.setText(String.valueOf(nuit));
        lblConges.setText(String.valueOf(conge));
        lblMaladie.setText(String.valueOf(maladie));
        lblFormation.setText(String.valueOf(formation));
        lblAutre.setText(String.valueOf(autre));
    }

    private void afficherMeteo(Planning planning) {
        try {
            String date = planning.getDate().toString();
            Employe emp = employeService.getEmployeById(planning.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + planning.getEmployeId();
            String dateF = planning.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String meteo = WeatherAPI.getWeatherForDate(date);
            lblMeteo.setText("🌤️ " + nom + " le " + dateF + " : " + meteo);
        } catch (Exception e) {
            lblMeteo.setText("🌤️ Sélectionnez un planning");
        }
    }

    @FXML
    private void retourFormulaire() {
        MainController.showPlanning();
    }
}