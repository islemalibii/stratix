package services;

import models.CategorieService;
import utils.MyDataBase; // Utilisation de votre nouveau package
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieServiceService {
    private Connection conn;

    public CategorieServiceService() {
        // Récupération de l'instance unique de connexion via votre Singleton
        this.conn = MyDataBase.getInstance().getCnx();
    }

    public void ajouter(CategorieService categorie) throws SQLException {
        String req = "INSERT INTO categorie_service (nom, description, date_creation, archive) VALUES (?, ?, CURRENT_DATE, 0)";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setString(1, categorie.getNom());
            pstmt.setString(2, categorie.getDescription());
            pstmt.executeUpdate();
        }
    }

    public List<CategorieService> afficherAll() throws SQLException {
        return afficherParStatut(false);
    }

    public List<CategorieService> afficherArchives() throws SQLException {
        return afficherParStatut(true);
    }

    private List<CategorieService> afficherParStatut(boolean archive) throws SQLException {
        List<CategorieService> liste = new ArrayList<>();
        String req = "SELECT * FROM categorie_service WHERE archive = ? ORDER BY nom";

        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, archive ? 1 : 0);
            try (ResultSet res = pstmt.executeQuery()) {
                while (res.next()) {
                    CategorieService c = new CategorieService();
                    c.setId(res.getInt("id"));
                    c.setNom(res.getString("nom"));
                    c.setDescription(res.getString("description"));
                    c.setDateCreation(res.getString("date_creation"));
                    c.setArchive(res.getBoolean("archive"));
                    liste.add(c);
                }
            }
        }
        return liste;
    }

    public CategorieService getById(int id) throws SQLException {
        String req = "SELECT * FROM categorie_service WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    CategorieService c = new CategorieService();
                    c.setId(res.getInt("id"));
                    c.setNom(res.getString("nom"));
                    c.setDescription(res.getString("description"));
                    c.setDateCreation(res.getString("date_creation"));
                    c.setArchive(res.getBoolean("archive"));
                    return c;
                }
            }
        }
        return null;
    }

    public void modifier(CategorieService categorie) throws SQLException {
        String req = "UPDATE categorie_service SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setString(1, categorie.getNom());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setInt(3, categorie.getId());
            pstmt.executeUpdate();
        }
    }

    public void archiver(int id) throws SQLException {
        // Vérification si des services actifs utilisent cette catégorie avant d'archiver
        String checkReq = "SELECT COUNT(*) FROM service WHERE categorie_id = ? AND archive = 0";
        try (PreparedStatement pstmtCheck = conn.prepareStatement(checkReq)) {
            pstmtCheck.setInt(1, id);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Impossible d'archiver : " + rs.getInt(1) + " service(s) actifs utilisent cette catégorie.");
                }
            }
        }

        String req = "UPDATE categorie_service SET archive = 1 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void desarchiver(int id) throws SQLException {
        String req = "UPDATE categorie_service SET archive = 0 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

}