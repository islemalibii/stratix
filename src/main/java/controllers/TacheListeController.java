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
    @FXML private TableColumn<Tache, Void> colActions;  // Nouvelle colonne pour les boutons

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
        ajouterBoutonsAction();  // Ajouter les boutons
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

        // Employé
        colEmploye.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            Employe emp = employeService.getEmployeById(t.getEmployeId());
            String nom = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        // Deadline formatée
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

        // Statut
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

    // Ajouter les boutons Modifier et Supprimer
    private void ajouterBoutonsAction() {
        colActions.setCellFactory(param -> new TableCell<Tache, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");

            {
                btnEdit.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");

                btnEdit.setOnAction(event -> {
                    Tache tache = getTableView().getItems().get(getIndex());
                    modifierTache(tache);
                });

                btnDelete.setOnAction(event -> {
                    Tache tache = getTableView().getItems().get(getIndex());
                    supprimerTache(tache);
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

    // Méthode pour modifier une tâche
    private void modifierTache(Tache tache) {
        try {
            System.out.println("✏️ Modification tâche ID: " + tache.getId());

            // Charger le formulaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TacheView.fxml"));
            Parent root = loader.load();

            // Passer la tâche au formulaire
            TacheController controller = loader.getController();
            controller.setTacheToEdit(tache);

            // Changer de scène
            Stage stage = (Stage) tableTaches.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer une tâche
    private void supprimerTache(Tache tache) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la tâche");
        confirm.setContentText("Voulez-vous vraiment supprimer cette tâche ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            tacheService.deleteTache(tache.getId());
            chargerTaches();
            showAlert("Succès", "✅ Tâche supprimée");
        }
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
    private void retourFormulaire() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/TacheView.fxml"));
            Stage stage = (Stage) tableTaches.getScene().getWindow();
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