import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class deleteRows {
    public static void deleteMovies(){
        int ogID = 499469;
        int ogG = 23;
        // Incorporate mySQL driver
        try {
            Connection connection;
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","mytestuser", "My6$Password");


            // check if this movie exists in the table
            String query = "select * from movies \n" +
                    "where movies.id > ? \n";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setObject(1, "tt0" + Integer.toString(ogID));

            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String id = rs.getString("id");
                query = "delete from movies where id like '" + id + "';";
                PreparedStatement statement1 = connection.prepareStatement(query);
                int rows = statement1.executeUpdate();
//                System.out.println("ROWS UDPATED: " + rows);

            }

            statement.close();

            // check if this movie exists in the table
            query = "select * from genres \n" +
                    "where genres.id > ? \n";

            statement = connection.prepareStatement(query);
            statement.setObject(1, ogG);

            rs = statement.executeQuery();
            while(rs.next()){
                String id = rs.getString("id");
                query = "delete from genres where id like " + id + ";";
                PreparedStatement statement1 = connection.prepareStatement(query);
                int rows = statement1.executeUpdate();
//                System.out.println("ROWS UDPATED: " + rows);
            }

            statement.close();

            // check if this movie exists in the table
            query = "select * from ratings \n" +
                    "where ratings.movieId > ? \n";

            statement = connection.prepareStatement(query);
            statement.setObject(1, "tt0" + Integer.toString(ogID));

            rs = statement.executeQuery();

            while(rs.next()){
                String id = rs.getString("movieId");
                query = "delete from ratings where movieId like " + id + ";";
                PreparedStatement statement1 = connection.prepareStatement(query);
                int rows = statement1.executeUpdate();
//                System.out.println("ROWS UDPATED: " + rows);
            }

            statement.close();

        } catch (Exception e) {

        }
    }
    public static void deleteStars(){
        String ogID = "nm9423080";
        // Incorporate mySQL driver
        try {
            Connection connection;
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","mytestuser", "My6$Password");


            // check if this movie exists in the table
            String query = "select * from stars \n" +
                    "where stars.id > ? \n";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setObject(1, ogID);

            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String id = rs.getString("id");
                query = "delete from stars where id like '" + id + "';";
                PreparedStatement statement1 = connection.prepareStatement(query);
                int rows = statement1.executeUpdate();
//                System.out.println("ROWS UDPATED: " + rows);

            }

            statement.close();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args){
        deleteMovies();
        deleteStars();
    }
}
