package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;
import java.time.LocalDate;

public class ModifierProjetController {

    @FXML private TextField txtNom, txtBudget, txtProgression;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dateDebut, dateFin;
    @FXML private ChoiceBox<String> comboStatut;

    private ProjetService service = new ProjetService();
    private Projet projetEnModification;

    public void chargerDonnees(int idProjet) {
        this.projetEnModification = service.chercherProjetParId(idProjet);

        if (projetEnModification != null) {
            txtNom.setText(projetEnModification.getNom());
            txtDescription.setText(projetEnModification.getDescription());
            txtBudget.setText(String.valueOf(projetEnModification.getBudget()));

            txtProgression.setText(String.valueOf(projetEnModification.getProgression()));

            comboStatut.setValue(projetEnModification.getStatut());

            if (projetEnModification.getDateDebut() != null) {
                dateDebut.setValue(((java.sql.Date) projetEnModification.getDateDebut()).toLocalDate());
            }
            if (projetEnModification.getDateFin() != null) {
                dateFin.setValue(((java.sql.Date) projetEnModification.getDateFin()).toLocalDate());
            }
        }
    }

    @FXML
    private void handleEnregistrer() {
        try {
            projetEnModification.setNom(txtNom.getText());
            projetEnModification.setDescription(txtDescription.getText());
            projetEnModification.setBudget(Double.parseDouble(txtBudget.getText()));

            projetEnModification.setProgression(Integer.parseInt(txtProgression.getText()));
            projetEnModification.setStatut(comboStatut.getValue());

            projetEnModification.setDateDebut(java.sql.Date.valueOf(dateDebut.getValue()));
            projetEnModification.setDateFin(java.sql.Date.valueOf(dateFin.getValue()));

            service.mettreAJourProjet(projetEnModification);
            fermerFenetre();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour : " + e.getMessage());
            alert.show();
        }
    }

    @FXML private void handleAnnuler() { fermerFenetre(); }

    private void fermerFenetre() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private boolean validerChamps() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        if (txtNom.getText().isEmpty() || debut == null || fin == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs.");
            return false;
        }

        if (debut.isBefore(aujourdhui)) {
            afficherAlerte(Alert.AlertType.WARNING, "Dates invalides", "La date de début ne peut pas être dans le passé.");
            return false;
        }

        if (fin.isBefore(debut)) {
            afficherAlerte(Alert.AlertType.WARNING, "Dates invalides", "La date de fin doit être après la date de début.");
            return false;
        }
        return true;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}