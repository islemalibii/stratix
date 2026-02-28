package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import models.Planning;
import models.Employe;
import services.SERVICEPlanning;
import services.EmployeeService;
import api.WeatherAPI;

import java.io.IOException;
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
    @FXML private TableColumn<Planning, Void> colActions;

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
        ajouterBoutonsAction();
        colorerLignes();
        chargerPlannings();

        tablePlannings.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) afficherMeteo(newVal);
                }
        );
    }

    private void configurerColonnes() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));

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

    // --- NEW METHOD: ADDS EDIT/DELETE BUTTONS TO THE TABLE ---
    private void ajouterBoutonsAction() {
        if (colActions == null) return;

        colActions.setCellFactory(param -> new TableCell<Planning, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");

            {
                btnEdit.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");

                btnEdit.setOnAction(event -> {
                    Planning p = getTableView().getItems().get(getIndex());
                    modifierPlanning(p);
                });

                btnDelete.setOnAction(event -> {
                    Planning p = getTableView().getItems().get(getIndex());
                    supprimerPlanning(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(5, btnEdit, btnDelete);
                    setGraphic(container);
                }
            }
        });
    }

    // --- NEW METHOD: REDIRECT TO FORM WITH DATAA ---
    private void modifierPlanning(Planning planning) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanningView.fxml"));
            Parent root = loader.load();

            // Assuming your Form controller is named PlanningController
            PlanningController controller = loader.getController();
            controller.setPlanningToEdit(planning);

            Stage stage = (Stage) tablePlannings.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- NEW METHOD: DELETE DATA ---
    private void supprimerPlanning(Planning planning) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce planning ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                planningService.deletePlanning(planning.getId());
                chargerPlannings(); // Refresh list
            }
        });
    }

    private void colorerLignes() {
        tablePlannings.setRowFactory(tv -> new TableRow<Planning>() {
            @Override
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
            if (p.getTypeShift() == null) continue;
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
            e.printStackTrace();
        }
    }

    @FXML
    private void retourFormulaire() {
        // Redirect back to the empty form to add a new planning
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PlanningView.fxml"));
            Stage stage = (Stage) tablePlannings.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}