package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;
import java.time.LocalDate;
import java.util.Date;

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
                Date d1 = projetEnModification.getDateDebut();
                dateDebut.setValue(new java.sql.Date(d1.getTime()).toLocalDate());
            }
            if (projetEnModification.getDateFin() != null) {
                Date d2 = projetEnModification.getDateFin();
                dateFin.setValue(new java.sql.Date(d2.getTime()).toLocalDate());
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

            if (dateDebut.getValue() != null) {
                projetEnModification.setDateDebut(java.sql.Date.valueOf(dateDebut.getValue()));
            }
            if (dateFin.getValue() != null) {
                projetEnModification.setDateFin(java.sql.Date.valueOf(dateFin.getValue()));
            }

            service.mettreAJourProjet(projetEnModification);

            fermerFenetre();

        } catch (NumberFormatException e) {
            afficherErreur("Erreur de format", "Le budget doit être un nombre et la progression un entier.");
        } catch (Exception e) {
            afficherErreur("Erreur", "Une erreur est survenue lors de la mise à jour.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        if (txtNom.getScene() != null) {
            ((Stage) txtNom.getScene().getWindow()).close();
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}