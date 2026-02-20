package services;

import models.Employe;
import utiles.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    public List<Employe> getAllEmployes() {
        List<Employe> list = new ArrayList<>();
        String sql = "SELECT e.*, u.username, u.email, u.tel, u.role " +
                "FROM employe e " +
                "JOIN utilisateur u ON e.utilisateur_id = u.id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Employe e = new Employe();
                e.setId(rs.getInt("id"));
                e.setUtilisateurId(rs.getInt("utilisateur_id"));
                e.setDepartment(rs.getString("department"));
                e.setPoste(rs.getString("poste"));
                e.setDateEmbauche(rs.getString("date_embauche"));
                e.setSalaire(rs.getDouble("salaire"));
                e.setCompetences(rs.getString("competences"));
                e.setUsername(rs.getString("username"));
                e.setEmail(rs.getString("email"));
                e.setTel(rs.getString("tel"));
                e.setRole(rs.getString("role"));

                list.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Employe getEmployeById(int id) {
        String sql = "SELECT e.*, u.username, u.email, u.tel, u.role " +
                "FROM employe e " +
                "JOIN utilisateur u ON e.utilisateur_id = u.id " +
                "WHERE e.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employe e = new Employe();
                e.setId(rs.getInt("id"));
                e.setUtilisateurId(rs.getInt("utilisateur_id"));
                e.setDepartment(rs.getString("department"));
                e.setPoste(rs.getString("poste"));
                e.setDateEmbauche(rs.getString("date_embauche"));
                e.setSalaire(rs.getDouble("salaire"));
                e.setCompetences(rs.getString("competences"));
                e.setUsername(rs.getString("username"));
                e.setEmail(rs.getString("email"));
                e.setTel(rs.getString("tel"));
                e.setRole(rs.getString("role"));
                return e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}