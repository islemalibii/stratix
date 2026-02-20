package services;

import models.DashboardStats;
import models.Tache;
import models.Planning;
import utiles.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsService {

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        try (Connection conn = DBConnection.getConnection()) {

            // 1. Total des employés (utilisateurs avec rôle employe)
            String sqlEmployes = "SELECT COUNT(*) FROM utilisateur WHERE role = 'employe' OR role = 'EMPLOYE' OR role LIKE 'responsable%'";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlEmployes)) {
                if (rs.next()) {
                    stats.setTotalEmployes(rs.getInt(1));
                }
            }

            // 2. Total des tâches
            String sqlTaches = "SELECT COUNT(*) FROM tache";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlTaches)) {
                if (rs.next()) {
                    stats.setTotalTaches(rs.getInt(1));
                }
            }

            // 3. Tâches par statut
            String sqlTachesParStatut = "SELECT statut, COUNT(*) FROM tache GROUP BY statut";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlTachesParStatut)) {
                while (rs.next()) {
                    String statut = rs.getString(1);
                    int count = rs.getInt(2);

                    switch(statut) {
                        case "A_FAIRE":
                            stats.setTachesAFaire(count);
                            break;
                        case "EN_COURS":
                            stats.setTachesEnCours(count);
                            break;
                        case "TERMINEE":
                            stats.setTachesTerminees(count);
                            break;
                    }
                }
            }

            // 4. Tâches en retard
            String sqlTachesRetard = "SELECT COUNT(*) FROM tache WHERE deadline < CURDATE() AND statut != 'TERMINEE'";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlTachesRetard)) {
                if (rs.next()) {
                    stats.setTachesEnRetard(rs.getInt(1));
                }
            }

            // 5. Total des plannings
            String sqlPlannings = "SELECT COUNT(*) FROM planning";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlPlannings)) {
                if (rs.next()) {
                    stats.setTotalPlannings(rs.getInt(1));
                }
            }

            // 6. Employés en poste aujourd'hui
            String sqlEnPoste = "SELECT COUNT(DISTINCT employe_id) FROM planning WHERE date = CURDATE()";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlEnPoste)) {
                if (rs.next()) {
                    stats.setEmployesEnPoste(rs.getInt(1));
                }
            }

            // 7. Employés absents aujourd'hui
            int totalEmployes = stats.getTotalEmployes();
            int enPoste = stats.getEmployesEnPoste();
            stats.setEmployesAbsents(totalEmployes - enPoste);

            // 8. Total des projets
            String sqlProjets = "SELECT COUNT(*) FROM projet";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlProjets)) {
                if (rs.next()) {
                    stats.setTotalProjets(rs.getInt(1));
                }
            }

            // 9. Plannings par type de shift
            String sqlShifts = "SELECT type_shift, COUNT(*) FROM planning GROUP BY type_shift";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlShifts)) {
                while (rs.next()) {
                    String shift = rs.getString(1);
                    int count = rs.getInt(2);

                    switch(shift) {
                        case "JOUR":
                            stats.setPlanningsMatin(count);
                            break;
                        case "SOIR":
                            stats.setPlanningsSoir(count);
                            break;
                        case "NUIT":
                            stats.setPlanningsNuit(count);
                            break;
                    }
                }
            }

            // 10. Dernières tâches
            List<String> dernieresTaches = new ArrayList<>();
            String sqlDernieresTaches = "SELECT t.*, u.username FROM tache t " +
                    "LEFT JOIN utilisateur u ON t.employe_id = u.id " +
                    "ORDER BY t.id DESC LIMIT 5";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlDernieresTaches)) {
                while (rs.next()) {
                    String tache = rs.getString("titre") + " - " +
                            (rs.getString("username") != null ? rs.getString("username") : "Employé " + rs.getInt("employe_id"));
                    dernieresTaches.add(tache);
                }
            }
            stats.setDernieresTaches(dernieresTaches);

            // 11. Prochains plannings
            List<String> prochainsPlannings = new ArrayList<>();
            String sqlProchainsPlannings = "SELECT p.*, u.username FROM planning p " +
                    "LEFT JOIN utilisateur u ON p.employe_id = u.id " +
                    "WHERE p.date >= CURDATE() " +
                    "ORDER BY p.date ASC LIMIT 5";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlProchainsPlannings)) {
                while (rs.next()) {
                    String planning = rs.getString("username") + " - " +
                            rs.getDate("date") + " " +
                            rs.getTime("heure_debut") + "-" +
                            rs.getTime("heure_fin");
                    prochainsPlannings.add(planning);
                }
            }
            stats.setProchainsPlannings(prochainsPlannings);

            // 12. Tâches par employé
            Map<String, Integer> tachesParEmploye = new HashMap<>();
            String sqlTachesParEmp = "SELECT u.username, COUNT(t.id) as nb_taches " +
                    "FROM utilisateur u " +
                    "LEFT JOIN tache t ON u.id = t.employe_id " +
                    "WHERE u.role = 'employe' OR u.role = 'EMPLOYE' " +
                    "GROUP BY u.id, u.username";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlTachesParEmp)) {
                while (rs.next()) {
                    tachesParEmploye.put(rs.getString("username"), rs.getInt("nb_taches"));
                }
            }
            stats.setTachesParEmploye(tachesParEmploye);

            // 13. Plannings par employé
            Map<String, Integer> planningsParEmploye = new HashMap<>();
            String sqlPlanningsParEmp = "SELECT u.username, COUNT(p.id) as nb_plannings " +
                    "FROM utilisateur u " +
                    "LEFT JOIN planning p ON u.id = p.employe_id " +
                    "WHERE u.role = 'employe' OR u.role = 'EMPLOYE' " +
                    "GROUP BY u.id, u.username";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlPlanningsParEmp)) {
                while (rs.next()) {
                    planningsParEmploye.put(rs.getString("username"), rs.getInt("nb_plannings"));
                }
            }
            stats.setPlanningsParEmploye(planningsParEmploye);

        } catch (SQLException e) {
            System.err.println("❌ Erreur dans StatsService:");
            e.printStackTrace();
        }

        return stats;
    }
}