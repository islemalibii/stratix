package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Projet;
import models.Utilisateur;
import models.UserRole;
import services.ProjetService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeListeProjetController {

    @FXML private FlowPane flowPaneProjets;
    @FXML private TextField searchField;
    @FXML private Label lblBienvenue;

    private ProjetService projetService = new ProjetService();

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserRole.getInstance().getUser();
        if (currentUser != null) {
            // Affichage sobre comme demandé
            lblBienvenue.setText("Bonjour,");
            rafraichirListe(currentUser);
        } else {
            lblBienvenue.setText("Bonjour,");
        }
    }

    public void rafraichirListe(Utilisateur user) {
        // Filtrage dynamique basé sur le nom + prénom de la session
        String nomComplet = (user.getNom() + " " + user.getPrenom()).toLowerCase().trim();
        List<Projet> tousLesProjets = projetService.listerTousLesProjets();

        List<Projet> mesProjets = tousLesProjets.stream()
                .filter(p -> p.getEquipeMembres() != null &&
                        p.getEquipeMembres().toLowerCase().contains(nomComplet))
                .filter(p -> !p.isArchived())
                .collect(Collectors.toList());

        afficherLesCartes(mesProjets);
    }

    private void afficherLesCartes(List<Projet> projets) {
        if (flowPaneProjets != null) {
            flowPaneProjets.getChildren().clear();
            if (projets.isEmpty()) {
                Label info = new Label("Aucun projet assigné trouvé.");
                info.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-padding: 20;");
                flowPaneProjets.getChildren().add(info);
            } else {
                for (Projet p : projets) {
                    flowPaneProjets.getChildren().add(creerCarteSimple(p));
                }
            }
        }
    }

    private VBox creerCarteSimple(Projet p) {
        VBox card = new VBox(15);
        card.setPrefWidth(350);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #e2e8f0; -fx-border-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Badge de statut avec couleur dynamique
        Label lblStatut = new Label(p.getStatut().toUpperCase());
        String color = getStatusColor(p.getStatut());
        lblStatut.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label title = new Label(p.getNom());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        title.setWrapText(true);

        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);

        Button btnDetails = new Button("Détails du projet");
        btnDetails.setMaxWidth(Double.MAX_VALUE);
        btnDetails.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        btnDetails.setOnAction(e -> afficherPopupDetails(p));

        card.getChildren().addAll(lblStatut, title, new Separator(), pb, btnDetails);
        return card;
    }

    private void afficherPopupDetails(Projet p) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Fiche Projet : " + p.getNom());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: white;");

        // Header avec couleur selon statut
        String color = getStatusColor(p.getStatut());
        Label header = new Label("Détails du Projet");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        // Remplissage des données (SANS LE BUDGET)
        ajouterLigne(grid, 0, "Nom du projet :", p.getNom());
        ajouterLigne(grid, 1, "Description :", p.getDescription());
        ajouterLigne(grid, 2, "Responsable :", p.getChefProjet());
        ajouterLigne(grid, 3, "Date Début :", p.getDateDebut() != null ? p.getDateDebut().toString() : "N/A");
        ajouterLigne(grid, 4, "Date Fin :", p.getDateFin() != null ? p.getDateFin().toString() : "N/A");
        ajouterLigne(grid, 5, "Membres :", p.getEquipe());
        ajouterLigne(grid, 6, "État actuel :", p.getStatut());
        ajouterLigne(grid, 7, "Avancement :", p.getProgression() + "%");

        Button btnFermer = new Button("Fermer la fiche");
        btnFermer.setPrefWidth(150);
        btnFermer.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnFermer.setOnAction(e -> stage.close());

        layout.getChildren().addAll(header, new Separator(), grid, new Separator(), btnFermer);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 500, 550);
        stage.setScene(scene);
        stage.show();
    }

    private void ajouterLigne(GridPane grid, int row, String label, String valeur) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");
        Label val = new Label(valeur != null ? valeur : "Non renseigné");
        val.setWrapText(true);
        val.setMaxWidth(300);
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private String getStatusColor(String statut) {
        if (statut == null) return "#94a3b8";
        switch (statut.toLowerCase()) {
            case "en cours": return "#10b981"; // Vert
            case "planifié": return "#3b82f6"; // Bleu
            case "terminé": return "#6366f1";  // Violet
            case "en attente": return "#f59e0b"; // Orange
            default: return "#94a3b8";         // Gris
        }
    }

    @FXML
    private void rechercherProjet() {
        Utilisateur currentUser = UserRole.getInstance().getUser();
        if (currentUser == null) return;

        String nomComplet = (currentUser.getNom() + " " + currentUser.getPrenom()).toLowerCase().trim();
        String search = searchField.getText().toLowerCase().trim();

        List<Projet> filtrés = projetService.listerTousLesProjets().stream()
                .filter(p -> p.getEquipeMembres() != null && p.getEquipeMembres().toLowerCase().contains(nomComplet))
                .filter(p -> p.getNom().toLowerCase().contains(search))
                .collect(Collectors.toList());

        afficherLesCartes(filtrés);
    }

    @FXML
    private void ouvrirChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatProjet.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chat d'équipe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}