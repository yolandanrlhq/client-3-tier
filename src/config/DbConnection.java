package config; //

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 

public class DbConnection { //
    private static final String URL = "jdbc:mysql://localhost:3306/db_kostum"; 
    private static final String USER = "root";  
    private static final String PASS = "";     

    public static Connection getConnection() { 
        try {             
            // Driver untuk MySQL Connector/J versi 8.0+
            Class.forName("com.mysql.cj.jdbc.Driver"); 
                         
            return DriverManager.getConnection(URL, USER, PASS); 
        } catch (SQLException | ClassNotFoundException e) { 
            e.printStackTrace(); 
            return null; 
        } 
    } 
}