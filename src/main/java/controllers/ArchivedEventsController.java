package controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Evenement;
import services.ServiceEvenemnet;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import utils.SessionManager;

import java.io.IOException;
import java.util.List;
public class ArchivedEventsController {
    @FXML private FlowPane archiveContainer;

    @FXML private Button btnEvenements;
    @FXML private Button logoutButton;

    private ServiceEvenemnet service = new ServiceEvenemnet();

    @FXML
    public void initialize() {
        loadArchivedEvents();
    }

    private void loadArchivedEvents() {
        List<Evenement> archived = service.getAllArchieved();

        archiveContainer.getChildren().clear();

        for (Evenement e : archived) {
            archiveContainer.getChildren().add(createArchiveCard(e));
        }
    }

    private VBox createArchiveCard(Evenement e) {
        VBox card = new VBox(12);
        card.getStyleClass().add("event-card");
        card.setPrefWidth(300);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(0, 0, 15, 0));

        ImageView imageView = new ImageView();
        try {
            String path = (e.getImageUrl() == null || e.getImageUrl().isEmpty())
                    ? "/images/placeholder.png" : e.getImageUrl();
            Image img = new Image(path, 300, 180, true, true);
            imageView.setImage(img);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);

        Rectangle clip = new Rectangle(300, 180);
        clip.setArcWidth(30); clip.setArcHeight(30);
        imageView.setClip(clip);

        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(0, 15, 0, 15));

        Label title = new Label(e.getTitre());
        title.getStyleClass().add("card-title");

        Label dateLieu = new Label(e.getDate_event() + " | " + e.getLieu());
        dateLieu.getStyleClass().add("card-details");

        Label status = new Label(e.getStatut().name().toUpperCase());
        status.getStyleClass().add("status-badge");

        switch (e.getStatut()) {
            case planifier: status.getStyleClass().add("bg-green"); break;
            case terminer: status.getStyleClass().add("bg-blue"); break;
            case annuler: status.getStyleClass().add("bg-red"); break;
        }

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button restoreBtn = new Button("Restaurer");
        restoreBtn.getStyleClass().add("btn-modify-card");
        restoreBtn.setOnAction(event -> {
            service.desarchiver(e.getId());
            loadArchivedEvents();
        });

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-archive-permanent");
        deleteBtn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer définitivement ?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().get() == ButtonType.YES) {
                service.deleteById(e.getId());
                loadArchivedEvents();
            }
        });

        actions.getChildren().addAll(restoreBtn, deleteBtn);
        infoBox.getChildren().addAll(title, dateLieu, status, actions);
        card.getChildren().addAll(imageView, infoBox);

        return card;
    }

    @FXML
    private void goBack() {
        try {
            Node dashboardView = FXMLLoader.load(getClass().getResource("/EventDashboard.fxml"));
            StackPane contentArea = (StackPane) archiveContainer.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(dashboardView);
            }
        } catch (IOException e) {
            System.err.println("Erreur retour dashboard: " + e.getMessage());
        }
    }


}