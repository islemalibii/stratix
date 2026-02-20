package service;

import model.Service;
import model.CategorieService;
import model.ResponsableInfo;
import util.db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceService {
    private Connection conn;
    private Statement ste;

    public ServiceService() throws SQLException {
        conn = db.getConnection();
    }

    private String escape(String str) {
        if (str == null) return "";
        return str.replace("'", "''");
    }

    public void ajouter(Service service) throws SQLException {
        String titre = escape(service.getTitre());
        String description = escape(service.getDescription());
        String req = "INSERT INTO service (titre, description, date_creation, date_debut, "
                + "date_fin, utilisateur_id, budget, categorie_id, archive) VALUES ("
                + "'" + titre + "', "
                + "'" + description + "', "
                + "'" + service.getDateCreation() + "', "
                + "'" + service.getDateDebut() + "', "
                + "'" + service.getDateFin() + "', "
                + service.getUtilisateurId() + ", "
                + service.getBudget() + ", "
                + service.getCategorieId() + ", 0)";
        ste = conn.createStatement();
        ste.executeUpdate(req);
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
                + "WHERE s.archive = " + (archive ? "1" : "0") + " "
                + "ORDER BY s.id";
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        while (res.next()) {
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
            liste.add(s);
        }
        res.close();
        return liste;
    }

    public Service getById(int id) throws SQLException {
        String req = "SELECT s.*, c.nom as categorie_nom, c.description as categorie_description "
                + "FROM service s "
                + "LEFT JOIN categorie_service c ON s.categorie_id = c.id "
                + "WHERE s.id = " + id;
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        if (res.next()) {
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
            res.close();
            return s;
        }
        res.close();
        return null;
    }

    public List<ResponsableInfo> getResponsables() throws SQLException {
        List<ResponsableInfo> liste = new ArrayList<>();
        String req = "SELECT id, CONCAT(nom, ' ', prenom) as nomComplet, email, poste "
                + "FROM utilisateur "
                + "WHERE role IN ('responsable', 'responsable_projet', 'responsable_production', 'responsable_rh') "
                + "ORDER BY nom, prenom";
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        while (res.next()) {
            ResponsableInfo r = new ResponsableInfo(
                    res.getInt("id"),
                    res.getString("nomComplet"),
                    res.getString("email"),
                    res.getString("poste")
            );
            liste.add(r);
        }
        res.close();
        return liste;
    }

    public String getResponsableNom(int id) throws SQLException {
        String req = "SELECT CONCAT(nom, ' ', prenom) as nomComplet FROM utilisateur WHERE id = " + id;
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        if (res.next()) {
            String nom = res.getString("nomComplet");
            res.close();
            return nom;
        }
        res.close();
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
        String req = "UPDATE service SET archive = 1 WHERE id = " + id;
        ste = conn.createStatement();
        ste.executeUpdate(req);
    }

    public void desarchiver(int id) throws SQLException {
        String req = "UPDATE service SET archive = 0 WHERE id = " + id;
        ste = conn.createStatement();
        ste.executeUpdate(req);
    }

    public void updateTitre(Service service) throws SQLException {
        String titre = escape(service.getTitre());
        String description = escape(service.getDescription());
        String req = "UPDATE service SET "
                + "titre = '" + titre + "', "
                + "description = '" + description + "', "
                + "date_debut = '" + service.getDateDebut() + "', "
                + "date_fin = '" + service.getDateFin() + "', "
                + "utilisateur_id = " + service.getUtilisateurId() + ", "
                + "budget = " + service.getBudget() + ", "
                + "categorie_id = " + service.getCategorieId() + " "
                + "WHERE id = " + service.getId();
        ste = conn.createStatement();
        ste.executeUpdate(req);
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
        if (ste != null) ste.close();
    }
}