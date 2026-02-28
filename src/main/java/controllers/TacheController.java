package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.Tache;
import models.Employe;
import services.SERVICETache;
import services.EmployeeService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class TacheController implements Initializable {

    @FXML private TextField txtTitre;
    @FXML private TextField txtDescription;
    @FXML private ComboBox<String> cbPriorite;
    @FXML private ComboBox<String> cbStatut;
    @FXML private ComboBox<Employe> cmbEmploye;
    @FXML private TextField txtProjetId;
    @FXML private DatePicker dpDeadline;

    @FXML private Label lblTotalTaches;
    @FXML private Label lblAFaire;
    @FXML private Label lblEnCours;
    @FXML private Label lblTerminees;

    private SERVICETache tacheService;
    private EmployeeService employeService;
    private int tacheIdEnModification = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation TacheController ===");

        tacheService = new SERVICETache();
        employeService = new EmployeeService();

        // Initialiser les combobox
        cbPriorite.setItems(FXCollections.observableArrayList("HAUTE", "MOYENNE", "BASSE"));
        cbStatut.setItems(FXCollections.observableArrayList("A_FAIRE", "EN_COURS", "TERMINEE"));

        // Charger les employés
        List<Employe> employes = employeService.getAllEmployes();
        ObservableList<Employe> employeList = FXCollections.observableArrayList(employes);
        cmbEmploye.setItems(employeList);

        // Configuration de l'affichage des employés
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getUsername() + " (ID: " + emp.getId() + ")");
                }
            }
        });

        cmbEmploye.setButtonCell(new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getUsername());
                }
            }
        });

        // Mettre à jour les statistiques
        mettreAJourStatistiques();
    }

    private void mettreAJourStatistiques() {
        List<Tache> toutesTaches = tacheService.getAllTaches();
        int total = toutesTaches.size();
        int aFaire = 0, enCours = 0, terminees = 0;

        for (Tache t : toutesTaches) {
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
    }

    @FXML
    private void ajouterTache() {
        if (!validerChamps()) return;

        try {
            Tache nouvelleTache = new Tache();
            nouvelleTache.setTitre(txtTitre.getText().trim());
            nouvelleTache.setDescription(txtDescription.getText().trim());
            nouvelleTache.setPriorite(cbPriorite.getValue());
            nouvelleTache.setStatut(cbStatut.getValue());

            Employe selectedEmp = cmbEmploye.getValue();
            nouvelleTache.setEmployeId(selectedEmp.getId());

            nouvelleTache.setProjetId(Integer.parseInt(txtProjetId.getText().trim()));
            nouvelleTache.setDeadline(Date.valueOf(dpDeadline.getValue()));

            tacheService.addTache(nouvelleTache);
            showAlert("Succès", "✅ Tâche ajoutée avec succès !");
            viderFormulaire();
            mettreAJourStatistiques();

        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierTache() {
        if (tacheIdEnModification == -1) {
            showAlert("Erreur", "❌ Aucune tâche sélectionnée pour modification");
            return;
        }

        if (!validerChamps()) return;

        try {
            Tache tache = new Tache();
            tache.setId(tacheIdEnModification);
            tache.setTitre(txtTitre.getText().trim());
            tache.setDescription(txtDescription.getText().trim());
            tache.setPriorite(cbPriorite.getValue());
            tache.setStatut(cbStatut.getValue());

            Employe selectedEmp = cmbEmploye.getValue();
            tache.setEmployeId(selectedEmp.getId());

            tache.setProjetId(Integer.parseInt(txtProjetId.getText().trim()));
            tache.setDeadline(Date.valueOf(dpDeadline.getValue()));

            tacheService.updateTache(tache);
            showAlert("Succès", "✏️ Tâche modifiée avec succès !");
            viderFormulaire();
            tacheIdEnModification = -1;
            mettreAJourStatistiques();

        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerTache() {
        if (tacheIdEnModification == -1) {
            showAlert("Erreur", "❌ Aucune tâche sélectionnée pour suppression");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la tâche");
        confirm.setContentText("Voulez-vous vraiment supprimer cette tâche ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            tacheService.deleteTache(tacheIdEnModification);
            showAlert("Succès", "🗑️ Tâche supprimée !");
            viderFormulaire();
            tacheIdEnModification = -1;
            mettreAJourStatistiques();
        }
    }

    public void setTacheToEdit(Tache tache) {
        if (tache != null) {
            this.tacheIdEnModification = tache.getId();
            txtTitre.setText(tache.getTitre());
            txtDescription.setText(tache.getDescription());
            cbPriorite.setValue(tache.getPriorite());
            cbStatut.setValue(tache.getStatut());

            Employe emp = employeService.getEmployeById(tache.getEmployeId());
            if (emp != null) {
                cmbEmploye.setValue(emp);
            }

            txtProjetId.setText(String.valueOf(tache.getProjetId()));
            dpDeadline.setValue(tache.getDeadline().toLocalDate());
        }
    }

    private boolean validerChamps() {
        if (txtTitre.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ Le titre est obligatoire");
            return false;
        }
        if (cbPriorite.getValue() == null) {
            showAlert("Erreur", "❌ La priorité est obligatoire");
            return false;
        }
        if (cbStatut.getValue() == null) {
            showAlert("Erreur", "❌ Le statut est obligatoire");
            return false;
        }
        if (cmbEmploye.getValue() == null) {
            showAlert("Erreur", "❌ L'employé est obligatoire");
            return false;
        }
        if (txtProjetId.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ L'ID du projet est obligatoire");
            return false;
        }
        if (dpDeadline.getValue() == null) {
            showAlert("Erreur", "❌ La deadline est obligatoire");
            return false;
        }
        return true;
    }

    private void viderFormulaire() {
        txtTitre.clear();
        txtDescription.clear();
        cbPriorite.setValue(null);
        cbStatut.setValue(null);
        cmbEmploye.setValue(null);
        txtProjetId.clear();
        dpDeadline.setValue(null);
    }

    @FXML
    private void openTacheListe() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/TacheListeView.fxml"));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
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