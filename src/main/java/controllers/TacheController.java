package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Employe;
import models.Tache;
import services.EmployeeService;
import services.SERVICETache;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TacheController {

    @FXML private TextField txtTitre;
    @FXML private TextField txtDescription;
    @FXML private TextField txtEmployeId;  // ← C'est un TextField, pas ComboBox
    @FXML private TextField txtProjetId;
    @FXML private ComboBox<String> cbPriorite;
    @FXML private ComboBox<String> cbStatut;
    @FXML private DatePicker dpDeadline;
    @FXML private ListView<String> listViewTaches;

    private SERVICETache service = new SERVICETache();
    private EmployeeService employeService = new EmployeeService();
    private int selectedTacheId = -1;

    @FXML
    public void initialize() {
        // Initialiser les ComboBox
        cbPriorite.getItems().addAll("HAUTE", "MOYENNE", "BASSE");
        cbStatut.getItems().addAll("A_FAIRE", "EN_COURS", "TERMINEE");

        System.out.println("✅ TacheController initialisé");
        System.out.println("   txtEmployeId: " + (txtEmployeId != null ? "OK" : "null"));
        System.out.println("   cbPriorite: " + (cbPriorite != null ? "OK" : "null"));
        System.out.println("   cbStatut: " + (cbStatut != null ? "OK" : "null"));

        chargerTaches();

        // Sélection dans la liste
        listViewTaches.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        String[] parts = newVal.split(" \\| ");
                        selectedTacheId = Integer.parseInt(parts[0]);
                        remplirFormulaire(selectedTacheId);
                    }
                }
        );
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
            t.setEmployeId(Integer.parseInt(txtEmployeId.getText()));
            t.setProjetId(Integer.parseInt(txtProjetId.getText()));

            service.addTache(t);
            showAlert("Succès", "Tâche ajoutée avec succès !");
            chargerTaches();
            viderFormulaire();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les IDs doivent être des nombres !");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

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
            t.setEmployeId(Integer.parseInt(txtEmployeId.getText()));
            t.setProjetId(Integer.parseInt(txtProjetId.getText()));

            service.updateTache(t);
            showAlert("Succès", "Tâche modifiée avec succès !");
            chargerTaches();
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

        if (confirm.showAndWait().get() == ButtonType.OK) {
            service.deleteTache(selectedTacheId);
            showAlert("Succès", "Tâche supprimée avec succès !");
            chargerTaches();
            viderFormulaire();
        }
    }

    private boolean validerChamps() {
        if (txtTitre.getText().isEmpty()) {
            showAlert("Erreur", "Le titre est requis !");
            return false;
        }
        if (txtDescription.getText().isEmpty()) {
            showAlert("Erreur", "La description est requise !");
            return false;
        }
        if (dpDeadline.getValue() == null) {
            showAlert("Erreur", "La deadline est requise !");
            return false;
        }
        if (cbStatut.getValue() == null) {
            showAlert("Erreur", "Le statut est requis !");
            return false;
        }
        if (cbPriorite.getValue() == null) {
            showAlert("Erreur", "La priorité est requise !");
            return false;
        }
        if (txtEmployeId.getText().isEmpty()) {
            showAlert("Erreur", "L'ID Employé est requis !");
            return false;
        }
        if (txtProjetId.getText().isEmpty()) {
            showAlert("Erreur", "L'ID Projet est requis !");
            return false;
        }
        return true;
    }

    private void remplirFormulaire(int id) {
        Tache t = service.getTacheById(id);
        if (t != null) {
            txtTitre.setText(t.getTitre());
            txtDescription.setText(t.getDescription());
            dpDeadline.setValue(t.getDeadline().toLocalDate());
            cbStatut.setValue(t.getStatut());
            cbPriorite.setValue(t.getPriorite());
            txtEmployeId.setText(String.valueOf(t.getEmployeId()));
            txtProjetId.setText(String.valueOf(t.getProjetId()));
        }
    }

    private void chargerTaches() {
        listViewTaches.getItems().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Tache> taches = service.getAllTaches();
        for (Tache t : taches) {
            String prioriteEmoji = "";
            switch(t.getPriorite()) {
                case "HAUTE": prioriteEmoji = "🔴"; break;
                case "MOYENNE": prioriteEmoji = "🟡"; break;
                case "BASSE": prioriteEmoji = "🟢"; break;
            }

            listViewTaches.getItems().add(
                    t.getId() + " | " + prioriteEmoji + " " + t.getTitre() +
                            " | Emp " + t.getEmployeId() +
                            " | " + t.getDeadline().toLocalDate().format(formatter) +
                            " | " + t.getStatut()
            );
        }
    }

    private void viderFormulaire() {
        txtTitre.clear();
        txtDescription.clear();
        dpDeadline.setValue(null);
        cbStatut.setValue(null);
        cbPriorite.setValue(null);
        txtEmployeId.clear();
        txtProjetId.clear();
        selectedTacheId = -1;
        listViewTaches.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}