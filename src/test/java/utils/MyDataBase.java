package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    final String URL ="jdbc:mysql://127.0.0.1:3306/stratix";
    final String USERNAME = "root";
    final String PASSWORD = "";
    private Connection connection;


    private MyDataBase() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
