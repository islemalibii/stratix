package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class VisioController implements Initializable {

    @FXML private VBox visioContainer;
    @FXML private Button btnStartCall;
    @FXML private Button btnHangup;
    @FXML private Button btnCopyLink;

    private String roomId;
    private String roomUrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roomId = "stratiX-" + System.currentTimeMillis();
        roomUrl = "https://meet.jit.si/" + roomId;

        btnStartCall.setOnAction(e -> startCall());
        btnHangup.setOnAction(e -> hangup());
        btnCopyLink.setOnAction(e -> copyLink());

        btnHangup.setDisable(true);
    }

    @FXML
    private void startCall() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(roomUrl));
                btnStartCall.setDisable(true);
                btnHangup.setDisable(false);
                showAlert("Info", "Jitsi Meet s'ouvre dans votre navigateur.\nSalle: " + roomId);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le navigateur: " + e.getMessage());
        }
    }

    @FXML
    private void hangup() {
        btnStartCall.setDisable(false);
        btnHangup.setDisable(true);
        showAlert("Info", "Appel terminé");
    }

    @FXML
    private void copyLink() {
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(roomUrl);
        clipboard.setContent(content);
        showAlert("Succès", "Lien copié !\n" + roomUrl);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnStartCall.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}