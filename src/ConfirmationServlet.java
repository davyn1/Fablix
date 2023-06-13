import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    // Create a dataSource which is registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http POST
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String movieTitle = request.getParameter("movieTitle");

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            JsonObject responseJsonObject = new JsonObject();

            // Create the query to retrieve the sale ID for the given movie title
            String query = "SELECT sales.id FROM sales, movies WHERE movies.id = sales.movieId AND sales.saleDate = curdate() AND movies.title = ? order by sales.id desc";

            // Create a prepared statement with the query
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, movieTitle);

            // Execute the query and get the result set
            ResultSet resultSet = statement.executeQuery();

            // Check if the query returned any results
            if (resultSet.next()) {
                int saleId = resultSet.getInt("id");

                responseJsonObject.addProperty("saleId", saleId);

            } else {
                // If the query did not return any results, add an error message to the response JSON object
                responseJsonObject.addProperty("errorMessage", "No sale ID found for the given movie title.");
            }

            // Send the response JSON object
            out.write(responseJsonObject.toString());

        } catch (Exception e) {
            request.getServletContext().log("Error: ", e);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("paymentErrorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}