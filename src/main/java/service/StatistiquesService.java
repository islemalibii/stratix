package service;

import model.Service;
import util.db;

import java.sql.*;
import java.util.*;

public class StatistiquesService {
    private Connection conn;
    private Statement ste;

    public StatistiquesService() throws SQLException {
        conn = db.getConnection();
    }

    public Map<String, Double> getBudgetParCategorie() throws SQLException {
        Map<String, Double> stats = new LinkedHashMap<>();
        String req = "SELECT c.nom, COALESCE(SUM(s.budget), 0) as total " +
                "FROM categorie_service c " +
                "LEFT JOIN service s ON c.id = s.categorie_id AND s.archive = 0 " +
                "GROUP BY c.id, c.nom " +
                "ORDER BY total DESC";
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        while (res.next()) {
            stats.put(res.getString("nom"), res.getDouble("total"));
        }
        res.close();
        return stats;
    }

    public Map<String, Integer> getNombreServicesParCategorie() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String req = "SELECT c.nom, COUNT(s.id) as nombre " +
                "FROM categorie_service c " +
                "LEFT JOIN service s ON c.id = s.categorie_id AND s.archive = 0 " +
                "GROUP BY c.id, c.nom " +
                "ORDER BY nombre DESC";
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        while (res.next()) {
            stats.put(res.getString("nom"), res.getInt("nombre"));
        }
        res.close();
        return stats;
    }

    public double getBudgetMoyen() throws SQLException {
        String req = "SELECT AVG(budget) as moyenne FROM service WHERE archive = 0";
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        if (res.next()) {
            return res.getDouble("moyenne");
        }
        res.close();
        return 0;
    }

    public Map<String, Integer> getServicesParMois() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String req = "SELECT DATE_FORMAT(date_creation, '%Y-%m') as mois, COUNT(*) as nombre " +
                "FROM service WHERE archive = 0 " +
                "GROUP BY mois ORDER BY mois";
        ste = conn.createStatement();
        ResultSet res = ste.executeQuery(req);
        while (res.next()) {
            stats.put(res.getString("mois"), res.getInt("nombre"));
        }
        res.close();
        return stats;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
        if (ste != null) ste.close();
    }
}