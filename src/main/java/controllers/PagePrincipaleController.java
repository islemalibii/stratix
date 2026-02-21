package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PagePrincipaleController {

    @FXML
    private Button btnEspaceEmploye;

    @FXML
    private Button btnDashboardGeneral;

    @FXML
    private void openEspaceEmploye() {
        try {
            System.out.println("🔄 Redirection vers Espace Employé");
            Parent root = FXMLLoader.load(getClass().getResource("/EmpMainView.fxml"));
            Stage stage = (Stage) btnEspaceEmploye.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openDashboardGeneral() {
        try {
            System.out.println("🔄 Redirection vers Dashboard Général");
            Parent root = FXMLLoader.load(getClass().getResource("/MainView.fxml"));
            Stage stage = (Stage) btnDashboardGeneral.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}