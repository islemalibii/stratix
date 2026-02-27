package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import services.StatsService;
import services.SERVICETache;
import services.EmployeeService;
import services.SERVICEPlanning;
import models.DashboardStats;
import models.Tache;
import models.Employe;
import api.QuoteAPI;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    // Labels principaux
    @FXML private Label lblTotalTaches;
    @FXML private Label lblEnCours;
    @FXML private Label lblTerminees;
    @FXML private Label lblEnRetard;
    @FXML private Label lblTotalEmployes;
    @FXML private Label lblEnPoste;
    @FXML private Label lblAbsents;
    @FXML private Label lblAFaire;

    // Mini indicateurs
    @FXML private Label lblTotalEmployesMini;
    @FXML private Label lblEnRetardMini;
    @FXML private Label lblProjetsActifs;
    @FXML private Label lblTauxCompletion;

    // Labels du graphique
    @FXML private Label lblAFaireGraph;
    @FXML private Label lblEnCoursGraph;
    @FXML private Label lblTermineesGraph;

    // Barres du graphique
    @FXML private Rectangle barAFaire;
    @FXML private Rectangle barEnCours;
    @FXML private Rectangle barTerminees;

    // Citation
    @FXML private Label lblCitation;
    @FXML private Button btnRefreshQuote;

    // Recherche
    @FXML private TextField searchField;
    @FXML private VBox searchResultsContainer;
    @FXML private ListView<String> searchResultsList;

    private StatsService statsService;
    private SERVICETache tacheService;
    private EmployeeService employeService;
    private SERVICEPlanning planningService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== INITIALISATION DASHBOARD ===");

        statsService = new StatsService();
        tacheService = new SERVICETache();
        employeService = new EmployeeService();
        planningService = new SERVICEPlanning();

        chargerStatistiques();
        chargerCitation();

        if (btnRefreshQuote != null) {
            btnRefreshQuote.setOnAction(e -> chargerCitation());
        }

        // Listener pour la recherche
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    searchResultsContainer.setVisible(false);
                    searchResultsContainer.setManaged(false);
                } else {
                    effectuerRecherche(newVal.trim().toLowerCase());
                }
            });
        }
    }

    private void chargerStatistiques() {
        try {
            DashboardStats stats = statsService.getDashboardStats();

            // Labels principaux
            if (lblTotalTaches != null) lblTotalTaches.setText(String.valueOf(stats.getTotalTaches()));
            if (lblEnCours != null) lblEnCours.setText(String.valueOf(stats.getTachesEnCours()));
            if (lblTerminees != null) lblTerminees.setText(String.valueOf(stats.getTachesTerminees()));
            if (lblEnRetard != null) lblEnRetard.setText(String.valueOf(stats.getTachesEnRetard()));
            if (lblTotalEmployes != null) lblTotalEmployes.setText(String.valueOf(stats.getTotalEmployes()));
            if (lblEnPoste != null) lblEnPoste.setText(String.valueOf(stats.getEmployesEnPoste()));
            if (lblAbsents != null) lblAbsents.setText(String.valueOf(stats.getEmployesAbsents()));
            if (lblAFaire != null) lblAFaire.setText(String.valueOf(stats.getTachesAFaire()));

            // Mini indicateurs (vérification null)
            if (lblTotalEmployesMini != null) lblTotalEmployesMini.setText(String.valueOf(stats.getTotalEmployes()));
            if (lblEnRetardMini != null) lblEnRetardMini.setText(String.valueOf(stats.getTachesEnRetard()));

            // Projets actifs (valeur par défaut)
            if (lblProjetsActifs != null) lblProjetsActifs.setText("3");

            // Taux de complétion
            int total = stats.getTotalTaches();
            int terminees = stats.getTachesTerminees();
            int taux = total > 0 ? (terminees * 100 / total) : 0;
            lblTauxCompletion.setText(taux + "%");

            // Graphique
            lblAFaireGraph.setText(String.valueOf(stats.getTachesAFaire()));
            lblEnCoursGraph.setText(String.valueOf(stats.getTachesEnCours()));
            lblTermineesGraph.setText(String.valueOf(stats.getTachesTerminees()));

            int max = Math.max(stats.getTachesAFaire(),
                    Math.max(stats.getTachesEnCours(), stats.getTachesTerminees()));
            if (max > 0) {
                barAFaire.setHeight((stats.getTachesAFaire() * 100.0) / max);
                barEnCours.setHeight((stats.getTachesEnCours() * 100.0) / max);
                barTerminees.setHeight((stats.getTachesTerminees() * 100.0) / max);
            }

            System.out.println("✅ Dashboard mis à jour!");

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerCitation() {
        try {
            String citation = QuoteAPI.getRandomQuote();
            lblCitation.setText(citation);
        } catch (Exception e) {
            lblCitation.setText("“Le succès c'est d'aller d'échec en échec sans perdre son enthousiasme.” — Winston Churchill");
        }
    }

    private void effectuerRecherche(String recherche) {
        searchResultsList.getItems().clear();

        // Recherche dans les tâches
        List<Tache> taches = tacheService.getAllTaches();
        List<String> resultats = taches.stream()
                .filter(t -> t.getTitre().toLowerCase().contains(recherche))
                .map(t -> "📋 Tâche: " + t.getTitre())
                .collect(Collectors.toList());

        // Recherche dans les employés
        List<Employe> employes = employeService.getAllEmployes();
        resultats.addAll(employes.stream()
                .filter(e -> e.getUsername().toLowerCase().contains(recherche))
                .map(e -> "👤 Employé: " + e.getUsername())
                .collect(Collectors.toList()));

        if (!resultats.isEmpty()) {
            searchResultsList.getItems().addAll(resultats);
            searchResultsContainer.setVisible(true);
            searchResultsContainer.setManaged(true);
        } else {
            searchResultsList.getItems().add("🔍 Aucun résultat");
            searchResultsContainer.setVisible(true);
            searchResultsContainer.setManaged(true);
        }
    }

    @FXML
    private void openTaches() {
        System.out.println("🔄 Navigation vers Tâches");
        MainsController.showTaches();
    }

    @FXML
    private void openPlanning() {
        System.out.println("🔄 Navigation vers Planning");
        MainsController.showPlanning();
    }

    @FXML
    private void openCalendar() {
        System.out.println("🔄 Navigation vers Calendrier");
        MainsController.showCalendar();
    }




    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboardFromButton() { loadView("/dashboard-view.fxml"); }

    @FXML
    private void showPlanningFromButton() { loadView("/PlanningListeView.fxml"); }

    @FXML
    private void showTachesFromButton() { loadView("/TacheListeView.fxml"); }

    @FXML
    private void showCalendarFromButton() { loadView("/calendar-view.fxml"); }

    @FXML
    private void showWhiteboardFromButton() { loadView("/WhiteboardView.fxml"); }
}