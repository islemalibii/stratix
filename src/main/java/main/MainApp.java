package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Charger MainView.fxml depuis la racine des resources
        URL fxmlLocation = getClass().getResource("/MainView.fxml");
        if (fxmlLocation == null) {
            System.err.println("ERREUR: MainView.fxml non trouvé!");
            System.err.println("Chemin recherché: /MainView.fxml");
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Gestion Planning & Tâches");
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}