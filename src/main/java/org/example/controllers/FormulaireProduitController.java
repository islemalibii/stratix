package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.produit;
import service.service_produit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private VBox autreCategorieContainer;
    @FXML
    private TextField autreCategorieField;
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

    // Composants pour l'image
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnChoisirImage;
    @FXML
    private Button btnSupprimerImage;
    @FXML
    private Label cheminImageLabel;

    // NOUVEAUX COMPOSANTS
    @FXML
    private ComboBox<String> typeProduitCombo;
    @FXML
    private DatePicker dateFabricationPicker;
    @FXML
    private DatePicker datePeremptionPicker;
    @FXML
    private DatePicker dateGarantiePicker;
    @FXML
    private Label statutPeremptionLabel;

    private service_produit serviceProduit = new service_produit();
    private Runnable onProduitAjoute;

    // Chemin pour stocker les images
    private static final String IMAGES_DIRECTORY = "src/main/resources/images/produits/";
    private File selectedImageFile;
    private String currentImagePath;

    // Liste des catégories prédéfinies
    private final String[] categoriesPredifinies = {
            "Électronique", "Informatique", "Bureau", "Mobilier", "Consommable", "Autre"
    };

    @FXML
    public void initialize() {
        // Initialisation de la ComboBox des catégories
        categorieCombo.setItems(FXCollections.observableArrayList(categoriesPredifinies));

        // Cacher le champ de catégorie personnalisée au démarrage
        autreCategorieContainer.setVisible(false);
        autreCategorieContainer.setManaged(false);

        // Ajouter un listener pour gérer la sélection "Autre"
        categorieCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Autre".equals(newVal)) {
                autreCategorieContainer.setVisible(true);
                autreCategorieContainer.setManaged(true);
                autreCategorieField.requestFocus();
                autreCategorieField.clear();
            } else {
                autreCategorieContainer.setVisible(false);
                autreCategorieContainer.setManaged(false);
            }
        });

        // Initialisation de la ComboBox des types de produit
        typeProduitCombo.setItems(FXCollections.observableArrayList(
                "Général", "Alimentaire", "Médicament", "Électronique", "Cosmétique", "Autre"
        ));

        // Listener pour changer les champs selon le type
        typeProduitCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Alimentaire".equals(newVal) || "Médicament".equals(newVal) || "Cosmétique".equals(newVal)) {
                // Ces types ont une date de péremption
                datePeremptionPicker.setDisable(false);
                datePeremptionPicker.setPromptText("Date de péremption");
                dateGarantiePicker.setDisable(true);
                dateGarantiePicker.setValue(null);
            } else if ("Électronique".equals(newVal)) {
                // Ces types ont une date de garantie
                dateGarantiePicker.setDisable(false);
                dateGarantiePicker.setPromptText("Date de fin de garantie");
                datePeremptionPicker.setDisable(true);
                datePeremptionPicker.setValue(null);
            } else {
                datePeremptionPicker.setDisable(true);
                datePeremptionPicker.setValue(null);
                dateGarantiePicker.setDisable(true);
                dateGarantiePicker.setValue(null);
            }
        });

        // Créer le répertoire d'images s'il n'existe pas
        createImagesDirectory();

        System.out.println("Formulaire initialisé");
    }

    /**
     * Crée le répertoire pour les images s'il n'existe pas
     */
    private void createImagesDirectory() {
        File directory = new File(IMAGES_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Gère le choix d'une image
     */
    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        File file = fileChooser.showOpenDialog(btnChoisirImage.getScene().getWindow());

        if (file != null) {
            selectedImageFile = file;

            try {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                cheminImageLabel.setText(file.getName());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de charger l'image : " + e.getMessage());
            }
        }
    }

    /**
     * Supprime l'image sélectionnée
     */
    @FXML
    private void supprimerImage() {
        imageView.setImage(null);
        selectedImageFile = null;
        currentImagePath = null;
        cheminImageLabel.setText("Aucune image sélectionnée");
    }

    /**
     * Copie l'image dans le répertoire de l'application
     */
    private String copyImageToAppDirectory(File sourceFile, int produitId) {
        if (sourceFile == null) {
            return null;
        }

        try {
            String extension = "";
            String fileName = sourceFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }

            String newFileName = "produit_" + produitId + "_" + System.currentTimeMillis() + extension;
            Path destinationPath = Paths.get(IMAGES_DIRECTORY, newFileName);

            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            return destinationPath.toString();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de copier l'image : " + e.getMessage());
            return null;
        }
    }

    public void setModeAjout() {
        titreFormulaire.setText("Ajouter un produit");
        btnValider.setText("Ajouter");
        dateCreationPicker.setValue(LocalDate.now());
        dateCreationPicker.setDisable(true); // Date système automatique

        // Réinitialiser les champs
        idField.clear();
        nomField.clear();
        descriptionField.clear();
        categorieCombo.getSelectionModel().clearSelection();
        autreCategorieContainer.setVisible(false);
        autreCategorieContainer.setManaged(false);
        autreCategorieField.clear();
        prixField.clear();
        stockActuelField.clear();
        stockMinField.clear();
        ressourcesField.clear();

        // Réinitialiser les nouveaux champs
        typeProduitCombo.getSelectionModel().clearSelection();
        dateFabricationPicker.setValue(null);
        datePeremptionPicker.setValue(null);
        dateGarantiePicker.setValue(null);
        datePeremptionPicker.setDisable(true);
        dateGarantiePicker.setDisable(true);
        statutPeremptionLabel.setText("");

        // Réinitialiser l'image
        supprimerImage();
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
            boolean estPredifinie = false;
            for (String cat : categoriesPredifinies) {
                if (cat.equals(categorieExistante)) {
                    estPredifinie = true;
                    break;
                }
            }

            if (estPredifinie) {
                categorieCombo.setValue(categorieExistante);
                autreCategorieContainer.setVisible(false);
                autreCategorieContainer.setManaged(false);
            } else {
                categorieCombo.setValue("Autre");
                autreCategorieContainer.setVisible(true);
                autreCategorieContainer.setManaged(true);
                autreCategorieField.setText(categorieExistante);
            }
        }

        prixField.setText(String.valueOf(p.getPrix()));
        stockActuelField.setText(String.valueOf(p.getStock_actuel()));
        stockMinField.setText(String.valueOf(p.getStock_min()));

        // Date de création (non modifiable)
        if (p.getDate_creation() != null) {
            dateCreationPicker.setValue(LocalDate.parse(p.getDate_creation()));
            dateCreationPicker.setDisable(true);
        }

        ressourcesField.setText(p.getRessources_necessaires());

        // NOUVEAUX CHAMPS
        if (p.getType_produit() != null) {
            typeProduitCombo.setValue(p.getType_produit());
        }

        if (p.getDate_fabrication() != null) {
            dateFabricationPicker.setValue(LocalDate.parse(p.getDate_fabrication()));
        }

        if (p.getDate_peremption() != null) {
            datePeremptionPicker.setValue(LocalDate.parse(p.getDate_peremption()));
            datePeremptionPicker.setDisable(false);

            // Afficher le statut de péremption
            if (p.estPerime()) {
                statutPeremptionLabel.setText("PÉRIMÉ");
                statutPeremptionLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else if (p.estBientotPerime(30)) {
                statutPeremptionLabel.setText("Bientôt périmé (moins de 30 jours)");
                statutPeremptionLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            } else {
                statutPeremptionLabel.setText("Valide");
                statutPeremptionLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }
        }

        if (p.getDate_garantie() != null) {
            dateGarantiePicker.setValue(LocalDate.parse(p.getDate_garantie()));
            dateGarantiePicker.setDisable(false);

            if (p.garantieExpiree()) {
                statutPeremptionLabel.setText("GARANTIE EXPIRÉE");
                statutPeremptionLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        }

        // Charger l'image si elle existe
        currentImagePath = p.getImage_path();
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            try {
                File imageFile = new File(currentImagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                    imageView.setFitHeight(150);
                    imageView.setFitWidth(150);
                    imageView.setPreserveRatio(true);
                    cheminImageLabel.setText(imageFile.getName());
                } else {
                    cheminImageLabel.setText("Image introuvable");
                }
            } catch (Exception e) {
                cheminImageLabel.setText("Erreur de chargement");
            }
        }
    }

    public void setOnProduitAjoute(Runnable callback) {
        this.onProduitAjoute = callback;
    }

    private String getCategorieValue() {
        String selection = categorieCombo.getValue();
        if ("Autre".equals(selection)) {
            String autreCategorie = autreCategorieField.getText();
            if (autreCategorie != null && !autreCategorie.trim().isEmpty()) {
                return autreCategorie.trim();
            } else {
                return null;
            }
        } else {
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
            boolean isNewProduct = (idField.getText() == null || idField.getText().isEmpty());

            if (!isNewProduct) {
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

            // NOUVEAUX CHAMPS
            p.setType_produit(typeProduitCombo.getValue());

            if (dateFabricationPicker.getValue() != null) {
                p.setDate_fabrication(dateFabricationPicker.getValue().toString());
            }

            if (datePeremptionPicker.getValue() != null) {
                p.setDate_peremption(datePeremptionPicker.getValue().toString());
            }

            if (dateGarantiePicker.getValue() != null) {
                p.setDate_garantie(dateGarantiePicker.getValue().toString());
            }

            // Validation des dates
            if (!validerDates(p)) {
                return;
            }

            if (isNewProduct) {
                // Ajout d'un nouveau produit
                if (selectedImageFile != null) {
                    p.setImage_path(null);
                    serviceProduit.add(p);

                    String imagePath = copyImageToAppDirectory(selectedImageFile, p.getId());
                    if (imagePath != null) {
                        p.setImage_path(imagePath);
                        serviceProduit.update(p);
                    }
                } else {
                    p.setImage_path(null);
                    serviceProduit.add(p);
                }

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès !");
            } else {
                // Modification du produit
                if (selectedImageFile != null) {
                    String imagePath = copyImageToAppDirectory(selectedImageFile, p.getId());
                    p.setImage_path(imagePath);
                } else {
                    p.setImage_path(currentImagePath);
                }

                serviceProduit.update(p);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit modifié avec succès !");
            }

            if (onProduitAjoute != null) {
                onProduitAjoute.run();
            }

            fermerFormulaire();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valide les dates entre elles
     */
    private boolean validerDates(produit p) {
        // Validation date fabrication
        if (p.getDate_fabrication() != null) {
            LocalDate dateFab = LocalDate.parse(p.getDate_fabrication());
            if (dateFab.isAfter(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "La date de fabrication ne peut pas être dans le futur");
                return false;
            }
        }

        // Validation date péremption
        if (p.getDate_peremption() != null) {
            LocalDate datePer = LocalDate.parse(p.getDate_peremption());
            if (p.getDate_fabrication() != null) {
                LocalDate dateFab = LocalDate.parse(p.getDate_fabrication());
                if (datePer.isBefore(dateFab)) {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "La date de péremption doit être après la date de fabrication");
                    return false;
                }
            }
        }

        return true;
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

        String categorie = getCategorieValue();
        if (categorie == null || categorie.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La catégorie est obligatoire");
            return false;
        }

        if (typeProduitCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le type de produit est obligatoire");
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