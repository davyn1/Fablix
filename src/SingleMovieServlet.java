import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
//            String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
//                    "where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

            String query = "SELECT ratings.rating, updatedTable.year, updatedTable.title, updatedTable.director, updatedTable.genres, updatedTable1.stars\n" +
                    "from ratings, (SELECT movies.id, movies.year, movies.director, movies.title, GROUP_CONCAT(genres.name SEPARATOR ', ') AS genres\n" +
                    "FROM movies\n" +
                    "JOIN genres_in_movies ON movies.id = genres_in_movies.movieId\n" +
                    "JOIN genres ON genres_in_movies.genreId = genres.id\n" +
                    "GROUP BY movies.id) updatedTable, (SELECT movies.id, GROUP_CONCAT(stars.name SEPARATOR ', ') AS stars\n" +
                    "FROM movies\n" +
                    "JOIN stars_in_movies ON movies.id = stars_in_movies.movieId\n" +
                    "JOIN stars ON stars_in_movies.starId = stars.id\n" +
                    "GROUP BY movies.id) updatedTable1\n" +
                    "WHERE ratings.movieId = updatedTable.id and updatedTable.id = updatedTable1.id and updatedTable.title = ?";
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

//                String starName = rs.getString("name");
//                String starDob = rs.getString("birthYear");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieGenre = rs.getString("genres");
                String movieStars = rs.getString("stars");
                String movieRating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();

//                jsonObject.addProperty("star_name", starName);
//                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movieTitle", movieTitle);
                jsonObject.addProperty("movieYear", movieYear);
                jsonObject.addProperty("movieDirector", movieDirector);
                jsonObject.addProperty("movieGenre", movieGenre);
                jsonObject.addProperty("movieStars", movieStars);
                jsonObject.addProperty("movieRating", movieRating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
