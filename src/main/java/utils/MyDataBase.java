package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/stratix";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    private MyDataBase() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion établie");
            } catch (SQLException e) {
                System.err.println("Erreur : " + e.getMessage());
            }
        }
        return connection;
    }
}