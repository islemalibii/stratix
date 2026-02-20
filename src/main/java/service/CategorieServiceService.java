package service;

import model.CategorieService;
import util.db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieServiceService {
    private Connection conn;
    private Statement ste;

    public CategorieServiceService() throws SQLException {
        conn = db.getConnection();
        System.out.println("Connexion catégorie établie");
    }

    private String escape(String str) {
        if (str == null) return "";
        return str.replace("'", "''");
    }

    public void ajouter(CategorieService categorie) throws SQLException {
        String nom = escape(categorie.getNom());
        String description = escape(categorie.getDescription());

        String req = "INSERT INTO categorie_service (nom, description, date_creation, archive) VALUES ("
                + "'" + nom + "', "
                + "'" + description + "', "
                + "CURRENT_DATE, 0)";

        ste = conn.createStatement();
        ste.executeUpdate(req);
        System.out.println("Catégorie ajoutée: " + categorie.getNom());
    }

    public List<CategorieService> afficherAll() throws SQLException {
        return afficherParStatut(false);
    }

    public List<CategorieService> afficherArchives() throws SQLException {
        return afficherParStatut(true);
    }

    private List<CategorieService> afficherParStatut(boolean archive) throws SQLException {
        List<CategorieService> liste = new ArrayList<>();
        String req = "SELECT * FROM categorie_service WHERE archive = " + (archive ? "1" : "0") + " ORDER BY nom";

        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);

        while (res.next()) {
            CategorieService c = new CategorieService();
            c.setId(res.getInt("id"));
            c.setNom(res.getString("nom"));
            c.setDescription(res.getString("description"));
            c.setDateCreation(res.getString("date_creation"));
            c.setArchive(res.getBoolean("archive"));
            liste.add(c);
        }
        res.close();
        return liste;
    }

    public CategorieService getById(int id) throws SQLException {
        String req = "SELECT * FROM categorie_service WHERE id = " + id;

        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);

        if (res.next()) {
            CategorieService c = new CategorieService();
            c.setId(res.getInt("id"));
            c.setNom(res.getString("nom"));
            c.setDescription(res.getString("description"));
            c.setDateCreation(res.getString("date_creation"));
            c.setArchive(res.getBoolean("archive"));
            res.close();
            return c;
        }
        res.close();
        return null;
    }

    public void modifier(CategorieService categorie) throws SQLException {
        String nom = escape(categorie.getNom());
        String description = escape(categorie.getDescription());

        String req = "UPDATE categorie_service SET "
                + "nom = '" + nom + "', "
                + "description = '" + description + "' "
                + "WHERE id = " + categorie.getId();

        ste = conn.createStatement();
        int rowsAffected = ste.executeUpdate(req);
        if (rowsAffected > 0) {
            System.out.println("Catégorie modifiée ID=" + categorie.getId());
        }
    }

    public void archiver(int id) throws SQLException {
        String checkReq = "SELECT COUNT(*) FROM service WHERE categorie_id = " + id + " AND archive = 0";
        ResultSet rs = ste.executeQuery(checkReq);
        rs.next();
        int count = rs.getInt(1);

        if (count > 0) {
            throw new SQLException("Impossible d'archiver : " + count + " service(s) actifs utilisent cette catégorie");
        }

        String req = "UPDATE categorie_service SET archive = 1 WHERE id = " + id;
        ste = conn.createStatement();
        int rowsAffected = ste.executeUpdate(req);
        if (rowsAffected > 0) {
            System.out.println("Catégorie archivée ID=" + id);
        }
    }

    public void desarchiver(int id) throws SQLException {
        String req = "UPDATE categorie_service SET archive = 0 WHERE id = " + id;
        ste = conn.createStatement();
        int rowsAffected = ste.executeUpdate(req);
        if (rowsAffected > 0) {
            System.out.println("Catégorie désarchivée ID=" + id);
        }
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
        if (ste != null) ste.close();
    }
}