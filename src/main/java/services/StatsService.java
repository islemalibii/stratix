package services;

import models.DashboardStats;  // ← IMPORTANT: ajoute cet import
import models.Tache;
import models.Planning;
import models.Employe;
import utiles.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsService {

    private EmployeeService employeeService;
    private SERVICETache tacheService;
    private SERVICEPlanning planningService;

    public StatsService() {
        this.employeeService = new EmployeeService();
        this.tacheService = new SERVICETache();
        this.planningService = new SERVICEPlanning();
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // Statistiques générales
        stats.setTotalEmployes(employeeService.getAllEmployes().size());
        stats.setTotalTaches(tacheService.getAllTaches().size());
        stats.setTotalPlannings(planningService.getAllPlannings().size());
        stats.setTotalProjets(getTotalProjets());

        // Statistiques des tâches
        stats.setTachesAFaire(compterTachesParStatut("A_FAIRE"));
        stats.setTachesEnCours(compterTachesParStatut("EN_COURS"));
        stats.setTachesTerminees(compterTachesParStatut("TERMINEE"));
        stats.setTachesEnRetard(compterTachesEnRetard());

        // Statistiques des plannings
        stats.setEmployesEnPoste(planningService.compterEnPoste());
        stats.setEmployesAbsents(planningService.compterAbsents());
        stats.setPlanningsMatin(compterPlanningsParShift("JOUR"));
        stats.setPlanningsSoir(compterPlanningsParShift("SOIR"));
        stats.setPlanningsNuit(compterPlanningsParShift("NUIT"));

        // Statistiques par employé
        stats.setTachesParEmploye(getTachesParEmploye());
        stats.setPlanningsParEmploye(getPlanningsParEmploye());

        // Dernières activités
        stats.setDernieresTaches(getDernieresTaches(5));
        stats.setProchainsPlannings(getProchainsPlannings(5));

        return stats;
    }

    private int getTotalProjets() {
        String sql = "SELECT COUNT(*) FROM projet";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int compterTachesParStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM tache WHERE statut = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int compterTachesEnRetard() {
        String sql = "SELECT COUNT(*) FROM tache WHERE deadline < CURDATE() AND statut != 'TERMINEE'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int compterPlanningsParShift(String shift) {
        String sql = "SELECT COUNT(*) FROM planning WHERE type_shift = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shift);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Map<String, Integer> getTachesParEmploye() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT e.id, u.username, COUNT(t.id) as nb_taches " +
                "FROM employe e " +
                "JOIN utilisateur u ON e.utilisateur_id = u.id " +
                "LEFT JOIN tache t ON e.id = t.employe_id " +
                "GROUP BY e.id, u.username";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                int nbTaches = rs.getInt("nb_taches");
                map.put(username, nbTaches);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<String, Integer> getPlanningsParEmploye() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT e.id, u.username, COUNT(p.id) as nb_plannings " +
                "FROM employe e " +
                "JOIN utilisateur u ON e.utilisateur_id = u.id " +
                "LEFT JOIN planning p ON e.id = p.employe_id " +
                "GROUP BY e.id, u.username";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                int nbPlannings = rs.getInt("nb_plannings");
                map.put(username, nbPlannings);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<String> getDernieresTaches(int limit) {
        List<String> dernieres = new ArrayList<>();
        String sql = "SELECT t.*, u.username FROM tache t " +
                "JOIN employe e ON t.employe_id = e.id " +
                "JOIN utilisateur u ON e.utilisateur_id = u.id " +
                "ORDER BY t.id DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String tache = String.format("%s - %s (%s)",
                        rs.getString("titre"),
                        rs.getString("username"),
                        rs.getString("statut"));
                dernieres.add(tache);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dernieres;
    }

    private List<String> getProchainsPlannings(int limit) {
        List<String> prochains = new ArrayList<>();
        String sql = "SELECT p.*, u.username FROM planning p " +
                "JOIN employe e ON p.employe_id = e.id " +
                "JOIN utilisateur u ON e.utilisateur_id = u.id " +
                "WHERE p.date >= CURDATE() " +
                "ORDER BY p.date ASC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String planning = String.format("%s - %s (%s)",
                        rs.getString("username"),
                        rs.getDate("date").toString(),
                        rs.getString("type_shift"));
                prochains.add(planning);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prochains;
    }
}