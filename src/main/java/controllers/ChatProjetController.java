package controllers;

import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.UserRole;
import models.Utilisateur;
import org.json.JSONObject;
import utils.MyDataBase;

import java.sql.*;

public class ChatProjetController {

    @FXML private VBox chatContainer;
    @FXML private TextField msgInput;
    @FXML private Label projetTitle;
    @FXML private ScrollPane scrollPane; // Ajout du ScrollPane pour l'auto-scroll

    private int currentProjetId;
    private String myName;

    // --- Identifiants Pusher ---
    private static final String APP_ID = "2120858";
    private static final String PUSHER_KEY = "8e0521301e0b1f5449dc";
    private static final String PUSHER_SECRET = "e67bbcccb7e9f6bfa41a";
    private static final String PUSHER_CLUSTER = "mt1";

    private com.pusher.client.Pusher pusherClient;

    public void initChat(int idProjet, String nomProjet) {
        this.currentProjetId = idProjet;
        this.projetTitle.setText("Chat : " + nomProjet);

        Utilisateur user = UserRole.getInstance().getUser();
        this.myName = (user != null)
                ? user.getNom() + " " + user.getPrenom()
                : "Anonyme";

        chatContainer.getChildren().clear();

        // Permet de scroller vers le bas à chaque nouveau message
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            // Si ton container est dans un ScrollPane (ex: @FXML ScrollPane scrollPane)
            if (scrollPane != null) {
                scrollPane.setVvalue(1.0);
            }
        });

        loadHistoryFromMySQL();
        setupPusherReceiver();
    }

    private void setupPusherReceiver() {
        PusherOptions options = new PusherOptions().setCluster(PUSHER_CLUSTER);
        pusherClient = new com.pusher.client.Pusher(PUSHER_KEY, options);

        Channel channel = pusherClient.subscribe("projet-" + currentProjetId);

        // ✅ VERSION CORRIGÉE
        channel.bind("nouveau-message", (PusherEvent event) -> {

            String data = event.getData();

            JSONObject json = new JSONObject(data);
            String sender = json.getString("sender");
            String text = json.getString("text");

            if (!sender.equals(myName)) {
                Platform.runLater(() -> displayMessage(sender, text));
            }
        });

        pusherClient.connect();
    }

    @FXML
    private void handleSend() {
        String text = msgInput.getText().trim();
        if (text.isEmpty()) return;

        saveToDatabase(text);
        displayMessage(myName, text);

        new Thread(() -> {
            try {
                com.pusher.rest.Pusher server =
                        new com.pusher.rest.Pusher(APP_ID, PUSHER_KEY, PUSHER_SECRET);
                server.setCluster(PUSHER_CLUSTER);

                JSONObject payload = new JSONObject();
                payload.put("sender", myName);
                payload.put("text", text);

                server.trigger(
                        "projet-" + currentProjetId,
                        "nouveau-message",
                        payload.toString()
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        msgInput.clear();
    }

    private void displayMessage(String sender, String content) {
        VBox msgBox = new VBox(2);
        Label nameLbl = new Label(sender);
        nameLbl.setStyle(
                "-fx-font-size: 10px; " +
                        "-fx-text-fill: #64748b; " +
                        "-fx-font-weight: bold;"
        );

        Label contentLbl = new Label(content);
        contentLbl.setWrapText(true);
        contentLbl.setMaxWidth(280);

        if (sender.equals(myName)) {
            msgBox.setAlignment(Pos.TOP_RIGHT);
            contentLbl.setStyle(
                    "-fx-background-color: #3b82f6; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 8 12; " +
                            "-fx-background-radius: 15 15 0 15;"
            );
        } else {
            msgBox.setAlignment(Pos.TOP_LEFT);
            contentLbl.setStyle(
                    "-fx-background-color: #f1f5f9; " +
                            "-fx-text-fill: #1e293b; " +
                            "-fx-padding: 8 12; " +
                            "-fx-background-radius: 15 15 15 0;"
            );
        }

        msgBox.getChildren().addAll(nameLbl, contentLbl);
        chatContainer.getChildren().add(msgBox);
    }

    private void loadHistoryFromMySQL() {
        String sql = """
                SELECT expediteur_nom, contenu
                FROM messages_projet
                WHERE projet_id = ?
                ORDER BY id ASC
                """;

        try (PreparedStatement pstmt =
                     MyDataBase.getInstance().getCnx().prepareStatement(sql)) {

            pstmt.setInt(1, currentProjetId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                displayMessage(
                        rs.getString("expediteur_nom"),
                        rs.getString("contenu")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveToDatabase(String text) {
        String sql = """
                INSERT INTO messages_projet
                (projet_id, expediteur_nom, contenu)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement pstmt =
                     MyDataBase.getInstance().getCnx().prepareStatement(sql)) {

            pstmt.setInt(1, currentProjetId);
            pstmt.setString(2, myName);
            pstmt.setString(3, text);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopChat() {
        if (pusherClient != null) {
            pusherClient.disconnect();
        }
    }
}