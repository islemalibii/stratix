package services;

import models.Tache;
import utiles.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SERVICETache {
    // CREATE
    public void addTache(Tache t) {
        String sql = """
            INSERT INTO tache (titre, description, deadline, statut, employe_id, projet_id, priorite)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, t.getTitre());
            ps.setString(2, t.getDescription());
            ps.setDate(3, t.getDeadline());
            ps.setString(4, t.getStatut());
            ps.setInt(5, t.getEmployeId());
            ps.setInt(6, t.getProjetId());
            ps.setString(7, t.getPriorite());

            ps.executeUpdate();
            System.out.println("✅ Tâche ajoutée");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public List<Tache> getAllTaches() {
        List<Tache> list = new ArrayList<>();
        String sql = "SELECT * FROM tache";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Tache t = new Tache();
                t.setId(rs.getInt("id"));
                t.setTitre(rs.getString("titre"));
                t.setDescription(rs.getString("description"));
                t.setDeadline(rs.getDate("deadline"));
                t.setStatut(rs.getString("statut"));
                t.setEmployeId(rs.getInt("employe_id"));
                t.setProjetId(rs.getInt("projet_id"));
                t.setPriorite(rs.getString("priorite"));

                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public void updateTache(Tache t) {
        String sql = """
            UPDATE tache
            SET titre=?, description=?, deadline=?, statut=?, employe_id=?, projet_id=?, priorite=?
            WHERE id=?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, t.getTitre());
            ps.setString(2, t.getDescription());
            ps.setDate(3, t.getDeadline());
            ps.setString(4, t.getStatut());
            ps.setInt(5, t.getEmployeId());
            ps.setInt(6, t.getProjetId());
            ps.setString(7, t.getPriorite());
            ps.setInt(8, t.getId());

            ps.executeUpdate();
            System.out.println(" Tâche mise à jour");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Ajoute cette méthode dans SERVICETache.java
    public void deleteTache(int id) {
        String sql = "DELETE FROM tache WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println(" Tâche supprimée");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Ajoute cette méthode pour récupérer une tâche par son ID
    public Tache getTacheById(int id) {
        String sql = "SELECT * FROM tache WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Tache t = new Tache();
                t.setId(rs.getInt("id"));
                t.setTitre(rs.getString("titre"));
                t.setDescription(rs.getString("description"));
                t.setDeadline(rs.getDate("deadline"));
                t.setStatut(rs.getString("statut"));
                t.setEmployeId(rs.getInt("employe_id"));
                t.setProjetId(rs.getInt("projet_id"));
                t.setPriorite(rs.getString("priorite"));
                return t;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}//
