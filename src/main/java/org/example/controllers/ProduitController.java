package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.produit;
import service.export.ExportExcelService;
import service.service_produit;

import service.export.ExportPDFService;

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
    @FXML
    private TextField searchField;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnClearSearch;

    private service_produit serviceProduit = new service_produit();
    private ObservableList<produit> produitsList = FXCollections.observableArrayList();
    private FilteredList<produit> filteredData;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur principal avec ListView...");

        debugRessourcePath();
        configurerAffichageProduits();
        chargerProduits();
        configurerRecherche();
        mettreAJourStatistiques();
    }

    private void debugRessourcePath() {
        System.out.println("=== DÉBOGAGE CHEMIN RESSOURCE ===");
        System.out.println("Resource /fxml/Ressource.fxml: " + getClass().getResource("/fxml/Ressource.fxml"));
        System.out.println("==================================");
    }

    private void configurerRecherche() {
        filteredData = new FilteredList<>(produitsList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerProduits(newValue);
        });

        btnSearch.setOnAction(e -> filtrerProduits(searchField.getText()));
        btnClearSearch.setOnAction(e -> {
            searchField.clear();
            filtrerProduits("");
        });

        listViewProduits.setItems(filteredData);
    }

    private void filtrerProduits(String texteRecherche) {
        filteredData.setPredicate(produit -> {
            if (texteRecherche == null || texteRecherche.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = texteRecherche.toLowerCase();

            if (produit.getNom() != null && produit.getNom().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            if (produit.getDescription() != null && produit.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            if (produit.getCategorie() != null && produit.getCategorie().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });

        mettreAJourStatistiquesFiltrees();
    }

    private void mettreAJourStatistiquesFiltrees() {
        int total = filteredData.size();
        int stockFaible = 0;
        int produitsPerimes = 0;
        int produitsBientotPerimes = 0;
        double valeurStock = 0;

        for (produit p : filteredData) {
            if (p.getStock_actuel() <= p.getStock_min()) stockFaible++;
            if (p.estPerime()) produitsPerimes++;
            if (p.estBientotPerime(30)) produitsBientotPerimes++;
            valeurStock += p.getPrix() * p.getStock_actuel();
        }

        if (searchField.getText() != null && !searchField.getText().isEmpty()) {
            statsTotal.setText("Résultats: " + total + " (sur " + produitsList.size() + ")");
        } else {
            statsTotal.setText("Total produits: " + total);
        }

        statsStockFaible.setText("Stock faible: " + stockFaible);
        statsValeurStock.setText(String.format("Valeur stock: %.2f DT | Périmés: %d | Bientôt: %d",
                valeurStock, produitsPerimes, produitsBientotPerimes));
    }

    private void configurerAffichageProduits() {
        listViewProduits.setCellFactory(param -> new ListCell<produit>() {
            private final ImageView imageView = new ImageView();
            private final GridPane gridPane = new GridPane();
            private final VBox contentBox = new VBox(5);
            private final HBox cellBox = new HBox(15);

            private final Label nomAttribut = new Label("Nom:");
            private final Label categorieAttribut = new Label("Catégorie:");
            private final Label prixAttribut = new Label("Prix:");
            private final Label stockAttribut = new Label("Stock:");
            private final Label dateCreationAttribut = new Label("Date création:");
            private final Label dateFabricationAttribut = new Label("Date fabrication:");
            private final Label datePeremptionAttribut = new Label("Date péremption:");
            private final Label dateGarantieAttribut = new Label("Garantie:");
            private final Label ressourcesAttribut = new Label("Ressources:");

            private final Label nomValeur = new Label();
            private final Label descriptionValeur = new Label();
            private final Label categorieValeur = new Label();
            private final Label prixValeur = new Label();
            private final Label stockValeur = new Label();
            private final Label dateCreationValeur = new Label();
            private final Label dateFabricationValeur = new Label();
            private final Label datePeremptionValeur = new Label();
            private final Label dateGarantieValeur = new Label();
            private final Label ressourcesValeur = new Label();
            private final Label statutLabel = new Label();

            {
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5;");

                String attributStyle = "-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12px;";
                nomAttribut.setStyle(attributStyle);
                categorieAttribut.setStyle(attributStyle);
                prixAttribut.setStyle(attributStyle);
                stockAttribut.setStyle(attributStyle);
                dateCreationAttribut.setStyle(attributStyle);
                dateFabricationAttribut.setStyle(attributStyle);
                datePeremptionAttribut.setStyle(attributStyle);
                dateGarantieAttribut.setStyle(attributStyle);
                ressourcesAttribut.setStyle(attributStyle);

                String valeurStyle = "-fx-text-fill: #27ae60; -fx-font-size: 12px;";
                nomValeur.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
                descriptionValeur.setStyle("-fx-text-fill: #666; -fx-font-size: 11px; -fx-font-style: italic;");
                categorieValeur.setStyle(valeurStyle);
                prixValeur.setStyle(valeurStyle);
                dateCreationValeur.setStyle(valeurStyle);
                dateFabricationValeur.setStyle(valeurStyle);
                datePeremptionValeur.setStyle(valeurStyle);
                dateGarantieValeur.setStyle(valeurStyle);
                ressourcesValeur.setStyle(valeurStyle);

                descriptionValeur.setWrapText(true);
                descriptionValeur.setMaxWidth(300);

                gridPane.setHgap(10);
                gridPane.setVgap(5);
                gridPane.setPadding(new Insets(5));

                int row = 0;
                gridPane.add(nomAttribut, 0, row);
                gridPane.add(nomValeur, 1, row);
                row++;
                gridPane.add(descriptionValeur, 0, row, 2, 1);
                row++;
                gridPane.add(categorieAttribut, 0, row);
                gridPane.add(categorieValeur, 1, row);
                row++;
                gridPane.add(prixAttribut, 0, row);
                gridPane.add(prixValeur, 1, row);
                row++;
                gridPane.add(stockAttribut, 0, row);
                gridPane.add(stockValeur, 1, row);
                row++;
                gridPane.add(dateCreationAttribut, 0, row);
                gridPane.add(dateCreationValeur, 1, row);
                row++;
                gridPane.add(dateFabricationAttribut, 0, row);
                gridPane.add(dateFabricationValeur, 1, row);
                row++;
                gridPane.add(datePeremptionAttribut, 0, row);
                gridPane.add(datePeremptionValeur, 1, row);
                row++;
                gridPane.add(dateGarantieAttribut, 0, row);
                gridPane.add(dateGarantieValeur, 1, row);
                row++;
                gridPane.add(ressourcesAttribut, 0, row);
                gridPane.add(ressourcesValeur, 1, row);

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
                    nomValeur.setText(produit.getNom());
                    String description = produit.getDescription();
                    if (description != null && description.length() > 80) {
                        description = description.substring(0, 77) + "...";
                    }
                    descriptionValeur.setText(description != null ? description : "Pas de description");
                    categorieValeur.setText(produit.getCategorie() != null ? produit.getCategorie() : "Non spécifiée");
                    prixValeur.setText(String.format("%.2f DT", produit.getPrix()));

                    String stockText = String.format("%d (min: %d)", produit.getStock_actuel(), produit.getStock_min());
                    stockValeur.setText(stockText);
                    if (produit.getStock_actuel() <= produit.getStock_min()) {
                        stockValeur.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
                    } else {
                        stockValeur.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12px;");
                    }

                    dateCreationValeur.setText(produit.getDate_creation() != null ? produit.getDate_creation() : "Non spécifiée");
                    dateFabricationValeur.setText(produit.getDate_fabrication() != null ? produit.getDate_fabrication() : "Non spécifiée");

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

                    String ressources = produit.getRessources_necessaires();
                    if (ressources != null && ressources.length() > 50) {
                        ressources = ressources.substring(0, 47) + "...";
                    }
                    ressourcesValeur.setText(ressources != null ? ressources : "Aucune");

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

                    chargerImageProduit(produit, imageView);
                    ajouterTooltipImage(imageView, produit);
                    setGraphic(cellBox);
                }
            }
        });
    }

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
                setDefaultImage(imageView);
            }
        } else {
            setDefaultImage(imageView);
        }
    }

    private void setDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            imageView.setImage(null);
            imageView.setStyle("-fx-background-color: #f0f0f0;");
        }
    }

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
                } catch (Exception e) {}
            }
        });
        imageView.setOnMouseExited(event -> tooltip.hide());
    }

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

    private void mettreAJourStatistiques() {
        int total = produitsList.size();
        int stockFaible = 0;
        int produitsPerimes = 0;
        int produitsBientotPerimes = 0;
        double valeurStock = 0;

        for (produit p : produitsList) {
            if (p.getStock_actuel() <= p.getStock_min()) stockFaible++;
            if (p.estPerime()) produitsPerimes++;
            if (p.estBientotPerime(30)) produitsBientotPerimes++;
            valeurStock += p.getPrix() * p.getStock_actuel();
        }

        statsTotal.setText("Total produits: " + total);
        statsStockFaible.setText("Stock faible: " + stockFaible);
        statsValeurStock.setText(String.format("Valeur stock: %.2f DT | Périmés: %d | Bientôt: %d",
                valeurStock, produitsPerimes, produitsBientotPerimes));
    }

    @FXML
    private void ouvrirAjoutProduit() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ajouterProduit.fxml");
            if (fxmlUrl == null) fxmlUrl = getClass().getResource("/ajouterProduit.fxml");
            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier ajouterProduit.fxml introuvable!");
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
        }
    }

    @FXML
    private void ouvrirModifierProduit() {
        produit selected = listViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à modifier");
            return;
        }
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ajouterProduit.fxml");
            if (fxmlUrl == null) fxmlUrl = getClass().getResource("/ajouterProduit.fxml");
            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier ajouterProduit.fxml introuvable!");
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
        }
    }

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
                if (selected.getImage_path() != null && !selected.getImage_path().isEmpty()) {
                    File imageFile = new File(selected.getImage_path());
                    if (imageFile.exists()) imageFile.delete();
                }
                serviceProduit.delete(selected);
                rafraichirListe();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le produit: " + e.getMessage());
            }
        }
    }

    @FXML
    private void rafraichirListe() {
        chargerProduits();
        filtrerProduits(searchField.getText());
        mettreAJourStatistiques();
    }

    @FXML
    private void naviguerRessources() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/Ressource.fxml");
            if (fxmlUrl == null) fxmlUrl = getClass().getResource("/Ressource.fxml");
            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier Ressource.fxml introuvable!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Stage stage = (Stage) listViewProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des ressources");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer: " + e.getMessage());
        }
    }

    @FXML private void naviguerEmployes() { System.out.println("Navigation vers Employés"); }
    @FXML private void naviguerProjets() { System.out.println("Navigation vers Projets"); }
    @FXML private void naviguerTaches() { System.out.println("Navigation vers Tâches"); }
    @FXML private void naviguerProduits() { System.out.println("Déjà sur Produits"); }
    @FXML private void naviguerPrevoyance() { System.out.println("Navigation vers Prévoyance"); }
    @FXML private void naviguerAnalyse() { System.out.println("Navigation vers Analyse"); }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== FONCTIONS D'EXPORT ====================

    @FXML
    private void exporterProduitsExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
            );
            fileChooser.setInitialFileName("produits.xlsx");

            File file = fileChooser.showSaveDialog(listViewProduits.getScene().getWindow());

            if (file != null) {
                ExportExcelService.exporterProduitsVersExcel(produitsList, file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Export Excel réussi !\nFichier : " + file.getName());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'export Excel : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exporterProduitsPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            fileChooser.setInitialFileName("produits.pdf");

            File file = fileChooser.showSaveDialog(listViewProduits.getScene().getWindow());

            if (file != null) {
                ExportPDFService.exporterProduitsVersPDF(produitsList, file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Export PDF réussi !\nFichier : " + file.getName());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'export PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirFrontOffice() {
        try {
            // Charger la vue Front Office des produits
            URL fxmlUrl = getClass().getResource("/fxml/front/frontProduits.fxml");
            if (fxmlUrl == null) {
                // Essayer d'autres chemins possibles
                fxmlUrl = getClass().getResource("/front/frontProduits.fxml");
            }

            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier frontProduits.fxml introuvable!\n" +
                                "Vérifiez qu'il est dans src/main/resources/fxml/front/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Créer une nouvelle fenêtre pour le Front Office
            Stage frontStage = new Stage();
            frontStage.setTitle("Front Office - Consultation");
            frontStage.setScene(new Scene(root));
            frontStage.setMaximized(true); // Ouvrir en plein écran

            // Optionnel : rendre la fenêtre modale (bloque la fenêtre principale)
            // frontStage.initModality(Modality.WINDOW_MODAL);
            // frontStage.initOwner(listViewProduits.getScene().getWindow());

            frontStage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le Front Office: " + e.getMessage());
            e.printStackTrace();
        }
    }
}