package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Evenement;
import models.Ressource;
import models.enums.EventStatus;
import models.enums.EventType;
import Services.ServiceEvenemnet;
import javafx.stage.FileChooser;
import Services.ServiceEventRessource;

import java.io.File;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddEventController {
    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private TextField lieuField;
    @FXML private DatePicker datePicker;
    @FXML private TextField imageUrlField;
    @FXML private VBox ressourcesContainer;



    @FXML private ComboBox<EventType> typeCombo;
    @FXML private ComboBox<EventStatus> statusCombo;

    private List<Ressource> selectedRessources = new ArrayList<>();
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


        //for the ressources
        ServiceEventRessource resService = new ServiceEventRessource();
        List<Ressource> Ressources = resService.getAllRessources();
        for (Ressource r : Ressources) {
            CheckBox check = new CheckBox(
                    r.getNom() + " (Stock: " + r.getQuatite() + ")"
            );
            TextField qField = new TextField();
            qField.setPromptText("Qté");
            qField.setPrefWidth(60);
            qField.setDisable(true);

            check.setOnAction(e -> qField.setDisable(!check.isSelected()));
            HBox row = new HBox(10, check, qField);
            row.setUserData(r);
            ressourcesContainer.getChildren().add(row);
        }


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
        if (imageUrlField.getText().isEmpty()) {
            showError("Veuillez sélectionner une image pour l'événement");
            return false;
        }
        return true;
    }
    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une affiche");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(titreField.getScene().getWindow());
        if (selectedFile != null) {
            imageUrlField.setText(selectedFile.toURI().toString());
        }
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
        e.setImageUrl(imageUrlField.getText());

        e.setRessources(selectedRessources);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/service-view.fxml"));
            Parent root = loader.load();
            Scene scene = titreField.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


            StackPane contentArea = (StackPane) root.lookup("#contentArea");
            if (contentArea != null) {
                Node eventView = FXMLLoader.load(getClass().getResource("/EventDashboard.fxml"));
                contentArea.getChildren().setAll(eventView);
            }
            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}