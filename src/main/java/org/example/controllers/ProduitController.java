package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.produit;
import service.service_produit;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {

    // ================= COMPOSANTS TABLEVIEW =================
    @FXML
    private TableView<produit> tableViewProduits;
    @FXML
    private TableColumn<produit, Integer> colId;
    @FXML
    private TableColumn<produit, String> colNom;
    @FXML
    private TableColumn<produit, String> colDescription;
    @FXML
    private TableColumn<produit, String> colCategorie;
    @FXML
    private TableColumn<produit, Double> colPrix;
    @FXML
    private TableColumn<produit, Integer> colStockActuel;
    @FXML
    private TableColumn<produit, Integer> colStockMin;
    @FXML
    private TableColumn<produit, String> colDateCreation;
    @FXML
    private TableColumn<produit, String> colRessources;

    // ================= COMPOSANTS STATISTIQUES =================
    @FXML
    private Label statsTotal;
    @FXML
    private Label statsStockFaible;
    @FXML
    private Label statsValeurStock;

    // ================= SERVICES =================
    private service_produit serviceProduit = new service_produit();
    private ObservableList<produit> observableList = FXCollections.observableArrayList();

    // ================= INITIALISATION =================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur principal...");

        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStockActuel.setCellValueFactory(new PropertyValueFactory<>("stock_actuel"));
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stock_min"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("date_creation"));
        colRessources.setCellValueFactory(new PropertyValueFactory<>("ressources_necessaires"));

        // Formatage du prix
        colPrix.setCellFactory(tc -> new TableCell<produit, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.3f DT", price));
                }
            }
        });

        // Charger les données
        chargerDonnees();
    }

    // ================= CHARGEMENT DONNEES =================
    private void chargerDonnees() {
        List<produit> produits = serviceProduit.getAll();
        observableList.setAll(produits);
        tableViewProduits.setItems(observableList);
        mettreAJourStatistiques();
    }

    // ================= STATISTIQUES =================
    private void mettreAJourStatistiques() {
        int total = observableList.size();
        statsTotal.setText("Total produits: " + total);

        long stockFaible = observableList.stream()
                .filter(p -> p.getStock_actuel() < p.getStock_min())
                .count();
        statsStockFaible.setText("Stock faible: " + stockFaible);

        double valeurTotale = observableList.stream()
                .mapToDouble(p -> p.getPrix() * p.getStock_actuel())
                .sum();
        statsValeurStock.setText(String.format("Valeur stock: %.3f DT", valeurTotale));
    }

    // ================= ACTIONS SUR LES PRODUITS =================

    @FXML
    private void ouvrirAjoutProduit() {
        try {
            System.out.println("Ouverture du formulaire d'ajout...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterProduit.fxml"));
            Parent root = loader.load();

            FormulaireProduitController controller = loader.getController();
            controller.setModeAjout();
            controller.setOnProduitAjoute(this::chargerDonnees);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void ouvrirModifierProduit() {
        produit selected = tableViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un produit à modifier");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterProduit.fxml"));
            Parent root = loader.load();

            FormulaireProduitController controller = loader.getController();
            controller.setModeModification(selected);
            controller.setOnProduitAjoute(this::chargerDonnees);

            Stage stage = new Stage();
            stage.setTitle("Modifier un produit");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    private void supprimerProduit() {
        produit selected = tableViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le produit");
        confirm.setContentText("Voulez-vous vraiment supprimer " + selected.getNom() + " ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceProduit.delete(selected);
            chargerDonnees();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès !");
        }
    }

    @FXML
    private void rafraichirListe() {
        chargerDonnees();
        showAlert(Alert.AlertType.INFORMATION, "Info", "Liste rafraîchie !");
    }

    // ================= METHODES DE NAVIGATION =================
    @FXML private void naviguerTableauBord() { /* Navigation */ }
    @FXML private void naviguerEmployes() { /* Navigation */ }
    @FXML private void naviguerProjets() { /* Navigation */ }
    @FXML private void naviguerTaches() { /* Navigation */ }
    @FXML private void naviguerProduits() { /* Déjà sur la page */ }
    @FXML private void naviguerPrevoyance() { /* Navigation */ }
    @FXML private void naviguerAnalyse() { /* Navigation */ }

    // ================= METHODES UTILITAIRES =================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void naviguerRessources() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ressource.fxml"));
            Stage stage = (Stage) tableViewProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Ressources");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la gestion des ressources");
        }
    }
}