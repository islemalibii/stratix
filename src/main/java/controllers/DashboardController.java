package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import services.StatsService;
import models.DashboardStats;

public class DashboardController {

    @FXML private Label lblTotalTaches;
    @FXML private Label lblEnCours;
    @FXML private Label lblTerminees;
    @FXML private Label lblEnRetard;
    @FXML private Label lblTotalEmployes;
    @FXML private Label lblEnPoste;
    @FXML private Label lblAbsents;
    @FXML private Label lblAFaire;

    private StatsService statsService;

    @FXML
    public void initialize() {
        System.out.println("=== INITIALISATION DASHBOARD ===");

        // Vérifier que les labels ne sont pas null
        verifierLabels();

        statsService = new StatsService();
        chargerStatistiques();
    }

    private void verifierLabels() {
        System.out.println("Vérification des labels:");
        System.out.println("  lblTotalTaches: " + (lblTotalTaches != null ? "✅" : "❌"));
        System.out.println("  lblEnCours: " + (lblEnCours != null ? "✅" : "❌"));
        System.out.println("  lblTerminees: " + (lblTerminees != null ? "✅" : "❌"));
        System.out.println("  lblEnRetard: " + (lblEnRetard != null ? "✅" : "❌"));
        System.out.println("  lblTotalEmployes: " + (lblTotalEmployes != null ? "✅" : "❌"));
        System.out.println("  lblEnPoste: " + (lblEnPoste != null ? "✅" : "❌"));
        System.out.println("  lblAbsents: " + (lblAbsents != null ? "✅" : "❌"));
        System.out.println("  lblAFaire: " + (lblAFaire != null ? "✅" : "❌"));
    }

    private void chargerStatistiques() {
        try {
            System.out.println("\nChargement des statistiques...");
            DashboardStats stats = statsService.getDashboardStats();

            System.out.println("Statistiques reçues:");
            System.out.println("  Total Tâches: " + stats.getTotalTaches());
            System.out.println("  En cours: " + stats.getTachesEnCours());
            System.out.println("  Terminées: " + stats.getTachesTerminees());
            System.out.println("  En retard: " + stats.getTachesEnRetard());
            System.out.println("  Total Employés: " + stats.getTotalEmployes());
            System.out.println("  En poste: " + stats.getEmployesEnPoste());
            System.out.println("  Absents: " + stats.getEmployesAbsents());
            System.out.println("  À faire: " + stats.getTachesAFaire());

            // Mettre à jour les labels
            lblTotalTaches.setText(String.valueOf(stats.getTotalTaches()));
            lblEnCours.setText(String.valueOf(stats.getTachesEnCours()));
            lblTerminees.setText(String.valueOf(stats.getTachesTerminees()));
            lblEnRetard.setText(String.valueOf(stats.getTachesEnRetard()));
            lblTotalEmployes.setText(String.valueOf(stats.getTotalEmployes()));
            lblEnPoste.setText(String.valueOf(stats.getEmployesEnPoste()));
            lblAbsents.setText(String.valueOf(stats.getEmployesAbsents()));
            lblAFaire.setText(String.valueOf(stats.getTachesAFaire()));

            System.out.println("✅ Dashboard mis à jour avec succès!");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des statistiques:");
            e.printStackTrace();

            // Afficher des valeurs par défaut en cas d'erreur
            lblTotalTaches.setText("0");
            lblEnCours.setText("0");
            lblTerminees.setText("0");
            lblEnRetard.setText("0");
            lblTotalEmployes.setText("0");
            lblEnPoste.setText("0");
            lblAbsents.setText("0");
            lblAFaire.setText("0");
        }
    }

    @FXML
    private void openTaches() {
        System.out.println("Navigation vers Tâches");
        MainController.showTaches();
    }

    @FXML
    private void openPlanning() {
        System.out.println("Navigation vers Planning");
        MainController.showPlanning();
    }

    @FXML
    private void openCalendar() {
        System.out.println("Navigation vers Calendrier");
        MainController.showCalendar();
    }
}