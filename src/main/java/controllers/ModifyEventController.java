package controllers;

import javafx.stage.FileChooser;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Evenement;
import models.enums.EventStatus;
import models.enums.EventType;
import services.ServiceEvenemnet;

import java.io.IOException;

public class ModifyEventController {
    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private TextField lieuField;
    @FXML private TextField imageUrlField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<EventType> typeCombo;
    @FXML private ComboBox<EventStatus> statusCombo;

    private ServiceEvenemnet service = new ServiceEvenemnet();
    private Evenement currentEvent;

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll(EventType.values());
        statusCombo.getItems().addAll(EventStatus.values());
    }


    public void setEvent(Evenement event) {
        this.currentEvent = event;

        titreField.setText(event.getTitre());
        descriptionField.setText(event.getDescription());
        lieuField.setText(event.getLieu());
        datePicker.setValue(event.getDate_event());
        typeCombo.setValue(event.getType_event());
        statusCombo.setValue(event.getStatut());
        imageUrlField.setText(event.getImageUrl());
    }

    private boolean validateInputs() {
        if (titreField.getText().isEmpty()) {
            showError("Le titre est obligatoire");
            return false;
        }
        if (descriptionField.getText().isEmpty()) {
            showError("La description est obligatoire");
            return false;
        }
        if (lieuField.getText().isEmpty()) {
            showError("Le lieu est obligatoire");
            return false;
        }
        if (datePicker.getValue() == null) {
            showError("Veuillez sélectionner une date");
            return false;
        }
        if (typeCombo.getValue() == null) {
            showError("Veuillez sélectionner le type d'événement");
            return false;
        }
        if (statusCombo.getValue() == null) {
            showError("Veuillez sélectionner le statut");
            return false;
        }
        return true;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Changer l'affiche");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(titreField.getScene().getWindow());

        if (selectedFile != null) {
            imageUrlField.setText(selectedFile.toURI().toString());
        }
    }

    @FXML
    private void modifyEvent() {
        if (!validateInputs()) return;

        currentEvent.setTitre(titreField.getText());
        currentEvent.setDescription(descriptionField.getText());
        currentEvent.setLieu(lieuField.getText());
        currentEvent.setDate_event(datePicker.getValue());
        currentEvent.setType_event(typeCombo.getValue());
        currentEvent.setStatut(statusCombo.getValue());
        currentEvent.setImageUrl(imageUrlField.getText());

        service.update(currentEvent);

        showSuccessAndGoBack();
    }

    private void showSuccessAndGoBack() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès !!!");
        alert.setHeaderText(null);
        alert.setContentText("Événement mis à jour avec succès !!!");
        alert.showAndWait();

        goBack();
    }

    @FXML
    private void goBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/service-view.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.scene.Scene scene = titreField.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            javafx.scene.layout.StackPane contentArea = (javafx.scene.layout.StackPane) root.lookup("#contentArea");
            if (contentArea != null) {
                javafx.scene.Node eventView = javafx.fxml.FXMLLoader.load(getClass().getResource("/EventDashboard.fxml"));
                contentArea.getChildren().setAll(eventView);
            }
            scene.setRoot(root);

        } catch (IOException e) {
            System.err.println("Erreur retour dashboard : " + e.getMessage());
            e.printStackTrace();
        }
    }
}