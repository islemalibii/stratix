package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import models.Tache;
import models.Employe;
import services.SERVICETache;
import services.EmployeeService;

import java.net.URL;
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

        // Configuration des colonnes
        configurerColonnes();

        // Charger les données
        chargerTaches();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Priorité avec emoji
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

        // Colonne Employé avec nom d'utilisateur
        colEmploye.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            Employe emp = employeService.getEmployeById(t.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        // Formatage de la deadline
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

        // Colorer les lignes selon la priorité
        tableTaches.setRowFactory(tv -> new TableRow<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (tache == null || empty) {
                    setStyle("");
                } else {
                    String priorite = tache.getPriorite();
                    switch(priorite) {
                        case "HAUTE":
                            setStyle("-fx-background-color: #fee2e2;"); // Rouge clair
                            break;
                        case "MOYENNE":
                            setStyle("-fx-background-color: #fef9c3;"); // Jaune clair
                            break;
                        case "BASSE":
                            setStyle("-fx-background-color: #dcfce7;"); // Vert clair
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Double-clic pour modifier
        tableTaches.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleRowDoubleClick();
            }
        });
    }

    private void chargerTaches() {
        tacheList.clear();
        List<Tache> taches = tacheService.getAllTaches();
        tacheList.addAll(taches);
        tableTaches.setItems(tacheList);

        // Mettre à jour les statistiques
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
    private void retourFormulaire() {
        System.out.println("🔄 Retour au formulaire des tâches");

        // Fermer la fenêtre actuelle
        Stage stage = (Stage) tableTaches.getScene().getWindow();
        stage.close();

        // Revenir à la vue tâches
        MainController.showTaches();
    }

    private void handleRowDoubleClick() {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Tâche sélectionnée: ID " + selected.getId());

            // Fermer la liste
            Stage stage = (Stage) tableTaches.getScene().getWindow();
            stage.close();

            // Retourner au formulaire
            MainController.showTaches();

            // Ici tu pourrais passer l'ID au TacheController
            // TacheController.setSelectedTacheId(selected.getId());
        }
    }
}