package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.produit;
import Services.service_produit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    // Date Pickers
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

    // Formateur de date pour l'affichage
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        // Créer le répertoire d'images s'il n'existe pas
        createImagesDirectory();

        // Configurer tous les DatePickers avec le bon format
        configurerDatePicker(dateCreationPicker);
        configurerDatePicker(dateFabricationPicker);
        configurerDatePicker(datePeremptionPicker);
        configurerDatePicker(dateGarantiePicker);

        // Initialiser la date de création avec la date du jour
        dateCreationPicker.setValue(LocalDate.now());

        // Désactiver l'édition manuelle pour la date de création (système)
        dateCreationPicker.setEditable(false);
        dateCreationPicker.setDisable(true);

        System.out.println("Formulaire initialisé");
    }

    /**
     * Configure un DatePicker avec un convertisseur de date français
     */
    private void configurerDatePicker(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        // Essayer le format par défaut
                        try {
                            return LocalDate.parse(string);
                        } catch (DateTimeParseException ex) {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            }
        });

        // Rendre éditable (sauf pour dateCreationPicker qui sera désactivé après)
        datePicker.setEditable(true);

        // Ajouter un prompt text en français
        datePicker.setPromptText("jj/mm/aaaa");
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

        // Date système automatique - toujours la date du jour
        dateCreationPicker.setValue(LocalDate.now());
        dateCreationPicker.setDisable(true);

        // Réinitialiser les autres dates
        dateFabricationPicker.setValue(null);
        datePeremptionPicker.setValue(null);
        dateGarantiePicker.setValue(null);
        statutPeremptionLabel.setText("");

        // Activer les autres DatePickers
        dateFabricationPicker.setDisable(false);
        datePeremptionPicker.setDisable(false);
        dateGarantiePicker.setDisable(false);

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
        if (p.getDate_creation() != null && !p.getDate_creation().isEmpty()) {
            try {
                dateCreationPicker.setValue(LocalDate.parse(p.getDate_creation()));
            } catch (Exception e) {
                dateCreationPicker.setValue(LocalDate.now());
            }
        } else {
            dateCreationPicker.setValue(LocalDate.now());
        }
        dateCreationPicker.setDisable(true);

        ressourcesField.setText(p.getRessources_necessaires());

        // Charger les dates
        if (p.getDate_fabrication() != null && !p.getDate_fabrication().isEmpty()) {
            try {
                dateFabricationPicker.setValue(LocalDate.parse(p.getDate_fabrication()));
            } catch (Exception e) {
                dateFabricationPicker.setValue(null);
            }
        }

        if (p.getDate_peremption() != null && !p.getDate_peremption().isEmpty()) {
            try {
                datePeremptionPicker.setValue(LocalDate.parse(p.getDate_peremption()));

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
            } catch (Exception e) {
                datePeremptionPicker.setValue(null);
            }
        }

        if (p.getDate_garantie() != null && !p.getDate_garantie().isEmpty()) {
            try {
                dateGarantiePicker.setValue(LocalDate.parse(p.getDate_garantie()));

                if (p.garantieExpiree()) {
                    statutPeremptionLabel.setText("GARANTIE EXPIRÉE");
                    statutPeremptionLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            } catch (Exception e) {
                dateGarantiePicker.setValue(null);
            }
        }

        // Activer les autres DatePickers
        dateFabricationPicker.setDisable(false);
        datePeremptionPicker.setDisable(false);
        dateGarantiePicker.setDisable(false);

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

            // Date de création (toujours définie)
            if (dateCreationPicker.getValue() != null) {
                p.setDate_creation(dateCreationPicker.getValue().toString());
            } else {
                p.setDate_creation(LocalDate.now().toString());
            }

            p.setRessources_necessaires(ressourcesField.getText().trim());

            // Dates optionnelles
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
        try {
            // Validation date fabrication
            if (p.getDate_fabrication() != null && !p.getDate_fabrication().isEmpty()) {
                LocalDate dateFab = LocalDate.parse(p.getDate_fabrication());
                if (dateFab.isAfter(LocalDate.now())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "La date de fabrication ne peut pas être dans le futur");
                    return false;
                }
            }

            // Validation date péremption
            if (p.getDate_peremption() != null && !p.getDate_peremption().isEmpty()) {
                LocalDate datePer = LocalDate.parse(p.getDate_peremption());

                // Vérifier que la date de péremption n'est pas avant la date de fabrication
                if (p.getDate_fabrication() != null && !p.getDate_fabrication().isEmpty()) {
                    LocalDate dateFab = LocalDate.parse(p.getDate_fabrication());
                    if (datePer.isBefore(dateFab)) {
                        showAlert(Alert.AlertType.ERROR, "Erreur",
                                "La date de péremption doit être après la date de fabrication");
                        return false;
                    }
                }

                // Avertir si le produit est déjà périmé
                if (datePer.isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.WARNING, "Attention",
                            "Ce produit est déjà périmé !");
                    // On continue quand même (juste un avertissement)
                }
            }

            // Validation date garantie
            if (p.getDate_garantie() != null && !p.getDate_garantie().isEmpty()) {
                LocalDate dateGar = LocalDate.parse(p.getDate_garantie());
                if (dateGar.isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.WARNING, "Attention",
                            "La garantie de ce produit est déjà expirée !");
                    // On continue quand même (juste un avertissement)
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur de validation des dates: " + e.getMessage());
            return false;
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

        // La date de création est toujours définie (soit par le système, soit par la modification)
        if (dateCreationPicker.getValue() == null) {
            dateCreationPicker.setValue(LocalDate.now());
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