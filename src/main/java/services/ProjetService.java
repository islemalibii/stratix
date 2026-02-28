package services;

import interfaces.ProjServices;
import models.Projet;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjetService implements ProjServices {

    private final Connection connection;

    public ProjetService() {
        this.connection = MyDataBase.getInstance().getCnx();
    }

    // ==============================
    // AJOUTER PROJET
    // ==============================
    @Override
    public void ajouterProjet(Projet p) {
        String sql = "INSERT INTO projet (nom, description, date_debut, date_fin, budget, statut, responsable_id, equipe_membres, progression, is_archived) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setDate(3, new java.sql.Date(p.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(p.getDateFin().getTime()));
            ps.setDouble(5, p.getBudget());
            ps.setString(6, p.getStatut());
            ps.setInt(7, p.getResponsableId());
            ps.setString(8, p.getEquipeMembres());
            ps.setInt(9, 0); // progression initiale
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // LISTER PROJETS
    // ==============================
    @Override
    public List<Projet> listerTousLesProjets() {
        return recupererParEtat(0);
    }

    @Override
    public List<Projet> listerArchives() {
        return recupererParEtat(1);
    }

    private List<Projet> recupererParEtat(int etat) {
        List<Projet> projets = new ArrayList<>();

        String sql = "SELECT p.*, u.nom AS nom_chef " +
                "FROM projet p " +
                "LEFT JOIN utilisateur u ON p.responsable_id = u.id " +
                "WHERE p.is_archived = ? ORDER BY p.id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etat);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Projet p = new Projet(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getDouble("budget"),
                        rs.getString("statut"),
                        rs.getInt("progression"),
                        rs.getBoolean("is_archived"),
                        rs.getInt("responsable_id"),
                        nettoyerNomsEquipe(rs.getString("equipe_membres"))
                );

                p.setNomResponsable(rs.getString("nom_chef"));
                projets.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projets;
    }

    // ==============================
    // CHERCHER PAR ID
    // ==============================
    @Override
    public Projet chercherProjetParId(int idProjet) {
        String sql = "SELECT p.*, u.nom AS nom_chef FROM projet p " +
                "LEFT JOIN utilisateur u ON p.responsable_id = u.id WHERE p.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Projet p = new Projet(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getDouble("budget"),
                        rs.getString("statut"),
                        rs.getInt("progression"),
                        rs.getBoolean("is_archived"),
                        rs.getInt("responsable_id"),
                        nettoyerNomsEquipe(rs.getString("equipe_membres"))
                );

                p.setNomResponsable(rs.getString("nom_chef"));
                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // MISE À JOUR PROGRESSION AUTO
    // ==============================
    public void mettreAJourProgression(int projetId) {

        String sqlCount = "SELECT COUNT(*) as total, " +
                "SUM(CASE WHEN statut = 'TERMINEE' THEN 1 ELSE 0 END) as terminees " +
                "FROM tache WHERE projet_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sqlCount)) {
            ps.setInt(1, projetId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                int terminees = rs.getInt("terminees");

                int progression = (total > 0) ? (terminees * 100) / total : 0;

                String sqlUpdate = "UPDATE projet SET progression = ? WHERE id = ?";
                try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)) {
                    psUpdate.setInt(1, progression);
                    psUpdate.setInt(2, projetId);
                    psUpdate.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // MODIFIER
    // ==============================
    @Override
    public void mettreAJourProjet(Projet p) {
        String sql = "UPDATE projet SET nom=?, description=?, date_debut=?, date_fin=?, budget=?, statut=?, responsable_id=?, equipe_membres=?, progression=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setDate(3, new java.sql.Date(p.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(p.getDateFin().getTime()));
            ps.setDouble(5, p.getBudget());
            ps.setString(6, p.getStatut());
            ps.setInt(7, p.getResponsableId());
            ps.setString(8, p.getEquipeMembres());
            ps.setInt(9, p.getProgression());
            ps.setInt(10, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // ARCHIVER / SUPPRIMER
    // ==============================
    @Override
    public void archiverUnProjet(int id) {
        changerEtatArchive(id, 1);
    }

    @Override
    public void desarchiverUnProjet(int id) {
        changerEtatArchive(id, 0);
    }

    private void changerEtatArchive(int id, int etat) {
        String sql = "UPDATE projet SET is_archived = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etat);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerUnProjet(int id) {
        String sql = "DELETE FROM projet WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // RECHERCHE
    // ==============================
    public List<Projet> rechercherProjets(String query, String statut) {
        return listerTousLesProjets().stream()
                .filter(p -> statut.equals("Tous les projets") || p.getStatut().equals(statut))
                .filter(p -> p.getNom().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ==============================
    // UTILITAIRE
    // ==============================
    private String nettoyerNomsEquipe(String bruts) {
        if (bruts == null || bruts.isEmpty()) return "Aucun membre";
        return bruts.replaceAll("\\d+\\s*-\\s*", "");
    }
}