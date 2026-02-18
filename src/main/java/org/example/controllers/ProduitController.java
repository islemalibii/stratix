package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.produit;
import service.service_produit;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {

    // ================= LISTVIEW =================
    @FXML
    private ListView<produit> listViewProduits;

    // ================= STATISTIQUES =================
    @FXML
    private Label statsTotal;
    @FXML
    private Label statsStockFaible;
    @FXML
    private Label statsValeurStock;

    private service_produit serviceProduit = new service_produit();
    private ObservableList<produit> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur principal avec ListView...");

        // Configuration du ListView avec des cellules personnalisées
        listViewProduits.setCellFactory(param -> new ProduitListCell());

        // Permettre la sélection
        listViewProduits.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Charger les données
        chargerDonnees();
    }

    private void chargerDonnees() {
        List<produit> produits = serviceProduit.getAll();
        observableList.setAll(produits);
        listViewProduits.setItems(observableList);
        mettreAJourStatistiques();
    }

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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    private void ouvrirModifierProduit() {
        produit selected = listViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit");
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    private void supprimerProduit() {
        produit selected = listViewProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit");
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
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé !");
        }
    }

    @FXML
    private void rafraichirListe() {
        chargerDonnees();
        showAlert(Alert.AlertType.INFORMATION, "Info", "Liste rafraîchie !");
    }

    // ================= METHODES DE NAVIGATION (AJOUTÉES) =================

    @FXML
    private void naviguerTableauBord() {
        System.out.println("Navigation vers Tableau de bord");
        // Implémenter selon votre besoin
    }

    @FXML
    private void naviguerEmployes() {
        System.out.println("Navigation vers Employés");
        // Implémenter selon votre besoin
    }

    @FXML
    private void naviguerProjets() {
        System.out.println("Navigation vers Projets");
        // Implémenter selon votre besoin
    }

    @FXML
    private void naviguerTaches() {
        System.out.println("Navigation vers Tâches");
        // Implémenter selon votre besoin
    }

    @FXML
    private void naviguerProduits() {
        System.out.println("Déjà sur la page Produits");
        // Ne rien faire, on est déjà sur produits
    }

    @FXML
    private void naviguerRessources() {
        try {
            System.out.println("Navigation vers la gestion des ressources...");

            // Charger la page des ressources
            Parent root = FXMLLoader.load(getClass().getResource("/ressource.fxml"));

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) listViewProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Ressources");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la gestion des ressources: " + e.getMessage());
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