package services;

import models.Ressource;
import utils.MyDataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceEventRessource {
    Connection cnx = MyDataBase.getInstance().getCnx();


    public void addRessourceToEvent(int eventId, int ressourceId, int quantite) {

        String req = "INSERT INTO event_ressource (evenement_id, ressource_id, quantite) VALUES (?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {

            ps.setInt(1, eventId);
            ps.setInt(2, ressourceId);
            ps.setInt(3, quantite);

            ps.executeUpdate();
            System.out.println("Ressource added to event");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    //temporary
    public List<Ressource> getAllRessources() {

        List<Ressource> list = new ArrayList<>();
        String req = "SELECT * FROM ressource";

        try (PreparedStatement ps = cnx.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ressource r = new Ressource();

                r.setid(rs.getInt("id"));
                r.setNom(rs.getString("nom"));
                r.setType_ressource(rs.getString("type_ressource"));
                r.setQuatite(rs.getInt("quantite"));
                r.setFournisseur(rs.getString("fournisseur"));

                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }
}
