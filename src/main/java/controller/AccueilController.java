package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import model.UserRole;

public class AccueilController {

    @FXML
    private void handleAdmin(ActionEvent event) {
        ouvrirApplication(event, "admin");
    }

    @FXML
    private void handleEmployee(ActionEvent event) {
        ouvrirApplication(event, "employee");
    }

    private void ouvrirApplication(ActionEvent event, String role) {
        try {
            UserRole.getInstance().setRole(role);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/service-view.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setUserRole(role);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 1300, 750);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("stratiX - " + (role.equals("admin") ? "Administrateur" : "Employé"));
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}