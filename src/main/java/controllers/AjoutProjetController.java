package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
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
    @FXML private ComboBox<UserWrapper> cbResponsable;
    @FXML private ListView<UserWrapper> lvMembres;

    private ProjetService projetService = new ProjetService();

    private static class UserWrapper {
        int id;
        String nomComplet;
        UserWrapper(int id, String nomComplet) {
            this.id = id;
            this.nomComplet = nomComplet;
        }
        @Override
        public String toString() { return nomComplet; }
    }

    @FXML
    public void initialize() {
        cbStatut.getItems().add("Planifié");
        cbStatut.setValue("Planifié");
        cbStatut.setDisable(true);

        lvMembres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cbResponsable.setConverter(new StringConverter<UserWrapper>() {
            @Override
            public String toString(UserWrapper user) { return (user == null) ? "" : user.nomComplet; }
            @Override
            public UserWrapper fromString(String string) { return null; }
        });

        bloquerDatesPassees(dpDateDebut);
        bloquerDatesPassees(dpDateFin);
        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        String sql = "SELECT id, nom, prenom FROM utilisateur";
        Connection conn = MyDataBase.getInstance().getCnx();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                UserWrapper user = new UserWrapper(
                        rs.getInt("id"),
                        rs.getString("nom") + " " + rs.getString("prenom")
                );
                cbResponsable.getItems().add(user);
                lvMembres.getItems().add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterProjet() {
        try {
            if (tfNom.getText().isEmpty() || cbResponsable.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs obligatoires manquants.");
                return;
            }

            int idChef = cbResponsable.getValue().id;
            String nomsMembres = lvMembres.getSelectionModel().getSelectedItems()
                    .stream().map(u -> u.nomComplet).collect(Collectors.joining(", "));

            Projet p = new Projet(
                    0,
                    tfNom.getText(),
                    taDescription.getText(),
                    java.sql.Date.valueOf(dpDateDebut.getValue()),
                    java.sql.Date.valueOf(dpDateFin.getValue()),
                    Double.parseDouble(tfBudget.getText()),
                    cbStatut.getValue(),
                    0, false, idChef, nomsMembres
            );

            projetService.ajouterProjet(p);
            showAlert(Alert.AlertType.INFORMATION, "Projet ajouté !");
            annuler();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
        }
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

    @FXML private void annuler() { ((Stage) tfNom.getScene().getWindow()).close(); }
    private void showAlert(Alert.AlertType type, String message) { new Alert(type, message).showAndWait(); }
}