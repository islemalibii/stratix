package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import models.produit;
import models.ressource;
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
    private Runnable onProduitAjoute; // Callback pour rafraîchir la liste

    // Liste des catégories prédéfinies
    private final String[] categoriesPredifinies = {
            "Électronique", "Informatique", "Bureau", "Mobilier", "Consommable", "Autre"
    };

    @FXML
    public void initialize() {
        // Initialisation de la ComboBox
        categorieCombo.setItems(FXCollections.observableArrayList(categoriesPredifinies));

        // Ajouter un listener pour gérer la sélection "Autre"
        categorieCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Autre".equals(newVal)) {
                // Rendre la ComboBox éditable
                categorieCombo.setEditable(true);

                // Effacer le texte et mettre le focus sur l'éditeur
                categorieCombo.getEditor().clear();

                // Utiliser Platform.runLater pour s'assurer que le focus est bien pris
                javafx.application.Platform.runLater(() -> {
                    categorieCombo.getEditor().requestFocus();
                });

                // Sélectionner tout le texte pour faciliter la saisie
                categorieCombo.getEditor().selectAll();
            } else {
                // Si ce n'est pas "Autre", on garde le comportement normal
                categorieCombo.setEditable(false);
            }
        });

        // Permettre la validation avec Entrée quand on saisit une catégorie personnalisée
        categorieCombo.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Validation implicite
                categorieCombo.setValue(categorieCombo.getEditor().getText());
            }
        });

        // Pour que la perte de focus valide aussi la saisie
        categorieCombo.getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Perte de focus
                String texteSaisi = categorieCombo.getEditor().getText();
                if (texteSaisi != null && !texteSaisi.trim().isEmpty()) {
                    categorieCombo.setValue(texteSaisi);
                }
            }
        });

        System.out.println("Formulaire initialisé");
    }

    public void setModeAjout() {
        titreFormulaire.setText("Ajouter un produit");
        btnValider.setText("Ajouter");
        dateCreationPicker.setValue(LocalDate.now());

        // Réinitialiser la ComboBox
        categorieCombo.setItems(FXCollections.observableArrayList(categoriesPredifinies));
        categorieCombo.getSelectionModel().clearSelection();
        categorieCombo.setEditable(false);
        categorieCombo.getEditor().clear();
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
            // Vérifier si la catégorie existe dans la liste prédéfinie
            boolean estCategoriePredifinie = false;
            for (String cat : categoriesPredifinies) {
                if (cat.equals(categorieExistante)) {
                    estCategoriePredifinie = true;
                    break;
                }
            }

            if (estCategoriePredifinie) {
                // C'est une catégorie prédéfinie
                categorieCombo.setValue(categorieExistante);
                categorieCombo.setEditable(false);
            } else {
                // C'est une catégorie personnalisée
                categorieCombo.setEditable(true);
                categorieCombo.getEditor().setText(categorieExistante);
                // Sélectionner "Autre" pour indiquer que c'est personnalisé
                categorieCombo.getSelectionModel().select("Autre");

                // Mettre le focus sur l'éditeur
                javafx.application.Platform.runLater(() -> {
                    categorieCombo.getEditor().requestFocus();
                    categorieCombo.getEditor().selectAll();
                });
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
     * Récupère la valeur de la catégorie (sélectionnée ou saisie)
     */
    private String getCategorieValue() {
        // Si la ComboBox est éditable, on prend la valeur de l'éditeur
        if (categorieCombo.isEditable()) {
            String saisie = categorieCombo.getEditor().getText();
            if (saisie != null && !saisie.trim().isEmpty()) {
                return saisie.trim();
            }
        }

        // Sinon, on prend la valeur sélectionnée
        String selection = categorieCombo.getValue();
        if (selection != null && !selection.trim().isEmpty()) {
            return selection.trim();
        }

        return null;
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

            // Récupérer la catégorie (sélectionnée ou saisie)
            String categorie = getCategorieValue();
            if (categorie != null) {
                p.setCategorie(categorie);
            }

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

            // Ajouter la catégorie personnalisée à la liste pour une prochaine utilisation
            if (categorie != null && !estDansCategoriesPredifinies(categorie)) {
                ajouterCategoriePersonnalisee(categorie);
            }

            if (onProduitAjoute != null) {
                onProduitAjoute.run(); // Rafraîchir la liste
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
        if (categorie == null || categorie.isEmpty()) {
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

    // Vérifier si une catégorie est dans la liste prédéfinie
    private boolean estDansCategoriesPredifinies(String categorie) {
        for (String cat : categoriesPredifinies) {
            if (cat.equalsIgnoreCase(categorie)) {
                return true;
            }
        }
        return false;
    }

    // Ajouter cette méthode pour sauvegarder les catégories personnalisées
    private void ajouterCategoriePersonnalisee(String nouvelleCategorie) {
        if (nouvelleCategorie != null && !nouvelleCategorie.isEmpty()) {
            // Vérifier si la catégorie n'existe pas déjà
            boolean existe = false;
            ObservableList<String> itemsActuels = categorieCombo.getItems();

            for (String cat : itemsActuels) {
                if (cat.equalsIgnoreCase(nouvelleCategorie)) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                // Ajouter à la liste des catégories pour une utilisation future
                itemsActuels.add(nouvelleCategorie);
            }
        }
    }
}