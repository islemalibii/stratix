package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private static MyDataBase instance;
    private static final String URL = "jdbc:mysql://localhost:3306/stratix";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private Connection cnx;

    private MyDataBase() {
        connect();
    }

    private void connect() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connecté à la base de données");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion: " + e.getMessage());
            this.cnx = null;
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            // Vérifier si la connexion est fermée ou null
            if (cnx == null || cnx.isClosed()) {
                System.out.println("🔄 Reconnexion à la base de données...");
                connect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur vérification connexion: " + e.getMessage());
            connect();
        }
        return cnx;
    }

    // Méthode pour fermer proprement la connexion
    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("✅ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur fermeture connexion: " + e.getMessage());
        }
    }
}