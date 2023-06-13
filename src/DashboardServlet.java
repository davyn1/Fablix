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
import java.sql.SQLException;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "DashboardServlet", urlPatterns = "/_dashboard/api/dashboard")
public class DashboardServlet extends HttpServlet {
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

//         Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }

        String email = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection conn = dataSource.getConnection()) {

            String query = "Select * FROM employees";

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query);

            JsonObject responseJsonObject = new JsonObject();
            boolean success = false;
            rs.next();
            String em = rs.getString("email");
            String pass =rs.getString("password");

//            boolean success = false;
            // rs.next() to move into the row of values, check for the email in password of that row
            if (email.equals(em)) {
                success = new StrongPasswordEncryptor().checkPassword(password, pass);

                if (success) {
                    // set this user into the session
                    request.getSession().setAttribute("user", new Employee(email));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "Wrong Username/Password");
                }
                // Login success w/ encrypted password:
//                String enP = rs.getString("password");
//                success = new StrongPasswordEncryptor().checkPassword(password, enP);
//
//                if (success) {
//                    // set this user into the session
//                    request.getSession().setAttribute("user", new User(email));
//
//                    responseJsonObject.addProperty("status", "success");
//                    responseJsonObject.addProperty("message", "success");
//                } else {
//                    // Login fail
//                    responseJsonObject.addProperty("status", "fail");
//                    // Log to localhost log
//                    request.getServletContext().log("Login failed");
//                    responseJsonObject.addProperty("message", "Wrong Username/Password");
//                }

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "Wrong Username/Password");
            }
            rs.close();
            statement.close();

            response.getWriter().write(responseJsonObject.toString());

        } catch (SQLException e) {
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
    }
}