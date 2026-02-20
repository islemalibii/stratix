package service;

import Interface.service;
import models.produit;
import utils.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class service_produit implements service<produit> {

    @Override
    public void add(produit p) {
        // CORRECTION: Enlever l'ID de la liste des colonnes (auto-incrémenté)
        String req = "INSERT INTO produit(nom, description, categorie, prix, stock_actuel, stock_min, date_creation, ressources_necessaires, image_path) VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            Connection cnx = database.getInstance().getCnx();
            // Utiliser RETURN_GENERATED_KEYS pour récupérer l'ID généré
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getCategorie());
            ps.setDouble(4, p.getPrix());
            ps.setInt(5, p.getStock_actuel());
            ps.setInt(6, p.getStock_min());
            ps.setDate(7, Date.valueOf(p.getDate_creation()));
            ps.setString(8, p.getRessources_necessaires());
            ps.setString(9, p.getImage_path()); // Ajout de l'image

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Récupérer l'ID généré automatiquement
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
                System.out.println("Produit ajouté avec succès ! ID: " + p.getId());
            } else {
                System.out.println("Échec de l'ajout du produit");
            }

        } catch (SQLException e) {
            System.out.println("Erreur add produit : " + e.getMessage());
            e.printStackTrace();
        }
    }

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

                Date dateSql = rs.getDate("date_creation");
                if (dateSql != null) {
                    p.setDate_creation(dateSql.toString());
                }

                p.setRessources_necessaires(rs.getString("ressources_necessaires"));

                // AJOUT: Récupérer l'image_path
                try {
                    p.setImage_path(rs.getString("image_path"));
                } catch (SQLException e) {
                    p.setImage_path(null); // La colonne n'existe pas encore
                }

                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll produit : " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void update(produit p) {
        // CORRECTION: Ajouter image_path dans la mise à jour
        String req = "UPDATE produit SET nom=?, description=?, categorie=?, prix=?, stock_actuel=?, stock_min=?, date_creation=?, ressources_necessaires=?, image_path=? WHERE id=?";

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
            ps.setString(9, p.getImage_path()); // Ajout de l'image
            ps.setInt(10, p.getId());

            int rows = ps.executeUpdate();

            if(rows > 0)
                System.out.println("Produit modifié ! ID: " + p.getId());
            else
                System.out.println("Produit introuvable !");

        } catch (SQLException e) {
            System.out.println("Erreur update produit : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(produit p) {
        String req = "DELETE FROM produit WHERE id=?";

        try {
            Connection cnx = database.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setInt(1, p.getId());

            int rows = ps.executeUpdate();

            if(rows > 0)
                System.out.println("Produit supprimé ! ID: " + p.getId());
            else
                System.out.println("Produit introuvable !");

        } catch (SQLException e) {
            System.out.println("Erreur delete produit : " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}