package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import models.Tache;
import models.Employe;
import Services.SERVICETache;
import Services.EmployeeService;
import Services.PDFExportService;
import Services.ExcelExportService;
import controllers.StatsChartController;  // ← NOUVEL IMPORT

import java.io.File;
import java.io.IOException;
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
    @FXML private TableColumn<Tache, Void> colActions;

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
        ajouterBoutonsAction();
        configurerColonnes();
        chargerTaches();
    }

    private void configurerColonnes() {
        // Priorité avec emoji
        colPriorite.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            String priorite = switch(t.getPriorite()) {
                case "HAUTE" -> "🔴";
                case "MOYENNE" -> "🟡";
                case "BASSE" -> "🟢";
                default -> "⚪";
            };
            return new javafx.beans.property.SimpleStringProperty(priorite);
        });

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));

        // Employé name mapping
        colEmploye.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            Employe emp = employeService.getEmployeById(t.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        // Deadline format
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

        // Statut avec emoji
        colStatut.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            String statut = switch(t.getStatut()) {
                case "A_FAIRE" -> "📌 À faire";
                case "EN_COURS" -> "⚡ En cours";
                case "TERMINEE" -> "✅ Terminée";
                default -> t.getStatut();
            };
            return new javafx.beans.property.SimpleStringProperty(statut);
        });

        colProjet.setCellValueFactory(new PropertyValueFactory<>("projetId"));
    }

    // --- INTEGRATED ACTION BUTTONS ---
    private void ajouterBoutonsAction() {
        colActions.setCellFactory(param -> new TableCell<Tache, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");

            {
                btnEdit.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");

                btnEdit.setOnAction(event -> modifierTache(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(event -> supprimerTache(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    HBox container = new HBox(5, btnEdit, btnDelete);
                    setGraphic(container);
                }
            }
        });
    }

    private void modifierTache(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TacheView.fxml"));
            Parent view = loader.load();

            // Pass data to form controller
            TacheController controller = loader.getController();
            controller.setTacheToEdit(tache);

            // Switch view inside Main Content Area (Planning Style)
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerTache(Tache tache) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer la tâche : " + tache.getTitre() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                tacheService.deleteTache(tache.getId());
                chargerTaches();
            }
        });
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
    private void showDashboardFromButton() { loadView("/dashboard-view.fxml"); }

    @FXML
    private void showPlanningFromButton() { loadView("/PlanningListeView.fxml"); }

    @FXML
    private void showTachesFromButton() { loadView("/TacheListeView.fxml"); }

    @FXML
    private void showCalendarFromButton() { loadView("/calendar-view.fxml"); }

    @FXML
    private void showWhiteboardFromButton() { loadView("/WhiteboardView.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            System.err.println("Error loading: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    private void retourFormulaire() {
        showTachesFromButton();
    }
}