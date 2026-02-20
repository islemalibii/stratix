package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.produit;
import service.service_produit;

import java.time.LocalDate;

public class FormulaireProduitController {

    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> categorieCombo;
    @FXML
    private VBox autreCategorieContainer; // NOUVEAU: Conteneur pour la catégorie personnalisée
    @FXML
    private TextField autreCategorieField; // NOUVEAU: Champ pour saisir la catégorie personnalisée
    @FXML
    private TextField prixField;
    @FXML
    private TextField stockActuelField;
    @FXML
    private TextField stockMinField;
    @FXML
    private DatePicker dateCreationPicker;
    @FXML
    private TextArea ressourcesField;
    @FXML
    private Label titreFormulaire;
    @FXML
    private Button btnValider;

    private service_produit serviceProduit = new service_produit();
    private Runnable onProduitAjoute;

    // Liste des catégories prédéfinies
    private final String[] categoriesPredifinies = {
            "Électronique", "Informatique", "Bureau", "Mobilier", "Consommable", "Autre"
    };

    @FXML
    public void initialize() {
        // Initialisation de la ComboBox
        categorieCombo.setItems(FXCollections.observableArrayList(categoriesPredifinies));

        // Cacher le champ de catégorie personnalisée au démarrage
        autreCategorieContainer.setVisible(false);
        autreCategorieContainer.setManaged(false);

        // Ajouter un listener pour gérer la sélection "Autre"
        categorieCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Autre".equals(newVal)) {
                // Afficher le champ de catégorie personnalisée
                autreCategorieContainer.setVisible(true);
                autreCategorieContainer.setManaged(true);

                // Mettre le focus sur le champ personnalisé
                autreCategorieField.requestFocus();
                autreCategorieField.clear();
            } else {
                // Cacher le champ de catégorie personnalisée
                autreCategorieContainer.setVisible(false);
                autreCategorieContainer.setManaged(false);
            }
        });

        System.out.println("Formulaire initialisé");
    }

    public void setModeAjout() {
        titreFormulaire.setText("Ajouter un produit");
        btnValider.setText("Ajouter");
        dateCreationPicker.setValue(LocalDate.now());

        // Réinitialiser
        categorieCombo.getSelectionModel().clearSelection();
        autreCategorieContainer.setVisible(false);
        autreCategorieContainer.setManaged(false);
        autreCategorieField.clear();
    }

    public void setModeModification(produit p) {
        titreFormulaire.setText("Modifier un produit");
        btnValider.setText("Modifier");

        idField.setText(String.valueOf(p.getId()));
        nomField.setText(p.getNom());
        descriptionField.setText(p.getDescription());

        // Gérer la catégorie existante
        String categorieExistante = p.getCategorie();
        if (categorieExistante != null && !categorieExistante.isEmpty()) {
            // Vérifier si c'est une catégorie prédéfinie
            boolean estPredifinie = false;
            for (String cat : categoriesPredifinies) {
                if (cat.equals(categorieExistante)) {
                    estPredifinie = true;
                    break;
                }
            }

            if (estPredifinie) {
                // Catégorie prédéfinie
                categorieCombo.setValue(categorieExistante);
                autreCategorieContainer.setVisible(false);
                autreCategorieContainer.setManaged(false);
            } else {
                // Catégorie personnalisée
                categorieCombo.setValue("Autre");
                autreCategorieContainer.setVisible(true);
                autreCategorieContainer.setManaged(true);
                autreCategorieField.setText(categorieExistante);
            }
        }

        prixField.setText(String.valueOf(p.getPrix()));
        stockActuelField.setText(String.valueOf(p.getStock_actuel()));
        stockMinField.setText(String.valueOf(p.getStock_min()));
        if (p.getDate_creation() != null) {
            dateCreationPicker.setValue(LocalDate.parse(p.getDate_creation()));
        }
        ressourcesField.setText(p.getRessources_necessaires());
    }

    public void setOnProduitAjoute(Runnable callback) {
        this.onProduitAjoute = callback;
    }

    /**
     * Récupère la valeur de la catégorie
     */
    private String getCategorieValue() {
        String selection = categorieCombo.getValue();

        if ("Autre".equals(selection)) {
            // Retourner la valeur du champ personnalisé
            String autreCategorie = autreCategorieField.getText();
            if (autreCategorie != null && !autreCategorie.trim().isEmpty()) {
                return autreCategorie.trim();
            } else {
                return null; // Pas de catégorie saisie
            }
        } else {
            // Retourner la catégorie sélectionnée
            return selection;
        }
    }

    @FXML
    private void validerFormulaire() {
        if (!validerChamps()) {
            return;
        }

        try {
            produit p = new produit();

            if (idField.getText() != null && !idField.getText().isEmpty()) {
                p.setId(Integer.parseInt(idField.getText()));
            }

            p.setNom(nomField.getText().trim());
            p.setDescription(descriptionField.getText().trim());
            p.setCategorie(getCategorieValue());
            p.setPrix(Double.parseDouble(prixField.getText().trim()));
            p.setStock_actuel(Integer.parseInt(stockActuelField.getText().trim()));
            p.setStock_min(Integer.parseInt(stockMinField.getText().trim()));
            p.setDate_creation(dateCreationPicker.getValue().toString());
            p.setRessources_necessaires(ressourcesField.getText().trim());

            if (idField.getText() == null || idField.getText().isEmpty()) {
                serviceProduit.add(p);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès !");
            } else {
                serviceProduit.update(p);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit modifié avec succès !");
            }

            if (onProduitAjoute != null) {
                onProduitAjoute.run();
            }

            fermerFormulaire();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
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

        // Validation de la catégorie
        String categorie = getCategorieValue();
        if (categorie == null || categorie.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La catégorie est obligatoire");
            return false;
        }

        try {
            Double.parseDouble(prixField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre valide");
            return false;
        }
        try {
            Integer.parseInt(stockActuelField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le stock actuel doit être un nombre valide");
            return false;
        }
        try {
            Integer.parseInt(stockMinField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le stock minimum doit être un nombre valide");
            return false;
        }
        if (dateCreationPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de création est obligatoire");
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