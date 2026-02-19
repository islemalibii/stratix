package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Service;
import model.CategorieService;
import service.ServiceService;
import service.CategorieServiceService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AjoutController implements Initializable {

    @FXML private Label lblTitrePopup;
    @FXML private Label lblError;
    @FXML private TextField txtTitre;
    @FXML private ComboBox<CategorieService> comboCategorie;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dateCreationPicker;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField txtResponsableId;
    @FXML private TextField txtBudget;
    @FXML private Button btnAction;

    private ServiceService serviceService;
    private CategorieServiceService categorieService;
    private Service serviceAModifier;
    private boolean modeModification = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateCreationPicker.setValue(LocalDate.now());

        chargerCategories();
    }

    private void chargerCategories() {
        try {
            categorieService = new CategorieServiceService();
            List<CategorieService> categories = categorieService.afficherAll();

            if (categories.isEmpty()) {
                System.out.println(" Aucune catégorie trouvée dans la base!");
                lblError.setText("Aucune catégorie disponible. Veuillez d'abord créer des catégories.");
            } else {
                comboCategorie.setItems(FXCollections.observableArrayList(categories));
                System.out.println("" + categories.size() + " catégories chargées");
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement catégories: " + e.getMessage());
            lblError.setText("Erreur: Impossible de charger les catégories");
        }
    }

    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    public void setServiceAModifier(Service service) {
        this.serviceAModifier = service;
        this.modeModification = true;

        lblTitrePopup.setText("MODIFIER SERVICE");
        btnAction.setText("Modifier");

        txtTitre.setText(service.getTitre());
        txtDescription.setText(service.getDescription());
        dateCreationPicker.setValue(LocalDate.parse(service.getDateCreation()));
        dateDebutPicker.setValue(LocalDate.parse(service.getDateDebut()));
        dateFinPicker.setValue(LocalDate.parse(service.getDateFin()));
        txtResponsableId.setText(String.valueOf(service.getResponsableId()));
        txtBudget.setText(String.valueOf(service.getBudget()));

        if (service.getCategorie() != null) {
            comboCategorie.setValue(service.getCategorie());
        } else if (service.getCategorieId() > 0) {
            // Chercher la catégorie par ID
            for (CategorieService cat : comboCategorie.getItems()) {
                if (cat.getId() == service.getCategorieId()) {
                    comboCategorie.setValue(cat);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleAjouter() {
        lblError.setText("");

        if (!validerChamps()) return;

        try {
            if (modeModification) {
                serviceAModifier.setTitre(txtTitre.getText());
                serviceAModifier.setDescription(txtDescription.getText());
                serviceAModifier.setDateDebut(dateDebutPicker.getValue().toString());
                serviceAModifier.setDateFin(dateFinPicker.getValue().toString());
                serviceAModifier.setResponsableId(Integer.parseInt(txtResponsableId.getText()));
                serviceAModifier.setBudget(Double.parseDouble(txtBudget.getText()));

                CategorieService catSelectionnee = comboCategorie.getValue();
                if (catSelectionnee != null) {
                    serviceAModifier.setCategorieId(catSelectionnee.getId());
                }

                serviceService.updateTitre(serviceAModifier);
                showAlert("Succès", "Service modifié avec succès!");
            } else {
                Service service = new Service();
                service.setTitre(txtTitre.getText());
                service.setDescription(txtDescription.getText());
                service.setDateCreation(dateCreationPicker.getValue().toString());
                service.setDateDebut(dateDebutPicker.getValue().toString());
                service.setDateFin(dateFinPicker.getValue().toString());
                service.setResponsableId(Integer.parseInt(txtResponsableId.getText()));
                service.setBudget(Double.parseDouble(txtBudget.getText()));

                CategorieService catSelectionnee = comboCategorie.getValue();
                if (catSelectionnee != null) {
                    service.setCategorieId(catSelectionnee.getId());
                }

                serviceService.ajouter(service);
                showAlert("Succès", "Service ajouté");
            }

            fermer();

        } catch (SQLException e) {
            lblError.setText("Erreur SQL: " + e.getMessage());
        } catch (NumberFormatException e) {
            lblError.setText("Budget et ID doivent être des nombres");
        }
    }

    private boolean validerChamps() {
        if (txtTitre.getText().trim().isEmpty()) {
            lblError.setText("Le titre est obligatoire");
            return false;
        }
        if (comboCategorie.getValue() == null) {
            lblError.setText("Veuillez sélectionner une catégorie");
            return false;
        }
        if (txtDescription.getText().trim().isEmpty()) {
            lblError.setText("La description est obligatoire");
            return false;
        }
        if (dateDebutPicker.getValue() == null) {
            lblError.setText("La date de début est obligatoire");
            return false;
        }
        if (dateFinPicker.getValue() == null) {
            lblError.setText("La date de fin est obligatoire");
            return false;
        }
        if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            lblError.setText("La date de fin doit être après la date de début");
            return false;
        }
        if (txtResponsableId.getText().trim().isEmpty()) {
            lblError.setText("L'ID responsable est obligatoire");
            return false;
        }
        if (txtBudget.getText().trim().isEmpty()) {
            lblError.setText("Le budget est obligatoire");
            return false;
        }
        return true;
    }

    @FXML
    private void handleFermer() {
        fermer();
    }

    private void fermer() {
        Stage stage = (Stage) txtTitre.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}