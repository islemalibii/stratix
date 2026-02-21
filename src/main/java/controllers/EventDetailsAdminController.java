package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import models.Evenement;
import services.ServiceEvenemnet;
import java.io.IOException;

public class EventDetailsAdminController {

    @FXML private ImageView eventImageView;
    @FXML private Label titleLabel, dateLabel, locationLabel, typeLabel, descriptionLabel, statusTextLabel, statusBadge;

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

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyEvent.fxml"));
            Parent root = loader.load();
            ModifyEventController controller = loader.getController();
            controller.setEvent(currentEvent);
            titleLabel.getScene().setRoot(root);
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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/EventDashboard.fxml"));
            titleLabel.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}