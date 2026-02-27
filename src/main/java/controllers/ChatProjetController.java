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
import java.text.SimpleDateFormat;
import java.util.Date;
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

    // Set pour mémoriser les IDs uniques (clés Firebase) déjà affichés
    private final Set<String> displayedMessageIds = new HashSet<>();

    // TON URL API FIREBASE
    private final String FIREBASE_URL = "https://stratixchat-default-rtdb.firebaseio.com/";

    public void initChat(int idProjet, String nomProjet) {
        this.currentProjetId = idProjet;
        this.projetTitle.setText("Discussion : " + nomProjet);

        Utilisateur user = UserRole.getInstance().getUser();
        this.myName = (user != null) ? user.getNom() + " " + user.getPrenom() : "Anonyme";

        chatContainer.getChildren().clear();
        displayedMessageIds.clear();

        // Auto-scroll vers le bas
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (scrollPane != null) scrollPane.setVvalue(1.0);
        });

        // Démarrer la synchronisation temps réel
        startRealTimeSync();
    }

    @FXML
    private void handleSend() {
        String text = msgInput.getText().trim();
        if (text.isEmpty()) return;

        // On envoie seulement à l'API.
        // Le message s'affichera dès que fetchMessages le récupérera (Source de vérité unique)
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
        // Vérification toutes les 2 secondes
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
                            // On filtre pour ne prendre que les nouveaux messages par leur ID unique
                            allMsgs.keySet().stream()
                                    .filter(key -> !displayedMessageIds.contains(key))
                                    .sorted((k1, k2) -> Long.compare(
                                            allMsgs.getJSONObject(k1).getLong("timestamp"),
                                            allMsgs.getJSONObject(k2).getLong("timestamp")))
                                    .forEach(key -> {
                                        JSONObject m = allMsgs.getJSONObject(key);

                                        // On affiche TOUT (les nôtres et ceux des autres)
                                        displayMessage(m.getString("sender"), m.getString("text"), m.getLong("timestamp"));

                                        // On marque cet ID comme "affiché"
                                        displayedMessageIds.add(key);
                                    });
                        });
                    }
                });
    }

    private void displayMessage(String sender, String content, long timestamp) {
        VBox msgBox = new VBox(2);

        // Formatage de l'heure à partir du timestamp Cloud
        String heure = new SimpleDateFormat("HH:mm").format(new Date(timestamp));
        Label nameLbl = new Label(sender + " • " + heure);
        nameLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label contentLbl = new Label(content);
        contentLbl.setWrapText(true);
        contentLbl.setMaxWidth(280);

        // Si c'est moi, à droite en bleu. Sinon, à gauche en gris.
        if (sender.equals(myName)) {
            msgBox.setAlignment(Pos.TOP_RIGHT);
            contentLbl.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                    "-fx-padding: 8 12; -fx-background-radius: 15 15 0 15;");
        } else {
            msgBox.setAlignment(Pos.TOP_LEFT);
            contentLbl.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; " +
                    "-fx-padding: 8 12; -fx-background-radius: 15 15 15 0;");
        }

        msgBox.getChildren().addAll(nameLbl, contentLbl);
        chatContainer.getChildren().add(msgBox);
    }

    public void stopChat() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}