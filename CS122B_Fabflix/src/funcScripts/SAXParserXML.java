package funcScripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dataClass.*;
import org.xml.sax.SAXException;
import funcScripts.xmlParserHandler.MovieXMLParserHandler;

public class SAXParserXML {
    private List<MovieRecordClass> movieRecord;
    private List<StarsRecordClass> starsRecord;
    private List<GenresInMoviesRecordClass> genresInMoviesRecord;
    private HashMap<String, String> movieIdXMLSQLMap;
    private HashMap<String, Integer> genreSQLMap;
    private HashMap<String, String> movieTitleSQLMap;   // If movieId = null in XML file
    private HashMap<String, String> starNameSQLIdMap;

    // Create a dataSource which registered in web.xml
    private Connection dbcon = null;


    public SAXParserXML() {
        movieIdXMLSQLMap = new HashMap<>();
        genreSQLMap = new HashMap<>();
        movieTitleSQLMap = new HashMap<>();
        starNameSQLIdMap = new HashMap<>();
    }


    public void run() throws SQLException, IOException {
        initializeDatabaseConnection();
        parseDocumentAndWriteToDatabase();
        closeDatabaseConnection();
    }


    private void parseDocumentAndWriteToDatabase() throws IOException {
        try {
            // Movie XML
            //get a factory
            SAXParserFactory spfMovie = SAXParserFactory.newInstance();
            //get a new instance of parser
            SAXParser spMovie = spfMovie.newSAXParser();
            MovieXMLParserHandler movieHandler = new MovieXMLParserHandler();
            //parse the file and also register this class for call backs
            spMovie.parse("./data/mains243.xml", movieHandler);
            movieRecord = movieHandler.getMovieRecordList();
            genresInMoviesRecord = movieHandler.getGenresInMoviesRecord();
            writeMovieRecord2();
            writeGenresInMovieRecord();

            movieRecord.clear();
            genresInMoviesRecord.clear();


            // Star XML
            //get a factory
            SAXParserFactory spfStar = SAXParserFactory.newInstance();
            //get a new instance of parser
            SAXParser spStar = spfStar.newSAXParser();
            funcScripts.xmlParserHandler.StarXMLParserHandler starHandler = new funcScripts.xmlParserHandler.StarXMLParserHandler();
            //parse the file and also register this class for call backs
            spStar.parse("./data/actors63.xml", starHandler);
            starsRecord = starHandler.getStarsRecordList();
            writeStarsRecord();
            starsRecord.clear();

            // Stars In Movie XML
            // get a factory
            SAXParserFactory spfStarInMovie = SAXParserFactory.newInstance();
            //get a new instance of parser
            SAXParser spStarInMovie = spfStarInMovie.newSAXParser();
            funcScripts.xmlParserHandler.StarsInMoviesParserHandler starsInMovieHandler  = new funcScripts.xmlParserHandler.StarsInMoviesParserHandler(starNameSQLIdMap, movieIdXMLSQLMap, movieTitleSQLMap);
            //parse the file and also register this class for call backs
            spStarInMovie.parse("./data/casts124.xml", starsInMovieHandler);
            String inputQuery = "load data local infile 'StarsInMoviesRecordData.txt'\n" +
                    "into table stars_in_movies\n" +
                    "fields terminated by '|' optionally enclosed by '\"' escaped by '\"'\n" +
                    "lines terminated by '[]'" + // '\\r\\n'\n" +
                    "(starId,movieId)";


            FileWriter fwSQL = new FileWriter("./src/funcScripts/logs/insertStarsRecord.sql");
            fwSQL.write(inputQuery + ";\n\n");
            fwSQL.close();


        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e){
            /*
            FileWriter fwSQL = new FileWriter("./src/funcScripts/logs/errorLogs.sql");
            fwSQL.write(e.toString() + ";\n\n");
            fwSQL.close();

             */
            e.printStackTrace();
        }

    }



