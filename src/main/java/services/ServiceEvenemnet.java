package services;

import interfaces.Services;
import models.Evenement;
import models.enums.EventStatus;
import models.enums.EventType;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvenemnet implements Services<Evenement> {

    Connection cnx ;
    public ServiceEvenemnet() {
        cnx = MyDataBase.getInstance().getCnx();
    }
    @Override
    public void add(Evenement evenement) {
        String req = "INSERT INTO evenement(type_event, date_event, description, statut, lieu, titre) " +
                "VALUES (?, ?, ?, ?, ?, ?)"; // l values ywalou treated as data mch sql ynajm yexecuta : protection contre l sql injection

        try (PreparedStatement pst = cnx.prepareStatement(req)) {

            pst.setString(1, evenement.getType_event().name());
            pst.setDate(2, java.sql.Date.valueOf(evenement.getDate_event())); // LocalDate ll SQL Date
            pst.setString(3, evenement.getDescription());
            pst.setString(4, evenement.getStatut().name());
            pst.setString(5, evenement.getLieu());
            pst.setString(6, evenement.getTitre());

            pst.executeUpdate();
            System.out.println("Evenement added successfully");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Evenement evenement) {

        String req = "update evenement set type_event=? , date_event=? , description=? , statut=? , lieu=? , titre=? where id=?";
        try{
            PreparedStatement pst = cnx.prepareStatement(req);

            pst.setString(1, evenement.getType_event().name());
            pst.setDate(2, java.sql.Date.valueOf(evenement.getDate_event())); // LocalDate → DATE
            pst.setString(3, evenement.getDescription());
            pst.setString(4, evenement.getStatut().name());
            pst.setString(5, evenement.getLieu());
            pst.setString(6, evenement.getTitre());
            pst.setInt(7, evenement.getId());

            pst.executeUpdate();
            System.out.println("evenement modifie");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void deleteById(int id) {
        String sql = "DELETE FROM evenement WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public List<Evenement> getAll() {
        return getByArchiveStatus(0);
    }

    public List<Evenement> getAllArchieved() {
        return getByArchiveStatus(1);
    }

    private List<Evenement> getByArchiveStatus(int status) {
        List<Evenement> list = new ArrayList<>();
        String req = "SELECT * FROM evenement WHERE isArchived = ?";

        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, status);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                e.setLieu(rs.getString("lieu"));
                e.setDate_event(rs.getDate("date_event").toLocalDate());
                e.setType_event(EventType.valueOf(rs.getString("type_event").toLowerCase()));
                e.setStatut(EventStatus.valueOf(rs.getString("statut").toLowerCase()));
                e.setArchived(rs.getInt("isArchived") == 1);

                list.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de récupération (status " + status + ") : " + ex.getMessage());
        }
        return list;
    }

    public void archiver(int id) {
        updateArchiveStatus(id, 1);
    }

    public void desarchiver(int id) {
        updateArchiveStatus(id, 0);
    }

    private void updateArchiveStatus(int id, int status) {
        String req = "UPDATE evenement SET isArchived = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, status);
            pst.setInt(2, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public List<Evenement> searchByTitle(String title) {
        List<Evenement> newList = new ArrayList<>();
        String req = "SELECT * FROM evenement WHERE titre LIKE ? AND isArchived = 0";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, "%" + title + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                e.setLieu(rs.getString("lieu"));
                e.setDate_event(rs.getDate("date_event").toLocalDate());
                e.setType_event(EventType.valueOf(rs.getString("type_event").toLowerCase()));
                e.setStatut(EventStatus.valueOf(rs.getString("statut").toLowerCase()));
                e.setArchived(false);

                newList.add(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return newList;
    }



    public void supprimer(int id) {
        String sql = "DELETE FROM evenement WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Événement supprimé définitivement de la base de données");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
