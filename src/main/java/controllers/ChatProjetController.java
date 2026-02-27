package controllers;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONObject;

import java.util.Collections;

public class ChatProjetController {
    @FXML private ListView<String> lvMessages;
    @FXML private TextField tfMessage;

    // Utilisation du nom complet pour éviter les conflits de classe Pusher
    private com.pusher.rest.Pusher pusherRest;
    private com.pusher.client.Pusher pusherClient;

    // Tes identifiants Pusher
    private final String APP_ID = "2120858";
    private final String KEY = "8e0521301e0b1f5449dc";
    private final String SECRET = "e67bbcccb7e9f6bfa41a";
    private final String CLUSTER = "mt1";

    @FXML
    public void initialize() {
        try {
            // 1. Initialisation de l'envoi (REST)
            pusherRest = new com.pusher.rest.Pusher(APP_ID, KEY, SECRET);
            pusherRest.setCluster(CLUSTER);

            // 2. Initialisation de la réception (Client WebSocket)
            PusherOptions options = new PusherOptions().setCluster(CLUSTER);
            pusherClient = new com.pusher.client.Pusher(KEY, options);

            // 3. Connexion au canal
            pusherClient.connect();
            Channel channel = pusherClient.subscribe("chat-channel");

            // 4. Écoute de l'événement "nouveau-message"
            channel.bind("nouveau-message", new SubscriptionEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    // Pusher reçoit sur un thread séparé, on utilise Platform.runLater pour l'UI
                    Platform.runLater(() -> {
                        try {
                            // Extraction du texte depuis le JSON
                            JSONObject json = new JSONObject(event.getData());
                            String messageRecu = json.getString("text");
                            lvMessages.getItems().add(messageRecu);

                            // Scroll automatique vers le bas
                            lvMessages.scrollTo(lvMessages.getItems().size() - 1);
                        } catch (Exception e) {
                            System.err.println("Erreur format message : " + e.getMessage());
                        }
                    });
                }
            });

        } catch (Exception e) {
            System.err.println("Erreur d'initialisation Chat : " + e.getMessage());
        }
    }

    @FXML
    private void envoyerMessage() {
        String msg = tfMessage.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            // On envoie dans un Thread pour ne pas bloquer l'interface JavaFX
            new Thread(() -> {
                try {
                    pusherRest.trigger("chat-channel", "nouveau-message",
                            Collections.singletonMap("text", "Admin: " + msg));
                } catch (Exception e) {
                    System.err.println("Erreur envoi Pusher : " + e.getMessage());
                }
            }).start();

            tfMessage.clear();
        }
    }
}