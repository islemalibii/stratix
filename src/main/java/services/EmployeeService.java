package services;

import models.Employe;
import utiles.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    public List<Employe> getAllEmployes() {
        List<Employe> list = new ArrayList<>();
        // Récupérer tous les utilisateurs avec rôle employé ou responsable
        String sql = "SELECT * FROM utilisateur WHERE role = 'employe' OR role = 'EMPLOYE' OR role LIKE 'responsable%'";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Employe e = new Employe();
                e.setId(rs.getInt("id"));
                e.setUsername(rs.getString("username"));
                e.setEmail(rs.getString("email"));
                e.setTel(rs.getString("tel"));
                e.setPassword(rs.getString("password"));
                e.setRole(rs.getString("role"));
                e.setDateAjout(rs.getString("date_ajout"));

                // Ces champs peuvent être null dans ta table
                e.setDepartment(null);
                e.setPoste(null);
                e.setDateEmbauche(null);
                e.setSalaire(0);
                e.setCompetences(null);

                list.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Employe getEmployeById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employe e = new Employe();
                e.setId(rs.getInt("id"));
                e.setUsername(rs.getString("username"));
                e.setEmail(rs.getString("email"));
                e.setTel(rs.getString("tel"));
                e.setPassword(rs.getString("password"));
                e.setRole(rs.getString("role"));
                e.setDateAjout(rs.getString("date_ajout"));

                // Champs optionnels
                e.setDepartment(null);
                e.setPoste(null);
                e.setDateEmbauche(null);
                e.setSalaire(0);
                e.setCompetences(null);

                return e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour vérifier si un ID existe
    public boolean employeExiste(int id) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}