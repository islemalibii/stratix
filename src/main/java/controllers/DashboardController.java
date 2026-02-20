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
        statsService = new StatsService();
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        DashboardStats stats = statsService.getDashboardStats();

        lblTotalTaches.setText(String.valueOf(stats.getTotalTaches()));
        lblEnCours.setText(String.valueOf(stats.getTachesEnCours()));
        lblTerminees.setText(String.valueOf(stats.getTachesTerminees()));
        lblEnRetard.setText(String.valueOf(stats.getTachesEnRetard()));
        lblTotalEmployes.setText(String.valueOf(stats.getTotalEmployes()));
        lblEnPoste.setText(String.valueOf(stats.getEmployesEnPoste()));
        lblAbsents.setText(String.valueOf(stats.getEmployesAbsents()));
        lblAFaire.setText(String.valueOf(stats.getTachesAFaire()));
    }

    @FXML
    private void openTaches() {
        MainController.showTaches();
    }

    @FXML
    private void openPlanning() {
        MainController.showPlanning();
    }

    @FXML
    private void openCalendar() {
        MainController.showCalendar();
    }
}