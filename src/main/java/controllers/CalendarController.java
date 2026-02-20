package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import models.Employe;
import models.Planning;
import services.EmployeeService;
import services.SERVICEPlanning;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CalendarController {

    @FXML private Label lblMonthYear;
    @FXML private GridPane calendarGrid;
    @FXML private ListView<String> eventsListView;

    private YearMonth currentYearMonth;
    private SERVICEPlanning planningService;
    private EmployeeService employeService;
    private Map<LocalDate, List<Planning>> planningsByDate;

    @FXML
    public void initialize() {
        planningService = new SERVICEPlanning();
        employeService = new EmployeeService();
        currentYearMonth = YearMonth.now();

        planningsByDate = new HashMap<>();

        loadPlannings();
        drawCalendar();
    }

    private void loadPlannings() {
        planningsByDate.clear();
        List<Planning> allPlannings = planningService.getAllPlannings();

        for (Planning p : allPlannings) {
            LocalDate date = p.getDate().toLocalDate();
            if (!planningsByDate.containsKey(date)) {
                planningsByDate.put(date, new java.util.ArrayList<>());
            }
            planningsByDate.get(date).add(p);
        }
    }

    private void drawCalendar() {
        calendarGrid.getChildren().clear();

        // Ajouter les noms des jours
        String[] daysOfWeek = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefWidth(120);
            dayLabel.setPrefHeight(30);
            dayLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5;");
            calendarGrid.add(dayLabel, i, 0);
        }

        // Obtenir le premierr jour du moiss
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // 1=Lundi, 7=Dimanche

        // Obtenir le nombre de jours dans le mois
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // Remplir les jours
        int row = 1;
        int col = dayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);

            // Créer la cellule du jour
            VBox dayCell = new VBox(5);
            dayCell.setPrefWidth(120);
            dayCell.setPrefHeight(100);
            dayCell.setPadding(new Insets(5));
            dayCell.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: white;");

            // Numéro du jour
            Label dayNumber = new Label(String.valueOf(day));
            dayNumber.setFont(Font.font("Arial", FontWeight.BOLD, 12));

            dayCell.getChildren().add(dayNumber);

            // Ajouter les plannings du jour
            if (planningsByDate.containsKey(date)) {
                List<Planning> dayPlannings = planningsByDate.get(date);

                for (int i = 0; i < Math.min(dayPlannings.size(), 2); i++) {
                    Planning p = dayPlannings.get(i);
                    Employe emp = employeService.getEmployeById(p.getEmployeId());
                    String empName = (emp != null) ? emp.getUsername() : "E" + p.getEmployeId();

                    Label eventLabel = new Label("• " + empName);
                    eventLabel.setFont(Font.font("Arial", 10));

                    // Couleur selon shift
                    String color;
                    switch(p.getTypeShift()) {
                        case "JOUR": color = "#3b82f6"; break;
                        case "SOIR": color = "#f59e0b"; break;
                        case "NUIT": color = "#8b5cf6"; break;
                        default: color = "#6b7280";
                    }
                    eventLabel.setStyle("-fx-text-fill: " + color + ";");

                    dayCell.getChildren().add(eventLabel);
                }

                if (dayPlannings.size() > 2) {
                    Label moreLabel = new Label("+" + (dayPlannings.size() - 2) + "...");
                    moreLabel.setFont(Font.font("Arial", 10));
                    moreLabel.setStyle("-fx-text-fill: #6b7280;");
                    dayCell.getChildren().add(moreLabel);
                }

                // Ajouter un effet si c'est aujourd'hui
                if (date.equals(LocalDate.now())) {
                    dayCell.setStyle("-fx-border-color: #3b82f6; -fx-border-width: 2; -fx-background-color: #eff6ff;");
                }
            } else {
                // Si pas d'événements
                if (date.equals(LocalDate.now())) {
                    dayCell.setStyle("-fx-border-color: #3b82f6; -fx-border-width: 2; -fx-background-color: #eff6ff;");
                }
            }

            // Rendre la cellule cliquable
            dayCell.setOnMouseClicked(event -> showDayEvents(date));

            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        // Mettre à jour le titre
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        lblMonthYear.setText(currentYearMonth.format(formatter));
    }

    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        loadPlannings();
        drawCalendar();
    }

    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        loadPlannings();
        drawCalendar();
    }

    private void showDayEvents(LocalDate date) {
        eventsListView.getItems().clear();

        if (planningsByDate.containsKey(date)) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            eventsListView.getItems().add("📅 " + date.format(dateFormatter));
            eventsListView.getItems().add("━━━━━━━━━━━━━━━━");

            for (Planning p : planningsByDate.get(date)) {
                Employe emp = employeService.getEmployeById(p.getEmployeId());
                String empName = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();
                String poste = (emp != null) ? "(" + emp.getPoste() + ")" : "";

                String shiftEmoji = "";
                switch(p.getTypeShift()) {
                    case "JOUR": shiftEmoji = "☀️"; break;
                    case "SOIR": shiftEmoji = "🌆"; break;
                    case "NUIT": shiftEmoji = "🌙"; break;
                }

                eventsListView.getItems().add(
                        shiftEmoji + " " + empName + " " + poste + "\n   " +
                                p.getHeureDebut().toLocalTime().format(timeFormatter) + " - " +
                                p.getHeureFin().toLocalTime().format(timeFormatter)
                );
            }
        } else {
            eventsListView.getItems().add("Aucun planning pour cette date");
        }
    }
}