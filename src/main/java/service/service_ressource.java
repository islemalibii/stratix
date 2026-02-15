package service;

import Interface.service;
import models.ressource;
import utils.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class service_ressource implements service<ressource> {
    @Override
    public void add(ressource ressource) {

        String req="INSERT INTO `ressource`(`id`, `nom`, `type_ressource`, `quantite`, `fournisseur`) VALUES (?,?,?,?,?)";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setInt(1, ressource.getid());
            ps.setString(2, ressource.getNom());
            ps.setString(3, ressource.getType_ressource());
            ps.setInt(4, ressource.getQuatite());
            ps.setString(5, ressource.getFournisseur());

            ps.executeUpdate();
            System.out.println("Ressource ajoutée avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }



    @Override
    public List<ressource> getAll() {

        List<ressource> list = new ArrayList<>();

        String req = "SELECT * FROM ressource";

        try {
            Connection cnx = database.getInstance().getCnx();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {

                ressource r = new ressource();

                r.setid(rs.getInt("id"));
                r.setNom(rs.getString("nom"));
                r.setType_ressource(rs.getString("type_ressource"));
                r.setQuatite(rs.getInt("quantite"));
                r.setFournisseur(rs.getString("fournisseur"));

                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll : " + e.getMessage());
        }

        return list;
    }


    @Override
    public void update(ressource ressource) {

        String req = "UPDATE ressource SET nom=?, type_ressource=?, quantite=?, fournisseur=? WHERE id=?";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setString(1, ressource.getNom());
            ps.setString(2, ressource.getType_ressource());
            ps.setInt(3, ressource.getQuatite());
            ps.setString(4, ressource.getFournisseur());
            ps.setInt(5, ressource.getid());

            int rows = ps.executeUpdate();

            if(rows > 0)
                System.out.println("Ressource modifiée !");
            else
                System.out.println("Aucune ressource avec cet id !");

        } catch (SQLException e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
    }


    @Override
    public void delete(ressource ressource) {

        String req = "DELETE FROM ressource WHERE id=?";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setInt(1, ressource.getid());

            int rows = ps.executeUpdate();

            if(rows > 0)
                System.out.println("Ressource supprimée !");
            else
                System.out.println("Aucune ressource trouvée avec cet id");

        } catch (SQLException e) {
            System.out.println("Erreur delete : " + e.getMessage());
        }
    }
}
