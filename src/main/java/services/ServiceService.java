package services;

import models.Service;
import models.CategorieService;
import models.ResponsableInfo;
import utils.MyDataBase; // Utilisation de ton nouveau Singleton
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceService {
    private Connection conn;

    public ServiceService() {
        // Récupération de l'unique connexion
        this.conn = MyDataBase.getInstance().getCnx();
    }

    public void ajouter(Service service) throws SQLException {
        String req = "INSERT INTO service (titre, description, date_creation, date_debut, "
                + "date_fin, utilisateur_id, budget, categorie_id, archive) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setString(1, service.getTitre());
            pstmt.setString(2, service.getDescription());
            pstmt.setString(3, service.getDateCreation());
            pstmt.setString(4, service.getDateDebut());
            pstmt.setString(5, service.getDateFin());
            pstmt.setInt(6, service.getUtilisateurId());
            pstmt.setDouble(7, service.getBudget());
            pstmt.setInt(8, service.getCategorieId());
            pstmt.executeUpdate();
        }
    }

    public List<Service> afficherAll() throws SQLException {
        return afficherParStatut(false);
    }

    public List<Service> afficherArchives() throws SQLException {
        return afficherParStatut(true);
    }

    private List<Service> afficherParStatut(boolean archive) throws SQLException {
        List<Service> liste = new ArrayList<>();
        String req = "SELECT s.*, c.nom as categorie_nom, c.description as categorie_description "
                + "FROM service s "
                + "LEFT JOIN categorie_service c ON s.categorie_id = c.id "
                + "WHERE s.archive = ? "
                + "ORDER BY s.id";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, archive ? 1 : 0);
            try (ResultSet res = pstmt.executeQuery()) {
                while (res.next()) {
                    liste.add(mapResultSetToService(res));
                }
            }
        }
        return liste;
    }

    public Service getById(int id) throws SQLException {
        String req = "SELECT s.*, c.nom as categorie_nom, c.description as categorie_description "
                + "FROM service s "
                + "LEFT JOIN categorie_service c ON s.categorie_id = c.id "
                + "WHERE s.id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return mapResultSetToService(res);
                }
            }
        }
        return null;
    }

    /**
     * Helper pour éviter la répétition du mapping ResultSet -> Objet
     */
    private Service mapResultSetToService(ResultSet res) throws SQLException {
        Service s = new Service();
        s.setId(res.getInt("id"));
        s.setTitre(res.getString("titre"));
        s.setDescription(res.getString("description"));
        s.setDateCreation(res.getString("date_creation"));
        s.setDateDebut(res.getString("date_debut"));
        s.setDateFin(res.getString("date_fin"));
        s.setUtilisateurId(res.getInt("utilisateur_id"));
        s.setBudget(res.getDouble("budget"));
        s.setCategorieId(res.getInt("categorie_id"));
        s.setArchive(res.getBoolean("archive"));

        if (s.getCategorieId() > 0 && res.getString("categorie_nom") != null) {
            CategorieService cat = new CategorieService();
            cat.setId(s.getCategorieId());
            cat.setNom(res.getString("categorie_nom"));
            cat.setDescription(res.getString("categorie_description"));
            s.setCategorie(cat);
        }
        return s;
    }

    public List<ResponsableInfo> getResponsables() throws SQLException {
        List<ResponsableInfo> liste = new ArrayList<>();
        String req = "SELECT id, CONCAT(nom, ' ', prenom) as nomComplet, email, poste "
                + "FROM utilisateur "
                + "WHERE role IN ('responsable', 'responsable_projet', 'responsable_production', 'responsable_rh') "
                + "ORDER BY nom, prenom";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            try (ResultSet res = pstmt.executeQuery()) {
                while (res.next()) {
                    liste.add(new ResponsableInfo(
                            res.getInt("id"),
                            res.getString("nomComplet"),
                            res.getString("email"),
                            res.getString("poste")
                    ));
                }
            }
        }
        return liste;
    }

    public String getResponsableNom(int id) throws SQLException {
        String req = "SELECT CONCAT(nom, ' ', prenom) as nomComplet FROM utilisateur WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return res.getString("nomComplet");
                }
            }
        }
        return "Non assigné";
    }

    public List<Service> rechercher(String texteRecherche, String categorieFiltre) throws SQLException {
        List<Service> tousLesServices = afficherAll();
        if ((texteRecherche == null || texteRecherche.isEmpty()) &&
                (categorieFiltre == null || categorieFiltre.equals("Tous"))) {
            return tousLesServices;
        }
        return tousLesServices.stream()
                .filter(service -> {
                    boolean correspondTexte = texteRecherche == null || texteRecherche.isEmpty() ||
                            service.getTitre().toLowerCase().contains(texteRecherche.toLowerCase()) ||
                            (service.getDescription() != null &&
                                    service.getDescription().toLowerCase().contains(texteRecherche.toLowerCase()));
                    boolean correspondCategorie = categorieFiltre == null || categorieFiltre.equals("Tous") ||
                            (service.getCategorie() != null &&
                                    service.getCategorie().getNom().equals(categorieFiltre));
                    return correspondTexte && correspondCategorie;
                })
                .toList();
    }

    public void archiver(int id) throws SQLException {
        String req = "UPDATE service SET archive = 1 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void desarchiver(int id) throws SQLException {
        String req = "UPDATE service SET archive = 0 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void updateTitre(Service service) throws SQLException {
        String req = "UPDATE service SET titre = ?, description = ?, date_debut = ?, "
                + "date_fin = ?, utilisateur_id = ?, budget = ?, categorie_id = ? "
                + "WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setString(1, service.getTitre());
            pstmt.setString(2, service.getDescription());
            pstmt.setString(3, service.getDateDebut());
            pstmt.setString(4, service.getDateFin());
            pstmt.setInt(5, service.getUtilisateurId());
            pstmt.setDouble(6, service.getBudget());
            pstmt.setInt(7, service.getCategorieId());
            pstmt.setInt(8, service.getId());
            pstmt.executeUpdate();
        }
    }

}