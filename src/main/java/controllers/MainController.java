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

    @FXML
    public void initialize() {
        instance = this;

        System.out.println("=== DÉBOGAGE COMPLET ===");
        System.out.println("1. Répertoire de travail: " + System.getProperty("user.dir"));
        System.out.println("2. Classpath: " + System.getProperty("java.class.path"));

        // Vérifier le contenu du dossier resources
        URL resourceUrl = getClass().getResource("/");
        System.out.println("3. Dossier resources: " + resourceUrl);

        try {
            // Dashboard
            System.out.println("\n--- Chargement dashboard-view.fxml ---");
            URL dashboardUrl = getClass().getResource("/dashboard-view.fxml");
            System.out.println("   URL: " + dashboardUrl);
            if (dashboardUrl != null) {
                dashboardView = FXMLLoader.load(dashboardUrl);
                System.out.println("   ✅ Dashboard chargé");
            } else {
                System.out.println("   ❌ dashboard-view.fxml non trouvé!");
            }

            // Planning
            System.out.println("\n--- Chargement PlanningView.fxml ---");
            URL planningUrl = getClass().getResource("/PlanningView.fxml");
            System.out.println("   URL: " + planningUrl);
            if (planningUrl != null) {
                planningView = FXMLLoader.load(planningUrl);
                System.out.println("   ✅ Planning chargé");
            } else {
                System.out.println("   ❌ PlanningView.fxml non trouvé!");
            }

            // Tâches - DÉBOGAGE APPROFONDI
            System.out.println("\n--- Chargement TacheView.fxml ---");

            // Essai 1: Avec slash
            URL tacheUrl1 = getClass().getResource("/TacheView.fxml");
            System.out.println("   Essai 1 (/TacheView.fxml): " + tacheUrl1);

            // Essai 2: Sans slash
            URL tacheUrl2 = getClass().getResource("TacheView.fxml");
            System.out.println("   Essai 2 (TacheView.fxml): " + tacheUrl2);

            // Essai 3: Avec views/
            URL tacheUrl3 = getClass().getResource("/views/TacheView.fxml");
            System.out.println("   Essai 3 (/views/TacheView.fxml): " + tacheUrl3);

            // Essai 4: Avec T majuscule/minuscule
            URL tacheUrl4 = getClass().getResource("/tache-view.fxml");
            System.out.println("   Essai 4 (/tache-view.fxml): " + tacheUrl4);

            // Utiliser le premier trouvé
            if (tacheUrl1 != null) {
                tacheView = FXMLLoader.load(tacheUrl1);
                System.out.println("   ✅ Tâches chargé avec Essai 1");
            } else if (tacheUrl3 != null) {
                tacheView = FXMLLoader.load(tacheUrl3);
                System.out.println("   ✅ Tâches chargé avec Essai 3");
            } else if (tacheUrl4 != null) {
                tacheView = FXMLLoader.load(tacheUrl4);
                System.out.println("   ✅ Tâches chargé avec Essai 4");
            } else {
                System.out.println("   ❌ TacheView.fxml introuvable dans tous les emplacements!");
            }

            // Calendar
            System.out.println("\n--- Chargement calendar-view.fxml ---");
            URL calendarUrl = getClass().getResource("/calendar-view.fxml");
            System.out.println("   URL: " + calendarUrl);
            if (calendarUrl != null) {
                calendarView = FXMLLoader.load(calendarUrl);
                System.out.println("   ✅ Calendar chargé");
            } else {
                System.out.println("   ❌ calendar-view.fxml non trouvé!");
            }

            // Whiteboard
            System.out.println("\n--- Chargement whiteboard-view.fxml ---");
            URL whiteboardUrl = getClass().getResource("/whiteboard-view.fxml");
            System.out.println("   URL: " + whiteboardUrl);
            if (whiteboardUrl != null) {
                whiteboardView = FXMLLoader.load(whiteboardUrl);
                System.out.println("   ✅ Whiteboard chargé");
            } else {
                System.out.println("   ❌ whiteboard-view.fxml non trouvé!");
            }

            System.out.println("\n=== RÉSULTAT FINAL ===");
            System.out.println("Dashboard: " + (dashboardView != null ? "✅" : "❌"));
            System.out.println("Planning: " + (planningView != null ? "✅" : "❌"));
            System.out.println("Tâches: " + (tacheView != null ? "✅" : "❌"));
            System.out.println("Calendrier: " + (calendarView != null ? "✅" : "❌"));
            System.out.println("Whiteboard: " + (whiteboardView != null ? "✅" : "❌"));

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
        } else {
            System.err.println("❌ Impossible d'afficher Dashboard");
        }
    }

    public static void showPlanning() {
        if (instance != null && instance.mainPane != null && instance.planningView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.planningView);
            System.out.println("🔄 Navigation vers Planning");
        } else {
            System.err.println("❌ Impossible d'afficher Planning");
        }
    }

    public static void showTaches() {
        if (instance != null && instance.mainPane != null && instance.tacheView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.tacheView);
            System.out.println("🔄 Navigation vers Tâches");
        } else {
            System.err.println("❌ Impossible d'afficher Tâches - tacheView est null");
        }
    }

    public static void showCalendar() {
        if (instance != null && instance.mainPane != null && instance.calendarView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.calendarView);
            System.out.println("🔄 Navigation vers Calendrier");
        } else {
            System.err.println("❌ Impossible d'afficher Calendrier");
        }
    }

    public static void showWhiteboard() {
        if (instance != null && instance.mainPane != null && instance.whiteboardView != null) {
            instance.mainPane.getChildren().clear();
            instance.mainPane.getChildren().add(instance.whiteboardView);
            System.out.println("🔄 Navigation vers Whiteboard");
        } else {
            System.err.println("❌ Impossible d'afficher Whiteboard");
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