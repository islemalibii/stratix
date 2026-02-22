package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AjoutProjetController {

    @FXML private TextField tfNom, tfBudget;
    @FXML private TextArea taDescription;
    @FXML private DatePicker dpDateDebut, dpDateFin;
    @FXML private ChoiceBox<String> cbStatut;
    @FXML private ComboBox<String> cbResponsable;
    @FXML private ListView<String> lvMembres;

    private ProjetService projetService = new ProjetService();

    @FXML
    public void initialize() {

        cbStatut.getItems().add("Planifié");
        cbStatut.setValue("Planifié");
        cbStatut.setDisable(true);

        lvMembres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bloquerDatesPassees(dpDateDebut);
        bloquerDatesPassees(dpDateFin);

        dpDateDebut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpDateFin.getValue() != null &&
                    dpDateFin.getValue().isBefore(newVal)) {
                dpDateFin.setValue(null);
            }
        });

        chargerUtilisateurs();
    }

    private void bloquerDatesPassees(DatePicker picker) {
        picker.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void chargerUtilisateurs() {
        String sql = "SELECT id, nom, prenom FROM utilisateur";
        try {
            Connection conn = MyDataBase.getInstance().getCnx();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String label = rs.getInt("id") + " - " + rs.getString("nom") + " " + rs.getString("prenom");
                cbResponsable.getItems().add(label);
                lvMembres.getItems().add(label);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterProjet() {

        try {
            if (tfNom.getText().isEmpty()
                    || taDescription.getText().isEmpty()
                    || tfBudget.getText().isEmpty()
                    || dpDateDebut.getValue() == null
                    || dpDateFin.getValue() == null
                    || cbResponsable.getValue() == null) {

                showAlert(Alert.AlertType.WARNING,
                        "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            double budget;
            try {
                budget = Double.parseDouble(tfBudget.getText());
                if (budget <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR,
                        "Le budget doit être un nombre positif.");
                return;
            }

            if (dpDateDebut.getValue().isAfter(dpDateFin.getValue())) {
                showAlert(Alert.AlertType.WARNING,
                        "La date de début doit être avant la date de fin.");
                return;
            }

            String selectedChef = cbResponsable.getValue();
            int idChef = Integer.parseInt(selectedChef.split(" - ")[0]);

            String membres = lvMembres.getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .collect(Collectors.joining(", "));

            Projet p = new Projet(
                    0,
                    tfNom.getText(),
                    taDescription.getText(),
                    Date.valueOf(dpDateDebut.getValue()),
                    Date.valueOf(dpDateFin.getValue()),
                    budget,
                    cbStatut.getValue(),   // toujours "Planifié"
                    0,
                    false,
                    idChef,
                    membres
            );

            projetService.ajouterProjet(p);

            showAlert(Alert.AlertType.INFORMATION,
                    "Projet ajouté avec succès.");

            ((Stage) tfNom.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur lors de l'ajout du projet.");
        }
    }

    @FXML
    private void annuler() {
        ((Stage) tfNom.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}