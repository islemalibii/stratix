package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.Planning;
import services.SERVICEPlanning;
import utils.SessionManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EmpPlanningController {

    @FXML private VBox cardsContainer;
    @FXML private ComboBox<String> filterMoisCombo;
    @FXML private ComboBox<String> filterAnneeCombo;
    @FXML private Label lblTotalShifts;
    @FXML private Label lblJoursTravailles;
    @FXML private Label lblConges;
    @FXML private Label lblPlanningCount;
    @FXML private Label lbWelcomeName;

    private SERVICEPlanning planningService = new SERVICEPlanning();
    private ObservableList<Planning> planningList = FXCollections.observableArrayList();
    private FilteredList<Planning> filteredData;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
    private int currentUserId;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation EmpPlanningController ===");

        // Récupérer l'ID de l'employé connecté
        currentUserId = SessionManager.getInstance().getUserId();
        String userEmail = SessionManager.getInstance().getEmail();
        String userRole = SessionManager.getInstance().getRole();

        System.out.println("🔍 INFORMATIONS DE SESSION:");
        System.out.println("   📌 ID utilisateur connecté: " + currentUserId);
        System.out.println("   📌 Email: " + userEmail);
        System.out.println("   📌 Rôle: " + userRole);

        // Afficher l'ID dans l'interface pour débogage
        String userName = (userEmail != null && userEmail.contains("@"))
                ? userEmail.substring(0, userEmail.indexOf('@'))
                : "Employé";
        lbWelcomeName.setText("👤 " + userName + " (ID: " + currentUserId + ")");

        // Initialiser les filtres de mois
        String[] mois = {"Tous", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        filterMoisCombo.getItems().addAll(mois);
        filterMoisCombo.setValue("Tous");

        // Initialiser les filtres d'année
        int anneeCourante = LocalDate.now().getYear();
        filterAnneeCombo.getItems().addAll("Tous",
                String.valueOf(anneeCourante - 1),
                String.valueOf(anneeCourante),
                String.valueOf(anneeCourante + 1));
        filterAnneeCombo.setValue("Tous");

        // Charger TOUS les plannings (sans filtre)
        chargerTousPlannings();

        // Configurer les filtres
        setupFilters();
    }

    /**
     * Charge TOUS les plannings de la base de données
     */
    private void chargerTousPlannings() {
        try {
            System.out.println("\n🔍 CHARGEMENT DE TOUS LES PLANNINGS");

            List<Planning> tousPlannings = planningService.getAllPlannings();
            System.out.println("📋 TOTAL des plannings dans la base: " + tousPlannings.size());

            if (tousPlannings.isEmpty()) {
                System.out.println("⚠️ La base de données ne contient aucun planning !");
            } else {
                // Afficher tous les plannings avec leurs détails
                System.out.println("\n--- LISTE DE TOUS LES PLANNINGS ---");
                for (Planning p : tousPlannings) {
                    String dateStr = p.getDate() != null ? p.getDate().toString() : "null";
                    String type = p.getTypeShift() != null ? p.getTypeShift() : "null";
                    String debut = p.getHeureDebut() != null ? p.getHeureDebut().toString() : "null";
                    String fin = p.getHeureFin() != null ? p.getHeureFin().toString() : "null";
                    int empId = p.getEmployeId();

                    System.out.println("   📅 Planning ID: " + p.getId() +
                            "\n      Date: " + dateStr +
                            "\n      Type: " + type +
                            "\n      Heures: " + debut + " - " + fin +
                            "\n      Employé ID: " + empId +
                            "\n");
                }

                // Afficher les IDs des employés qui ont des plannings
                List<Integer> employeIds = tousPlannings.stream()
                        .map(Planning::getEmployeId)
                        .distinct()
                        .collect(Collectors.toList());
                System.out.println("📊 IDs des employés avec plannings: " + employeIds);
            }

            // Utiliser TOUS les plannings sans filtre
            planningList.setAll(tousPlannings);
            filteredData = new FilteredList<>(planningList, p -> true);

            // Afficher les cartes
            afficherCartes(filteredData);

            // Mettre à jour les statistiques
            mettreAJourStatistiques(tousPlannings);

            lblPlanningCount.setText(tousPlannings.size() + " jour(s)");

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement: " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte
            showAlert("Erreur", "Impossible de charger les plannings: " + e.getMessage());
        }
    }

    private void setupFilters() {
        filterMoisCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            appliquerFiltres();
        });

        filterAnneeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            appliquerFiltres();
        });
    }

    @FXML
    private void appliquerFiltres() {
        if (filteredData == null) return;

        String moisFilter = filterMoisCombo.getValue();
        String anneeFilter = filterAnneeCombo.getValue();

        filteredData.setPredicate(planning -> {
            LocalDate date = planning.getDate().toLocalDate();

            // Filtre par mois
            if (moisFilter != null && !moisFilter.equals("Tous")) {
                String moisPlanning = date.format(DateTimeFormatter.ofPattern("MMMM"));
                if (!moisPlanning.equalsIgnoreCase(moisFilter)) {
                    return false;
                }
            }

            // Filtre par année
            if (anneeFilter != null && !anneeFilter.equals("Tous")) {
                int annee = Integer.parseInt(anneeFilter);
                if (date.getYear() != annee) {
                    return false;
                }
            }

            return true;
        });

        afficherCartes(filteredData);
        lblPlanningCount.setText(filteredData.size() + " jour(s)");
    }

    private void afficherCartes(FilteredList<Planning> plannings) {
        cardsContainer.getChildren().clear();

        if (plannings.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

            Label iconLabel = new Label("📅");
            iconLabel.setFont(Font.font("System", 48));

            Label messageLabel = new Label("Aucun planning trouvé");
            messageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            messageLabel.setTextFill(Color.web("#64748b"));

            Label subLabel = new Label("Aucun shift n'est planifié");
            subLabel.setFont(Font.font("System", 14));
            subLabel.setTextFill(Color.web("#94a3b8"));

            emptyBox.getChildren().addAll(iconLabel, messageLabel, subLabel);
            cardsContainer.getChildren().add(emptyBox);
            return;
        }

        // Grouper par date pour un affichage plus organisé
        LocalDate currentDate = null;
        VBox dateGroup = null;

        for (Planning p : plannings) {
            LocalDate planningDate = p.getDate().toLocalDate();

            if (currentDate == null || !currentDate.equals(planningDate)) {
                // Nouveau groupe de date
                currentDate = planningDate;
                dateGroup = createDateGroup(currentDate);
                cardsContainer.getChildren().add(dateGroup);
            }

            // Ajouter la carte du planning au groupe
            if (dateGroup != null) {
                dateGroup.getChildren().add(createPlanningCard(p));
            }
        }
    }

    private VBox createDateGroup(LocalDate date) {
        VBox group = new VBox(8);
        group.setPadding(new Insets(5, 0, 10, 0));

        // Déterminer si c'est aujourd'hui
        String jourText;
        if (date.equals(LocalDate.now())) {
            jourText = "📌 AUJOURD'HUI - " + date.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy"));
        } else {
            jourText = date.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy"));
        }

        Label dateLabel = new Label(jourText);
        dateLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        dateLabel.setTextFill(date.equals(LocalDate.now()) ? Color.web("#3b82f6") : Color.web("#334155"));
        dateLabel.setPadding(new Insets(5, 10, 5, 10));

        group.getChildren().add(dateLabel);
        return group;
    }

    private HBox createPlanningCard(Planning planning) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");
        card.setPrefHeight(80);
        card.setMaxWidth(Double.MAX_VALUE);

        // Indicateur de couleur selon le type
        Region typeIndicator = new Region();
        typeIndicator.setPrefWidth(6);
        typeIndicator.setPrefHeight(50);
        typeIndicator.setStyle(getColorForType(planning.getTypeShift()));

        // Icône selon le type
        Label iconLabel = new Label(getIconForType(planning.getTypeShift()));
        iconLabel.setFont(Font.font("System", 26));
        iconLabel.setPrefWidth(45);
        iconLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Informations principales
        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Type et badge
        HBox typeBox = new HBox(10);
        Label typeLabel = new Label(getDisplayType(planning.getTypeShift()));
        typeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        typeLabel.setTextFill(Color.web("#0f172a"));

        Label typeBadge = new Label(planning.getTypeShift());
        typeBadge.setStyle(getBadgeStyle(planning.getTypeShift()));
        typeBadge.setPadding(new Insets(3, 12, 3, 12));
        typeBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        typeBox.getChildren().addAll(typeLabel, typeBadge);

        // Horaires
        Label timeLabel = new Label(formatTime(planning.getHeureDebut()) + " → " + formatTime(planning.getHeureFin()));
        timeLabel.setFont(Font.font("System", 14));
        timeLabel.setTextFill(Color.web("#475569"));

        // Ajouter l'ID de l'employé pour débogage (optionnel)
        Label empIdLabel = new Label("Employé ID: " + planning.getEmployeId());
        empIdLabel.setFont(Font.font("System", 11));
        empIdLabel.setTextFill(Color.web("#94a3b8"));

        infoBox.getChildren().addAll(typeBox, timeLabel, empIdLabel);

        card.getChildren().addAll(typeIndicator, iconLabel, infoBox);

        // Animation au survol
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.15), 12, 0, 0, 5);");
            card.setTranslateX(2);
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");
            card.setTranslateX(0);
        });

        return card;
    }

    private String getColorForType(String type) {
        if (type == null) return "-fx-background-color: #64748b; -fx-background-radius: 5;";
        switch (type) {
            case "JOUR": return "-fx-background-color: #3b82f6; -fx-background-radius: 5;";
            case "SOIR": return "-fx-background-color: #8b5cf6; -fx-background-radius: 5;";
            case "NUIT": return "-fx-background-color: #1e293b; -fx-background-radius: 5;";
            case "CONGE": return "-fx-background-color: #10b981; -fx-background-radius: 5;";
            case "MALADIE": return "-fx-background-color: #ef4444; -fx-background-radius: 5;";
            case "FORMATION": return "-fx-background-color: #f59e0b; -fx-background-radius: 5;";
            default: return "-fx-background-color: #64748b; -fx-background-radius: 5;";
        }
    }

    private String getIconForType(String type) {
        if (type == null) return "📋";
        switch (type) {
            case "JOUR": return "☀️";
            case "SOIR": return "🌙";
            case "NUIT": return "🌌";
            case "CONGE": return "🏖️";
            case "MALADIE": return "🤒";
            case "FORMATION": return "📚";
            default: return "📋";
        }
    }

    private String getDisplayType(String type) {
        if (type == null) return "Autre";
        switch (type) {
            case "JOUR": return "Journée";
            case "SOIR": return "Soirée";
            case "NUIT": return "Nuit";
            case "CONGE": return "Congé";
            case "MALADIE": return "Maladie";
            case "FORMATION": return "Formation";
            default: return type;
        }
    }

    private String getBadgeStyle(String type) {
        if (type == null) return "-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-background-radius: 20;";
        switch (type) {
            case "JOUR": return "-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-background-radius: 20;";
            case "SOIR": return "-fx-background-color: #ede9fe; -fx-text-fill: #5b21b6; -fx-background-radius: 20;";
            case "NUIT": return "-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-background-radius: 20;";
            case "CONGE": return "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-background-radius: 20;";
            case "MALADIE": return "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 20;";
            case "FORMATION": return "-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-background-radius: 20;";
            default: return "-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-background-radius: 20;";
        }
    }

    private String formatTime(Object time) {
        if (time == null) return "--:--";
        String timeStr = time.toString();
        return timeStr.length() >= 5 ? timeStr.substring(0, 5) : timeStr;
    }

    private void mettreAJourStatistiques(List<Planning> plannings) {
        long total = plannings.size();
        long joursTravailles = plannings.stream()
                .filter(p -> p.getTypeShift().equals("JOUR") ||
                        p.getTypeShift().equals("SOIR") ||
                        p.getTypeShift().equals("NUIT"))
                .count();
        long conges = plannings.stream()
                .filter(p -> p.getTypeShift().equals("CONGE"))
                .count();

        lblTotalShifts.setText(String.valueOf(total));
        lblJoursTravailles.setText(String.valueOf(joursTravailles));
        lblConges.setText(String.valueOf(conges));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showMesTaches() {
        try {
            if (MainController.staticContentArea != null) {
                Node node = FXMLLoader.load(getClass().getResource("/EmpTacheView.fxml"));
                MainController.staticContentArea.getChildren().setAll(node);
                System.out.println("🔄 Navigation vers Tâches réussie");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des tâches");
        }
    }

    @FXML
    private void refreshPlanning() {
        chargerTousPlannings();
        filterMoisCombo.setValue("Tous");
        filterAnneeCombo.setValue("Tous");
    }
}//