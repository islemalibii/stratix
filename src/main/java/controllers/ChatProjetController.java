package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.UserRole;
import models.Utilisateur;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatProjetController {

    @FXML private VBox chatContainer;
    @FXML private TextField msgInput;
    @FXML private Label projetTitle;
    @FXML private ScrollPane scrollPane;

    private int currentProjetId;
    private String myName;
    private ScheduledExecutorService scheduler;

    // On garde en mémoire les IDs des messages déjà affichés pour éviter les doublons
    private final Set<String> displayedMessageIds = new HashSet<>();

    // TON URL FIREBASE
    private final String FIREBASE_URL = "https://stratixchat-default-rtdb.firebaseio.com/";

    public void initChat(int idProjet, String nomProjet) {
        this.currentProjetId = idProjet;
        this.projetTitle.setText("Chat : " + nomProjet);

        Utilisateur user = UserRole.getInstance().getUser();
        this.myName = (user != null) ? user.getNom() + " " + user.getPrenom() : "Anonyme";

        chatContainer.getChildren().clear();
        displayedMessageIds.clear();

        // Auto-scroll vers le bas
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (scrollPane != null) scrollPane.setVvalue(1.0);
        });

        // Lancer la synchronisation temps réel (Polling toutes les 2 secondes)
        startRealTimeSync();
    }

    @FXML
    private void handleSend() {
        String text = msgInput.getText().trim();
        if (text.isEmpty()) return;

        // On envoie à l'API Cloud
        envoyerVersFirebase(text);
        msgInput.clear();
    }

    private void envoyerVersFirebase(String text) {
        JSONObject payload = new JSONObject();
        payload.put("sender", myName);
        payload.put("text", text);
        payload.put("timestamp", System.currentTimeMillis());

        String url = FIREBASE_URL + "projet-" + currentProjetId + ".json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private void startRealTimeSync() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchMessages, 0, 2, TimeUnit.SECONDS);
    }

    private void fetchMessages() {
        String url = FIREBASE_URL + "projet-" + currentProjetId + ".json";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    String body = response.body();
                    if (body != null && !body.equals("null")) {
                        JSONObject allMsgs = new JSONObject(body);

                        Platform.runLater(() -> {
                            // On ne traite que les messages qu'on n'a pas encore affichés
                            allMsgs.keySet().stream()
                                    .filter(key -> !displayedMessageIds.contains(key))
                                    .sorted((k1, k2) -> Long.compare(
                                            allMsgs.getJSONObject(k1).getLong("timestamp"),
                                            allMsgs.getJSONObject(k2).getLong("timestamp")))
                                    .forEach(key -> {
                                        JSONObject m = allMsgs.getJSONObject(key);
                                        displayMessage(m.getString("sender"), m.getString("text"));
                                        displayedMessageIds.add(key); // Marquer comme affiché
                                    });
                        });
                    }
                });
    }

    private void displayMessage(String sender, String content) {
        VBox msgBox = new VBox(2);
        Label nameLbl = new Label(sender);
        nameLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label contentLbl = new Label(content);
        contentLbl.setWrapText(true);
        contentLbl.setMaxWidth(280);

        if (sender.equals(myName)) {
            msgBox.setAlignment(Pos.TOP_RIGHT);
            contentLbl.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 8 12; -fx-background-radius: 15 15 0 15;");
        } else {
            msgBox.setAlignment(Pos.TOP_LEFT);
            contentLbl.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 8 12; -fx-background-radius: 15 15 15 0;");
        }

        msgBox.getChildren().addAll(nameLbl, contentLbl);
        chatContainer.getChildren().add(msgBox);
    }

    public void stopChat() {
        if (scheduler != null) scheduler.shutdownNow();
    }
}