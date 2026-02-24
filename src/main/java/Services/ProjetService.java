package Services;

import interfaces.ProjServices;
import models.Projet;
import utils.MyDataBase; // Mise à jour de l'import
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjetService implements ProjServices {
    private Connection connection;

    public ProjetService() {
        this.connection = MyDataBase.getInstance().getCnx();
    }

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
            ps.setInt(9, p.getProgression());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Projet> listerTousLesProjets() {
        return recupererParEtat(0); // 0 = Non archivés
    }

    @Override
    public List<Projet> listerArchives() {
        return recupererParEtat(1); // 1 = Archivés
    }

    private List<Projet> recupererParEtat(int etat) {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM projet WHERE is_archived = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projets.add(new Projet(
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
                        rs.getString("equipe_membres")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    @Override
    public void archiverUnProjet(int id) {
        String sql = "UPDATE projet SET is_archived = 1 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void desarchiverUnProjet(int id) {
        String sql = "UPDATE projet SET is_archived = 0 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public Projet chercherProjetParId(int idProjet) {
        String sql = "SELECT * FROM projet WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idProjet);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Projet(
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
                            rs.getString("equipe_membres")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du projet (ID: " + idProjet + ") : " + e.getMessage());
        }
        return null;
    }

    public List<Projet> rechercherProjets(String query, String statut) {
        List<Projet> tous = listerTousLesProjets();
        return tous.stream()
                .filter(p -> (statut.equals("Tous les projets") || p.getStatut().equals(statut)))
                .filter(p -> p.getNom().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}