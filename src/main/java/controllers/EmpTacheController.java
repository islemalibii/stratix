package controllers;

import models.Tache;
import models.Employe;
import services.SERVICETache;
import services.EmployeeService;
import utiles.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmpTacheController {

    @FXML private TableView<Tache> tableTaches;
    @FXML private TableColumn<Tache, String> colTitre;
    @FXML private TableColumn<Tache, String> colDescription;
    @FXML private TableColumn<Tache, String> colDeadline;
    @FXML private TableColumn<Tache, String> colStatut;
    @FXML private TableColumn<Tache, String> colPriorite;
    @FXML private TableColumn<Tache, Integer> colEmployeId;  // ← Nouvelle colonne

    private SERVICETache tacheService = new SERVICETache();
    private EmployeeService employeService = new EmployeeService();

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation EmpTacheController (TOUTES les tâches) ===");

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDeadline.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            if (t.getDeadline() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        t.getDeadline().toLocalDate().format(formatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colPriorite.setCellValueFactory(new PropertyValueFactory<>("priorite"));

        // Nouvelle colonne pour afficher l'employé
        colEmployeId.setCellValueFactory(new PropertyValueFactory<>("employeId"));

        chargerToutesTaches();
    }

    private void chargerToutesTaches() {
        List<Tache> toutesTaches = tacheService.getAllTaches();
        tableTaches.setItems(FXCollections.observableArrayList(toutesTaches));

        System.out.println("✅ " + toutesTaches.size() + " tâches chargées (tous employés)");
    }

    @FXML
    private void retourAccueil() {
        MainController.showEmpMain();
    }
}