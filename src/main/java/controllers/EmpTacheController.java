package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import models.Tache;
import Services.SERVICETache;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmpTacheController {

    @FXML private TableView<Tache> tableTaches;
    @FXML private TableColumn<Tache, String> colTitre;
    @FXML private TableColumn<Tache, String> colDescription;
    @FXML private TableColumn<Tache, String> colDeadline;
    @FXML private TableColumn<Tache, String> colStatut;
    @FXML private TableColumn<Tache, String> colPriorite;

    private SERVICETache tacheService = new SERVICETache();

    @FXML
    public void initialize() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colPriorite.setCellValueFactory(new PropertyValueFactory<>("priorite"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDeadline.setCellValueFactory(cellData -> {
            Tache t = cellData.getValue();
            return new SimpleStringProperty(t.getDeadline() != null ?
                    t.getDeadline().toLocalDate().format(formatter) : "");
        });

        chargerToutesTaches();
    }

    private void chargerToutesTaches() {
        // Ici, on récupère toutes les tâches via le service (qui utilise le Singleton MyDataBase)
        List<Tache> toutesTaches = tacheService.getAllTaches();
        tableTaches.setItems(FXCollections.observableArrayList(toutesTaches));
    }

    @FXML
    private void showMesTaches() {
        chargerToutesTaches();
    }

    @FXML
    private void showMonPlanning() {
        try {
            if (MainController.staticContentArea != null) {
                Node node = FXMLLoader.load(getClass().getResource("/EmpPlanningView.fxml"));
                MainController.staticContentArea.getChildren().setAll(node);
                System.out.println("🔄 Navigation vers Planning réussie");
            } else {
                System.err.println("❌ Erreur : staticContentArea est null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}