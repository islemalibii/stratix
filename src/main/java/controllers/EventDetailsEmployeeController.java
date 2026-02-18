package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import models.Evenement;
import java.io.IOException;

public class EventDetailsEmployeeController {

    @FXML private ImageView eventImageView;
    @FXML private Label titleLabel, dateLabel, locationLabel, typeLabel, descriptionLabel;

    public void setEventData(Evenement e) {
        titleLabel.setText(e.getTitre());
        descriptionLabel.setText(e.getDescription());
        dateLabel.setText(e.getDate_event().toString());
        locationLabel.setText(e.getLieu());

        if (e.getType_event() != null) {
            typeLabel.setText(e.getType_event().name());
        } else {
            typeLabel.setText("Non spécifié");
        }
        try {
            String imagePath = (e.getImageUrl() == null || e.getImageUrl().isEmpty())
                    ? "/images/placeholder.png" : e.getImageUrl();
            Image img = new Image(imagePath);
            eventImageView.setImage(img);

            Rectangle clip = new Rectangle(450, 350);
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            eventImageView.setClip(clip);
        } catch (Exception ex) {
            System.out.println("Image non trouvée: " + ex.getMessage());
        }
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/EventEmployeeDashboard.fxml"));
            titleLabel.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}