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
    public List<Evenement> getAll() {
        List<Evenement> evenements = new ArrayList<>();
        String req ="SELECT * FROM `evenement`";
        try{
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while(rs.next()){
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setDescription(rs.getString("description"));
                e.setTitre(rs.getString("titre"));
                e.setType_event(EventType.valueOf(rs.getString("type_event").toLowerCase()));
                e.setStatut(EventStatus.valueOf(rs.getString("statut").toLowerCase()));
                e.setLieu(rs.getString("lieu"));
                e.setDate_event(rs.getDate("date_event").toLocalDate());


                evenements.add(e);
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return evenements;
    }

    @Override
    public void archiver(int id) {
        try {
            String req = "INSERT INTO archiveEvent (id, type_event, date_event, description, statut, lieu, titre) " +
                    "SELECT id, type_event, date_event, description, statut, lieu, titre " +
                    "FROM evenement WHERE id = ?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();

            String delete = "DELETE FROM evenement WHERE id = ?";
            PreparedStatement psDelete = cnx.prepareStatement(delete);
            psDelete.setInt(1, id);
            psDelete.executeUpdate();

            System.out.println("Evenement archived successfully");

        } catch (SQLException ex) {
            System.out.println("Erreur: " + ex.getMessage());
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

    public List<Evenement> getAllArchieved() {
        List<Evenement> evenementsArchiver = new ArrayList<>();
        String req ="SELECT * FROM `archiveevent`";
        try{
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while(rs.next()){
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setDescription(rs.getString("description"));
                e.setTitre(rs.getString("titre"));
                e.setType_event(EventType.valueOf(rs.getString("type_event").toLowerCase()));
                e.setStatut(EventStatus.valueOf(rs.getString("statut").toLowerCase()));
                e.setLieu(rs.getString("lieu"));
                e.setDate_event(rs.getDate("date_event").toLocalDate());


                evenementsArchiver.add(e);
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return evenementsArchiver;
    }

    public List<Evenement> searchByTitle(String title) {
        List<Evenement> newList = new ArrayList<>();
        String req = "SELECT * FROM evenement WHERE titre LIKE ?";

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

                newList.add(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return newList;
    }
}
