package service;

import interfaces.Services;
import model.Projet;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
public class ProjetService implements Services {

    private Connection connection;

    public ProjetService() {
        this.connection = DBConnection.getConnection();
    }

    public void ajouterProjet(Projet p) {
        if (p.getBudget() < 0) {
            System.err.println("Erreur : Le budget ne peut pas être négatif !");
            return;
        }

        if (p.getDateFin() != null && p.getDateDebut() != null && p.getDateFin().before(p.getDateDebut())) {
            System.err.println("Erreur : La date de fin doit être après la date de début !");
            return;
        }

        String sql = "INSERT INTO projet (nom, description, date_debut, date_fin, budget, statut, progression) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());

            ps.setDate(3, new java.sql.Date(p.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(p.getDateFin().getTime()));

            ps.setDouble(5, p.getBudget());
            ps.setString(6, p.getStatut());
            ps.setInt(7, p.getProgression());

            ps.executeUpdate();
            System.out.println("> Projet inséré avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur Insertion: " + e.getMessage());
        }
    }

    public void mettreAJourProjet(Projet p) {
        if (p.getProgression() < 0 || p.getProgression() > 100) {
            System.err.println("Erreur : Progression doit être entre 0 et 100.");
            return;
        }

        if (p.getDateFin() != null && p.getDateDebut() != null && p.getDateFin().before(p.getDateDebut())) {
            System.err.println("Erreur : Date fin invalide !");
            return;
        }

        String sql = "UPDATE projet SET nom=?, description=?, date_debut=?, date_fin=?, budget=?, statut=?, progression=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());

            ps.setDate(3, new java.sql.Date(p.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(p.getDateFin().getTime()));

            ps.setDouble(5, p.getBudget());
            ps.setString(6, p.getStatut());
            ps.setInt(7, p.getProgression());
            ps.setInt(8, p.getId());

            ps.executeUpdate();
            System.out.println("> Projet mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Projet mapResultSetToProjet(ResultSet rs) throws SQLException {
        Date dateDebut = rs.getDate("date_debut");
        Date dateFin = rs.getDate("date_fin");

        return new Projet(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                dateDebut,
                dateFin,
                rs.getDouble("budget"),
                rs.getString("statut"),
                rs.getInt("progression")
        );
    }

    public List<Projet> listerTousLesProjets() {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM projet";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) { projets.add(mapResultSetToProjet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return projets;
    }

    public List<Projet> listerArchives() {
        List<Projet> projetsArchives = new ArrayList<>();
        String sql = "SELECT * FROM archiveProjet";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) { projetsArchives.add(mapResultSetToProjet(rs)); }
        } catch (SQLException e) { System.err.println("Erreur archives : " + e.getMessage()); }
        return projetsArchives;
    }

    public void archiverUnProjet(int id) {
        String sqlCopie = "INSERT INTO archiveProjet SELECT * FROM projet WHERE id=?";
        String sqlSuppression = "DELETE FROM projet WHERE id=?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psCopie = connection.prepareStatement(sqlCopie);
                 PreparedStatement psSupp = connection.prepareStatement(sqlSuppression)) {
                psCopie.setInt(1, id);
                if (psCopie.executeUpdate() > 0) {
                    psSupp.setInt(1, id);
                    psSupp.executeUpdate();
                    connection.commit();
                }
            } catch (SQLException e) { connection.rollback(); e.printStackTrace();
            } finally { connection.setAutoCommit(true); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void supprimerUnProjet(int id) {
        String sql = "DELETE FROM projet WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Projet chercherProjetParId(int id) {
        String sql = "SELECT * FROM projet WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToProjet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}