package service;

import model.Service;
import model.CategorieService;
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
                + "date_fin, responsable_id, budget, categorie_id) VALUES ("
                + "'" + titre + "', "
                + "'" + description + "', "
                + "'" + service.getDateCreation() + "', "
                + "'" + service.getDateDebut() + "', "
                + "'" + service.getDateFin() + "', "
                + service.getResponsableId() + ", "
                + service.getBudget() + ", "
                + service.getCategorieId() + ")";

        ste = conn.createStatement();
        ste.executeUpdate(req);
        System.out.println("Service ajouté: " + service.getTitre());
    }
    public List<Service> afficherAll() throws SQLException {
        List<Service> liste = new ArrayList<>();
        String req = "SELECT s.*, c.nom as categorie_nom, c.description as categorie_description "
                + "FROM service s "
                + "LEFT JOIN categorie_service c ON s.categorie_id = c.id "
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
            s.setResponsableId(res.getInt("responsable_id"));
            s.setBudget(res.getDouble("budget"));
            s.setCategorieId(res.getInt("categorie_id"));

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
            s.setResponsableId(res.getInt("responsable_id"));
            s.setBudget(res.getDouble("budget"));
            s.setCategorieId(res.getInt("categorie_id"));

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

    public void updateTitre(Service service) throws SQLException {
        String titre = escape(service.getTitre());
        String description = escape(service.getDescription());

        String req = "UPDATE service SET "
                + "titre = '" + titre + "', "
                + "description = '" + description + "', "
                + "date_debut = '" + service.getDateDebut() + "', "
                + "date_fin = '" + service.getDateFin() + "', "
                + "responsable_id = " + service.getResponsableId() + ", "
                + "budget = " + service.getBudget() + ", "
                + "categorie_id = " + service.getCategorieId() + " "
                + "WHERE id = " + service.getId();

        ste = conn.createStatement();
        ste.executeUpdate(req);
        System.out.println("Service modifié ID=" + service.getId());
    }

    public void delete(int id) throws SQLException {
        String req = "DELETE FROM service WHERE id = " + id;

        ste = conn.createStatement();
        int rowsAffected = ste.executeUpdate(req);
        if (rowsAffected > 0) {
            System.out.println("Service supprimé ID=" + id);
        }
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
        if (ste != null) ste.close();
    }
}