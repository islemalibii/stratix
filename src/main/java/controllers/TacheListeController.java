package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;        // ← AJOUTE CECI
import javafx.scene.layout.Priority;      // ← AJOUTE CECI
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.FileChooser;

import models.Tache;
import models.Employe;
import services.SERVICETache;
import services.EmployeeService;
import services.PDFExportService;
import services.ExcelExportService;
import controllers.StatsChartController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TacheListeController implements Initializable {

    @FXML private VBox cardsContainer;
    @FXML private TextField searchField;
    @FXML private Label lblTotalTaches;
    @FXML private Label lblAFaire;
    @FXML private Label lblEnCours;
    @FXML private Label lblTerminees;
    @FXML private Label lblTachesCount;

    private SERVICETache tacheService;
    private EmployeeService employeService;
    private ObservableList<Tache> tacheList = FXCollections.observableArrayList();
    private FilteredList<Tache> filteredData;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation TacheListeController (Version Cartes) ===");

        tacheService = new SERVICETache();
        employeService = new EmployeeService();

        chargerTaches();

        // Configuration de la recherche
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });
    }

    private void chargerTaches() {
        List<Tache> toutesTaches = tacheService.getAllTaches();
        tacheList.setAll(toutesTaches);
        filteredData = new FilteredList<>(tacheList, p -> true);

        afficherCartes(filteredData);
        mettreAJourStatistiques(toutesTaches);

        lblTachesCount.setText(toutesTaches.size() + " tâche" + (toutesTaches.size() > 1 ? "s" : ""));
        System.out.println("✅ " + toutesTaches.size() + " tâches chargées");
    }

    private void filterData(String searchText) {
        if (filteredData == null) return;

        filteredData.setPredicate(tache -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerSearch = searchText.toLowerCase();
            String titre = tache.getTitre() != null ? tache.getTitre().toLowerCase() : "";
            String description = tache.getDescription() != null ? tache.getDescription().toLowerCase() : "";

            Employe emp = employeService.getEmployeById(tache.getEmployeId());
            String employeNom = emp != null ? emp.getUsername().toLowerCase() : "";

            return titre.contains(lowerSearch) ||
                    description.contains(lowerSearch) ||
                    employeNom.contains(lowerSearch);
        });

        afficherCartes(filteredData);
        mettreAJourStatistiques(filteredData);
        lblTachesCount.setText(filteredData.size() + " tâche" + (filteredData.size() > 1 ? "s" : ""));
    }

    private void afficherCartes(FilteredList<Tache> taches) {
        cardsContainer.getChildren().clear();

        if (taches.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

            Label iconLabel = new Label("📋");
            iconLabel.setFont(Font.font("System", 48));

            Label messageLabel = new Label("Aucune tâche trouvée");
            messageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            messageLabel.setTextFill(javafx.scene.paint.Color.web("#64748b"));

            cardsContainer.getChildren().add(emptyBox);
            emptyBox.getChildren().addAll(iconLabel, messageLabel);
            return;
        }

        for (Tache t : taches) {
            cardsContainer.getChildren().add(createTaskCard(t));
        }
    }

    private HBox createTaskCard(Tache tache) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");
        card.setPrefHeight(100);
        card.setMaxWidth(Double.MAX_VALUE);

        // Indicateur de priorité
        Region priorityIndicator = new Region();
        priorityIndicator.setPrefWidth(6);
        priorityIndicator.setPrefHeight(70);
        priorityIndicator.setStyle(getPriorityColor(tache.getPriorite()));

        // Icône priorité
        Label iconLabel = new Label(getPriorityIcon(tache.getPriorite()));
        iconLabel.setFont(Font.font("System", 24));
        iconLabel.setPrefWidth(40);
        iconLabel.setAlignment(Pos.CENTER);

        // Informations principales
        VBox infoBox = new VBox(8);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Titre
        Label titleLabel = new Label(tache.getTitre());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(javafx.scene.paint.Color.web("#0f172a"));

        // Description
        String description = tache.getDescription() != null ? tache.getDescription() : "";
        Label descLabel = new Label(truncateText(description, 60));
        descLabel.setFont(Font.font("System", 13));
        descLabel.setTextFill(javafx.scene.paint.Color.web("#475569"));

        // Métadonnées
        HBox metaBox = new HBox(20);

        Employe emp = employeService.getEmployeById(tache.getEmployeId());
        String employeName = emp != null ? emp.getUsername() : "Employé " + tache.getEmployeId();
        Label employeLabel = new Label("👤 " + employeName);
        employeLabel.setFont(Font.font("System", 12));
        employeLabel.setTextFill(javafx.scene.paint.Color.web("#64748b"));

        Label deadlineLabel = new Label("📅 " + formatDate(tache.getDeadline()));
        deadlineLabel.setFont(Font.font("System", 12));
        deadlineLabel.setTextFill(javafx.scene.paint.Color.web("#64748b"));

        Label statutLabel = new Label(getStatusIcon(tache.getStatut()) + " " + getStatusText(tache.getStatut()));
        statutLabel.setFont(Font.font("System", 12));
        statutLabel.setTextFill(getStatusColor(tache.getStatut()));

        metaBox.getChildren().addAll(employeLabel, deadlineLabel, statutLabel);

        infoBox.getChildren().addAll(titleLabel, descLabel, metaBox);

        // Boutons d'action
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 8 12; -fx-background-radius: 5; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> modifierTache(tache));

        Button btnDelete = new Button("🗑️");
        btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 8 12; -fx-background-radius: 5; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> supprimerTache(tache));

        actionBox.getChildren().addAll(btnEdit, btnDelete);

        card.getChildren().addAll(priorityIndicator, iconLabel, infoBox, actionBox);

        // Animation au survol
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.15), 12, 0, 0, 5);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");
        });

        return card;
    }

    private String getPriorityColor(String priorite) {
        switch(priorite) {
            case "HAUTE": return "-fx-background-color: #ef4444; -fx-background-radius: 5;";
            case "MOYENNE": return "-fx-background-color: #f59e0b; -fx-background-radius: 5;";
            case "BASSE": return "-fx-background-color: #10b981; -fx-background-radius: 5;";
            default: return "-fx-background-color: #94a3b8; -fx-background-radius: 5;";
        }
    }

    private String getPriorityIcon(String priorite) {
        switch(priorite) {
            case "HAUTE": return "🔴";
            case "MOYENNE": return "🟡";
            case "BASSE": return "🟢";
            default: return "⚪";
        }
    }

    private String getStatusIcon(String statut) {
        switch(statut) {
            case "A_FAIRE": return "⏳";
            case "EN_COURS": return "🔄";
            case "TERMINEE": return "✅";
            default: return "📌";
        }
    }

    private String getStatusText(String statut) {
        switch(statut) {
            case "A_FAIRE": return "À faire";
            case "EN_COURS": return "En cours";
            case "TERMINEE": return "Terminée";
            default: return statut;
        }
    }

    private javafx.scene.paint.Color getStatusColor(String statut) {
        switch(statut) {
            case "A_FAIRE": return javafx.scene.paint.Color.web("#f59e0b");
            case "EN_COURS": return javafx.scene.paint.Color.web("#3b82f6");
            case "TERMINEE": return javafx.scene.paint.Color.web("#10b981");
            default: return javafx.scene.paint.Color.web("#64748b");
        }
    }

    private String formatDate(Object date) {
        if (date == null) return "Non définie";
        try {
            if (date instanceof java.sql.Date) {
                return ((java.sql.Date) date).toLocalDate().format(dateFormatter);
            }
        } catch (Exception e) {
            return "Date invalide";
        }
        return "Non définie";
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.isEmpty()) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private void mettreAJourStatistiques(List<Tache> taches) {
        int total = taches.size();
        int aFaire = 0, enCours = 0, terminees = 0;

        for (Tache t : taches) {
            switch(t.getStatut()) {
                case "A_FAIRE": aFaire++; break;
                case "EN_COURS": enCours++; break;
                case "TERMINEE": terminees++; break;
            }
        }

        lblTotalTaches.setText(String.valueOf(total));
        lblAFaire.setText(String.valueOf(aFaire));
        lblEnCours.setText(String.valueOf(enCours));
        lblTerminees.setText(String.valueOf(terminees));
    }

    private void mettreAJourStatistiques(FilteredList<Tache> taches) {
        int total = taches.size();
        int aFaire = 0, enCours = 0, terminees = 0;

        for (Tache t : taches) {
            switch(t.getStatut()) {
                case "A_FAIRE": aFaire++; break;
                case "EN_COURS": enCours++; break;
                case "TERMINEE": terminees++; break;
            }
        }

        lblTotalTaches.setText(String.valueOf(total));
        lblAFaire.setText(String.valueOf(aFaire));
        lblEnCours.setText(String.valueOf(enCours));
        lblTerminees.setText(String.valueOf(terminees));
    }

    private void modifierTache(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TacheView.fxml"));
            Parent view = loader.load();
            TacheController controller = loader.getController();
            controller.setTacheToEdit(tache);

            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerTache(Tache tache) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la tâche");
        confirm.setContentText("Voulez-vous vraiment supprimer \"" + tache.getTitre() + "\" ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                tacheService.deleteTache(tache.getId());
                chargerTaches();
            }
        });
    }

    @FXML
    private void nouvelleTache() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/TacheView.fxml"));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportToPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les tâches en PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            fileChooser.setInitialFileName("taches_" + LocalDate.now() + ".pdf");

            File file = fileChooser.showSaveDialog(cardsContainer.getScene().getWindow());

            if (file != null) {
                List<Tache> toutesTaches = tacheService.getAllTaches();
                PDFExportService.exportTachesToPDF(toutesTaches, file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("✅ PDF exporté avec succès !\n" + file.getName());
                alert.showAndWait();
            }
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void exportToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les tâches en Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
            );
            fileChooser.setInitialFileName("taches_" + LocalDate.now() + ".xlsx");

            File file = fileChooser.showSaveDialog(cardsContainer.getScene().getWindow());

            if (file != null) {
                List<Tache> toutesTaches = tacheService.getAllTaches();
                ExcelExportService.exportTachesToExcel(toutesTaches, file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("✅ Excel exporté avec succès !\n" + file.getName());
                alert.showAndWait();
            }
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void showStatistics() {
        try {
            StatsChartController.showTaskStatistics();
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void showDashboardFromButton() { loadView("/dashboard-view.fxml"); }
    @FXML
    private void showPlanningFromButton() { loadView("/PlanningListeView.fxml"); }
    @FXML
    private void showTachesFromButton() { loadView("/TacheListeView.fxml"); }
    @FXML
    private void showCalendarFromButton() { loadView("/calendar-view.fxml"); }
    @FXML
    private void showWhiteboardFromButton() { loadView("/WhiteboardView.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (MainController.staticContentArea != null) {
                MainController.staticContentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            System.err.println("Error loading: " + fxmlPath);
        }
    }

    @FXML
    private void retourFormulaire() {
        showTachesFromButton();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}//*