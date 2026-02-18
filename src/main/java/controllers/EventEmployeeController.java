package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import models.Evenement;
import services.ServiceEvenemnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventEmployeeController {


    @FXML private FlowPane eventContainer;
    @FXML private TextField searchField;

    private ServiceEvenemnet service = new ServiceEvenemnet();
    private List<Evenement> events = new ArrayList<>();

    @FXML
    public void initialize() {


        events = service.getPlanifierOnly();
        displayEvents(events);
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

        Button participeBtn = new Button("Participer");
        participeBtn.getStyleClass().add("btn-modify-card");


        infoBox.getChildren().addAll(title, desc, dateLieu, participeBtn);
        card.getChildren().addAll(imageView, infoBox);

        return card;
    }

    @FXML
    private void search() {
        String query = searchField.getText();
        List<Evenement> results = service.searchPlanifierByTitle(query);
        displayEvents(results);
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