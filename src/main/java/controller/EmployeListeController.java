package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Projet;
import service.ProjetService;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeListeController {
    @FXML private FlowPane flowPaneProjets;
    @FXML private TextField searchField;

    private ProjetService projetService = new ProjetService();

    @FXML
    public void initialize() {
        chargerProjetsEmploye();
    }

    private void chargerProjetsEmploye() {
        // L'employé ne voit que les projets actifs
        List<Projet> projetsActifs = projetService.listerTousLesProjets().stream()
                .filter(p -> "En cours".equals(p.getStatut()) || "Planifié".equals(p.getStatut()))
                .collect(Collectors.toList());

        afficherLesCartes(projetsActifs);
    }

    private void afficherLesCartes(List<Projet> projets) {
        flowPaneProjets.getChildren().clear();
        for (Projet p : projets) {
            flowPaneProjets.getChildren().add(creerCarteSimple(p));
        }
    }

    private VBox creerCarteSimple(Projet p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("project-card");
        card.setPrefWidth(400); // Grande carte comme demandé

        Label title = new Label(p.getNom());
        title.getStyleClass().add("project-title");

        Label desc = new Label(p.getDescription());
        desc.setWrapText(true);
        desc.getStyleClass().add("project-desc");

        ProgressBar pb = new ProgressBar(p.getProgression() / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);

        Label lblStatut = new Label(p.getStatut());
        lblStatut.getStyleClass().addAll("statut-badge", "badge-" + p.getStatut().toLowerCase().replace(" ", "-"));

        card.getChildren().addAll(lblStatut, title, desc, pb);
        return card;
    }

    @FXML
    private void rechercherProjet() {
        String search = searchField.getText().toLowerCase();
        List<Projet> filtrés = projetService.listerTousLesProjets().stream()
                .filter(p -> "En cours".equals(p.getStatut()) || "Planifié".equals(p.getStatut()))
                .filter(p -> p.getNom().toLowerCase().contains(search))
                .collect(Collectors.toList());
        afficherLesCartes(filtrés);
    }

    @FXML
    private void versBackOffice() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeProjets.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) flowPaneProjets.getScene().getWindow();
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }


}