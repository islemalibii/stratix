package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db {
    private static final String URL = "jdbc:mysql://localhost:3306/stratix";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static boolean premiereConnexion = true;

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        if (premiereConnexion) {
            System.out.println("Connexion reussie");
            premiereConnexion = false;
        }
        return conn;
    }
}