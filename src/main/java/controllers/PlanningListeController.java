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
    @FXML private Label lblMeteo;

    private SERVICEPlanning planningService;
    private EmployeeService employeService;
    private ObservableList<Planning> planningList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation PlanningListeController ===");

        planningService = new SERVICEPlanning();
        employeService = new EmployeeService();
        planningList = FXCollections.observableArrayList();

        // Configuration des colonnes avec formatage correct
        configurerColonnes();

        // Charger les données
        chargerPlannings();

        // Listener pour la sélection
        tablePlannings.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        System.out.println("✅ Planning sélectionné: ID " + newVal.getId());
                        afficherMeteo(newVal);
                    }
                }
        );

        System.out.println("✅ Initialisation terminée");
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Afficher le nom de l'employé au lieu de l'ID
        colEmploye.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            Employe emp = employeService.getEmployeById(p.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        // Formatage de la date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDate.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    p.getDate().toLocalDate().format(dateFormatter)
            );
        });

        // Formatage de l'heure (HH:MM)
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

        System.out.println("📊 " + total + " plannings chargés");
    }

    private void afficherMeteo(Planning planning) {
        try {
            String date = planning.getDate().toString();
            Employe emp = employeService.getEmployeById(planning.getEmployeId());
            String employeName = (emp != null) ? emp.getUsername() : "Employé " + planning.getEmployeId();

            // Récupérer la météo
            String meteo = WeatherAPI.getWeatherForDate(date);

            String dateFormatted = planning.getDate().toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String message = "🌤️ Météo pour " + employeName + " le " + dateFormatted + " : " + meteo;
            lblMeteo.setText(message);

            // Changer la couleur selon la météo
            if (meteo.contains("☀️")) {
                lblMeteo.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #ffeeba; -fx-border-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else if (meteo.contains("☁️")) {
                lblMeteo.setStyle("-fx-background-color: #e2e3e5; -fx-text-fill: #383d41; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #d6d8db; -fx-border-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else if (meteo.contains("🌧️")) {
                lblMeteo.setStyle("-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #bee5eb; -fx-border-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else {
                lblMeteo.setStyle("-fx-background-color: #e6f7ff; -fx-text-fill: #0050b3; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #91d5ff; -fx-border-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");
            }

            System.out.println("✅ Météo affichée: " + message);

        } catch (Exception e) {
            System.err.println("❌ Erreur météo: " + e.getMessage());
            lblMeteo.setText("🌤️ Erreur de récupération de la météo");
        }
    }

    @FXML
    private void retourFormulaire() {
        MainController.showPlanning();
    }
}