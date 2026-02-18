package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Evenement;
import models.enums.EventStatus;
import models.enums.EventType;
import services.ServiceEvenemnet;

import java.io.IOException;
import java.time.LocalDate;
public class AddEventController {
    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private TextField lieuField;
    @FXML private DatePicker datePicker;

    @FXML private ComboBox<EventType> typeCombo;
    @FXML private ComboBox<EventStatus> statusCombo;

    private ServiceEvenemnet service = new ServiceEvenemnet();

    @FXML
    public void initialize() {
        // types kol yokhrjou
        typeCombo.getItems().addAll(EventType.values());

        // Date par défaut hya lyoum
        datePicker.setValue(LocalDate.now());

        // par defaut tji planifier w disabled
        statusCombo.getItems().addAll(EventStatus.values());
        statusCombo.setValue(EventStatus.planifier);
        statusCombo.setDisable(true);
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

        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showError("La date ne peut pas être dans le passé !");
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
    private void addEvent() {
        if (!validateInputs()) return;

        Evenement e = new Evenement();
        e.setTitre(titreField.getText());
        e.setDescription(descriptionField.getText());
        e.setLieu(lieuField.getText());
        e.setDate_event(datePicker.getValue());
        e.setType_event(typeCombo.getValue());
        e.setStatut(statusCombo.getValue());

        service.add(e);

        showSuccessAndGoBack();
    }

    private void showSuccessAndGoBack() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès !!!");
        alert.setHeaderText(null);
        alert.setContentText("Événement créé avec succès !!!");
        alert.showAndWait();
        goBack();
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) titreField.getScene().getWindow();

            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/EventDashboard.fxml"));

            stage.setScene(new javafx.scene.Scene(loader.load()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}