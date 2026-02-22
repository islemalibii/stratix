package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mydatabase {

    private static Mydatabase instance;
    final String URL="jdbc:mysql://127.0.0.1:3306/stratix";
    final String USERNAME= "root";
    final String PASSWORD="";
    private Connection cnx;

    private Mydatabase() throws SQLException {
        try {
            this.cnx= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("connected...");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());;
        }

    }

public static Mydatabase getInstance(){
        if (instance== null) {
            try {
                instance= new Mydatabase();
            } catch (SQLException e) {


            }
        }


    return instance;
}

    public Connection getCnx() {
        return cnx;
    }
}
