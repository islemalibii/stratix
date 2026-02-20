package controller;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Service;
import service.PDFService;
import service.ServiceService;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ServiceController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private VBox servicesContainer;
    @FXML private Button btnArchives;
    @FXML private Button btnRetour;

    private ServiceService serviceService;
    private List<Service> allServices;
    private boolean modeArchive = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            serviceService = new ServiceService();
            filterType.getItems().addAll("Tous", "Développement", "Formation", "Maintenance", "Conseil", "Support");
            filterType.setValue("Tous");
            chargerDonnees();
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void chargerDonnees() {
        try {
            if (modeArchive) {
                allServices = serviceService.afficherArchives();
            } else {
                allServices = serviceService.afficherAll();
            }
            afficherLignes(allServices);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void afficherLignes(List<Service> services) {
        servicesContainer.getChildren().clear();
        for (Service s : services) {
            HBox row = new HBox(10);
            row.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-alignment: center-left;");
            row.setPrefHeight(50);

            Label titre = new Label(s.getTitre());
            titre.setStyle("-fx-font-weight: bold; -fx-min-width: 150; -fx-text-fill: #2c3e50;");
            titre.setPrefWidth(200);

            String catName = (s.getCategorie() != null) ? s.getCategorie().getNom() : "Non catégorisé";
            Label categorie = new Label(catName);
            categorie.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-min-width: 100;");
            categorie.setPrefWidth(120);

            Label budget = new Label(String.format("%,.0f DT", s.getBudget()));
            budget.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-min-width: 80;");
            budget.setPrefWidth(120);

            Label dateDebut = new Label(s.getDateDebut());
            dateDebut.setStyle("-fx-text-fill: #7f8c8d; -fx-min-width: 100;");
            dateDebut.setPrefWidth(120);

            Label dateFin = new Label(s.getDateFin());
            dateFin.setStyle("-fx-text-fill: #7f8c8d; -fx-min-width: 100;");
            dateFin.setPrefWidth(120);

            String responsableNom = "Non assigné";
            if (s.getUtilisateurId() > 0) {
                try {
                    responsableNom = serviceService.getResponsableNom(s.getUtilisateurId());
                } catch (SQLException e) {
                    responsableNom = "Erreur";
                }
            }

            Label resp = new Label(responsableNom);
            resp.setStyle("-fx-text-fill: #7f8c8d; -fx-min-width: 100; -fx-alignment: center;");
            resp.setPrefWidth(100);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox actions = new HBox(5);
            actions.setAlignment(Pos.CENTER_RIGHT);
            actions.setPrefWidth(200);

            if (modeArchive) {
                Button btnDesarchiver = new Button("Restaurer");
                btnDesarchiver.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
                btnDesarchiver.setPrefWidth(90);
                btnDesarchiver.setOnAction(e -> desarchiverService(s));
                actions.getChildren().add(btnDesarchiver);
            } else {
                Button btnArchiver = new Button("Archiver");
                btnArchiver.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
                btnArchiver.setPrefWidth(80);
                btnArchiver.setOnAction(e -> archiverService(s));

                Button btnModifier = new Button("Modifier");
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
                btnModifier.setPrefWidth(80);
                btnModifier.setOnAction(e -> ouvrirModification(s));

                actions.getChildren().addAll(btnArchiver, btnModifier);
            }

            row.getChildren().addAll(titre, categorie, budget, dateDebut, dateFin, resp, spacer, actions);
            servicesContainer.getChildren().add(row);
        }
    }

    private void archiverService(Service service) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Archiver ce service ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                serviceService.archiver(service.getId());
                chargerDonnees();
                showAlert("Succès", "Service archivé!");
            } catch (SQLException e) {
                showAlert("Erreur", e.getMessage());
            }
        }
    }

    private void desarchiverService(Service service) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Restaurer ce service ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                serviceService.desarchiver(service.getId());
                chargerDonnees();
                showAlert("Succès", "Service restauré!");
            } catch (SQLException e) {
                showAlert("Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleVoirArchives() {
        modeArchive = true;
        chargerDonnees();
        btnArchives.setVisible(false);
        btnRetour.setVisible(true);
    }

    @FXML
    private void handleRetour() {
        modeArchive = false;
        chargerDonnees();
        btnArchives.setVisible(true);
        btnRetour.setVisible(false);
    }

    @FXML
    private void handleSearch() {
        String texteRecherche = searchField.getText();
        String categorieFiltre = filterType.getValue();
        try {
            List<Service> resultats;
            if (modeArchive) {
                resultats = serviceService.afficherArchives().stream()
                        .filter(s -> texteRecherche.isEmpty() ||
                                s.getTitre().toLowerCase().contains(texteRecherche.toLowerCase()))
                        .toList();
            } else {
                resultats = serviceService.rechercher(texteRecherche, categorieFiltre);
            }
            afficherLignes(resultats);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    private void ouvrirModification(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-service.fxml"));
            Parent root = loader.load();
            AjoutController controller = loader.getController();
            controller.setServiceService(serviceService);
            controller.setServiceAModifier(service);
            Stage stage = new Stage();
            stage.setTitle("Modifier Service");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            chargerDonnees();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-service.fxml"));
            Parent root = loader.load();
            AjoutController controller = loader.getController();
            controller.setServiceService(serviceService);
            Stage stage = new Stage();
            stage.setTitle("Ajouter Service");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            chargerDonnees();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleExporterPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les services");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("rapport_services_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");

        File file = fileChooser.showSaveDialog(servicesContainer.getScene().getWindow());

        if (file != null) {
            try {
                PDFService.exporterServices(allServices, file.getAbsolutePath());
                showAlert("Succès", "Rapport PDF exporté avec succès !\n" + file.getName());
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'exporter le PDF: " + e.getMessage());
            }
        }
    }
}