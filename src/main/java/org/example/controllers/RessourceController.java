package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private ListView<ressource> listViewRessources;
    @FXML
    private Label statsTotal;
    @FXML
    private Label statsQuantiteTotale;
    @FXML
    private Label statsTypes;

    // Nouveaux champs pour la recherche
    @FXML
    private TextField searchField;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnClearSearch;

    private service_ressource serviceRessource = new service_ressource();
    private ObservableList<ressource> observableList = FXCollections.observableArrayList();
    private FilteredList<ressource> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur des ressources avec ListView...");

        // Configuration du ListView avec des cellules personnalisées
        listViewRessources.setCellFactory(param -> new RessourceListCell());

        // Permettre la sélection
        listViewRessources.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Charger les données
        chargerDonnees();

        // Configurer la recherche
        configurerRecherche();
    }

    private void configurerRecherche() {
        filteredData = new FilteredList<>(observableList, p -> true);

        // Configurer le listener sur le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerRessources(newValue);
        });

        // Bouton de recherche
        btnSearch.setOnAction(e -> filtrerRessources(searchField.getText()));

        // Bouton pour effacer la recherche
        btnClearSearch.setOnAction(e -> {
            searchField.clear();
            filtrerRessources("");
        });

        // Lier la ListView à la liste filtrée
        listViewRessources.setItems(filteredData);
    }

    private void filtrerRessources(String texteRecherche) {
        filteredData.setPredicate(ressource -> {
            // Si le champ de recherche est vide, afficher toutes les ressources
            if (texteRecherche == null || texteRecherche.isEmpty()) {
                return true;
            }

            // Convertir en minuscules pour une recherche insensible à la casse
            String lowerCaseFilter = texteRecherche.toLowerCase();

            // Recherche par nom
            if (ressource.getNom() != null && ressource.getNom().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            // Recherche par type
            if (ressource.getType_ressource() != null &&
                    ressource.getType_ressource().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            // Recherche par fournisseur
            if (ressource.getFournisseur() != null &&
                    ressource.getFournisseur().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            return false;
        });

        // Mettre à jour les statistiques avec les résultats filtrés
        mettreAJourStatistiquesFiltrees();
    }

    private void mettreAJourStatistiquesFiltrees() {
        int total = filteredData.size();

        int quantiteTotale = filteredData.stream()
                .mapToInt(ressource::getQuatite)
                .sum();

        long typesUniques = filteredData.stream()
                .map(ressource::getType_ressource)
                .distinct()
                .count();

        // Indiquer si on est en mode recherche
        if (searchField.getText() != null && !searchField.getText().isEmpty()) {
            statsTotal.setText("Résultats: " + total + " (sur " + observableList.size() + ")");
        } else {
            statsTotal.setText("Total ressources: " + total);
        }

        statsQuantiteTotale.setText("Quantité totale: " + quantiteTotale);
        statsTypes.setText("Types: " + typesUniques);
    }

    private void chargerDonnees() {
        List<ressource> ressources = serviceRessource.getAll();
        observableList.setAll(ressources);
        // Réappliquer le filtre après rechargement
        if (filteredData != null) {
            filtrerRessources(searchField != null ? searchField.getText() : "");
        } else {
            listViewRessources.setItems(observableList);
        }
        mettreAJourStatistiques();
    }

    private void mettreAJourStatistiques() {
        int total = observableList.size();
        int quantiteTotale = observableList.stream()
                .mapToInt(ressource::getQuatite)
                .sum();
        long typesUniques = observableList.stream()
                .map(ressource::getType_ressource)
                .distinct()
                .count();

        statsTotal.setText("Total ressources: " + total);
        statsQuantiteTotale.setText("Quantité totale: " + quantiteTotale);
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
        ressource selected = listViewRessources.getSelectionModel().getSelectedItem();
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
        ressource selected = listViewRessources.getSelectionModel().getSelectedItem();
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
            Stage stage = (Stage) listViewRessources.getScene().getWindow();
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