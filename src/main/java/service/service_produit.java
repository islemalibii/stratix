package service;

import Interface.service;
import models.produit;
import utils.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class service_produit implements service<produit> {

    // ================= ADD =================
    @Override
    public void add(produit p) {

        String req = "INSERT INTO produit(id, nom, description, categorie, prix, stock_actuel, stock_min, date_creation, ressources_necessaires) VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setInt(1, p.getId());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getCategorie());
            ps.setDouble(5, p.getPrix());
            ps.setInt(6, p.getStock_actuel());
            ps.setInt(7, p.getStock_min());
            ps.setDate(8, Date.valueOf(p.getDate_creation()));
            ps.setString(9, p.getRessources_necessaires());

            ps.executeUpdate();
            System.out.println("Produit ajouté avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur add produit : " + e.getMessage());
        }
    }

    // ================= GET ALL =================
    @Override
    public List<produit> getAll() {

        List<produit> list = new ArrayList<>();
        String req = "SELECT * FROM produit";

        try {
            Connection cnx = database.getInstance().getCnx();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {

                produit p = new produit();

                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setDescription(rs.getString("description"));
                p.setCategorie(rs.getString("categorie"));
                p.setPrix(rs.getDouble("prix"));
                p.setStock_actuel(rs.getInt("stock_actuel"));
                p.setStock_min(rs.getInt("stock_min"));
                p.setDate_creation(String.valueOf(rs.getDate("date_creation").toLocalDate()));
                p.setRessources_necessaires(rs.getString("ressources_necessaires"));

                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll produit : " + e.getMessage());
        }

        return list;
    }

    // ================= UPDATE =================
    @Override
    public void update(produit p) {

        String req = "UPDATE produit SET nom=?, description=?, categorie=?, prix=?, stock_actuel=?, stock_min=?, date_creation=?, ressources_necessaires=? WHERE id=?";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getCategorie());
            ps.setDouble(4, p.getPrix());
            ps.setInt(5, p.getStock_actuel());
            ps.setInt(6, p.getStock_min());
            ps.setDate(7, Date.valueOf(p.getDate_creation()));
            ps.setString(8, p.getRessources_necessaires());
            ps.setInt(9, p.getId());

            int rows = ps.executeUpdate();

            if(rows > 0)
                System.out.println("Produit modifié !");
            else
                System.out.println("Produit introuvable !");

        } catch (SQLException e) {
            System.out.println("Erreur update produit : " + e.getMessage());
        }
    }

    // ================= DELETE =================
    @Override
    public void delete(produit p) {

        String req = "DELETE FROM produit WHERE id=?";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setInt(1, p.getId());

            int rows = ps.executeUpdate();

            if(rows > 0)
                System.out.println("Produit supprimé !");
            else
                System.out.println("Produit introuvable !");

        } catch (SQLException e) {
            System.out.println("Erreur delete produit : " + e.getMessage());
        }
    }
    public void deleteAll() {
        String sql = "DELETE FROM produit";

        try (Connection cnx = database.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.executeUpdate();
            System.out.println("Table produit vidée avec succès");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }

}}