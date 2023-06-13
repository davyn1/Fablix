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
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Retrieve parameter id from url request.
            String title = request.getParameter("title");
            String[] titles = title.split(" ", 0);
            title = "";
            int len = titles.length;
            for(int i = 0; i < len; i++){
                if(i != len - 1){
                    title += titles[i] + "% ";
                } else{
                    title += titles[i];
                }
            }

            String query = "SELECT distinct ratings.rating, updatedTable.year, updatedTable.title, updatedTable.director, updatedTable.genres, updatedTable1.stars\n" +
                    "FROM ratings, (SELECT movies.id, movies.year, movies.director, movies.title, SUBSTRING_INDEX(GROUP_CONCAT(genres.name SEPARATOR ', '), ',', 3 ) AS genres\n" +
                    "FROM movies\n" +
                    "JOIN genres_in_movies ON movies.id = genres_in_movies.movieId\n" +
                    "JOIN genres ON genres_in_movies.genreId = genres.id\n" +
                    "GROUP BY movies.id) updatedTable, (SELECT movies.id, SUBSTRING_INDEX(GROUP_CONCAT(stars.name SEPARATOR ', '), ',', 3 ) AS stars\n" +
                    "FROM movies\n" +
                    "JOIN stars_in_movies ON movies.id = stars_in_movies.movieId\n" +
                    "JOIN stars ON stars_in_movies.starId = stars.id\n" +
                    "GROUP BY movies.id) updatedTable1, stars, stars_in_movies as sm\n" +
                    "WHERE ratings.movieId = updatedTable.id AND updatedTable.id = updatedTable1.id and sm.movieId = ratings.movieId and sm.starId = stars.id and updatedTable.title like ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "%"+title+"%");

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieTitle = rs.getString("title");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieTitle", movieTitle);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}