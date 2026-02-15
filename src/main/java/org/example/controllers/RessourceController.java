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
import models.ressource;
import service.service_ressource;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RessourceController implements Initializable {

    @FXML
    private TableView<ressource> tableViewRessources;
    @FXML
    private TableColumn<ressource, Integer> colId;
    @FXML
    private TableColumn<ressource, String> colNom;
    @FXML
    private TableColumn<ressource, String> colType;
    @FXML
    private TableColumn<ressource, Integer> colQuantite;
    @FXML
    private TableColumn<ressource, String> colFournisseur;

    @FXML
    private Label statsTotal;
    @FXML
    private Label statsQuantiteTotale;
    @FXML
    private Label statsTypes;

    private service_ressource serviceRessource = new service_ressource();
    private ObservableList<ressource> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur des ressources...");

        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type_ressource"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quatite"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));

        // Charger les données
        chargerDonnees();
    }

    private void chargerDonnees() {
        List<ressource> ressources = serviceRessource.getAll();
        observableList.setAll(ressources);
        tableViewRessources.setItems(observableList);
        mettreAJourStatistiques();
    }

    private void mettreAJourStatistiques() {
        int total = observableList.size();
        statsTotal.setText("Total ressources: " + total);

        int quantiteTotale = observableList.stream()
                .mapToInt(ressource::getQuatite)
                .sum();
        statsQuantiteTotale.setText("Quantité totale: " + quantiteTotale);

        long typesUniques = observableList.stream()
                .map(ressource::getType_ressource)
                .distinct()
                .count();
        statsTypes.setText("Types: " + typesUniques);
    }

    @FXML
    private void ouvrirAjoutRessource() {
        try {
            System.out.println("Ouverture du formulaire d'ajout de ressource...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterRessource.fxml"));
            Parent root = loader.load();

            FormulaireRessourceController controller = loader.getController();
            controller.setModeAjout();
            controller.setOnRessourceAjoutee(this::chargerDonnees);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une ressource");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void ouvrirModifierRessource() {
        ressource selected = tableViewRessources.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une ressource à modifier");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterRessource.fxml"));
            Parent root = loader.load();

            FormulaireRessourceController controller = loader.getController();
            controller.setModeModification(selected);
            controller.setOnRessourceAjoutee(this::chargerDonnees);

            Stage stage = new Stage();
            stage.setTitle("Modifier une ressource");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    private void supprimerRessource() {
        ressource selected = tableViewRessources.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une ressource à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la ressource");
        confirm.setContentText("Voulez-vous vraiment supprimer " + selected.getNom() + " ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceRessource.delete(selected);
            chargerDonnees();
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Ressource supprimée avec succès !");
        }
    }

    @FXML
    private void rafraichirListe() {
        chargerDonnees();
        showAlert(Alert.AlertType.INFORMATION, "Info", "Liste rafraîchie !");
    }

    @FXML
    private void retourMenuPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/produit.fxml"));
            Stage stage = (Stage) tableViewRessources.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Produits");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}