package services;

import models.Projet;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetService {

    /**
     * Récupère tous les projets non archivés
     */
    public List<Projet> getAllProjets() {
        List<Projet> list = new ArrayList<>();
        String sql = "SELECT * FROM projet WHERE is_archived = 0 ORDER BY id DESC";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractProjetFromResultSet(rs));
            }
            System.out.println("✅ " + list.size() + " projets chargés");

        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllProjets: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère un projet par son ID
     */
    public Projet getProjetById(int id) {
        String sql = "SELECT * FROM projet WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractProjetFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur getProjetById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ajouter un nouveau projet
     */
    public void ajouterProjet(Projet projet) {
        String sql = "INSERT INTO projet (nom, description, date_debut, date_fin, budget, statut, responsable_id, equipe_membres, progression, is_archived) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projet.getNom());
            ps.setString(2, projet.getDescription());
            ps.setDate(3, new java.sql.Date(projet.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(projet.getDateFin().getTime()));
            ps.setDouble(5, projet.getBudget());
            ps.setString(6, projet.getStatut());
            ps.setInt(7, projet.getResponsableId());
            ps.setString(8, projet.getEquipeMembres());
            ps.setInt(9, projet.getProgression());

            ps.executeUpdate();
            System.out.println("✅ Projet ajouté: " + projet.getNom());

        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout projet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Modifier un projet existant
     */
    public void modifierProjet(Projet projet) {
        String sql = "UPDATE projet SET nom=?, description=?, date_debut=?, date_fin=?, budget=?, statut=?, " +
                "responsable_id=?, equipe_membres=?, progression=? WHERE id=?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projet.getNom());
            ps.setString(2, projet.getDescription());
            ps.setDate(3, new java.sql.Date(projet.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(projet.getDateFin().getTime()));
            ps.setDouble(5, projet.getBudget());
            ps.setString(6, projet.getStatut());
            ps.setInt(7, projet.getResponsableId());
            ps.setString(8, projet.getEquipeMembres());
            ps.setInt(9, projet.getProgression());
            ps.setInt(10, projet.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Projet modifié: " + projet.getNom());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur modification projet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprimer un projet définitivement
     */
    public void supprimerProjet(int projetId) {
        String sql = "DELETE FROM projet WHERE id=?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Projet supprimé (ID: " + projetId + ")");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression projet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ⭐ Archiver un projet (soft delete)
     */
    public void archiverUnProjet(int projetId) {
        String sql = "UPDATE projet SET is_archived = 1 WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Projet archivé (ID: " + projetId + ")");
            } else {
                System.out.println("⚠️ Aucun projet trouvé avec ID: " + projetId);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur archivage projet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ⭐ Désarchiver un projet
     */
    public void desarchiverProjet(int projetId) {
        String sql = "UPDATE projet SET is_archived = 0 WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Projet désarchivé (ID: " + projetId + ")");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur désarchivage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Récupère tous les projets archivés
     */
    public List<Projet> getProjetsArchives() {
        List<Projet> list = new ArrayList<>();
        String sql = "SELECT * FROM projet WHERE is_archived = 1 ORDER BY id DESC";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractProjetFromResultSet(rs));
            }
            System.out.println("✅ " + list.size() + " projets archivés chargés");

        } catch (SQLException e) {
            System.err.println("❌ Erreur getProjetsArchives: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Extrait un projet depuis un ResultSet
     */
    private Projet extractProjetFromResultSet(ResultSet rs) throws SQLException {
        Projet p = new Projet();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setDateDebut(rs.getDate("date_debut"));
        p.setDateFin(rs.getDate("date_fin"));
        p.setBudget(rs.getDouble("budget"));
        p.setStatut(rs.getString("statut"));
        p.setProgression(rs.getInt("progression"));
        p.setArchived(rs.getBoolean("is_archived"));

        // Champs supplémentaires (peuvent ne pas exister)
        try { p.setResponsableId(rs.getInt("responsable_id")); } catch (SQLException e) { p.setResponsableId(0); }
        try { p.setEquipeMembres(rs.getString("equipe_membres")); } catch (SQLException e) { p.setEquipeMembres(""); }

        return p;
    }
}