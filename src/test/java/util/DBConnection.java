package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Correction de l'URL pour MySQL
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/stratix";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Vide par défaut sur XAMPP

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }
}