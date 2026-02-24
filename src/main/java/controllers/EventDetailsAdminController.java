package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Evenement;
import models.EventFeedback;
import models.enums.EventStatus;
import Services.ServiceEvenemnet;
import Services.ServiceEventFeedback;

import java.io.IOException;
import java.util.List;

public class EventDetailsAdminController {

    @FXML private ImageView eventImageView;
    @FXML private Label titleLabel, dateLabel, locationLabel, typeLabel, descriptionLabel, statusTextLabel, statusBadge;
    @FXML private VBox feedbackContainer;

    private Evenement currentEvent;
    private ServiceEvenemnet service = new ServiceEvenemnet();

    public void setEventData(Evenement e) {
        this.currentEvent = e;

        titleLabel.setText(e.getTitre());
        descriptionLabel.setText(e.getDescription());
        dateLabel.setText(e.getDate_event().toString());
        locationLabel.setText(e.getLieu());

        typeLabel.setText(e.getType_event() != null ? e.getType_event().name() : "NON SPÉCIFIÉ");

        if (e.getStatut() != null) {
            String statusName = e.getStatut().name().toUpperCase();
            statusTextLabel.setText(statusName);
            statusBadge.setText(statusName);

            statusBadge.getStyleClass().removeAll("status-planifier", "status-termine", "status-annule");
            statusBadge.getStyleClass().add("status-" + e.getStatut().name().toLowerCase());
        }

        //load ratings if terminer l event
        if (e.getStatut() == EventStatus.terminer) {
            System.out.println("Event is TERMINE, loading feedbacks...");
            loadFeedbacks(e.getId());
        } else {
            System.out.println("Event is not TERMINE, hiding feedbacks");
            feedbackContainer.setVisible(false);
        }


        try {
            String imagePath = (e.getImageUrl() == null || e.getImageUrl().isEmpty())
                    ? "/images/placeholder.png" : e.getImageUrl();
            eventImageView.setImage(new Image(imagePath));

            Rectangle clip = new Rectangle(450, 350);
            clip.setArcWidth(30); clip.setArcHeight(30);
            eventImageView.setClip(clip);
        } catch (Exception ex) {
            System.out.println("Erreur image: " + ex.getMessage());
        }
    }

    private void loadFeedbacks(int eventId) {
        ServiceEventFeedback sf = new ServiceEventFeedback();
        List<EventFeedback> feedbacks = sf.getByEvent(eventId);

        System.out.println("Fetched feedbacks count: " + feedbacks.size());

        feedbackContainer.getChildren().clear();

        if (feedbacks.isEmpty()) {
            Label empty = new Label("Aucun feedback pour cet événement.");
            feedbackContainer.getChildren().add(empty);
            return;
        }

        for (EventFeedback f : feedbacks) {
            Label lbl = new Label("Note: " + f.getRating() + " - " + f.getCommentaire());
            lbl.setStyle("-fx-font-size:14px; -fx-padding:5; -fx-text-fill: black;");
            feedbackContainer.getChildren().add(lbl);
        }
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            StackPane contentArea = (StackPane) titleLabel.getScene().lookup("#contentArea");

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException ex) {
            System.err.println("Erreur de navigation : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyEvent.fxml"));
            Parent root = loader.load();

            ModifyEventController controller = loader.getController();
            controller.setEvent(currentEvent);

            Stage stage = (Stage) titleLabel.getScene().getWindow();
            Scene scene = stage.getScene();


            scene.setRoot(root);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    private void handleArchive() {
        service.archiver(currentEvent.getId());
        goBack();
    }

    @FXML
    private void goBack() {
        navigateTo("/EventDashboard.fxml");
    }

    @FXML
    private void handleEvenements() {
        navigateTo("/EventDashboard.fxml");
    }

}