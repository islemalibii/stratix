package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private StackPane mainPane;

    private static MainController instance;

    private Parent dashboardView;
    private Parent planningView;
    private Parent tacheView;
    private Parent calendarView;
    private Parent whiteboardView;
    private Parent empMainView;  // ← Pour l'espace employé

    @FXML
    public void initialize() {
        instance = this;

        System.out.println("=== DÉBOGAGE CHARGEMENT DES FICHIERS FXML ===");
        System.out.println("Répertoire de travail: " + System.getProperty("user.dir"));

        try {
            // Dashboard
            URL dashboardUrl = getClass().getResource("/dashboard-view.fxml");
            if (dashboardUrl != null) {
                dashboardView = FXMLLoader.load(dashboardUrl);
                System.out.println("   ✅ Dashboard chargé");
            }

            // Planning
            URL planningUrl = getClass().getResource("/PlanningView.fxml");
            if (planningUrl != null) {
                planningView = FXMLLoader.load(planningUrl);
                System.out.println("   ✅ Planning chargé");
            }

            // Tâches
            URL tacheUrl = getClass().getResource("/TacheView.fxml");
            if (tacheUrl != null) {
                tacheView = FXMLLoader.load(tacheUrl);
                System.out.println("   ✅ Tâches chargé");
            }

            // Calendar
            URL calendarUrl = getClass().getResource("/calendar-view.fxml");
            if (calendarUrl != null) {
                calendarView = FXMLLoader.load(calendarUrl);
                System.out.println("   ✅ Calendar chargé");
            }

            // Whiteboard
            URL whiteboardUrl = getClass().getResource("/WhiteboardView.fxml");
            if (whiteboardUrl != null) {
                whiteboardView = FXMLLoader.load(whiteboardUrl);
                System.out.println("   ✅ Whiteboard chargé");
            }

            // Espace Employé
            URL empMainUrl = getClass().getResource("/EmpMainView.fxml");
            if (empMainUrl != null) {
                empMainView = FXMLLoader.load(empMainUrl);
                System.out.println("   ✅ Espace Employé chargé");
            }

            System.out.println("\n=== RÉSULTAT FINAL ===");
            System.out.println("Dashboard: " + (dashboardView != null ? "✅" : "❌"));
            System.out.println("Planning: " + (planningView != null ? "✅" : "❌"));
            System.out.println("Tâches: " + (tacheView != null ? "✅" : "❌"));
            System.out.println("Calendrier: " + (calendarView != null ? "✅" : "❌"));
            System.out.println("Whiteboard: " + (whiteboardView != null ? "✅" : "❌"));
            System.out.println("Espace Employé: " + (empMainView != null ? "✅" : "❌"));

            // Afficher dashboard par défaut
            showDashboard();

        } catch (IOException e) {
            System.err.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les vues: " + e.getMessage());
        }
    }

    public static void showDashboard() {
        if (instance != null && instance.mainPane != null && instance.dashboardView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.dashboardView);
            System.out.println("🔄 Navigation vers Dashboard");
        }
    }

    public static void showPlanning() {
        if (instance != null && instance.mainPane != null && instance.planningView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.planningView);
            System.out.println("🔄 Navigation vers Planning");
        }
    }

    public static void showTaches() {
        if (instance != null && instance.mainPane != null && instance.tacheView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.tacheView);
            System.out.println("🔄 Navigation vers Tâches");
        }
    }

    public static void showCalendar() {
        if (instance != null && instance.mainPane != null && instance.calendarView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.calendarView);
            System.out.println("🔄 Navigation vers Calendrier");
        }
    }

    public static void showWhiteboard() {
        if (instance != null && instance.mainPane != null && instance.whiteboardView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.whiteboardView);
            System.out.println("🔄 Navigation vers Whiteboard");
        }
    }

    // NOUVELLE MÉTHODE
    public static void showEmpMain() {
        if (instance != null && instance.mainPane != null && instance.empMainView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.empMainView);
            System.out.println("🔄 Retour à l'accueil employé");
        } else {
            System.err.println("❌ Impossible d'afficher l'accueil employé");
        }
    }

    @FXML
    private void showPlanningFromButton() { showPlanning(); }
    @FXML
    private void showTachesFromButton() { showTaches(); }
    @FXML
    private void showDashboardFromButton() { showDashboard(); }
    @FXML
    private void showCalendarFromButton() { showCalendar(); }
    @FXML
    private void showWhiteboardFromButton() { showWhiteboard(); }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}