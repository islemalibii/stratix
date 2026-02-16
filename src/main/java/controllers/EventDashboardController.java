package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.Evenement;
import models.enums.EventStatus;
import services.ServiceEvenemnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDashboardController {

    @FXML private Label totalLabel;
    @FXML private Label plannedLabel;
    @FXML private Label finishedLabel;
    @FXML private Label cancelledLabel;

    @FXML private VBox eventContainer;
    @FXML private TextField searchField;

    private ServiceEvenemnet service = new ServiceEvenemnet();
    private List<Evenement> events = new ArrayList<>();

    @FXML
    public void initialize() {
        events = service.getAll();
        updateCounters();
        displayEvents(events);
        search();
    }

    private void updateCounters() {
        long planned = events.stream()
                .filter(e -> e.getStatut() == EventStatus.planifier)
                .count();
        long finished = events.stream()
                .filter(e -> e.getStatut() == EventStatus.terminer)
                .count();
        long cancelled = events.stream()
                .filter(e -> e.getStatut() == EventStatus.annuler)
                .count();

        totalLabel.setText(String.valueOf(events.size()));
        plannedLabel.setText(String.valueOf(planned));
        finishedLabel.setText(String.valueOf(finished));
        cancelledLabel.setText(String.valueOf(cancelled));
    }

    private void displayEvents(List<Evenement> list) {
        eventContainer.getChildren().clear();
        for (Evenement e : list) {
            eventContainer.getChildren().add(createEventRow(e));
        }
    }

    private HBox createEventRow(Evenement e) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 25, 10, 25));
        row.setPrefHeight(80);
        row.getStyleClass().add("event-row");

        Label title = new Label(e.getTitre());
        title.setPrefWidth(150);
        title.getStyleClass().add("event-title");

        Label description = new Label(e.getDescription());
        description.setPrefWidth(200);
        description.getStyleClass().add("event-description");
        description.setWrapText(true);

        Label date = new Label(String.valueOf(e.getDate_event()));
        date.setPrefWidth(100);
        date.getStyleClass().add("event-details");

        Label type = new Label(String.valueOf(e.getType_event()));
        type.setPrefWidth(100);
        type.getStyleClass().add("event-details");

        Label status = new Label(String.valueOf(e.getStatut()).toUpperCase());
        status.setPrefWidth(100);
        if (e.getStatut() == EventStatus.planifier) {
            status.getStyleClass().add("status-planned");
        } else if (e.getStatut() == EventStatus.terminer) {
            status.getStyleClass().add("status-finished");
        } else {
            status.getStyleClass().add("status-cancelled");
        }

        Label lieu = new Label(e.getLieu());
        lieu.setPrefWidth(150);
        lieu.getStyleClass().add("event-details");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button modifyBtn = new Button("Modifier");
        Button archiveBtn = new Button("Archiver");
        modifyBtn.getStyleClass().add("btn-modify");
        archiveBtn.getStyleClass().add("btn-archive");
        modifyBtn.setPrefWidth(90);
        archiveBtn.setPrefWidth(90);

        modifyBtn.setOnAction(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyEvent.fxml"));
                Parent root = loader.load();
                ModifyEventController controller = loader.getController();
                controller.setEvent(e);
                eventContainer.getScene().setRoot(root);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        archiveBtn.setOnAction(ev -> {
            service.archiver(e.getId());
            events.remove(e);
            displayEvents(events);
            updateCounters();
        });

        HBox actions = new HBox(10, modifyBtn, archiveBtn);
        actions.setAlignment(Pos.CENTER);
        row.getChildren().addAll(title, description, date, type, status, lieu, spacer, actions);

        return row;
    }

    @FXML
    private void goToAddEvent() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/addEvent.fxml"));
            eventContainer.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void goToArchived() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ArchivedEvents.fxml"));

            eventContainer.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    @FXML
    private void search() {
        String s = searchField.getText().toLowerCase();

        List<Evenement> filtered = events.stream()
                .filter(e -> e.getTitre().toLowerCase().contains(s))
                .toList();

        displayEvents(filtered);
    }
}