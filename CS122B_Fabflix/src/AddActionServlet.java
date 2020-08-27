import com.google.gson.JsonObject;
import funcScripts.HelperFunc;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "AddactionServlet", urlPatterns = "/api/addaction")
public class AddActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/localmoviedb");
            Connection dbcon = ds.getConnection();
            PrintWriter out = response.getWriter();
            if (action != null) {
                if (action.equals("addstar")) {
                    JsonObject addstarresult = HelperFunc.addStarButton(dbcon, request);
                    out.write(addstarresult.toString());
                }
                if (action.equals("addmovie")) {
                    JsonObject addmovieresult = HelperFunc.addMovieButton(dbcon, request);
                    out.write(addmovieresult.toString());
                }

                out.close();
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
}
