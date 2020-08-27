import com.google.gson.JsonArray;
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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "paymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private java.sql.Date strToDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            return null;
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String cardNum = request.getParameter("cardNum");
        String expirDate = request.getParameter("expirDate");

        JsonObject responseJsonObject = new JsonObject();
        Connection dbcon = null;
        try {
            java.sql.Date date = strToDate(expirDate);
            // If date info is not correctly formatted
            if(date == null){
                HelperFunc.printToConsole("fail");
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Payment information is invalid.");
            }
            // If date info is correctly formatted -> check database
            else {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/localmoviedb");
                dbcon = ds.getConnection();
                String query = "SELECT * from creditcards as c " +
                                " where id = '" + cardNum + "'" +
                                " and firstName = '" + firstName + "'" +
                                " and lastName = '" + lastName + "'" +
                                " and expiration = '" + date + "'";
                PreparedStatement statement = dbcon.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                if (!rs.next()) {
                    HelperFunc.printToConsole("fail");
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Payment information is invalid.");
                } else {
                    HelperFunc.printToConsole("success");
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                    // If success, write this purchase record to the database
                    HttpSession session = request.getSession();
                    String customerId = (String) session.getAttribute("user");
                    Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
                    for (Map.Entry<String, float[]> entry : cartItems.entrySet()) {
                        String movieId = entry.getKey();
                        float[] itemInfo = entry.getValue();
                        Date day = new Date();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String d = df.format(day);
                        for(int i = 0; i < itemInfo[0]; i++) {
                            String query2 = "insert into sales (customerId, movieId, saleDate) values (" +
                                                customerId + ", '" + movieId + "', '" + d + "')";
                            PreparedStatement statement2 = dbcon.prepareStatement(query2);
                            int retID = statement2.executeUpdate();
                            HelperFunc.printToConsole("retId" + retID);
                            statement2.close();
                        }
                    }
                }
                dbcon.close();
                rs.close();
                statement.close();
            }
            HelperFunc.printToConsole("?");
            HelperFunc.printToConsole(responseJsonObject);
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        PrintWriter out = response.getWriter();
        out.write(responseJsonObject.toString());
        out.close();
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();
        HttpSession session = request.getSession();
        if (session != null) {
            Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
            float price = 0;
            if (cartItems != null) {
                for (Map.Entry<String, float[]> entry : cartItems.entrySet()) {
                    float[] itemInfo = entry.getValue();
                    price += itemInfo[0] * itemInfo[1];
                }
            }
            HelperFunc.printToConsole(price);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("price", price);
            jsonArray.add(jsonObject);
            out.write(jsonArray.toString());
            HelperFunc.printToConsole(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            out.close();
        }
    }
}
