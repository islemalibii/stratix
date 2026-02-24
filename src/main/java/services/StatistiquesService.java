package services;

import utils.MyDataBase;
import java.sql.*;
import java.util.*;


public class StatistiquesService {
    private Connection conn;

    public StatistiquesService() {
        // Récupération de l'instance unique de connexion
        this.conn = MyDataBase.getInstance().getCnx();
    }

    public Map<String, Double> getBudgetParCategorie() throws SQLException {
        Map<String, Double> stats = new LinkedHashMap<>();
        String req = "SELECT c.nom, COALESCE(SUM(s.budget), 0) as total " +
                "FROM categorie_service c " +
                "LEFT JOIN service s ON c.id = s.categorie_id AND s.archive = 0 " +
                "GROUP BY c.id, c.nom " +
                "ORDER BY total DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(req);
             ResultSet res = pstmt.executeQuery()) {
            while (res.next()) {
                stats.put(res.getString("nom"), res.getDouble("total"));
            }
        }
        return stats;
    }


    public Map<String, Integer> getNombreServicesParCategorie() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String req = "SELECT c.nom, COUNT(s.id) as nombre " +
                "FROM categorie_service c " +
                "LEFT JOIN service s ON c.id = s.categorie_id AND s.archive = 0 " +
                "GROUP BY c.id, c.nom " +
                "ORDER BY nombre DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(req);
             ResultSet res = pstmt.executeQuery()) {
            while (res.next()) {
                stats.put(res.getString("nom"), res.getInt("nombre"));
            }
        }
        return stats;
    }


    public double getBudgetMoyen() throws SQLException {
        String req = "SELECT AVG(budget) as moyenne FROM service WHERE archive = 0";

        try (PreparedStatement pstmt = conn.prepareStatement(req);
             ResultSet res = pstmt.executeQuery()) {
            if (res.next()) {
                return res.getDouble("moyenne");
            }
        }
        return 0.0;
    }


    public Map<String, Integer> getServicesParMois() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String req = "SELECT DATE_FORMAT(date_creation, '%Y-%m') as mois, COUNT(*) as nombre " +
                "FROM service WHERE archive = 0 " +
                "GROUP BY mois ORDER BY mois";

        try (PreparedStatement pstmt = conn.prepareStatement(req);
             ResultSet res = pstmt.executeQuery()) {
            while (res.next()) {
                stats.put(res.getString("mois"), res.getInt("nombre"));
            }
        }
        return stats;
    }


}