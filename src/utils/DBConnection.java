package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * manages connection to database
 */
public class DBConnection {
    //JDBC string
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ip = "//wgudb.ucertify.com/";
    private static final String name = "WJ07VlG";
    private static final String jdbcURL = protocol + vendorName + ip + name;

    private static final String mysqljdbcdriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn;

    private static final String username = "U07VlG";
    private static final String password =  "53689141334";

    /**
     * creates new singleton Connection to database with saved parameters
     * @return Connection
     */
    public static Connection startConnection(){
        try{
            Class.forName(mysqljdbcdriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("successful connection");
        } catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error opening: " + e.getMessage());
        }

        return conn;
    }

    /**
     * closes saved singleton Connection
     */
    public static void closeConnection(){
        try {
            conn.close();
            System.out.println("connection closed");
        } catch (SQLException e) {
            System.out.println("Error closing: " + e.getMessage());
        }
    }
}
