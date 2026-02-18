package controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.Evenement;
import services.ServiceEvenemnet;

import java.util.List;
public class ArchivedEventsController {
    @FXML
    private VBox archiveContainer;

    private ServiceEvenemnet service = new ServiceEvenemnet();

    @FXML
    public void initialize() {
        loadArchivedEvents();
    }

    private void loadArchivedEvents() {
        List<Evenement> archived = service.getAllArchieved();

        archiveContainer.getChildren().clear();

        for (Evenement e : archived) {
            archiveContainer.getChildren().add(createRow(e));
        }
    }

    private HBox createRow(Evenement e) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 25, 10, 25));
        row.setPrefHeight(70);

        row.getStyleClass().add("archive-row");

        Label title = new Label(e.getTitre());
        title.setPrefWidth(150);
        title.getStyleClass().add("archive-title");

        Label description = new Label(e.getDescription());
        description.setPrefWidth(200);
        description.getStyleClass().add("archive-description");

        Label date = new Label(e.getDate_event().toString());
        date.setPrefWidth(100);
        date.getStyleClass().add("archive-text");

        Label type = new Label(e.getType_event().name());
        type.setPrefWidth(100);
        type.getStyleClass().add("archive-text");

        Label status = new Label(e.getStatut().name().toUpperCase());
        status.setPrefWidth(100);
        status.getStyleClass().add("archive-status-label");

        Label lieu = new Label(e.getLieu());
        lieu.setPrefWidth(150);
        lieu.getStyleClass().add("archive-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button restoreBtn = new Button("Restaurer");
        restoreBtn.getStyleClass().add("btn-restore");
        restoreBtn.setOnAction(event -> {
            service.desarchiver(e.getId()); // Appelle votre nouvelle méthode
            loadArchivedEvents(); // Rafraîchit la liste
        });

        // Bouton Supprimer (Définitivement)
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-delete-permanent");

        deleteBtn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous supprimer cet événement ?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().get() == ButtonType.YES) {
                service.supprimer(e.getId());
                loadArchivedEvents();
            }
        });

        HBox actionBox = new HBox(10, restoreBtn, deleteBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(title, description, date, type, status, lieu, spacer, actionBox);

        return row;
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/EventDashboard.fxml"));
            archiveContainer.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}