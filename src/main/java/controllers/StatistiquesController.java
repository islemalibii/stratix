package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import services.StatistiquesService;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable {

    @FXML private BarChart<String, Number> budgetChart;
    @FXML private PieChart repartitionChart;
    @FXML private LineChart<String, Number> evolutionChart;
    @FXML private Label lblBudgetMoyen;
    @FXML private Label lblTotalServices;
    @FXML private Label lblTotalBudget;

    private StatistiquesService statsService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            statsService = new StatistiquesService();
            chargerStatistiques();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerStatistiques() throws SQLException {
        XYChart.Series<String, Number> seriesBudget = new XYChart.Series<>();
        seriesBudget.setName("Budget par catégorie (DT)");

        Map<String, Double> budgetParCategorie = statsService.getBudgetParCategorie();
        double totalBudget = 0;
        for (Map.Entry<String, Double> entry : budgetParCategorie.entrySet()) {
            seriesBudget.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            totalBudget += entry.getValue();
        }
        budgetChart.getData().add(seriesBudget);
        lblTotalBudget.setText(String.format("%,.0f DT", totalBudget));

        Map<String, Integer> repartition = statsService.getNombreServicesParCategorie();
        int totalServices = 0;
        for (int val : repartition.values()) {
            totalServices += val;
        }
        lblTotalServices.setText(String.valueOf(totalServices));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : repartition.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
        repartitionChart.setData(pieData);

        XYChart.Series<String, Number> seriesEvolution = new XYChart.Series<>();
        seriesEvolution.setName("Créations de services");

        Map<String, Integer> evolution = statsService.getServicesParMois();
        for (Map.Entry<String, Integer> entry : evolution.entrySet()) {
            seriesEvolution.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        evolutionChart.getData().add(seriesEvolution);

        lblBudgetMoyen.setText(String.format("%,.0f DT", statsService.getBudgetMoyen()));
    }
}