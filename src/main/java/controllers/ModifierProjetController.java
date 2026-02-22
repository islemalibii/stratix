package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;
import utils.MyDataBase;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModifierProjetController {

    @FXML private TextField txtNom, txtBudget, txtProgression;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dateDebut, dateFin;
    @FXML private ChoiceBox<String> comboStatut;

    @FXML private ComboBox<String> cbResponsable;
    @FXML private ListView<String> lvMembres;

    private ProjetService service = new ProjetService();
    private Projet projetEnModification;

    @FXML
    public void initialize() {
        comboStatut.getItems().addAll("Planifié", "En cours", "Terminé", "Annulé");

        lvMembres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        String sql = "SELECT id, nom, prenom FROM utilisateur";
        try (Connection conn = MyDataBase.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String label = rs.getInt("id") + " - " + rs.getString("nom") + " " + rs.getString("prenom");
                cbResponsable.getItems().add(label);
                lvMembres.getItems().add(label);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void chargerDonnees(int idProjet) {
        this.projetEnModification = service.chercherProjetParId(idProjet);

        if (projetEnModification != null) {
            txtNom.setText(projetEnModification.getNom());
            txtDescription.setText(projetEnModification.getDescription());
            txtBudget.setText(String.valueOf(projetEnModification.getBudget()));
            txtProgression.setText(String.valueOf(projetEnModification.getProgression()));
            comboStatut.setValue(projetEnModification.getStatut());

            if (projetEnModification.getDateDebut() != null) {
                dateDebut.setValue(((java.sql.Date) projetEnModification.getDateDebut()).toLocalDate());
            }
            if (projetEnModification.getDateFin() != null) {
                dateFin.setValue(((java.sql.Date) projetEnModification.getDateFin()).toLocalDate());
            }
            for (String item : cbResponsable.getItems()) {
                if (item.startsWith(projetEnModification.getResponsableId() + " - ")) {
                    cbResponsable.setValue(item);
                    break;
                }
            }

            if (projetEnModification.getEquipeMembres() != null && !projetEnModification.getEquipeMembres().isEmpty()) {
                List<String> membresEnregistres = Arrays.asList(projetEnModification.getEquipeMembres().split(", "));

                for (int i = 0; i < lvMembres.getItems().size(); i++) {
                    String currentItem = lvMembres.getItems().get(i);
                    if (membresEnregistres.contains(currentItem)) {
                        lvMembres.getSelectionModel().select(i);
                    }
                }
            }
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!validerChamps()) return;

        try {
            // Mise à jour de l'objet projet avec les champs texte
            projetEnModification.setNom(txtNom.getText());
            projetEnModification.setDescription(txtDescription.getText());
            projetEnModification.setBudget(Double.parseDouble(txtBudget.getText()));
            projetEnModification.setProgression(Integer.parseInt(txtProgression.getText()));
            projetEnModification.setStatut(comboStatut.getValue());
            projetEnModification.setDateDebut(java.sql.Date.valueOf(dateDebut.getValue()));
            projetEnModification.setDateFin(java.sql.Date.valueOf(dateFin.getValue()));

            String selectedChef = cbResponsable.getValue();
            int idChef = Integer.parseInt(selectedChef.split(" - ")[0]);
            projetEnModification.setResponsableId(idChef);

            String nouveauxMembres = lvMembres.getSelectionModel().getSelectedItems()
                    .stream()
                    .collect(Collectors.joining(", "));
            projetEnModification.setEquipeMembres(nouveauxMembres);

            service.mettreAJourProjet(projetEnModification);

            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Projet mis à jour avec succès !");
            fermerFenetre();

        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @FXML private void handleAnnuler() { fermerFenetre(); }

    private void fermerFenetre() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private boolean validerChamps() {
        if (txtNom.getText().isEmpty() || dateDebut.getValue() == null || dateFin.getValue() == null || cbResponsable.getValue() == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs (Nom, Dates et Responsable).");
            return false;
        }

        if (dateFin.getValue().isBefore(dateDebut.getValue())) {
            afficherAlerte(Alert.AlertType.WARNING, "Dates invalides", "La date de fin doit être après la date de début.");
            return false;
        }
        return true;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}