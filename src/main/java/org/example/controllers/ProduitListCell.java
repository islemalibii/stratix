package org.example.controllers;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.produit;

public class ProduitListCell extends ListCell<produit> {

    @Override
    protected void updateItem(produit p, boolean empty) {
        super.updateItem(p, empty);

        if (empty || p == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Créer une carte pour chaque produit
            VBox card = new VBox(10);
            card.setStyle("-fx-border-color: #3498db; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            card.setPrefWidth(450);

            // En-tête avec ID et Nom
            Label titleLabel = new Label(p.getId() + " - " + p.getNom());
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            titleLabel.setStyle("-fx-text-fill: #2c3e50;");

            // Catégorie avec badge
            Label categorieBadge = new Label(p.getCategorie());
            categorieBadge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 12;");

            // Grille pour les détails
            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(15);
            detailsGrid.setVgap(8);
            detailsGrid.setPadding(new Insets(10, 0, 10, 0));

            // Prix
            detailsGrid.add(new Label("💰 Prix:"), 0, 0);
            Label prixLabel = new Label(String.format("%.3f DT", p.getPrix()));
            prixLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 16;");
            detailsGrid.add(prixLabel, 1, 0);

            // Stock
            detailsGrid.add(new Label("📦 Stock:"), 2, 0);
            Label stockLabel = new Label(p.getStock_actuel() + " unités");
            // Changer la couleur selon le stock
            if (p.getStock_actuel() < p.getStock_min()) {
                stockLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            } else {
                stockLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }
            detailsGrid.add(stockLabel, 3, 0);

            // Stock minimum
            detailsGrid.add(new Label("⚠️ Stock min:"), 0, 1);
            detailsGrid.add(new Label(String.valueOf(p.getStock_min())), 1, 1);

            // Date création
            detailsGrid.add(new Label("📅 Date:"), 2, 1);
            detailsGrid.add(new Label(p.getDate_creation()), 3, 1);

            // Description (si elle existe)
            VBox descriptionBox = new VBox(5);
            descriptionBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

            Label descLabel = new Label("📝 Description:");
            descLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

            Label descriptionText = new Label(p.getDescription());
            descriptionText.setWrapText(true);
            descriptionText.setStyle("-fx-text-fill: #34495e;");

            descriptionBox.getChildren().addAll(descLabel, descriptionText);

            // Ressources nécessaires (si elles existent)
            VBox ressourcesBox = null;
            if (p.getRessources_necessaires() != null && !p.getRessources_necessaires().isEmpty()) {
                ressourcesBox = new VBox(5);
                ressourcesBox.setStyle("-fx-background-color: #fff3cd; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ffeeba; -fx-border-radius: 5;");

                Label ressLabel = new Label("🔧 Ressources nécessaires:");
                ressLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

                Label ressourcesText = new Label(p.getRessources_necessaires());
                ressourcesText.setWrapText(true);

                ressourcesBox.getChildren().addAll(ressLabel, ressourcesText);
            }

            // Assembler la carte
            card.getChildren().addAll(titleLabel, categorieBadge, detailsGrid, descriptionBox);
            if (ressourcesBox != null) {
                card.getChildren().add(ressourcesBox);
            }

            // Ajouter une marge en bas de la carte
            VBox.setMargin(card, new Insets(0, 0, 10, 0));

            setGraphic(card);
        }
    }
}