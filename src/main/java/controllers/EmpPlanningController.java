package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import models.Planning;
import services.SERVICEPlanning;

import java.io.IOException;
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

        // Date Column Formatting
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDate.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new SimpleStringProperty(
                    p.getDate() != null ? p.getDate().toLocalDate().format(dateFormatter) : ""
            );
        });

        // Start Time Formatting (HH:mm)
        colHeureDebut.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new SimpleStringProperty(
                    p.getHeureDebut() != null ? p.getHeureDebut().toString().substring(0, 5) : ""
            );
        });

        // End Time Formatting (HH:mm)
        colHeureFin.setCellValueFactory(cellData -> {
            Planning p = cellData.getValue();
            return new SimpleStringProperty(
                    p.getHeureFin() != null ? p.getHeureFin().toString().substring(0, 5) : ""
            );
        });

        colTypeShift.setCellValueFactory(new PropertyValueFactory<>("typeShift"));
        colEmployeId.setCellValueFactory(new PropertyValueFactory<>("employeId"));

        chargerTousPlannings();
    }

    private void chargerTousPlannings() {
        List<Planning> tousPlannings = planningService.getAllPlannings();
        tablePlannings.setItems(FXCollections.observableArrayList(tousPlannings));
    }

    @FXML
    private void showMesTaches() {
        try {
            if (MainController.staticContentArea != null) {
                Node node = FXMLLoader.load(getClass().getResource("/EmpTacheView.fxml"));
                MainController.staticContentArea.getChildren().setAll(node);
                System.out.println("🔄 Navigation vers Tâches réussie");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showMonPlanning() {
        chargerTousPlannings();
        System.out.println("🔄 Données planning actualisées");
    }


}