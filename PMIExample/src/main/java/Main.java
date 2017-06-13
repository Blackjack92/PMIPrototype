
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Created by kevin on 12.05.2017.
 */

public class Main {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/";
        String settings = "autoReconnect=true&useSSL=false";
        String username = "root";
        String password = "password";

        System.out.println("Connecting database...");

        String database = "AC";
        try (Connection connection = DriverManager.getConnection(url + "?" + settings, username, password)) {
            System.out.println("Database connected!");

            // Create database if necessary
            Statement s = connection.createStatement();
            int result = s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database + ";");
            System.out.println("Database creation: " + result);

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);

        }
    }

}

