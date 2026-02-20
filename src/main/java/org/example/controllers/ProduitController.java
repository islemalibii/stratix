package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.produit;
import service.service_produit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class ProduitController {

    @FXML
    private ListView<produit> listViewProduits;
    @FXML
    private Label statsTotal;
    @FXML
    private Label statsStockFaible;
    @FXML
    private Label statsValeurStock;

    private service_produit serviceProduit = new service_produit();
    private ObservableList<produit> produitsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur principal avec ListView...");

        // Configuration de l'affichage personnalisé pour la ListView
        configurerAffichageProduits();

        // Charger les données
        chargerProduits();

        // Mettre à jour les statistiques
        mettreAJourStatistiques();
    }

    /**
     * Configure l'affichage personnalisé des produits dans la ListView
     */
    private void configurerAffichageProduits() {
        listViewProduits.setCellFactory(param -> new ListCell<produit>() {
            private final ImageView imageView = new ImageView();
            private final Label nomLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label prixLabel = new Label();
            private final Label stockLabel = new Label();
            private final VBox infoBox = new VBox(5);
            private final HBox cellBox = new HBox(15);

            {
                // Configuration de l'ImageView
                imageView.setFitHeight(60);
                imageView.setFitWidth(60);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5;");

                // Configuration des labels
                nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                descriptionLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(300);

                prixLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

                // Style du stock selon le niveau
                stockLabel.setStyle("-fx-font-size: 12px;");

                // Organisation des informations
                infoBox.getChildren().addAll(nomLabel, descriptionLabel, prixLabel, stockLabel);
                infoBox.setSpacing(3);

                cellBox.getChildren().addAll(imageView, infoBox);
                cellBox.setStyle("-fx-padding: 10; -fx-border-color: transparent transparent #ecf0f1 transparent;");
            }

            @Override
            protected void updateItem(produit produit, boolean empty) {
                super.updateItem(produit, empty);

                if (empty || produit == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Définir le nom
                    nomLabel.setText(produit.getNom());

                    // Description (limitée en longueur)
                    String description = produit.getDescription();
                    if (description != null && description.length() > 50) {
                        description = description.substring(0, 47) + "...";
                    }
                    descriptionLabel.setText(description != null ? description : "Pas de description");

                    // Prix formaté
                    prixLabel.setText(String.format("%.2f DT", produit.getPrix()));

                    // Stock avec couleur selon le niveau
                    String stockText = String.format("Stock: %d (min: %d)",
                            produit.getStock_actuel(), produit.getStock_min());
                    stockLabel.setText(stockText);

                    if (produit.getStock_actuel() <= produit.getStock_min()) {
                        stockLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        stockLabel.setStyle("-fx-text-fill: #27ae60;");
                    }

                    // Charger l'image
                    chargerImageProduit(produit, imageView);

                    // Ajouter le tooltip pour voir l'image en grand
                    ajouterTooltipImage(imageView, produit);

                    setGraphic(cellBox);
                }
            }
        });
    }

    /**
     * Charge l'image du produit dans l'ImageView
     */
    private void chargerImageProduit(produit produit, ImageView imageView) {
        String imagePath = produit.getImage_path();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), true);
                    imageView.setImage(image);
                } else {
                    setDefaultImage(imageView);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement image: " + e.getMessage());
                setDefaultImage(imageView);
            }
        } else {
            setDefaultImage(imageView);
        }
    }

    /**
     * Définit une image par défaut
     */
    private void setDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            imageView.setImage(null);
            imageView.setStyle("-fx-background-color: #f0f0f0;");
        }
    }

    /**
     * Ajoute un tooltip pour voir l'image en grand au survol
     */
    private void ajouterTooltipImage(ImageView imageView, produit produit) {
        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(new ImageView());
        tooltip.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-padding: 5;");

        imageView.setOnMouseEntered(event -> {
            if (produit.getImage_path() != null && !produit.getImage_path().isEmpty()) {
                try {
                    File imageFile = new File(produit.getImage_path());
                    if (imageFile.exists()) {
                        Image fullImage = new Image(imageFile.toURI().toString(), 200, 200, true, true);
                        ImageView fullImageView = new ImageView(fullImage);
                        fullImageView.setPreserveRatio(true);
                        tooltip.setGraphic(fullImageView);
                        tooltip.show(imageView, event.getScreenX() + 10, event.getScreenY() + 10);
                    }
                } catch (Exception e) {
                    // Ignorer
                }
            }
        });

        imageView.setOnMouseExited(event -> tooltip.hide());
        imageView.setOnMouseMoved(event -> {
            if (tooltip.isShowing()) {
                tooltip.hide();
            }
        });
    }

    /**
     * Charge tous les produits depuis la base de données
     */
    private void chargerProduits() {
        try {
            List<produit> produits = serviceProduit.getAll();
            produitsList.clear();
            produitsList.addAll(produits);
            listViewProduits.setItems(produitsList);
            System.out.println(produits.size() + " produits chargés");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }

    /**
     * Met à jour les statistiques affichées en bas
     */
    private void mettreAJourStatistiques() {
        int total = produitsList.size();
        int stockFaible = 0;
        double valeurStock = 0;

        for (produit p : produitsList) {
            if (p.getStock_actuel() <= p.getStock_min()) {
                stockFaible++;
            }
            valeurStock += p.getPrix() * p.getStock_actuel();
        }

        statsTotal.setText("Total produits: " + total);
        statsStockFaible.setText("Stock faible: " + stockFaible);
        statsValeurStock.setText(String.format("Valeur stock: %.2f DT", valeurStock));
    }

    /**
     * Ouvre le formulaire d'ajout de produit
     * CORRECTION: Utilise ajouterProduit.fxml au lieu de FormulaireProduit.fxml
     */
    @FXML
    private void ouvrirAjoutProduit() {
        try {
            // Charger le bon fichier FXML - ajouterProduit.fxml
            URL fxmlUrl = getClass().getResource("/fxml/ajouterProduit.fxml");
            if (fxmlUrl == null) {
                // Essayer sans le dossier fxml
                fxmlUrl = getClass().getResource("/ajouterProduit.fxml");
            }

            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier ajouterProduit.fxml introuvable!\n" +
                                "Vérifiez qu'il est dans src/main/resources/fxml/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            FormulaireProduitController controller = loader.getController();
            controller.setModeAjout();
            controller.setOnProduitAjoute(this::rafraichirListe);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ouvre le formulaire de modification de produit
     * CORRECTION: Utilise ajouterProduit.fxml au lieu de FormulaireProduit.fxml
     */
    @FXML
    private void ouvrirModifierProduit() {
        produit selected = listViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à modifier");
            return;
        }

        try {
            // Charger le bon fichier FXML - ajouterProduit.fxml
            URL fxmlUrl = getClass().getResource("/fxml/ajouterProduit.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/ajouterProduit.fxml");
            }

            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier ajouterProduit.fxml introuvable!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            FormulaireProduitController controller = loader.getController();
            controller.setModeModification(selected);
            controller.setOnProduitAjoute(this::rafraichirListe);

            Stage stage = new Stage();
            stage.setTitle("Modifier un produit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime un produit sélectionné
     */
    @FXML
    private void supprimerProduit() {
        produit selected = listViewProduits.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer le produit");
        confirm.setContentText("Voulez-vous vraiment supprimer le produit \"" + selected.getNom() + "\" ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'image associée si elle existe
                if (selected.getImage_path() != null && !selected.getImage_path().isEmpty()) {
                    File imageFile = new File(selected.getImage_path());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }

                // Supprimer le produit
                serviceProduit.delete(selected);

                rafraichirListe();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Produit supprimé avec succès");

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de supprimer le produit: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void rafraichirListe() {
        chargerProduits();
        mettreAJourStatistiques();
    }

    // Méthodes de navigation
    @FXML
    private void naviguerEmployes() {
        System.out.println("Navigation vers Employés");
    }

    @FXML
    private void naviguerProjets() {
        System.out.println("Navigation vers Projets");
    }

    @FXML
    private void naviguerTaches() {
        System.out.println("Navigation vers Tâches");
    }

    @FXML
    private void naviguerProduits() {
        System.out.println("Déjà sur Produits");
    }

    @FXML
    private void naviguerRessources() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Ressource.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) listViewProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des ressources");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer: " + e.getMessage());
        }
    }

    @FXML
    private void naviguerPrevoyance() {
        System.out.println("Navigation vers Prévoyance");
    }

    @FXML
    private void naviguerAnalyse() {
        System.out.println("Navigation vers Analyse");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}