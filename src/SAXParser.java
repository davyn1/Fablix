import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SAXParser {
    public static void main(String[] args) throws SQLException, IOException {
        Connection conn = null;
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","mytestuser", "My6$Password");
        }catch (Exception e){

        }
        // movies
        System.out.println("main243.xml: \n");
        SAXParserMovies spm = new SAXParserMovies(conn);
        spm.runExample();

        // stars
        System.out.println("\nactors63.xml: \n");
        SAXParserStars sps = new SAXParserStars(conn);
        sps.runExample();

        // overall
        System.out.println("\ncasts124.xml: \n");
        SAXParserSIM spe = new SAXParserSIM(conn);
        spe.runExample();
        
        conn.close();
    }
}
