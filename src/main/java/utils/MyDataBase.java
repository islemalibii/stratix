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

    private MyDataBase(){
        try {
            cnx = DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public static MyDataBase getInstance(){
        if(instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }

}
