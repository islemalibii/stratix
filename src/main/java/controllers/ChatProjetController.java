package controllers;

import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.UserRole;
import models.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatProjetController {

    @FXML private VBox chatContainer;
    @FXML private TextField msgInput;
    @FXML private Label projetTitle;

    private int currentProjetId;
    private String myName = "Utilisateur";
    private Timeline autoRefresh;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/stratix", "root", "");
    }

    public void initChat(int idProjet, String nomProjet) {
        this.currentProjetId = idProjet;
        this.projetTitle.setText("Discussion : " + nomProjet);

        Utilisateur user = UserRole.getInstance().getUser();
        if (user != null) {
            this.myName = user.getNom() + " " + (user.getPrenom() != null ? user.getPrenom() : "");
        }

        loadHistory();
        setupRealTime();
    }

    private void setupRealTime() {
        if (autoRefresh != null) autoRefresh.stop();
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> loadHistory()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    @FXML
    private void handleSend() {
        String text = msgInput.getText().trim();
        if (text.isEmpty() || currentProjetId == 0) return;

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO messagesProjet (projet_id, expediteur_nom, contenu) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentProjetId);
            pstmt.setString(2, myName);
            pstmt.setString(3, text);
            pstmt.executeUpdate();

            msgInput.clear();
            loadHistory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        // CORRECTION : On stocke les messages dans une liste AVANT de fermer la connexion
        List<String[]> tempMessages = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String sql = "SELECT expediteur_nom, contenu FROM messagesProjet WHERE projet_id = ? ORDER BY date_envoi ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentProjetId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tempMessages.add(new String[]{rs.getString("expediteur_nom"), rs.getString("contenu")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Mise à jour de l'UI une fois les données prêtes
        Platform.runLater(() -> {
            chatContainer.getChildren().clear();
            for (String[] m : tempMessages) {
                displayMessage(m[0], m[1]);
            }
        });
    }

    private void displayMessage(String sender, String content) {
        VBox msgBox = new VBox(2);
        Label nameLbl = new Label(sender);
        nameLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748b;");

        Label contentLbl = new Label(content);
        contentLbl.setWrapText(true);
        contentLbl.setMaxWidth(260);

        if (sender != null && sender.equals(myName)) {
            msgBox.setAlignment(Pos.TOP_RIGHT);
            contentLbl.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 7 12; -fx-background-radius: 12 12 0 12;");
        } else {
            msgBox.setAlignment(Pos.TOP_LEFT);
            contentLbl.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 7 12; -fx-background-radius: 12 12 12 0;");
        }

        msgBox.getChildren().addAll(nameLbl, contentLbl);
        chatContainer.getChildren().add(msgBox);
    }

    public void stopChat() {
        if (autoRefresh != null) autoRefresh.stop();
    }
}