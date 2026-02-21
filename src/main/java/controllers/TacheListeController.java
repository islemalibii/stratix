package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import models.Tache;
import models.Employe;
import services.SERVICETache;
import services.EmployeeService;
import services.PDFExportService;
import services.ExcelExportService;
import controllers.StatsChartController;  // ← NOUVEL IMPORT

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TacheListeController implements Initializable {

    @FXML private TableView<Tache> tableTaches;
    @FXML private TableColumn<Tache, Integer> colId;
    @FXML private TableColumn<Tache, String> colPriorite;
    @FXML private TableColumn<Tache, String> colTitre;
    @FXML private TableColumn<Tache, String> colEmploye;
    @FXML private TableColumn<Tache, String> colDeadline;
    @FXML private TableColumn<Tache, String> colStatut;
    @FXML private TableColumn<Tache, Integer> colProjet;

    @FXML private Label lblTotalTaches;
    @FXML private Label lblAFaire;
    @FXML private Label lblEnCours;
    @FXML private Label lblTerminees;

    private SERVICETache tacheService;
    private EmployeeService employeService;
    private ObservableList<Tache> tacheList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation TacheListeController ===");

        tacheService = new SERVICETache();
        employeService = new EmployeeService();
        tacheList = FXCollections.observableArrayList();

        configurerColonnes();
        chargerTaches();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPriorite.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            String priorite = "";
            switch(t.getPriorite()) {
                case "HAUTE": priorite = "🔴"; break;
                case "MOYENNE": priorite = "🟡"; break;
                case "BASSE": priorite = "🟢"; break;
                default: priorite = "⚪";
            }
            return new javafx.beans.property.SimpleStringProperty(priorite);
        });

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));

        colEmploye.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            Employe emp = employeService.getEmployeById(t.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

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

        colStatut.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            String statut = "";
            switch(t.getStatut()) {
                case "A_FAIRE": statut = "📌 À faire"; break;
                case "EN_COURS": statut = "⚡ En cours"; break;
                case "TERMINEE": statut = "✅ Terminée"; break;
                default: statut = t.getStatut();
            }
            return new javafx.beans.property.SimpleStringProperty(statut);
        });

        colProjet.setCellValueFactory(new PropertyValueFactory<>("projetId"));
    }

    private void chargerTaches() {
        tacheList.clear();
        List<Tache> taches = tacheService.getAllTaches();
        tacheList.addAll(taches);
        tableTaches.setItems(tacheList);

        int total = taches.size();
        int aFaire = 0, enCours = 0, terminees = 0;

        for (Tache t : taches) {
            switch(t.getStatut()) {
                case "A_FAIRE": aFaire++; break;
                case "EN_COURS": enCours++; break;
                case "TERMINEE": terminees++; break;
            }
        }

        lblTotalTaches.setText(String.valueOf(total));
        lblAFaire.setText(String.valueOf(aFaire));
        lblEnCours.setText(String.valueOf(enCours));
        lblTerminees.setText(String.valueOf(terminees));

        System.out.println("✅ " + total + " tâches chargées");
    }

    @FXML
    private void exportToPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les tâches en PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            fileChooser.setInitialFileName("taches_" + LocalDate.now() + ".pdf");

            File file = fileChooser.showSaveDialog(tableTaches.getScene().getWindow());

            if (file != null) {
                List<Tache> toutesTaches = tacheService.getAllTaches();
                PDFExportService.exportTachesToPDF(toutesTaches, file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("✅ PDF exporté avec succès !\n" + file.getName());
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("❌ Erreur : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void exportToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les tâches en Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
            );
            fileChooser.setInitialFileName("taches_" + LocalDate.now() + ".xlsx");

            File file = fileChooser.showSaveDialog(tableTaches.getScene().getWindow());

            if (file != null) {
                List<Tache> toutesTaches = tacheService.getAllTaches();
                ExcelExportService.exportTachesToExcel(toutesTaches, file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("✅ Excel exporté avec succès !\n" + file.getName());
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("❌ Erreur : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    // ← NOUVELLE MÉTHODE POUR LES STATISTIQUES
    @FXML
    private void showStatistics() {
        try {
            StatsChartController.showTaskStatistics();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("❌ Erreur lors de l'affichage des statistiques: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void retourFormulaire() {
        MainController.showTaches();
    }
}