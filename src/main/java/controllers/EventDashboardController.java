package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import models.Evenement;
import models.enums.EventStatus;
import services.ServiceEvenemnet;

import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDashboardController {

    @FXML private Label totalLabel;
    @FXML private Label plannedLabel;
    @FXML private Label finishedLabel;
    @FXML private Label cancelledLabel;

    @FXML private FlowPane eventContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusCombo;

    private ServiceEvenemnet service = new ServiceEvenemnet();
    private List<Evenement> events = new ArrayList<>();

    @FXML
    public void initialize() {

        filterStatusCombo.getItems().add("Tous");
        for (EventStatus s : EventStatus.values()) {
            filterStatusCombo.getItems().add(s.name());
        }
        filterStatusCombo.setValue("Tous");

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
            eventContainer.getChildren().add(createEventCard(e));
        }
    }

    private VBox createEventCard(Evenement e) {
        VBox card = new VBox(12);
        card.getStyleClass().add("event-card");
        card.setPrefWidth(300);
        card.setPadding(new Insets(0, 0, 15, 0));
        card.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView();
        try {
            String imagePath = (e.getImageUrl() == null || e.getImageUrl().isEmpty())
                    ? "/images/placeholder.png" : e.getImageUrl();
            Image img = new Image(imagePath, 300, 180, true, true);
            imageView.setImage(img);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(300, 180);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageView.setClip(clip);

        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(0, 15, 0, 15));

        Label title = new Label(e.getTitre());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);

        Label desc = new Label(e.getDescription());
        desc.getStyleClass().add("card-description");
        desc.setWrapText(true);

        Label dateLieu = new Label(e.getDate_event() + " | " + e.getLieu());
        dateLieu.getStyleClass().add("card-details");

        Label status = new Label(e.getStatut().name().toUpperCase());
        status.getStyleClass().addAll("status-badge", "status-" + e.getStatut().name().toLowerCase());

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button modifyBtn = new Button("Modifier");
        Button archiveBtn = new Button("Archiver");
        modifyBtn.getStyleClass().add("btn-modify-card");
        archiveBtn.getStyleClass().add("btn-archive-card");

        modifyBtn.setOnAction(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyEvent.fxml"));
                Parent root = loader.load();
                ModifyEventController controller = loader.getController();
                controller.setEvent(e);

                eventContainer.getScene().setRoot(root);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        archiveBtn.setOnAction(ev -> {
            service.archiver(e.getId());
            events = service.getAll();
            updateCounters();
            displayEvents(events);
        });

        actions.getChildren().addAll(modifyBtn, archiveBtn);
        infoBox.getChildren().addAll(title, desc, dateLieu, status, actions);
        card.getChildren().addAll(imageView, infoBox);

        return card;
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
        String query = searchField.getText();
        String statusFilter = filterStatusCombo.getValue();

        List<Evenement> results = service.getAll().stream()
                .filter(e -> e.getTitre().toLowerCase().contains(query))
                .filter(e -> statusFilter == null || statusFilter.equals("Tous") || e.getStatut().name().equals(statusFilter))
                .toList();

        displayEvents(results);
    }



    @FXML
    private void handleFilter() {
        String selected = filterStatusCombo.getValue();

        if (selected == null || selected.equals("Tous")) {
            events = service.getAll();
        } else {
            EventStatus status = EventStatus.valueOf(selected);
            events = service.filterByStatus(status);
        }

        displayEvents(events);

    }
}