package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;
import java.time.LocalDate;
import java.util.Date;

public class AjoutProjetController {

    @FXML private TextField tfNom, tfBudget;
    @FXML private TextArea taDescription;
    @FXML private DatePicker dpDateDebut, dpDateFin;
    @FXML private ChoiceBox<String> cbStatut;

    private ProjetService projetService = new ProjetService();

    @FXML
    public void initialize() {
        cbStatut.getItems().addAll("Planifié", "En cours", "Terminé", "Annulé");
        cbStatut.setValue("Planifié");
    }

    @FXML
    private void ajouterProjet() {
        try {
            if (validationsEchouent()) return;

            Date dateDebut = java.sql.Date.valueOf(dpDateDebut.getValue());
            Date dateFin = java.sql.Date.valueOf(dpDateFin.getValue());
            double budget = Double.parseDouble(tfBudget.getText());

            Projet p = new Projet(0, tfNom.getText(), taDescription.getText(),
                    dateDebut, dateFin, budget, "Planifié", 0, false);

            projetService.ajouterProjet(p);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet ajouté !");
            fermerFenetre();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Budget invalide.");
        }
    }

    private boolean validationsEchouent() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debut = dpDateDebut.getValue();
        LocalDate fin = dpDateFin.getValue();

        if (tfNom.getText().isEmpty() || debut == null || fin == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir les champs obligatoires.");
            return true;
        }

        if (debut.isBefore(aujourdhui)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début ne peut pas être antérieure à aujourd'hui.");
            return true;
        }

        if (fin.isBefore(debut)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de fin ne peut pas être avant la date de début.");
            return true;
        }
        return false;
    }

    @FXML private void annuler() { fermerFenetre(); }
    private void fermerFenetre() { ((Stage) tfNom.getScene().getWindow()).close(); }
    private void showAlert(Alert.AlertType type, String t, String m) {
        Alert a = new Alert(type); a.setTitle(t); a.setContentText(m); a.show();
    }


}