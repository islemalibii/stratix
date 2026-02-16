package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    @FXML private HBox progressionBox;

    private ProjetService projetService;

    @FXML
    public void initialize() {
        projetService = new ProjetService();

        cbStatut.getItems().addAll("Planifié", "En cours", "Terminé", "Annulé");
        cbStatut.setValue("Planifié");

        cbStatut.setDisable(true);

        if (progressionBox != null) {
            progressionBox.setVisible(false);
            progressionBox.setManaged(false);
        }
    }

    @FXML
    private void ajouterProjet() {
        try {
            String nom = tfNom.getText();
            String desc = taDescription.getText();
            LocalDate debutLD = dpDateDebut.getValue();
            LocalDate finLD = dpDateFin.getValue();
            String budgetStr = tfBudget.getText();

            if (nom.isEmpty() || desc.isEmpty() || debutLD == null || finLD == null || budgetStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs !");
                return;
            }

            if (finLD.isBefore(debutLD)) {
                showAlert(Alert.AlertType.ERROR, "Erreur de date", "La date de fin doit être après la date de début !");
                return;
            }

            double budget = Double.parseDouble(budgetStr);

            Date dateDebut = java.sql.Date.valueOf(debutLD);
            Date dateFin = java.sql.Date.valueOf(finLD);

            Projet p = new Projet(0, nom, desc, dateDebut, dateFin, budget, "Planifié", 0);

            projetService.ajouterProjet(p);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le projet a été ajouté avec succès !");
            fermerFenetre();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le budget doit être un nombre valide !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML private void annuler() { fermerFenetre(); }

    private void fermerFenetre() {
        if (tfNom.getScene() != null) {
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}