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
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

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

    // @FXML private TableColumn<Planning, Integer> colId;  ← SUPPRIMÉ

    @FXML private TableView<Planning> tablePlannings;
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
        System.out.println("=== Initialisation PlanningListeController ===");

        planningService = new SERVICEPlanning();
        employeService = new EmployeeService();
        planningList = FXCollections.observableArrayList();

        configurerColonnes();
        ajouterBoutonsAction();
        configurerCouleursLignes();
        chargerPlannings();

        tablePlannings.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        afficherMeteo(newVal);
                    }
                }
        );
    }

    private void configurerColonnes() {
        // SUPPRIMÉ : colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Employé
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
            String heure = p.getHeureDebut().toString();
            return new javafx.beans.property.SimpleStringProperty(
                    heure.length() >= 5 ? heure.substring(0, 5) : heure
            );
        });

        colHeureFin.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            String heure = p.getHeureFin().toString();
            return new javafx.beans.property.SimpleStringProperty(
                    heure.length() >= 5 ? heure.substring(0, 5) : heure
            );
        });

        colTypeShift.setCellValueFactory(new PropertyValueFactory<>("typeShift"));
    }

    private void ajouterBoutonsAction() {
        colActions.setCellFactory(param -> new TableCell<Planning, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");

            {
                btnEdit.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");

                btnEdit.setOnAction(event -> {
                    Planning planning = getTableView().getItems().get(getIndex());
                    modifierPlanning(planning);
                });

                btnDelete.setOnAction(event -> {
                    Planning planning = getTableView().getItems().get(getIndex());
                    supprimerPlanning(planning);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, btnEdit, btnDelete);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void modifierPlanning(Planning planning) {
        try {
            System.out.println("✏️ Modification planning ID: " + planning.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanningView.fxml"));
            Parent root = loader.load();

            PlanningController controller = loader.getController();
            controller.setPlanningToEdit(planning);

            Stage stage = (Stage) tablePlannings.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supprimerPlanning(Planning planning) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le planning");
        confirm.setContentText("Voulez-vous vraiment supprimer ce planning ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            planningService.deletePlanning(planning.getId());
            chargerPlannings();
            showAlert("Succès", "✅ Planning supprimé");
        }
    }

    private void configurerCouleursLignes() {
        tablePlannings.setRowFactory(tv -> new TableRow<Planning>() {
            @Override
            protected void updateItem(Planning planning, boolean empty) {
                super.updateItem(planning, empty);
                if (planning == null || empty) {
                    setStyle("");
                } else {
                    String type = planning.getTypeShift();
                    switch(type) {
                        case "CONGE":
                            setStyle("-fx-background-color: #fef3c7;");
                            break;
                        case "MALADIE":
                            setStyle("-fx-background-color: #fee2e2;");
                            break;
                        case "FORMATION":
                            setStyle("-fx-background-color: #dbeafe;");
                            break;
                        case "AUTRE":
                            setStyle("-fx-background-color: #f3f4f6;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void chargerPlannings() {
        planningList.clear();
        List<Planning> plannings = planningService.getAllPlannings();
        planningList.addAll(plannings);
        tablePlannings.setItems(planningList);

        int total = plannings.size();
        int jour = 0, soir = 0, nuit = 0;
        int conge = 0, maladie = 0, formation = 0, autre = 0;

        for (Planning p : plannings) {
            switch(p.getTypeShift()) {
                case "JOUR": jour++; break;
                case "SOIR": soir++; break;
                case "NUIT": nuit++; break;
                case "CONGE": conge++; break;
                case "MALADIE": maladie++; break;
                case "FORMATION": formation++; break;
                case "AUTRE": autre++; break;
            }
        }

        lblTotalPlannings.setText(String.valueOf(total));
        lblTotalJour.setText(String.valueOf(jour));
        lblTotalSoir.setText(String.valueOf(soir));
        lblTotalNuit.setText(String.valueOf(nuit));
        lblConges.setText(String.valueOf(conge));
        lblMaladie.setText(String.valueOf(maladie));
        lblFormation.setText(String.valueOf(formation));
        lblAutre.setText(String.valueOf(autre));

        System.out.println("📊 " + total + " plannings chargés");
    }

    private void afficherMeteo(Planning planning) {
        try {
            String date = planning.getDate().toString();
            Employe emp = employeService.getEmployeById(planning.getEmployeId());
            String employeName = (emp != null) ? emp.getUsername() : "Employé " + planning.getEmployeId();

            String meteo = WeatherAPI.getWeatherForDate(date);
            String dateFormatted = planning.getDate().toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            lblMeteo.setText("🌤️ " + employeName + " le " + dateFormatted + " : " + meteo);

        } catch (Exception e) {
            lblMeteo.setText("🌤️ Sélectionnez un planning pour voir la météo");
        }
    }

    @FXML
    private void retourFormulaire() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PlanningView.fxml"));
            Stage stage = (Stage) tablePlannings.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}