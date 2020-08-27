import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;

import funcScripts.HelperFunc;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String fromAndroid = request.getParameter("android");

        JsonObject responseJsonObject = new JsonObject();
        Connection dbcon = null;

        if( fromAndroid == null ) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            HelperFunc.printToConsole("gRecaptchaResponse=" + gRecaptchaResponse);

            // Verify reCAPTCHA
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                // gRecaptcha error message: e.getMessage()
                responseJsonObject.addProperty("message", "Error: Please indicate you're a person");
                response.getWriter().write(responseJsonObject.toString());
                return;
            }
        }
        // If reCAPTCHA is verified
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        // int sameuser = 0;
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/localmoviedb");
            dbcon = ds.getConnection();
            String query = "SELECT password, id from customers where email = '"+email+"'";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            // Process database output
            if(!rs.next()){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            }
            else{
                if(verifyCredentials(email,password)){
                    // Set session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", rs.getString("id"));
                    session.setAttribute("accessBoolean", true);
                    // Add property to the result jsonObject
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect password");
                }
            }
            dbcon.close();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }


    private static boolean verifyCredentials(String email, String password) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String query = String.format("SELECT * from customers where email='%s'", email);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        boolean success = false;
        if (rs.next()) {
            // get the encrypted password from the database
            String encryptedPassword = rs.getString("password");

            // use the same encryptor to compare the user input password with encrypted password stored in DB
            success = new StrongPasswordEncryptor().    checkPassword(password, encryptedPassword);
        }

        rs.close();
        statement.close();
        connection.close();

        HelperFunc.printToConsole("verify " + email + " - " + password);

        return success;
    }

}

