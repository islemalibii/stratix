package Services;

import models.Role;
import models.Utilisateur;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService {
    private static UtilisateurService instance;
    private Connection connection;

    private UtilisateurService() {
        connection = MyDataBase.getInstance().getConnection();
        if (connection == null) {
            System.err.println("ATTENTION: Connexion à la base de données non disponible");
        }
    }

    public static UtilisateurService getInstance() {
        if (instance == null) {
            instance = new UtilisateurService();
        }
        return instance;
    }

    // Ajouter un utilisateur
    public void ajouter(Utilisateur utilisateur) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }
        
        String query = "INSERT INTO utilisateur (nom, prenom, email, tel, cin, password, role, date_ajout, " +
                      "department, poste, date_embauche, competences, salaire) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getEmail());
        ps.setString(4, utilisateur.getTel());
        ps.setInt(5, utilisateur.getCin());
        ps.setString(6, utilisateur.getPassword());
        ps.setString(7, utilisateur.getRole().name().toLowerCase());
        ps.setDate(8, Date.valueOf(utilisateur.getDateAjout() != null ? utilisateur.getDateAjout() : LocalDate.now()));
        
        // Champs optionnels (NULL si non employé)
        ps.setString(9, utilisateur.getDepartment());
        ps.setString(10, utilisateur.getPoste());
        ps.setDate(11, utilisateur.getDateEmbauche() != null ? Date.valueOf(utilisateur.getDateEmbauche()) : null);
        ps.setString(12, utilisateur.getCompetences());
        
        // Salaire: NULL si 0.0 (pour admin/CEO)
        if (utilisateur.getSalaire() == 0.0) {
            ps.setNull(13, Types.DECIMAL);
        } else {
            ps.setDouble(13, utilisateur.getSalaire());
        }
        
        ps.executeUpdate();
    }

    // Récupérer tous les utilisateurs
    public List<Utilisateur> getAll() throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }
        
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        
        while (rs.next()) {
            utilisateurs.add(mapResultSetToUtilisateur(rs));
        }
        
        return utilisateurs;
    }

    // Récupérer par rôle
    public List<Utilisateur> getByRole(Role role) throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE role = ?";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, role.name().toLowerCase());
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            utilisateurs.add(mapResultSetToUtilisateur(rs));
        }
        
        return utilisateurs;
    }

    // Récupérer par ID
    public Utilisateur getById(int id) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }
        
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToUtilisateur(rs);
        }
        return null;
    }

    // Récupérer par email (pour login)
    public Utilisateur getByEmail(String email) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }
        
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToUtilisateur(rs);
        }
        return null;
    }

    // Modifier un utilisateur
    public void modifier(Utilisateur utilisateur) throws SQLException {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, tel = ?, cin = ?, " +
                      "password = ?, role = ?, department = ?, poste = ?, competences = ?, salaire = ? " +
                      "WHERE id = ?";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getEmail());
        ps.setString(4, utilisateur.getTel());
        ps.setInt(5, utilisateur.getCin());
        ps.setString(6, utilisateur.getPassword());
        ps.setString(7, utilisateur.getRole().name().toLowerCase());
        ps.setString(8, utilisateur.getDepartment());
        ps.setString(9, utilisateur.getPoste());
        ps.setString(10, utilisateur.getCompetences());
        
        // Salaire: NULL si 0.0 (pour admin/CEO)
        if (utilisateur.getSalaire() == 0.0) {
            ps.setNull(11, Types.DECIMAL);
        } else {
            ps.setDouble(11, utilisateur.getSalaire());
        }
        ps.setInt(12, utilisateur.getId());
        
        ps.executeUpdate();
    }

    // Supprimer un utilisateur
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM utilisateur WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    // Mettre à jour le mot de passe
    public void updatePassword(String email, String hashedPassword) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }
        
        String query = "UPDATE utilisateur SET password = ? WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, hashedPassword);
        ps.setString(2, email);
        ps.executeUpdate();
    }

    // Mapper ResultSet vers Utilisateur
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id"));
        utilisateur.setNom(rs.getString("nom"));
        utilisateur.setPrenom(rs.getString("prenom"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setTel(rs.getString("tel"));
        utilisateur.setCin(rs.getInt("cin"));
        utilisateur.setPassword(rs.getString("password"));
        
        // Convertir le role string en enum
        String roleStr = rs.getString("role");
        utilisateur.setRole(Role.valueOf(roleStr.toUpperCase()));
        
        // Date d'ajout
        Date dateAjout = rs.getDate("date_ajout");
        if (dateAjout != null) {
            utilisateur.setDateAjout(dateAjout.toLocalDate());
        }
        
        // Champs optionnels (peuvent être NULL)
        utilisateur.setDepartment(rs.getString("department"));
        utilisateur.setPoste(rs.getString("poste"));
        utilisateur.setCompetences(rs.getString("competences"));
        utilisateur.setSalaire(rs.getDouble("salaire"));
        
        Date dateEmbauche = rs.getDate("date_embauche");
        if (dateEmbauche != null) {
            utilisateur.setDateEmbauche(dateEmbauche.toLocalDate());
        }
        
        return utilisateur;
    }
}
