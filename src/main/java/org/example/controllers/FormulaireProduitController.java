package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
                // Rendre la ComboBox éditable et effacer le texte pour permettre la saisie
                categorieCombo.setEditable(true);
                categorieCombo.getEditor().clear();
                categorieCombo.getEditor().requestFocus();
            } else {
                // Si ce n'est pas "Autre", on garde le comportement normal
                categorieCombo.setEditable(false);
            }
        });

        // Listener pour capturer la saisie manuelle quand "Autre" est sélectionné
        categorieCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (categorieCombo.isEditable() && newVal != null && !newVal.isEmpty()) {
                // La valeur saisie sera utilisée comme catégorie personnalisée
                System.out.println("Catégorie personnalisée : " + newVal);
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
    }

    public void setModeModification(produit p) {
        titreFormulaire.setText("Modifier un produit");
        btnValider.setText("Modifier");

        idField.setText(String.valueOf(p.getId()));
        nomField.setText(p.getNom());
        descriptionField.setText(p.getDescription());

        // Gérer la catégorie existante
        String categorieExistante = p.getCategorie();
        if (categorieExistante != null) {
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
        if (categorieCombo.isEditable()) {
            // Si la ComboBox est éditable, on prend la valeur saisie
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

    // Ajouter cette méthode pour sauvegarder les catégories personnalisées
    private void ajouterCategoriePersonnalisee(String nouvelleCategorie) {
        if (nouvelleCategorie != null && !nouvelleCategorie.isEmpty()) {
            // Vérifier si la catégorie n'existe pas déjà
            boolean existe = false;
            for (String cat : categoriesPredifinies) {
                if (cat.equalsIgnoreCase(nouvelleCategorie)) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                // Ajouter à la liste des catégories pour une utilisation future
                ObservableList<String> items = FXCollections.observableArrayList(categoriesPredifinies);
                items.add(nouvelleCategorie);
                categorieCombo.setItems(items);
            }
        }
    }
}