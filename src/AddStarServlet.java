import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 */

// Declaring a WebServlet called MainServlet, which maps to url "/main"
@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/newStar")
public class AddStarServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");

        boolean isBirthYear = birthYear != null && !birthYear.equals("");

        try (Connection conn = dataSource.getConnection()){

            // obtain the maximum value id given by this query
            String idQuery = "SELECT MAX(id) as max_id from stars";
            PreparedStatement idStatement = conn.prepareStatement(idQuery);
            ResultSet max_id_rs = idStatement.executeQuery(idQuery);
            // ids start with nm
            String new_id = "nm";
            JsonObject jsonObject = new JsonObject();

            while(max_id_rs.next()){
                String id_string = max_id_rs.getString("max_id");
                // remove nm from old id
                id_string = id_string.substring(2);
                int id_num = Integer.parseInt(id_string);
                id_num += 1; // Increment to next id for new id #
                new_id += Integer.toString(id_num);
                jsonObject.addProperty("starId", new_id);
            }

            String newstarQuery = "INSERT INTO stars (id, name";
            if (isBirthYear) {
                newstarQuery += ", birthYear";
            }
            newstarQuery += ") VALUES (?, ?";
            if (isBirthYear) {
                newstarQuery += ", ?";
            }
            newstarQuery += ")";

            PreparedStatement statement = conn.prepareStatement(newstarQuery);
            statement.setString(1, new_id);
            statement.setString(2, starName);
            if (isBirthYear) {
                int birthYearInt = Integer.parseInt(birthYear);
                statement.setInt(3, birthYearInt);
            }

            statement.executeUpdate();

            out.write(jsonObject.toString());

            statement.close();
            out.close();

            response.setStatus(200);

        } catch (Exception e) {
            request.getServletContext().log("Error: ", e);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}

