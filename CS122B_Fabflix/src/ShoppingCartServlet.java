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
import java.util.Map;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    private float getPriceFromId(String id){
        String temp = id.substring(id.length()-2);
        return Float.parseFloat(temp) / 10;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HelperFunc.printToConsole("action: ");
        HelperFunc.printToConsole(action);
        if(action != null) {
            HelperFunc.printToConsole("Post: action not null");
            HelperFunc.printToConsole(action);
            String movieId = request.getParameter("movieId");
            HttpSession session = request.getSession();
            Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
            float[] temp;
            if(action.equals("addItem")){
                temp = cartItems.get(movieId);
                temp[0]++;
                cartItems.put(movieId, temp);
            }
            else if (action.equals("deleteItem")){
                cartItems.remove(movieId);
            }
            else if (action.equals("decreaseItem")){
                temp = cartItems.get(movieId);
                float new_num = temp[0] - 1;
                if (new_num <= 0){
                    cartItems.remove(movieId);
                }
                else{
                    temp[0]--;
                    cartItems.put(movieId, temp);
                }
            }
            HelperFunc.printToConsole(movieId);
            HelperFunc.printToConsole(cartItems);
            session.setAttribute("cartItems", cartItems);
            doGet(request, response);
        }
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession();
        Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");

        if(cartItems != null) {
            // Output stream to STDOUT
            PrintWriter out = response.getWriter();

            try {
                // Get a connection from dataSource
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/localmoviedb");
                Connection dbcon = ds.getConnection();

                JsonArray jsonArray = new JsonArray();
                for (Map.Entry<String, float[]> entry : cartItems.entrySet()) {
                    // Declare our statement
                    String movieId = entry.getKey();
                    float[] itemInfo = entry.getValue();
                    String query = "SELECT m.title from movies as m where id = '" + movieId + "'";
                    PreparedStatement statement = dbcon.prepareStatement(query);

                    // Perform the query
                    ResultSet rs = statement.executeQuery();
                    // Iterate through each row of rs
                    while (rs.next()) {
                        String movie_title = rs.getString("title");

                        // Create a JsonObject based on the data we retrieve from rs
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id", movieId);
                        jsonObject.addProperty("title", movie_title);
                        jsonObject.addProperty("num", itemInfo[0]);
                        jsonObject.addProperty("price", getPriceFromId(movieId));
                        jsonArray.add(jsonObject);
                        itemInfo[1] = getPriceFromId(movieId);
                        cartItems.put(movieId, itemInfo);
                        session.setAttribute("cartItems", cartItems);
                    }
                    rs.close();
                    statement.close();
                }
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);
                dbcon.close();
                HelperFunc.printToConsole("SingleMovieServletReturn");
                out.close();

            } catch (Exception e) {
                // write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());
                // set response status to 500 (Internal Server Error)
                response.setStatus(500);
            }
        }
    }
}
