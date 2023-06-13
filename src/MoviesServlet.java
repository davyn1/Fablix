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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    long startSearchTime = 0;
    long startQueryTime = 0;
    long totalSearchTime = 0;
    long totalQueryTime = 0;

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

        startSearchTime = System.nanoTime();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Retrieve parameter id from url request.
            String sort = request.getParameter("sort");
            String genre = request.getParameter("genre");
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String yearStr = request.getParameter("year");
            String name = request.getParameter("name");
            String letter = request.getParameter("letter");
//            String limit = request.getParameter("limit");
//            String currentPage = request.getParameter("curr");
            int year = -1;
            if (yearStr != null && !yearStr.isEmpty()) {
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    // Handle the error if the year parameter is not a valid integer
                }
            }
            // The log message can be found in localhost log
            request.getServletContext().log("title: " + sort);


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
                    "WHERE ratings.movieId = updatedTable.id AND updatedTable.id = updatedTable1.id and sm.movieId = ratings.movieId and sm.starId = stars.id";

            // Create StringBuilder
            StringBuilder newQuery = new StringBuilder(query);
            int qMarks = 0;
            int len = 0;
            if(genre != null && !genre.equalsIgnoreCase("null")){
                newQuery.append(" AND updatedTable.genres like ?");
                qMarks += 1;
            }
            if (title != null && !title.isEmpty() && !title.equalsIgnoreCase("null")) {
                newQuery.append(" AND updatedTable.title LIKE ?");
                String[] titles = title.split(" ", 0);
                title = "";
                len = titles.length;
                for(int i = 0; i < len; i++){
                    if(i != len - 1){
                        title += titles[i] + "% ";
                    } else{
                        title += titles[i];
                    }
                }
                qMarks += 1;
            }
            if (year >= 0) {
                newQuery.append(" AND updatedTable.year = ?");
                qMarks += 1;
            }
            if (director != null && !director.isEmpty() && !director.equalsIgnoreCase("null")) {
                newQuery.append(" AND updatedTable.director LIKE ?");
                qMarks += 1;
            }
            if (name != null && !name.isEmpty() && !name.equalsIgnoreCase("null")) {
                newQuery.append(" AND stars.name LIKE ?");
                qMarks += 1;
            }
            if (letter != null && !letter.isEmpty() && !letter.equalsIgnoreCase("null") && !letter.equals("*")) {
                newQuery.append(" AND updatedTable.title LIKE ?");
                qMarks += 1;
            } else if (letter.equals("*")) {
                newQuery.append(" AND updatedTable.title REGEXP '^[^0-9A-Za-z]'");
            }
            newQuery.append("\norder by");

            if(sort != null && !sort.equalsIgnoreCase("null")) {
                if (sort.equalsIgnoreCase("1")) {
                    newQuery.append(" updatedTable.title asc, ratings.rating asc\n");
                } else if (sort.equalsIgnoreCase("2")) {
                    newQuery.append(" updatedTable.title asc, ratings.rating desc\n");
                } else if (sort.equalsIgnoreCase("3")) {
                    newQuery.append(" updatedTable.title desc, ratings.rating asc\n");
                } else if (sort.equalsIgnoreCase("4")) {
                    newQuery.append(" updatedTable.title desc, ratings.rating desc\n");
                } else if (sort.equalsIgnoreCase("5")) {
                    newQuery.append(" ratings.rating asc, updatedTable.title asc\n");
                } else if (sort.equalsIgnoreCase("6")) {
                    newQuery.append(" ratings.rating asc, updatedTable.title desc\n");
                } else if (sort.equalsIgnoreCase("7")) {
                    newQuery.append(" ratings.rating desc, updatedTable.title asc\n");
                } else {
                    newQuery.append(" ratings.rating desc, updatedTable.title desc\n");
                }
            } else{
                newQuery.append(" ratings.rating desc\n");
            }
//            query = query.concat("limit " + currentPage + ", ");
//            if(limit != null && !limit.equalsIgnoreCase("null")){
//                query = query.concat(limit);
//            }else{
//                query = query.concat("20");
//            }
            newQuery.append(";");
            // Perform the query
            PreparedStatement statement = conn.prepareStatement(newQuery.toString());
            List<Object> already = new ArrayList<>(qMarks);

            // fill in q marks
            for(int i = 0; i < qMarks; i++){
                int q = i + 1;
                if(genre != null && !genre.equalsIgnoreCase("null") && !already.contains(genre)){
                    statement.setString(q, '%' + genre + '%');
                    already.add(genre);
                } else if (title != null && !title.isEmpty() && !title.equalsIgnoreCase("null") && !already.contains(title)){
                    statement.setObject(q, "%" + title + "%");
                    already.add(title);
                } else if (year >= 0 && !already.contains(year)){
                    statement.setObject(q, year);
                    already.add(year);
                } else if (director != null && !director.isEmpty() && !director.equalsIgnoreCase("null")  && !already.contains(director)){
                    statement.setObject(q, "%" + director + "%");
                    already.add(director);
                } else if (name != null && !name.isEmpty() && !name.equalsIgnoreCase("null")  && !already.contains(name)){
                    statement.setObject(q, "%" + name + "%");
                    already.add(name);
                } else if (letter != null && !letter.isEmpty() && !letter.equalsIgnoreCase("null") && !letter.equals("*") && !already.contains(letter)){
                    statement.setObject(q, letter + "%");
                    already.add(letter);
                }
            }

            startQueryTime = System.nanoTime();
            ResultSet rs = statement.executeQuery();
            totalQueryTime = System.nanoTime() - startQueryTime;

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieTitle = rs.getString("title");
                String movieDirector = rs.getString("director");
                String movieYear = rs.getString("year");
                String movieRating = rs.getString("rating");
                String movieGenre = rs.getString("genres");
                String movieStars = rs.getString("stars");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieTitle", movieTitle);
                jsonObject.addProperty("movieDirector", movieDirector);
                jsonObject.addProperty("movieYear", movieYear);
                jsonObject.addProperty("movieRating", movieRating);
                jsonObject.addProperty("movieGenre", movieGenre);
                jsonObject.addProperty("movieStars", movieStars);

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
            totalSearchTime = System.nanoTime() - startSearchTime;
            System.out.println(totalSearchTime);

            String str = request.getServletContext().getRealPath("/");
            FileWriter fileWriter = new FileWriter(str + "log.txt", true);
            System.out.println(str);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf(totalSearchTime + " , " + totalQueryTime + "\n");
            printWriter.close();

            out.close();
        }
        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}