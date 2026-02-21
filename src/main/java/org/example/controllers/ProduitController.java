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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.produit;
import service.service_produit;

import javafx.geometry.Insets;
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
            private final GridPane gridPane = new GridPane();
            private final VBox contentBox = new VBox(5);
            private final HBox cellBox = new HBox(15);

            // Labels pour les noms d'attributs
            private final Label nomAttribut = new Label("Nom:");
            private final Label typeAttribut = new Label("Type:");
            private final Label categorieAttribut = new Label("Catégorie:");
            private final Label prixAttribut = new Label("Prix:");
            private final Label stockAttribut = new Label("Stock:");
            private final Label dateCreationAttribut = new Label("Date création:");
            private final Label dateFabricationAttribut = new Label("Date fabrication:");
            private final Label datePeremptionAttribut = new Label("Date péremption:");
            private final Label dateGarantieAttribut = new Label("Garantie:");
            private final Label ressourcesAttribut = new Label("Ressources:");

            // Labels pour les valeurs
            private final Label nomValeur = new Label();
            private final Label descriptionValeur = new Label();
            private final Label typeValeur = new Label();
            private final Label categorieValeur = new Label();
            private final Label prixValeur = new Label();
            private final Label stockValeur = new Label();
            private final Label dateCreationValeur = new Label();
            private final Label dateFabricationValeur = new Label();
            private final Label datePeremptionValeur = new Label();
            private final Label dateGarantieValeur = new Label();
            private final Label ressourcesValeur = new Label();

            // Label pour le statut
            private final Label statutLabel = new Label();

            {
                // Configuration de l'ImageView
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5;");

                // Style pour les noms d'attributs
                String attributStyle = "-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12px;";
                nomAttribut.setStyle(attributStyle);
                typeAttribut.setStyle(attributStyle);
                categorieAttribut.setStyle(attributStyle);
                prixAttribut.setStyle(attributStyle);
                stockAttribut.setStyle(attributStyle);
                dateCreationAttribut.setStyle(attributStyle);
                dateFabricationAttribut.setStyle(attributStyle);
                datePeremptionAttribut.setStyle(attributStyle);
                dateGarantieAttribut.setStyle(attributStyle);
                ressourcesAttribut.setStyle(attributStyle);

                // Style pour les valeurs
                String valeurStyle = "-fx-text-fill: #27ae60; -fx-font-size: 12px;";
                nomValeur.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
                descriptionValeur.setStyle("-fx-text-fill: #666; -fx-font-size: 11px; -fx-font-style: italic;");
                typeValeur.setStyle(valeurStyle);
                categorieValeur.setStyle(valeurStyle);
                prixValeur.setStyle(valeurStyle);
                dateCreationValeur.setStyle(valeurStyle);
                dateFabricationValeur.setStyle(valeurStyle);
                datePeremptionValeur.setStyle(valeurStyle);
                dateGarantieValeur.setStyle(valeurStyle);
                ressourcesValeur.setStyle(valeurStyle);

                statutLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                descriptionValeur.setWrapText(true);
                descriptionValeur.setMaxWidth(300);

                // Configuration du GridPane
                gridPane.setHgap(10);
                gridPane.setVgap(5);
                gridPane.setPadding(new Insets(5));

                // Ajout des composants au GridPane
                int row = 0;

                // Ligne 0: Nom
                gridPane.add(nomAttribut, 0, row);
                gridPane.add(nomValeur, 1, row);
                row++;

                // Ligne 1: Description (occupe 2 colonnes)
                gridPane.add(descriptionValeur, 0, row, 2, 1);
                row++;

                // Ligne 2: Type
                gridPane.add(typeAttribut, 0, row);
                gridPane.add(typeValeur, 1, row);
                row++;

                // Ligne 3: Catégorie
                gridPane.add(categorieAttribut, 0, row);
                gridPane.add(categorieValeur, 1, row);
                row++;

                // Ligne 4: Prix
                gridPane.add(prixAttribut, 0, row);
                gridPane.add(prixValeur, 1, row);
                row++;

                // Ligne 5: Stock
                gridPane.add(stockAttribut, 0, row);
                gridPane.add(stockValeur, 1, row);
                row++;

                // Ligne 6: Date création
                gridPane.add(dateCreationAttribut, 0, row);
                gridPane.add(dateCreationValeur, 1, row);
                row++;

                // Ligne 7: Date fabrication
                gridPane.add(dateFabricationAttribut, 0, row);
                gridPane.add(dateFabricationValeur, 1, row);
                row++;

                // Ligne 8: Date péremption
                gridPane.add(datePeremptionAttribut, 0, row);
                gridPane.add(datePeremptionValeur, 1, row);
                row++;

                // Ligne 9: Date garantie
                gridPane.add(dateGarantieAttribut, 0, row);
                gridPane.add(dateGarantieValeur, 1, row);
                row++;

                // Ligne 10: Ressources
                gridPane.add(ressourcesAttribut, 0, row);
                gridPane.add(ressourcesValeur, 1, row);
                row++;

                // Ajouter le statut dans une VBox séparée
                contentBox.getChildren().addAll(gridPane, statutLabel);
                contentBox.setSpacing(10);

                cellBox.getChildren().addAll(imageView, contentBox);
                cellBox.setStyle("-fx-padding: 15; -fx-border-color: transparent transparent #ecf0f1 transparent; -fx-background-color: white;");
                cellBox.setSpacing(20);
            }

            @Override
            protected void updateItem(produit produit, boolean empty) {
                super.updateItem(produit, empty);

                if (empty || produit == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Remplir les valeurs de base
                    nomValeur.setText(produit.getNom());

                    String description = produit.getDescription();
                    if (description != null && description.length() > 80) {
                        description = description.substring(0, 77) + "...";
                    }
                    descriptionValeur.setText(description != null ? description : "Pas de description");

                    typeValeur.setText(produit.getType_produit() != null ? produit.getType_produit() : "Non spécifié");
                    categorieValeur.setText(produit.getCategorie() != null ? produit.getCategorie() : "Non spécifiée");
                    prixValeur.setText(String.format("%.2f DT", produit.getPrix()));

                    // Stock avec couleur
                    String stockText = String.format("%d (min: %d)",
                            produit.getStock_actuel(), produit.getStock_min());
                    stockValeur.setText(stockText);

                    if (produit.getStock_actuel() <= produit.getStock_min()) {
                        stockValeur.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
                    } else {
                        stockValeur.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12px;");
                    }

                    // Dates
                    dateCreationValeur.setText(produit.getDate_creation() != null ? produit.getDate_creation() : "Non spécifiée");
                    dateFabricationValeur.setText(produit.getDate_fabrication() != null ? produit.getDate_fabrication() : "Non spécifiée");

                    // Date péremption avec statut
                    if (produit.getDate_peremption() != null) {
                        datePeremptionValeur.setText(produit.getDate_peremption());
                        if (produit.estPerime()) {
                            datePeremptionValeur.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else if (produit.estBientotPerime(30)) {
                            datePeremptionValeur.setStyle("-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else {
                            datePeremptionValeur.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                        }
                    } else {
                        datePeremptionValeur.setText("Non spécifiée");
                        datePeremptionValeur.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                    }

                    // Date garantie avec statut
                    if (produit.getDate_garantie() != null) {
                        dateGarantieValeur.setText(produit.getDate_garantie());
                        if (produit.garantieExpiree()) {
                            dateGarantieValeur.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else {
                            dateGarantieValeur.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
                        }
                    } else {
                        dateGarantieValeur.setText("Non spécifiée");
                        dateGarantieValeur.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                    }

                    // Ressources
                    String ressources = produit.getRessources_necessaires();
                    if (ressources != null && ressources.length() > 50) {
                        ressources = ressources.substring(0, 47) + "...";
                    }
                    ressourcesValeur.setText(ressources != null ? ressources : "Aucune");

                    // Statut global du produit
                    String statut = "";
                    String statutStyle = "";

                    if (produit.estPerime()) {
                        statut = "⚠ PRODUIT PÉRIMÉ ⚠";
                        statutStyle = "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 5; -fx-background-radius: 5;";
                    } else if (produit.estBientotPerime(30)) {
                        statut = "⚠ Bientôt périmé (moins de 30 jours)";
                        statutStyle = "-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00; -fx-padding: 5; -fx-background-radius: 5;";
                    } else if (produit.garantieExpiree()) {
                        statut = "⚠ Garantie expirée";
                        statutStyle = "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 5; -fx-background-radius: 5;";
                    } else if (produit.getStock_actuel() <= produit.getStock_min()) {
                        statut = "⚠ Stock faible";
                        statutStyle = "-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00; -fx-padding: 5; -fx-background-radius: 5;";
                    } else {
                        statut = "✓ Produit en bon état";
                        statutStyle = "-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32; -fx-padding: 5; -fx-background-radius: 5;";
                    }

                    statutLabel.setText(statut);
                    statutLabel.setStyle(statutStyle);
                    statutLabel.setMaxWidth(Double.MAX_VALUE);
                    statutLabel.setAlignment(javafx.geometry.Pos.CENTER);

                    // Charger l'image
                    chargerImageProduit(produit, imageView);

                    // Ajouter le tooltip pour l'image
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
        int produitsPerimes = 0;
        int produitsBientotPerimes = 0;
        double valeurStock = 0;

        for (produit p : produitsList) {
            if (p.getStock_actuel() <= p.getStock_min()) {
                stockFaible++;
            }
            if (p.estPerime()) {
                produitsPerimes++;
            }
            if (p.estBientotPerime(30)) {
                produitsBientotPerimes++;
            }
            valeurStock += p.getPrix() * p.getStock_actuel();
        }

        statsTotal.setText("Total produits: " + total);
        statsStockFaible.setText("Stock faible: " + stockFaible);

        String statsCompletes = String.format("Valeur stock: %.2f DT | Périmés: %d | Bientôt périmés: %d",
                valeurStock, produitsPerimes, produitsBientotPerimes);
        statsValeurStock.setText(statsCompletes);
    }

    /**
     * Ouvre le formulaire d'ajout de produit
     */
    @FXML
    private void ouvrirAjoutProduit() {
        try {
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
     */
    @FXML
    private void ouvrirModifierProduit() {
        produit selected = listViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à modifier");
            return;
        }

        try {
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

                serviceProduit.delete(selected);
                rafraichirListe();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès");

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