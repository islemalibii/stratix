package controllers;

import models.Planning;
import services.SERVICEPlanning;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmpPlanningController {

    @FXML private TableView<Planning> tablePlannings;
    @FXML private TableColumn<Planning, String> colDate;
    @FXML private TableColumn<Planning, String> colHeureDebut;
    @FXML private TableColumn<Planning, String> colHeureFin;
    @FXML private TableColumn<Planning, String> colTypeShift;
    @FXML private TableColumn<Planning, Integer> colEmployeId;

    private SERVICEPlanning planningService = new SERVICEPlanning();

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation EmpPlanningController ===");

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
        colEmployeId.setCellValueFactory(new PropertyValueFactory<>("employeId"));

        chargerTousPlannings();
    }

    private void chargerTousPlannings() {
        List<Planning> tousPlannings = planningService.getAllPlannings();
        tablePlannings.setItems(FXCollections.observableArrayList(tousPlannings));
        System.out.println("✅ " + tousPlannings.size() + " plannings chargés");
    }

    @FXML
    private void retourAccueil() {
        MainController.showEmpMain();
    }
}