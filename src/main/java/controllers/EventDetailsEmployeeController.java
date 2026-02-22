package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import models.Evenement;
import models.enums.EventStatus;
import javafx.scene.control.Button;

import java.io.IOException;

public class EventDetailsEmployeeController {

    @FXML private ImageView eventImageView;
    @FXML private Label titleLabel, dateLabel, locationLabel, typeLabel, descriptionLabel;
    @FXML private ImageView qrCodeImageView;
    @FXML private VBox qrCodeContainer;
    @FXML private Button participateBtn;

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


        if (e.getStatut() == EventStatus.terminer) {
            qrCodeContainer.setVisible(true);
            qrCodeContainer.setManaged(true);
            setupQRCode(e);
            participateBtn.setVisible(false);
            participateBtn.setManaged(false);
        }
        else if (e.getStatut() == EventStatus.planifier) {

            qrCodeContainer.setVisible(false);
            qrCodeContainer.setManaged(false);

            participateBtn.setVisible(true);
            participateBtn.setManaged(true);
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

    public void setupQRCode(Evenement e) {
        try {
            String baseUrl = "https://docs.google.com/forms/d/e/1FAIpQLSdyhSYlc-lUcxNWibtq93mYXuAggpd-ZjwN2q2Y9hvc9NiPpw/viewform?usp=pp_url&entry.826892988=";
            String fullUrl = baseUrl + e.getId();
            String encodedUrl = java.net.URLEncoder.encode(fullUrl, java.nio.charset.StandardCharsets.UTF_8.toString());
            Image qrImage = new Image("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodedUrl);
            qrCodeImageView.setImage(qrImage);

        } catch (Exception ex) {
            System.out.println("Erreur QR Code : " + ex.getMessage());
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