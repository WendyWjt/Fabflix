package funcScripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataClass.SessionParamList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HelperFunc {
    static boolean debugMode = true;

    static public void printToConsole(Object tar){
        if(debugMode){
            System.out.println(tar);
        }
    }

    static public String getCurrentDate(boolean hasTime){
        java.util.Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if(hasTime){
            return df_time.format(day);
        }
        else{
            return df.format(day);
        }
    }

    public static FileWriter fw = null;
    public static int logLineCount = 1;
    public static HashMap<String, String> movieGenreMap = initializeGenreMap();


    static public HashMap<String, String> initializeGenreMap(){
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("susp", "Thriller");

        resultMap.put("cnr", "Cops And Robbers");
        resultMap.put("cnrb", "Cops And Robbers");
        resultMap.put("cnrbb", "Cops And Robbers");

        resultMap.put("dram", "Drama");
        resultMap.put("draam", "Drama");
        resultMap.put("ram", "Drama");
        resultMap.put("dramn", "Drama");
        resultMap.put("drama", "Drama");
        resultMap.put("dramd", "Drama");
        resultMap.put("dram>", "Drama");
        resultMap.put("romt dram", "Drama");
        resultMap.put("docu dram", "Drama");

        resultMap.put("west", "Western");
        resultMap.put("west1", "Western");

        resultMap.put("myst", "Mystery");
        resultMap.put("mystp", "Mystery");

        resultMap.put("s.f.", "Sci-Fi");
        resultMap.put("scfi", "Sci-Fi");
        resultMap.put("sxfi", "Sci-Fi");
        resultMap.put("scif", "Sci-Fi");

        resultMap.put("advt", "Adventure");
        resultMap.put("romtadvt", "Adventure");

        resultMap.put("natu", "Nature");

        resultMap.put("horr", "Horror");
        resultMap.put("hor", "Horror");
        resultMap.put("scat", "Horror");
        resultMap.put("sctn", "Horror");

        resultMap.put("adct", "Adult");
        resultMap.put("adctx", "Adult");
        resultMap.put("h", "Adult");
        resultMap.put("h**", "Adult");
        resultMap.put("h0", "Adult");
        resultMap.put("porn", "Adult");

        resultMap.put("anti-dram", "Anti-Drama");

        resultMap.put("avga", "Avant Garde");

        resultMap.put("romt", "Romance");
        resultMap.put("romtx", "Romance");
        resultMap.put("ront", "Romance");

        resultMap.put("musc", "Musical");
        resultMap.put("muscl", "Musical");
        resultMap.put("tage musical", "Musical");

        resultMap.put("muusc", "Music");

        resultMap.put("sports", "Sport");

        resultMap.put("docu", "Documentary");
        resultMap.put("ducu", "Documentary");

        resultMap.put("actn", "Action");
        resultMap.put("act", "Action");
        resultMap.put("axtn", "Action");
        resultMap.put("romt actn", "Action");
        resultMap.put("dram.actn", "Action");

        resultMap.put("faml", "Family");

        resultMap.put("fant", "Fantasy");
        resultMap.put("fantH*", "Fantasy");
        resultMap.put("romt fant", "Fantasy");

        resultMap.put("noir", "Black");
        resultMap.put("comd noir", "Black");
        resultMap.put("noir comd", "Black");
        resultMap.put("noir comd romt", "Black");

        resultMap.put("biop", "Biography");
        resultMap.put("biob", "Biography");
        resultMap.put("biog", "Biography");
        resultMap.put("biopp", "Biography");
        resultMap.put("biopx", "Biography");

        resultMap.put("tv", "Reality-TV");

        resultMap.put("comd", "Comedy");
        resultMap.put("comd west", "Comedy");
        resultMap.put("comdx", "Comedy");
        resultMap.put("cond", "Comedy");
        resultMap.put("romt comd", "Comedy");
        resultMap.put("romt. comd", "Comedy");

        resultMap.put("tv series", "tvs");

        resultMap.put("tvm", "TV Miniseries");
        resultMap.put("tvmini", "TV Miniseries");
        resultMap.put("txx", "TV Miniseries");

        resultMap.put("cart", "Animation");

        resultMap.put("hist", "History");

        resultMap.put("sati", "Satire");

        resultMap.put("disa", "Disaster");

        resultMap.put("psyc", "Psycho");

        resultMap.put("rfp; h*", "RFP");

        resultMap.put("ca", "Campy");
        resultMap.put("camp", "Campy");

        resultMap.put("cmr", "Christian-Muslim Relations");

        resultMap.put("surr", "Surreal");
        resultMap.put("surl", "Surreal");

        resultMap.put("crim", "Crime");

        resultMap.put("dist", "Disaster");

        resultMap.put("viol", "Violence");

        resultMap.put("ctxx", "Uncategorized");
        resultMap.put("ctcxx", "Uncategorized");
        resultMap.put("ctxxx", "Uncategorized");
        resultMap.put("undr", "Uncategorized");

        return resultMap;
    }


    static public void initializeLogFile(String fileName){
        File logDir = new File("./src/funcScripts/logs");

        if( !logDir.exists() ){
            logDir.mkdir();
        }

        String d = getCurrentDate(false);
        String logFilePath = "./src/funcScripts/logs/log_" + d + "_" + fileName + ".log";
        File logFile = new File(logFilePath);
        try{
            fw = new FileWriter(logFile);
            logLineCount = 1;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static public void xmlHandlerLog(Object tar){
        try{
            String d = getCurrentDate(true);
            fw.write(Integer.toString(logLineCount) + " " + d + " " + tar.toString() + "\n");
            logLineCount++;
            fw.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static public void closeLogFile(){
        try {
            fw.close();
            fw = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    static public String genreMap(String g){
        String result = movieGenreMap.get(g.toLowerCase());
        if(result != null) {
            return result;
        }
        else{
            return firstInit(g);
        }
    }


    static public String firstInit(String s){
        if(s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        else{
            return s.toUpperCase();
        }
    }


    static public void addToCartButton(PrintWriter out, String action, HttpServletRequest request) throws IOException {
        HelperFunc.printToConsole("Post: action not null");
        HelperFunc.printToConsole(action.equals("addToCart"));
        if(action.equals("addToCart")) {
            String movieId = request.getParameter("movieId");
            HttpSession session = request.getSession();
            Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
            if (cartItems == null) {
                Map<String, float[]> result = new HashMap<String, float[]>();
                float[] temp = new float[2];
                temp[0] = 1;
                temp[1] = 0;
                result.put(movieId, temp);
                session.setAttribute("cartItems", result);
            } else {
                float[] temp = new float[2];
                if (cartItems.get(movieId) == null) {
                    temp[0] = 1;
                    temp[1] = 0;
                    cartItems.put(movieId, temp);
                } else {
                    temp[0] = cartItems.get(movieId)[0] + 1;
                    temp[1] = cartItems.get(movieId)[1];
                    cartItems.put(movieId, temp);
                }
                session.setAttribute("cartItems", cartItems);
            }
            HelperFunc.printToConsole(movieId);
            HelperFunc.printToConsole(cartItems);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            out.write(jsonObject.toString());
        }
    }

    static public JsonObject addStarButton(Connection dbcon, HttpServletRequest request) throws IOException, SQLException {
        String starname = request.getParameter("starname");
        String birthyear = request.getParameter("birthyear");
        PreparedStatement statement = dbcon.prepareStatement("select max(id) as maxid from stars");
        ResultSet rs = statement.executeQuery();
        String maxid = "0";
        while(rs.next()){
            maxid = rs.getString("maxid");
        }
        rs.close();
        statement.close();
        int id = Integer.parseInt(maxid.substring(maxid.length()-7));
        String sql = "insert into stars values (?,?,?)";
        PreparedStatement preparedStatement = dbcon.prepareStatement(sql);
        preparedStatement.setString(1,"nm"+(id+1));
        preparedStatement.setString(2,starname);
        int len = birthyear.length();
        boolean ii = birthyear.equals("");
        if(birthyear.equals("")){
            preparedStatement.setNull(3,Types.INTEGER);
        }
        else{
            preparedStatement.setInt(3,Integer.parseInt(birthyear));
        }

        int value = preparedStatement.executeUpdate();
        JsonObject jsonObject = new JsonObject();
        if(value > 0){
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("id", "nm"+(id+1));

        }
        else{
            jsonObject.addProperty("status", "fail");
        }
        return jsonObject;
    }

    static public JsonObject addMovieButton(Connection dbcon, HttpServletRequest request) throws IOException, SQLException {
        String title = request.getParameter("title");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String starname = request.getParameter("starname");
        JsonObject jsonObject = new JsonObject();
        String sql = "call add_movie(?,?,?,?,?,?,?,?,?)";
        CallableStatement cstm = dbcon.prepareCall(sql);
        cstm.registerOutParameter(1, Types.INTEGER);
        cstm.registerOutParameter(2, Types.VARCHAR);
        cstm.registerOutParameter(3, Types.INTEGER);
        cstm.registerOutParameter(4, Types.VARCHAR);
        cstm.setString(5,title);
        cstm.setInt(6,Integer.parseInt(year));
        cstm.setString(7,director);
        cstm.setString(8,starname);
        cstm.setString(9,genre);
        cstm.execute();
        int result = cstm.getInt(1);
        if(result==1){
            String new_movie_id = cstm.getString(2);
            int new_genre_id = cstm.getInt(3);
            String new_star_id = cstm.getString(4);
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("new_movie_id",new_movie_id);
            jsonObject.addProperty("new_genre_id",new_genre_id);
            jsonObject.addProperty("new_star_id",new_star_id);
        }
        else{
            jsonObject.addProperty("status", "fail");
        }

        return jsonObject;
    }


    static public JsonObject sessionParamToJsonObject(SessionParamList paramList){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("search", paramList.search);
        jsonObject.addProperty("genreid", paramList.genre);
        jsonObject.addProperty("startwith", paramList.startwith);
        jsonObject.addProperty("firstSort", paramList.firstSort);
        jsonObject.addProperty("firstSortOrder", paramList.firstSortOrder);
        jsonObject.addProperty("secondSort", paramList.secondSort);
        jsonObject.addProperty("secondSortOrder", paramList.secondSortOrder);
        jsonObject.addProperty("offset", paramList.offset);
        jsonObject.addProperty("itemNum", paramList.itemNum);
        jsonObject.addProperty("starname", paramList.starname);
        jsonObject.addProperty("title", paramList.title);
        jsonObject.addProperty("year", paramList.year);
        jsonObject.addProperty("director", paramList.director);
        jsonObject.addProperty("fullTextSearchTitle", paramList.fullTextSearchTitle);
        return jsonObject;
    }


    static public JsonArray movieListTable(String query, Connection dbcon) throws SQLException {
        PreparedStatement statement = dbcon.prepareStatement(query);
        HelperFunc.printToConsole("movieListTable: " + query);
        ResultSet rs = statement.executeQuery();
        JsonArray jsonArray = new JsonArray();
        // Iterate through each row of rs
        while (rs.next()) {
            HelperFunc.printToConsole("movieListTable: hasnext");
            String movie_id = rs.getString("movieid");
            String movie_title = rs.getString("title");
            String movie_year = rs.getString("year");
            String movie_director = rs.getString("director");
            String movie_rating = rs.getString("rating");

            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_id", movie_id);
            jsonObject.addProperty("movie_title", movie_title);
            jsonObject.addProperty("movie_year", movie_year);
            jsonObject.addProperty("movie_director", movie_director);
            jsonObject.addProperty("movie_rating", movie_rating);
            PreparedStatement statement2 = dbcon.prepareStatement(
                    "select sim.starId, siom.name, count(sim.starId) from stars_in_movies as sim, (" +
                            "select starId, name from movies,stars,stars_in_movies where stars.id=stars_in_movies.starId " +
                            "and stars_in_movies.movieId=movies.id and movies.id='" + movie_id + "' " +
                            ")as siom " + // siom: stars_in_one_movie
                            "where sim.starId = siom.starId " +
                            "group by sim.starId " +
                            "order by count(sim.starId) desc, siom.name");
            ResultSet rs2 = statement2.executeQuery();
            JsonObject starsJsonObject = new JsonObject();
            int count = 1;
            while(rs2.next()){
                JsonObject singleStarJsonObject = new JsonObject();
                singleStarJsonObject.addProperty("id", rs2.getString("starId"));
                singleStarJsonObject.addProperty("name", rs2.getString("name"));
                starsJsonObject.add(Integer.toString(count), singleStarJsonObject);
                count += 1;
            }
            jsonObject.add("movie_stars", starsJsonObject);
            rs2.close();
            statement2.close();

            PreparedStatement statement3 = dbcon.prepareStatement("select * from movies,genres,genres_in_movies" +
                    " where genres.id=genres_in_movies.genreId and genres_in_movies.movieId=movies.id" +
                    " and movies.id='" + movie_id + "' order by genres.name");
            ResultSet rs3 = statement3.executeQuery();
            JsonObject genresJsonObject = new JsonObject();
            count = 1;
            while(rs3.next()){
                JsonObject oneGenresJsonObject = new JsonObject();
                oneGenresJsonObject.addProperty("name", rs3.getString("name"));
                oneGenresJsonObject.addProperty("genreId", rs3.getString("genreId"));
                genresJsonObject.add(Integer.toString(count), oneGenresJsonObject);
                count += 1;
            }
            jsonObject.add("movie_genres", genresJsonObject);
            rs3.close();
            statement3.close();
            HelperFunc.printToConsole("Add: " + movie_id);
            jsonArray.add(jsonObject);
        }
        rs.close();
        statement.close();
        return jsonArray;
    }

}
