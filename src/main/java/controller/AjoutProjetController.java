package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;
import util.DBConnection;
import java.sql.*;
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
        cbStatut.getItems().addAll("Planifié", "En cours", "Terminé", "Annulé");
        cbStatut.setValue("Planifié");
        lvMembres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        String sql = "SELECT id, nom, prenom FROM utilisateur";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String label = rs.getInt("id") + " - " + rs.getString("nom") + " " + rs.getString("prenom");
                cbResponsable.getItems().add(label);
                lvMembres.getItems().add(label);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void ajouterProjet() {
        try {
            // Extraction de l'ID du chef
            String selectedChef = cbResponsable.getValue();
            int idChef = Integer.parseInt(selectedChef.split(" - ")[0]);

            // Extraction des noms des membres
            String membres = lvMembres.getSelectionModel().getSelectedItems()
                    .stream().collect(Collectors.joining(", "));

            Projet p = new Projet(0, tfNom.getText(), taDescription.getText(),
                    java.sql.Date.valueOf(dpDateDebut.getValue()),
                    java.sql.Date.valueOf(dpDateFin.getValue()),
                    Double.parseDouble(tfBudget.getText()),
                    cbStatut.getValue(), 0, false, idChef, membres);

            projetService.ajouterProjet(p);
            ((Stage) tfNom.getScene().getWindow()).close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Vérifiez vos saisies (Chef requis)").show();
        }
    }

    @FXML private void annuler() { ((Stage) tfNom.getScene().getWindow()).close(); }
}