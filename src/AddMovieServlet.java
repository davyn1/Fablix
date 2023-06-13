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
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 */

// Declaring a WebServlet called AddMovieServlet, which maps to url "/newMovie"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/newMovie")
public class AddMovieServlet extends HttpServlet {

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

        try (Connection conn = dataSource.getConnection()) {

            String movieTitle = request.getParameter("movieTitle");
            String movieYear = request.getParameter("movieYear");
            String movieDirector = request.getParameter("movieDirector");
            String starName = request.getParameter("starName");
            String birthYear = request.getParameter("birthYear");
            String movieGenre = request.getParameter("movieGenre");

            if (movieTitle.equals("")) {
                movieTitle = null;
            }
            if (movieYear.equals("")) {
                movieYear = null;
            }
            if (movieDirector.equals("")) {
                movieDirector = null;
            }
            if (starName.equals("")) {
                starName = null;
            }
            if (birthYear.equals("")) {
                birthYear = null;
            }
            if (movieGenre.equals("")) {
                movieGenre = null;
            }

            // Add in order movieId,movieName,movieYear,movieDirector,starId,starName,starBirthYear,genreId,genreName
            String query = "CALL add_movie(?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(query);

            // Retrieve movieId
            String getMovieId = "SELECT MAX(id) AS max_movie_id FROM movies";
            PreparedStatement maxMovieId = conn.prepareStatement(getMovieId);
            ResultSet maxMovieIdData = maxMovieId.executeQuery(getMovieId);
            while(maxMovieIdData.next()) {
                String max_id = maxMovieIdData.getString("max_movie_id");
                statement.setString(1, max_id);
            }

            // Retrieve starId
            String getStarId = "SELECT MAX(id) AS max_star_id FROM stars";
            PreparedStatement maxStarId = conn.prepareStatement(getStarId);
            ResultSet maxStarIdData = maxStarId.executeQuery(getStarId);
            while(maxStarIdData.next()) {
                String max_id = maxStarIdData.getString("max_star_id");
                statement.setString(5, max_id);
            }

            // Retrieve genreId
            String getGenreId = "SELECT MAX(id) AS max_genre_id FROM genres";
            PreparedStatement maxGenreId = conn.prepareStatement(getGenreId);
            ResultSet maxGenreIdData = maxGenreId.executeQuery(getGenreId);
            while(maxGenreIdData.next()) {
                String max_id = maxGenreIdData.getString("max_genre_id");
                statement.setString(8, max_id);
            }

            // Close everything
            maxGenreId.close();
            maxGenreIdData.close();
            maxStarId.close();
            maxStarIdData.close();
            maxMovieId.close();
            maxMovieIdData.close();

            // Rest of parameters
            statement.setString(2, movieTitle);
            statement.setString(3, movieYear);
            statement.setString(4, movieDirector);
            statement.setString(6, starName);
            statement.setString(7, birthYear);
            statement.setString(9, movieGenre);

            // Run and return results from query for success/failure message
            ResultSet messageData = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();

            while (messageData.next()) {
                String report = messageData.getString("report");

                if (report.equals("1")) {
                    String star_id = messageData.getString("s_id");
                    String genre_id = messageData.getString("g_id");
                    String movie_id = messageData.getString("m_id");
                    jsonObject.addProperty("status", "success");
                    jsonObject.addProperty("star_id", star_id);
                    jsonObject.addProperty("genre_id", genre_id);
                    jsonObject.addProperty("movie_id", movie_id);

                } else if (report.equals("-1")) {
                    jsonObject.addProperty("status", "error");
                }
            }

            messageData.close();
            statement.close();
            out.write(jsonObject.toString());
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