    private void initializeDatabaseConnection() throws SQLException{
        dbcon = DriverManager.getConnection("jdbc:mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                "mytestuser", "mypassword");
    }


    private void closeDatabaseConnection() throws SQLException {
        dbcon.close();
    }


    private String getId(String prefix, int digit, int curId){
        String r = Integer.toString(curId);
        if(r.length() < digit){
            for(int i = r.length(); i < digit; i++){
                r = "0" + r;
            }
        }
        return prefix + r;
    }


    private String getMovieSQLMapKey(String title, String year, String director){
        return title + "|" + year + "|" + director;
    }


    private Map<String, String> getCurrentSQLMovieMap() throws SQLException {
        String getallQuery = "select * from movies";
        Map<String, String> moviemap = new HashMap<>(); // title|year|director : movieId
        Statement allmovieStatement = dbcon.createStatement();
        ResultSet allmovies = allmovieStatement.executeQuery(getallQuery);
        while(allmovies.next()){
            String key = getMovieSQLMapKey(allmovies.getString("title"),
                    allmovies.getString("year"),
                    allmovies.getString("director"));
            String id = allmovies.getString("id");
            moviemap.put(key, id);
        }
        allmovies.close();
        allmovieStatement.close();

        return moviemap;
    }


    private int getMaxId(String query) throws SQLException {
        PreparedStatement statement0 = dbcon.prepareStatement(query);
        ResultSet rs0 = statement0.executeQuery();
        int nextMovieId = -1;
        if(rs0.next()) {
            String id = rs0.getString("maxId");
            HelperFunc.printToConsole(id);
            if(id != null) {
                nextMovieId = Integer.parseInt(id.substring(2, id.length()));
                HelperFunc.printToConsole(nextMovieId);
            }
            else{
                nextMovieId = 0;
            }
        }
        else{
            nextMovieId = 0;
        }
        rs0.close();
        statement0.close();
        return nextMovieId;
    }



    private void writeMovieRecord2(){
        try {
            HelperFunc.initializeLogFile("MovieRecordWriter2");
            HelperFunc.xmlHandlerLog("Start writing MoviesRecord.");
            Map<String, String> movieMap = getCurrentSQLMovieMap();
            String movieIdQuery = "select max(id) as maxId from movies";
            int maxMovieId = getMaxId(movieIdQuery);
            int nextMovieId = maxMovieId + 1;
            // System.out.println(nextMovieId);

            // File f1 = new File("C://ProgramData//MySQL//MySQL Server 8.0//Uploads//MovieRecordData.txt");
            File f = new File("./src/funcScripts/logs/MovieRecordData.txt");
            File f2 = new File("./src/funcScripts/logs/RatingRecordData.txt");
            FileWriter fw = new FileWriter(f);
            FileWriter fw2 = new FileWriter(f2);
            // FileWriter fw2 = new FileWriter(f2);
            // String filepathMovieRecord = "C:///ProgramData///MySQL///MySQL Server 8.0///Uploads///MovieRecordData.txt";
            // HelperFunc.printToConsole(filepathMovieRecord);
            for(int i = 0; i < movieRecord.size(); i++) {
                // Check duplication
                MovieRecordClass currentRecord = movieRecord.get(i);
                String currentKey = getMovieSQLMapKey(currentRecord.title, Integer.toString(currentRecord.year), currentRecord.director);
                String line = "[]"; //System.getProperty("line.separator");
                StringBuffer str = new StringBuffer();
                StringBuffer str2 = new StringBuffer();
                // If duplicate
                if(movieMap.containsKey(currentKey)){
                    updateMovieMap(currentRecord.id, currentRecord.title, movieMap.get(currentKey));
                    HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Duplicate entries.");
                    continue;
                }
                // If no duplication, then write into a file
                else{
                    String sqlId = getId("tt", 7, nextMovieId);
                    movieMap.put(currentKey, sqlId);
                    updateMovieMap(currentRecord.id, currentRecord.title, sqlId);
                    str.append(sqlId + "|" + currentRecord.title + "|" + currentRecord.year + "|" + currentRecord.director).append(line);
                    str2.append(sqlId + "|0|0").append(line);
                    //str.append("tt"+nextMovieId + "," + newmovieElement.get(0) + "," + newmovieElement.get(1) + "," +
                    //            newmovieElement.get(2)
                    //          ).append(line);
                    fw.write(str.toString());
                    fw2.write(str2.toString());
                    // fw2.write(str.toString());
                    nextMovieId++;
                }
            }
            // After checking is down, put the data into sql database
            fw.close();
            fw2.close();
            movieMap = null; // release memory
            if(nextMovieId != maxMovieId + 1) {
                //here should write the whole file into the database
                String inputQuery = "load data local infile 'MovieRecordData.txt'\n" +
                        "into table movies\n" +
                        "fields terminated by '|' optionally enclosed by '\"' escaped by '\"'\n" +
                        "lines terminated by '[]'" + // '\\r\\n'\n" +
                        "(id,@title,@year,@director)\n" +
                        "set\n" +
                        "title = nullif(@title,\"\"),\n" +
                        "year = nullif(@year,\"\"),\n" +
                        "director = nullif(@director,\"\");";


                inputQuery += "\n\nload data local infile 'RatingRecordData.txt'\n" +
                        "into table ratings\n" +
                        "fields terminated by '|' optionally enclosed by '\"' escaped by '\"'\n" +
                        "lines terminated by '[]'" + // '\\r\\n'\n" +
                        "(movieId,rating,numVotes)";

                HelperFunc.printToConsole(inputQuery);

                FileWriter fwSQL = new FileWriter("./src/funcScripts/logs/insertMovieRecord.sql");
                fwSQL.write(inputQuery + ";\n\n");
                fwSQL.close();

                /*
                PreparedStatement statementInput = dbcon.prepareStatement(inputQuery);
                int rsInt = statementInput.executeUpdate();
                if (rsInt != 1) {
                    HelperFunc.xmlHandlerLog("Error: Fail to write to database.");
                }
                */
            }

            HelperFunc.xmlHandlerLog("Finish writing MoviesRecord.");
            HelperFunc.closeLogFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    private void writeMovieRecord(){
        try {
            HelperFunc.initializeLogFile("MovieRecordWriter");
            HelperFunc.xmlHandlerLog("Start writing MoviesRecord.");
            String insertQuery = "Insert into movies values(?, ?, ?, ?)";
            String movieIdQuery = "select max(id) as maxId from movies";
            String checkDuplicateQuery = "select * from movies where title = ? and year = ? and director = ?";
            String insertRatingQuery = "Insert into ratings values(?, 0, 0)";
            int nextMovieId = getMaxId(movieIdQuery) + 1;

            for(int i = 0; i < movieRecord.size(); i++) {
                // Check duplication
                MovieRecordClass currentRecord = movieRecord.get(i);
                PreparedStatement statement1 = dbcon.prepareStatement(checkDuplicateQuery);
                statement1.setString(1, currentRecord.title);
                statement1.setInt(2, currentRecord.year);
                statement1.setString(3, currentRecord.director);
                ResultSet rs1 = statement1.executeQuery();
                if(rs1.next()){
                    String sqlId = rs1.getString("id");
                    updateMovieMap(currentRecord.id, currentRecord.title, sqlId);
                    HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Duplicate entries.");
                }
                // If no duplication, then insert
                else{
                    PreparedStatement statement2 = dbcon.prepareStatement(insertQuery);
                    String sqlId = getId("tt", 7, nextMovieId);
                    HelperFunc.printToConsole(currentRecord.id);
                    HelperFunc.printToConsole(currentRecord.id == null);
                    updateMovieMap(currentRecord.id, currentRecord.title, sqlId);
                    statement2.setString(1, sqlId);
                    statement2.setString(2, currentRecord.title);
                    statement2.setInt(3, currentRecord.year);
                    statement2.setString(4, currentRecord.director);
                    PreparedStatement statementInsertRating = dbcon.prepareStatement(insertRatingQuery);
                    statementInsertRating.setString(1, sqlId);
                    int retID = statement2.executeUpdate();
                    int retID2 = statementInsertRating.executeUpdate();
                    // HelperFunc.printToConsole(retID);
                    if(retID == 1 && retID2 == 1) {
                        nextMovieId++;
                    }
                    else{
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into table movies.");
                    }
                }
            }
            HelperFunc.xmlHandlerLog("Finish writing MoviesRecord.");
            HelperFunc.closeLogFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    private void updateMovieMap(String movieId, String movieTitle, String sqlId){
        if(movieId == null) {
            // HelperFunc.printToConsole("put to title map");
            if(movieTitle != null) {
                movieTitleSQLMap.put(movieTitle, sqlId);
            }
        }
        else {
            // HelperFunc.printToConsole("put to id map");
            movieIdXMLSQLMap.put(movieId, sqlId);
        }
    }


    private String getMovieSQLId(String movieId, String movieTitle){
        if(movieId == null){
            return movieTitleSQLMap.get(movieTitle);
        }
        else{
            return movieIdXMLSQLMap.get(movieId);
        }
    }


    private void writeGenresInMovieRecord(){
        try {
            HelperFunc.initializeLogFile("GenresInMovieRecordWriter");
            HelperFunc.xmlHandlerLog("Start writing GenresInMoviesRecord.");

            // String insertGenreInMovieQuery = "Insert into genres_in_movies values(?, ?)";
            String insertGenreQuery = "Insert into genres (name) values (?)";
            String genreIdQuery = "select id from genres where name = ?";
            String selectAllGenreQuery = "select * from genres";
            String checkDuplicateQuery = "select * from genres_in_movies where genreId = ? and movieId = ?";

            File f = new File("./src/funcScripts/logs/GenreInMovieRecordData.txt");
            FileWriter fw = new FileWriter(f);
            StringBuffer str = new StringBuffer();
            String line = "[]"; //System.getProperty("line.separator");

            // Select all genres
            PreparedStatement statement = dbcon.prepareStatement(selectAllGenreQuery);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String genreName = rs.getString("name");
                int genreId = Integer.parseInt(rs.getString("id"));
                genreSQLMap.put(genreName, genreId);
            }
            statement.close();
            rs.close();


            // Insert genresInMoviesRecord
            for(int i = 0; i < genresInMoviesRecord.size(); i++) {
                GenresInMoviesRecordClass currentRecord = genresInMoviesRecord.get(i);
                String genreName = currentRecord.genreName;
                boolean genreInDatabase = false;
                // Check duplication
                if(genreSQLMap.containsKey(genreName)) {
                    genreInDatabase = true;
                    PreparedStatement statement1 = dbcon.prepareStatement(checkDuplicateQuery);
                    statement1.setInt(1, genreSQLMap.get(genreName));
                    statement1.setString(2, getMovieSQLId(currentRecord.movieId, currentRecord.movieName));
                    ResultSet rs1 = statement1.executeQuery();
                    if (rs1.next()) {
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Duplicate entries.");
                        continue;
                    }
                }

                // If no duplication, then insert
                // If the genre is not in database, then insert the genre first
                if (!genreInDatabase){
                    PreparedStatement statementInsertGenre = dbcon.prepareStatement(insertGenreQuery);
                    statementInsertGenre.setString(1, genreName);
                    int retID = statementInsertGenre.executeUpdate();
                    if(retID == 1) {
                        PreparedStatement statementGetGenreId = dbcon.prepareStatement(genreIdQuery);
                        statementGetGenreId.setString(1, genreName);
                        ResultSet genreIdSet = statementGetGenreId.executeQuery();
                        if(genreIdSet.next()) {
                            genreSQLMap.put(genreName, Integer.parseInt(genreIdSet.getString("id")));
                        }
                        else{
                            HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into genres movies.");
                            genreIdSet.close();
                            statementGetGenreId.close();
                            continue;
                        }
                        genreIdSet.close();
                        statementGetGenreId.close();
                    }
                    else{
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into genres movies.");
                        statementInsertGenre.close();
                        continue;
                    }
                    statementInsertGenre.close();
                }

                // Insert into genres_in_movie
                // PreparedStatement statementInsertGenreInMovie = dbcon.prepareStatement(insertGenreInMovieQuery);
                // statementInsertGenreInMovie.setInt(1, genreSQLMap.get(genreName));
                // HelperFunc.printToConsole(currentRecord.movieId);
                // HelperFunc.printToConsole(currentRecord.movieName);
                String movieSQLId = getMovieSQLId(currentRecord.movieId, currentRecord.movieName);
                // HelperFunc.printToConsole(movieIdXMLSQLMap);
                // HelperFunc.printToConsole(movieSQLId);
                if(movieSQLId != null) {
                    /*
                    statementInsertGenreInMovie.setString(2, movieSQLId);
                    int retID = statementInsertGenreInMovie.executeUpdate();
                    if (retID == 0) {
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into genres_in_movie movies.");
                    }
                    */
                    str.append(genreSQLMap.get(genreName) + "|" + movieSQLId).append(line);
                    fw.write(str.toString());
                }
                else{
                    HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into genres_in_movie movies.");
                }
            }
            fw.close();

            String inputQuery = "load data local infile 'GenreInMovieRecordData.txt'\n" +
                    "into table genres_in_movies\n" +
                    "fields terminated by '|' optionally enclosed by '\"' escaped by '\"'\n" +
                    "lines terminated by '[]'" + // '\\r\\n'\n" +
                    "(genreId,movieId)";

            FileWriter fwSQL = new FileWriter("./src/funcScripts/logs/insertGenreInMovieRecord.sql");
            fwSQL.write(inputQuery + ";\n\n");
            fwSQL.close();

            HelperFunc.xmlHandlerLog("Finish writing GenresInMoviesRecord.");
            HelperFunc.closeLogFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void writeStarsRecord(){
        try {
            HelperFunc.initializeLogFile("StarRecordWriter");
            HelperFunc.xmlHandlerLog("Start writing StarRecordWriter.");
            String insertQuery = "Insert into stars values(?, ?, ?)";
            String starIdQuery = "select max(id) as maxId from stars";
            int nextStarId = getMaxId(starIdQuery);

            for(int i = 0; i < starsRecord.size(); i++) {
                StarsRecordClass currentRecord = starsRecord.get(i);
                PreparedStatement statement2 = dbcon.prepareStatement(insertQuery);
                String sqlId = getId("nm", 7, nextStarId);
                starNameSQLIdMap.put(currentRecord.name, sqlId);
                statement2.setString(1, sqlId);
                statement2.setString(2, currentRecord.name);
                if(currentRecord.birthYear != 0) {
                    statement2.setInt(3, currentRecord.birthYear);
                }
                else{
                    statement2.setNull(3, Types.INTEGER);
                }
                try {
                    int retID = statement2.executeUpdate();
                    // HelperFunc.printToConsole(retID);
                    if (retID == 1) {
                        nextStarId++;
                    } else {
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into table stars.");
                    }
                }
                catch (Exception e){
                    HelperFunc.xmlHandlerLog("Error: " + currentRecord.toString() + " -> Fail to insert into table stars.");
                }
            }
            HelperFunc.xmlHandlerLog("Finish writing StarRecordWriter.");
            HelperFunc.closeLogFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    private void writeStarsInMovieRecord(){
        try {
            HelperFunc.initializeLogFile("StarsInMovieRecordWriter");
            HelperFunc.xmlHandlerLog("Start writing StarsInMoviesRecord.");

            String insertStarsInMovieQuery = "Insert into stars_in_movies values(?, ?)";
            String checkDuplicateQuery = "select * from stars_in_movies where starId = ? and movieId = ?";
            String selectStarFromDatabaseQuery = "select * from stars where name = ?";
            String selectMovieFromDatabaseQuery = "select * from movies where title = ?";


            // Insert genresInMoviesRecord
            for(int i = 0; i < starsInMoviesRecord.size(); i++) {
                StarsInMoviesRecordClass currentRecord = starsInMoviesRecord.get(i);
                String movieId = currentRecord.movieId;
                String movieTitle = currentRecord.movieTitle;
                String movieSQLId = getMovieSQLId(movieId, movieTitle);
                // If movieSQLId not in the main.xml: it might in the database
                if(movieSQLId == null){
                    PreparedStatement statementSelectStar = dbcon.prepareStatement(selectMovieFromDatabaseQuery);
                    statementSelectStar.setString(1, movieTitle);
                    ResultSet rsMovie = statementSelectStar.executeQuery();
                    if(rsMovie.next()){
                        movieSQLId = rsMovie.getString("id");
                        updateMovieMap(movieId, movieTitle, movieSQLId);
                    }
                    else{
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.singleNameDupliateString("") + " -> Movie doesn't exist.");
                        continue;
                    }
                }
                for(int j = 0; j < currentRecord.starNameList.size(); j++) {
                    // Check duplication
                    String starName = currentRecord.starNameList.get(j);
                    // HelperFunc.printToConsole(starNameSQLIdMap);
                    String starId = starNameSQLIdMap.get(starName);
                    // If starId not in the actors.xml: it might in the database
                    if(starId == null){
                        PreparedStatement statementSelectStar = dbcon.prepareStatement(selectStarFromDatabaseQuery);
                        statementSelectStar.setString(1, starName);
                        ResultSet rsStar = statementSelectStar.executeQuery();
                        if(rsStar.next()){
                            starId = rsStar.getString("id");
                            starNameSQLIdMap.put(starName, starId);
                        }
                        else{
                            HelperFunc.xmlHandlerLog("Error: " + currentRecord.singleNameDupliateString(starName) + " -> Actor doesn't exist.");
                            continue;
                        }
                    }

                    PreparedStatement statement1 = dbcon.prepareStatement(checkDuplicateQuery);
                    statement1.setString(1, starId);
                    statement1.setString(2, movieSQLId);
                    ResultSet rs1 = statement1.executeQuery();
                    if (rs1.next()) {
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.singleNameDupliateString(starName) + " -> Duplicate entries.");
                        continue;
                    }

                    // Insert into stars_in_movie
                    PreparedStatement statementInsertGenreInMovie = dbcon.prepareStatement(insertStarsInMovieQuery);
                    statementInsertGenreInMovie.setString(1, starId);
                    statementInsertGenreInMovie.setString(2, movieSQLId);
                    int retID = statementInsertGenreInMovie.executeUpdate();
                    if(retID == 0) {
                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.singleNameDupliateString(starName) + " -> Fail to insert into stars_in_movie movies.");
                    }
                }
            }
            HelperFunc.xmlHandlerLog("Finish writing StarsInMovieRecordWriter.");
            HelperFunc.closeLogFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    public static void main(String[] args) {
        try {
            System.out.println(HelperFunc.getCurrentDate(true) + ": Begin Parsing.");
            SAXParserXML spx = new SAXParserXML();
            spx.run();
            System.out.println(HelperFunc.getCurrentDate(true) + ": End Parsing.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
