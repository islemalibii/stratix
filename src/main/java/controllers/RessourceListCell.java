package controllers;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.ressource;

public class RessourceListCell extends ListCell<ressource> {

    @Override
    protected void updateItem(ressource r, boolean empty) {
        super.updateItem(r, empty);

        if (empty || r == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Créer une carte pour chaque ressource
            VBox card = new VBox(10);
            card.setStyle("-fx-border-color: #3498db; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            card.setPrefWidth(450);

            // En-tête avec ID et Nom
            Label titleLabel = new Label(r.getid() + " - " + r.getNom());
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            titleLabel.setStyle("-fx-text-fill: #2c3e50;");

            // Type avec badge
            Label typeBadge = new Label(r.getType_ressource());
            typeBadge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 12;");

            // Grille pour les détails
            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(20);
            detailsGrid.setVgap(8);
            detailsGrid.setPadding(new Insets(10, 0, 10, 0));

            // Fournisseur
            detailsGrid.add(new Label("🏢 Fournisseur:"), 0, 0);
            Label fournisseurLabel = new Label(r.getFournisseur());
            fournisseurLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
            detailsGrid.add(fournisseurLabel, 1, 0);

            // Quantité
            detailsGrid.add(new Label("📦 Quantité:"), 2, 0);
            Label quantiteLabel = new Label(String.valueOf(r.getQuatite()));

            // Changer la couleur selon la quantité
            if (r.getQuatite() <= 0) {
                quantiteLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-font-size: 16;"); // Rouge (rupture)
            } else if (r.getQuatite() < 10) {
                quantiteLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #f39c12; -fx-font-size: 16;"); // Orange (stock faible)
            } else {
                quantiteLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 16;"); // Vert (stock OK)
            }
            detailsGrid.add(quantiteLabel, 3, 0);

            // Icône selon le type
            HBox headerBox = new HBox(10);
            Label iconLabel = new Label(getEmojiForType(r.getType_ressource()));
            iconLabel.setStyle("-fx-font-size: 24;");
            headerBox.getChildren().addAll(iconLabel, titleLabel, typeBadge);

            // Assembler la carte
            card.getChildren().addAll(headerBox, detailsGrid);

            // Ajouter une marge en bas
            VBox.setMargin(card, new Insets(0, 0, 10, 0));

            setGraphic(card);
        }
    }

    private String getEmojiForType(String type) {
        if (type == null) return "📦";

        String typeLower = type.toLowerCase();
        if (typeLower.contains("informatique") || typeLower.contains("ordinateur")) {
            return "💻";
        } else if (typeLower.contains("mobilier") || typeLower.contains("chaise") || typeLower.contains("bureau")) {
            return "🪑";
        } else if (typeLower.contains("fourniture") || typeLower.contains("papier")) {
            return "📝";
        } else if (typeLower.contains("équipement") || typeLower.contains("outil")) {
            return "🔧";
        } else if (typeLower.contains("matière") || typeLower.contains("matériau")) {
            return "⚙️";
        } else if (typeLower.contains("logiciel")) {
            return "💿";
        } else {
            return "📦";
        }
    }
}