import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import funcScripts.HelperFunc;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class dashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonArray result = new JsonArray();
        try {
            HelperFunc.printToConsole("dashboard code is here");

            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/localmoviedb");
            Connection dbcon = ds.getConnection();
            // Declare our statement
            String query = "show tables where Tables_in_moviedb != 'customer_save'";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String tablename = rs.getString("Tables_in_moviedb");
                PreparedStatement statement2 = dbcon.prepareStatement("describe " + tablename);
                ResultSet rs2 = statement2.executeQuery();
                while(rs2.next()){
                    JsonObject tableObject = new JsonObject();
                    tableObject.addProperty("tablename",tablename);
                    tableObject.addProperty("fieldname", rs2.getString("Field"));
                    tableObject.addProperty("type", rs2.getString("Type"));
                    result.add(tableObject);
                }
                statement2.close();
                rs2.close();
            }
            out.write( result.toString() );
            // set response status to 200 (OK)
            response.setStatus(200);
            dbcon.close();
            statement.close();
            rs.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        HelperFunc.printToConsole("MovieListServlet?");
        out.close();
    }
}
