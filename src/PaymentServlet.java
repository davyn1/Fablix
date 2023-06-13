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
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 */

// Declaring a WebServlet called PaymentServlet, which maps to url "/payment"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

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
        String cardNumber = request.getParameter("cardNumber");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String date = request.getParameter("date");
        String movies = request.getParameter("movies");


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            JsonObject responseJsonObject = new JsonObject();
            if (cardNumber != null && firstName != null && lastName != null && date != null) {
                String query = "Select * FROM creditcards " +
                        "WHERE id = ? AND firstName = ? " +
                        "AND lastName = ? AND expiration = ?;";

                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by the "?" in the query to a value
                // index indicates which question mark in the query, order is which comes first
                statement.setString(1, cardNumber);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, date);

                ResultSet rs = statement.executeQuery();


                // rs.next() to move into the row of values, check for the
                if (!rs.isBeforeFirst()) {
                    // Payment failed
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Credit card information not found");

                    responseJsonObject.addProperty("message", "Credit Card Not Found");

                } else {
                    //Payment Information Matches
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    insertSales(cardNumber, movies);

                }
                rs.close();
                statement.close();
            } else { // Payment failed
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Credit card information not found");

                responseJsonObject.addProperty("message", "Missing Information");
            }

            response.getWriter().write(responseJsonObject.toString());

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

    public void insertSales(String ccid, String movieTitle) {
        String[] movies = movieTitle.split(",");
//        String date = getDate();
        int custId = getCustID(ccid);
        try (Connection conn = dataSource.getConnection()) {
            for (String m : movies) {
                int saleId = lastId();
                String movieId = getMovieId(m);
                String query = "insert into sales (id, customerId, movieId, saleDate)\n" +
                        "values (" + saleId + ", " + custId + ", '" + movieId + "', curdate());";

                PreparedStatement statement = conn.prepareStatement(query);

                int rowsAffected = statement.executeUpdate();
                System.out.println(rowsAffected + " rows affected\n");
            }

        } catch (Exception e) {

        }
        ;


    }

    public int lastId() {
        String id = "0";
        try (Connection conn = dataSource.getConnection()) {
            String query = "select * from sales \n" +
                    "order by id desc \n" +
                    "limit 1 ;";

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();
            rs.next();
            id = rs.getString("id");
        } catch (Exception e) {

        }

        return Integer.parseInt(id) + 1;
    }

    public int getCustID(String ccid) {
        String cust = "0";
        try (Connection conn = dataSource.getConnection()) {
            String query = "select * from customers\n" +
                    "where ccId = '" + ccid + "';";

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();
            rs.next();
            cust = rs.getString("id");
        } catch (Exception e) {

        }
        ;
        return Integer.parseInt(cust);
    }

    public String getMovieId(String title) {
        String id = "0";
        try (Connection conn = dataSource.getConnection()) {
            String query = "select * from movies\n" +
                    "where title = '" + title + "';";

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();
            rs.next();
            id = rs.getString("id");
        } catch (Exception e) {

        }
        ;
        return id;
    }
}