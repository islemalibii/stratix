package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import models.Tache;
import models.Planning;
import services.SERVICETache;
import services.SERVICEPlanning;

import java.util.List;

public class StatsChartController {

    private static SERVICETache tacheService = new SERVICETache();
    private static SERVICEPlanning planningService = new SERVICEPlanning();

    /**
     * Affiche les statistiques des tâches (graphique circulaire)
     */
    public static void showTaskStatistics() {
        try {
            List<Tache> taches = tacheService.getAllTaches();

            // Compter par statut
            int aFaire = 0, enCours = 0, terminees = 0;
            for (Tache t : taches) {
                switch(t.getStatut()) {
                    case "A_FAIRE": aFaire++; break;
                    case "EN_COURS": enCours++; break;
                    case "TERMINEE": terminees++; break;
                }
            }

            // Créer les données du graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("À faire (" + aFaire + ")", aFaire),
                    new PieChart.Data("En cours (" + enCours + ")", enCours),
                    new PieChart.Data("Terminées (" + terminees + ")", terminees)
            );

            // Créer le graphique
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("📊 Répartition des tâches par statut");
            pieChart.setClockwise(true);
            pieChart.setLabelLineLength(50);
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);
            pieChart.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Appliquer des couleurs personnalisées
            applyCustomColors(pieChart, pieChartData);

            // Créer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("📊 Statistiques des tâches");

            VBox vbox = new VBox(pieChart);
            vbox.setStyle("-fx-padding: 20; -fx-background-color: white;");

            Scene scene = new Scene(vbox, 650, 550);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche les statistiques des plannings (graphique circulaire)
     */
    public static void showPlanningStatistics() {
        try {
            List<Planning> plannings = planningService.getAllPlannings();

            // Compter par shift
            int jour = 0, soir = 0, nuit = 0;
            for (Planning p : plannings) {
                switch(p.getTypeShift()) {
                    case "JOUR": jour++; break;
                    case "SOIR": soir++; break;
                    case "NUIT": nuit++; break;
                }
            }

            // Créer les données du graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("JOUR (" + jour + ")", jour),
                    new PieChart.Data("SOIR (" + soir + ")", soir),
                    new PieChart.Data("NUIT (" + nuit + ")", nuit)
            );

            // Créer le graphique
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("📊 Répartition des plannings par shift");
            pieChart.setClockwise(true);
            pieChart.setLabelLineLength(50);
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);
            pieChart.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Appliquer des couleurs personnalisées
            applyPlanningColors(pieChart, pieChartData);

            // Créer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("📊 Statistiques des plannings");

            VBox vbox = new VBox(pieChart);
            vbox.setStyle("-fx-padding: 20; -fx-background-color: white;");

            Scene scene = new Scene(vbox, 650, 550);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Applique des couleurs personnalisées au graphique des tâches
     */
    private static void applyCustomColors(PieChart pieChart, ObservableList<PieChart.Data> pieChartData) {
        // Définir les couleurs après l'affichage
        pieChart.applyCss();
        for (PieChart.Data data : pieChartData) {
            String statut = data.getName().split(" ")[0];
            if (statut.contains("À faire")) {
                data.getNode().setStyle("-fx-pie-color: #f59e0b;"); // Orange
            } else if (statut.contains("En cours")) {
                data.getNode().setStyle("-fx-pie-color: #3b82f6;"); // Bleu
            } else if (statut.contains("Terminées")) {
                data.getNode().setStyle("-fx-pie-color: #10b981;"); // Vert
            }
        }
    }

    /**
     * Applique des couleurs personnalisées au graphique des plannings
     */
    private static void applyPlanningColors(PieChart pieChart, ObservableList<PieChart.Data> pieChartData) {
        pieChart.applyCss();
        for (PieChart.Data data : pieChartData) {
            String shift = data.getName().split(" ")[0];
            if (shift.contains("JOUR")) {
                data.getNode().setStyle("-fx-pie-color: #3b82f6;"); // Bleu
            } else if (shift.contains("SOIR")) {
                data.getNode().setStyle("-fx-pie-color: #f59e0b;"); // Orange
            } else if (shift.contains("NUIT")) {
                data.getNode().setStyle("-fx-pie-color: #8b5cf6;"); // Violet
            }
        }
    }

    /**
     * Affiche un résumé textuel des statistiques (optionnel)
     */
    public static void showTextSummary() {
        List<Tache> taches = tacheService.getAllTaches();
        List<Planning> plannings = planningService.getAllPlannings();

        int aFaire = 0, enCours = 0, terminees = 0;
        for (Tache t : taches) {
            switch(t.getStatut()) {
                case "A_FAIRE": aFaire++; break;
                case "EN_COURS": enCours++; break;
                case "TERMINEE": terminees++; break;
            }
        }

        int jour = 0, soir = 0, nuit = 0;
        for (Planning p : plannings) {
            switch(p.getTypeShift()) {
                case "JOUR": jour++; break;
                case "SOIR": soir++; break;
                case "NUIT": nuit++; break;
            }
        }

        System.out.println("\n📊 RÉSUMÉ STATISTIQUE");
        System.out.println("=".repeat(50));
        System.out.println("TÂCHES:");
        System.out.println("  • À faire   : " + aFaire);
        System.out.println("  • En cours  : " + enCours);
        System.out.println("  • Terminées : " + terminees);
        System.out.println("  • TOTAL     : " + taches.size());
        System.out.println();
        System.out.println("PLANNINGS:");
        System.out.println("  • JOUR : " + jour);
        System.out.println("  • SOIR : " + soir);
        System.out.println("  • NUIT : " + nuit);
        System.out.println("  • TOTAL : " + plannings.size());
        System.out.println("=".repeat(50));
    }
}