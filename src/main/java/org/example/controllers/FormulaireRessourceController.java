package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.ressource;
import service.service_ressource;

public class FormulaireRessourceController {

    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField quantiteField;
    @FXML
    private TextField fournisseurField;
    @FXML
    private Label titreFormulaire;
    @FXML
    private Button btnValider;

    private service_ressource serviceRessource = new service_ressource();
    private Runnable onRessourceAjoutee;

    @FXML
    public void initialize() {
        System.out.println("Formulaire ressource initialisé");

        // Types de ressources prédéfinis
        typeCombo.setItems(FXCollections.observableArrayList(
                "Matériel informatique",
                "Mobilier",
                "Fourniture de bureau",
                "Équipement",
                "Matière première",
                "Logiciel",
                "Autre"
        ));
    }

    public void setModeAjout() {
        titreFormulaire.setText("Ajouter une ressource");
        btnValider.setText("Ajouter");

        // Vider les champs
        idField.clear();
        nomField.clear();
        typeCombo.getSelectionModel().clearSelection();
        quantiteField.clear();
        fournisseurField.clear();
    }

    public void setModeModification(ressource r) {
        titreFormulaire.setText("Modifier une ressource");
        btnValider.setText("Modifier");

        idField.setText(String.valueOf(r.getid()));
        nomField.setText(r.getNom());
        typeCombo.setValue(r.getType_ressource());
        quantiteField.setText(String.valueOf(r.getQuatite()));
        fournisseurField.setText(r.getFournisseur());
    }

    public void setOnRessourceAjoutee(Runnable callback) {
        this.onRessourceAjoutee = callback;
    }

    @FXML
    private void validerFormulaire() {
        if (!validerChamps()) {
            return;
        }

        try {
            ressource r = new ressource();

            if (idField.getText() != null && !idField.getText().isEmpty()) {
                r.setid(Integer.parseInt(idField.getText()));
            }

            r.setNom(nomField.getText().trim());
            r.setType_ressource(typeCombo.getValue());
            r.setQuatite(Integer.parseInt(quantiteField.getText().trim()));
            r.setFournisseur(fournisseurField.getText().trim());

            if (idField.getText() == null || idField.getText().isEmpty()) {
                serviceRessource.add(r);
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Ressource ajoutée avec succès !");
            } else {
                serviceRessource.update(r);
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Ressource modifiée avec succès !");
            }

            if (onRessourceAjoutee != null) {
                onRessourceAjoutee.run();
            }

            fermerFormulaire();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void annulerFormulaire() {
        fermerFormulaire();
    }

    private void fermerFormulaire() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private boolean validerChamps() {
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom est obligatoire");
            return false;
        }
        if (typeCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le type est obligatoire");
            return false;
        }
        try {
            int quantite = Integer.parseInt(quantiteField.getText().trim());
            if (quantite < 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être positive");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être un nombre valide");
            return false;
        }
        if (fournisseurField.getText() == null || fournisseurField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le fournisseur est obligatoire");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}