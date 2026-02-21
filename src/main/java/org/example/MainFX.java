package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/service-view.fxml"));
            HBox root = loader.load();
            Scene scene = new Scene(root, 1300, 750);

            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            primaryStage.setTitle("stratiX - Gestion des Services");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}