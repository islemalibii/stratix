package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Evenement;
import models.enums.EventStatus;
import services.ServiceEvenemnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventEmployeeController {

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
        updateCounters(); // Même méthode
        displayEvents(events);
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
        if (finishedLabel != null) finishedLabel.setText(String.valueOf(finished));
        if (cancelledLabel != null) cancelledLabel.setText(String.valueOf(cancelled));
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


        infoBox.getChildren().addAll(title, desc, dateLieu, status);
        card.getChildren().addAll(imageView, infoBox);

        return card;
    }

    @FXML
    private void search() {
        String query = searchField.getText().toLowerCase();
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


    //temporaryyy
    @FXML
    private void switchToAdminView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/EventDashboard.fxml"));
            eventContainer.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}