package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Employe;
import models.Tache;
import services.EmployeeService;
import services.SERVICETache;

import java.sql.Date;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TacheController {

    @FXML private TextField txtTitre;
    @FXML private TextField txtDescription;
    @FXML private ComboBox<Employe> cmbEmploye;
    @FXML private TextField txtProjetId;
    @FXML private ComboBox<String> cbPriorite;
    @FXML private ComboBox<String> cbStatut;
    @FXML private DatePicker dpDeadline;

    @FXML private Label lblTotalTaches;
    @FXML private Label lblAFaire;
    @FXML private Label lblEnCours;
    @FXML private Label lblTerminees;

    private SERVICETache service = new SERVICETache();
    private EmployeeService employeService = new EmployeeService();
    private int selectedTacheId = -1;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation TacheController ===");

        // Charger les employés dans la ComboBox
        List<Employe> employes = employeService.getAllEmployes();
        cmbEmploye.setItems(FXCollections.observableArrayList(employes));

        // Afficher le nom dans la ComboBox
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getDisplayName());
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
                    setText(emp.getDisplayName());
                }
            }
        });

        // Initialiser les ComboBox
        cbPriorite.getItems().addAll("HAUTE", "MOYENNE", "BASSE");
        cbStatut.getItems().addAll("A_FAIRE", "EN_COURS", "TERMINEE");

        // Vérification des composants
        System.out.println("   cmbEmploye: " + (cmbEmploye != null ? "✅" : "❌"));
        System.out.println("   cbPriorite: " + (cbPriorite != null ? "✅" : "❌"));
        System.out.println("   cbStatut: " + (cbStatut != null ? "✅" : "❌"));
        System.out.println("   txtTitre: " + (txtTitre != null ? "✅" : "❌"));

        mettreAJourStatistiques();
    }

    @FXML
    void ajouterTache() {
        try {
            if (!validerChamps()) return;

            Tache t = new Tache();
            t.setTitre(txtTitre.getText());
            t.setDescription(txtDescription.getText());
            t.setDeadline(Date.valueOf(dpDeadline.getValue()));
            t.setStatut(cbStatut.getValue());
            t.setPriorite(cbPriorite.getValue());

            Employe selectedEmploye = cmbEmploye.getValue();
            t.setEmployeId(selectedEmploye.getId());
            t.setProjetId(Integer.parseInt(txtProjetId.getText()));

            service.addTache(t);
            showAlert("Succès", "✅ Tâche ajoutée avec succès !");
            mettreAJourStatistiques();
            viderFormulaire();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID Projet doit être un nombre !");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
//
    @FXML
    void modifierTache() {
        if (selectedTacheId == -1) {
            showAlert("Erreur", "Sélectionnez d'abord une tâche dans la liste !");
            return;
        }

        try {
            if (!validerChamps()) return;

            Tache t = new Tache();
            t.setId(selectedTacheId);
            t.setTitre(txtTitre.getText());
            t.setDescription(txtDescription.getText());
            t.setDeadline(Date.valueOf(dpDeadline.getValue()));
            t.setStatut(cbStatut.getValue());
            t.setPriorite(cbPriorite.getValue());

            Employe selectedEmploye = cmbEmploye.getValue();
            t.setEmployeId(selectedEmploye.getId());
            t.setProjetId(Integer.parseInt(txtProjetId.getText()));

            service.updateTache(t);
            showAlert("Succès", "✅ Tâche modifiée avec succès !");
            mettreAJourStatistiques();
            viderFormulaire();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerTache() {
        if (selectedTacheId == -1) {
            showAlert("Erreur", "Sélectionnez d'abord une tâche dans la liste !");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer cette tâche ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.deleteTache(selectedTacheId);
            showAlert("Succès", "✅ Tâche supprimée avec succès !");
            mettreAJourStatistiques();
            viderFormulaire();
        }
    }

    @FXML
    private void openTacheListe() {
        try {
            System.out.println("Ouverture de la liste des tâches...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TacheListeView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("📋 Liste des Tâches");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste des tâches");
        }
    }

    private boolean validerChamps() {
        if (txtTitre.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ Le titre est requis !");
            return false;
        }
        if (txtDescription.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ La description est requise !");
            return false;
        }
        if (dpDeadline.getValue() == null) {
            showAlert("Erreur", "❌ La deadline est requise !");
            return false;
        }
        if (cbStatut.getValue() == null) {
            showAlert("Erreur", "❌ Le statut est requis !");
            return false;
        }
        if (cbPriorite.getValue() == null) {
            showAlert("Erreur", "❌ La priorité est requise !");
            return false;
        }
        if (cmbEmploye.getValue() == null) {
            showAlert("Erreur", "❌ Sélectionnez un employé !");
            return false;
        }
        if (txtProjetId.getText().trim().isEmpty()) {
            showAlert("Erreur", "❌ L'ID Projet est requis !");
            return false;
        }
        return true;
    }

    private void mettreAJourStatistiques() {
        List<Tache> taches = service.getAllTaches();
        int total = taches.size();
        int aFaire = 0;
        int enCours = 0;
        int terminees = 0;

        for (Tache t : taches) {
            switch(t.getStatut()) {
                case "A_FAIRE": aFaire++; break;
                case "EN_COURS": enCours++; break;
                case "TERMINEE": terminees++; break;
            }
        }

        if (lblTotalTaches != null) lblTotalTaches.setText(String.valueOf(total));
        if (lblAFaire != null) lblAFaire.setText(String.valueOf(aFaire));
        if (lblEnCours != null) lblEnCours.setText(String.valueOf(enCours));
        if (lblTerminees != null) lblTerminees.setText(String.valueOf(terminees));
    }

    private void viderFormulaire() {
        txtTitre.clear();
        txtDescription.clear();
        dpDeadline.setValue(null);
        cbStatut.setValue(null);
        cbPriorite.setValue(null);
        cmbEmploye.setValue(null);
        txtProjetId.clear();
        selectedTacheId = -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}