
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import funcScripts.HelperFunc;

// server endpoint URL
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public MovieSuggestion() {
        super();
    }


    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */

    private String getSql(String query){
        String sqlQuery = "select distinct id, title from movies where match(title) against (";
        String[] wordList = query.split(" ");
        if(wordList.length == 1){
            sqlQuery += "'+" + query + "*' in boolean mode)";
        }
        else{
            for(int i = 0; i < wordList.length; i++){
                sqlQuery += "'+" + wordList[i] + "*' ";
            }
            sqlQuery += " in boolean mode)";
        }
        // T3: fuzzy search
        Integer lenient = query.length() / 5;
        HelperFunc.printToConsole("lenient: " + lenient);
        sqlQuery += " or edrec('" + query + "', title, " + lenient + ") ";
        sqlQuery += " limit 10";
        return sqlQuery;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/localmoviedb");
            Connection dbcon = ds.getConnection();
            String sqlQuery = getSql(query);
            System.out.println(sqlQuery);
            PreparedStatement statement = dbcon.prepareStatement(sqlQuery);
            ResultSet rs = statement.executeQuery();
            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");

                // Create a JsonObject based on the data we retrieve from rs
                jsonArray.add(generateJsonObject(movieId, movieTitle));
            }

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "movieTitle",
     *   "data": { "movieId": "11" }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

}
