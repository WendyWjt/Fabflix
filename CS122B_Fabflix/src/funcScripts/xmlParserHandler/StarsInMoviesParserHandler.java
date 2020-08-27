package funcScripts.xmlParserHandler;

import dataClass.StarsInMoviesRecordClass;
import funcScripts.HelperFunc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StarsInMoviesParserHandler extends DefaultHandler  {
    //to maintain context
    private String tempVal;
    private String currentMovieId;
    private StarsInMoviesRecordClass tempStarsInMovieRecord;
    private int starsInMoviesRecordCount;
    private boolean recordIsValid;

    private HashMap<String, String> starNameSQLIdMap;
    private HashMap<String, String> movieIdXMLSQLMap;
    private HashMap<String, String> movieTitleSQLMap;   // If movieId = null in XML file

    File f;
    FileWriter fw;

    // Create a dataSource which registered in web.xml
    private Connection dbcon = null;


    public StarsInMoviesParserHandler(HashMap<String, String> hm, HashMap<String, String> movieId, HashMap<String, String> movieTitle) {
        starNameSQLIdMap = hm;
        movieIdXMLSQLMap = movieId;
        movieTitleSQLMap = movieTitle;
    }


    //Event Handlers
    public void startDocument() throws SAXException {
        try {
            HelperFunc.initializeLogFile("StarInMoviesXMLParser");
            starsInMoviesRecordCount = 0;
            initializeDatabaseConnection();
            f = new File("./src/funcScripts/logs/StarsInMoviesRecordData.txt");
            fw = new FileWriter(f);
            HelperFunc.xmlHandlerLog("Start parsing StarInMoviesXML.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void endDocument() throws SAXException {
        try {
            HelperFunc.printToConsole(starsInMoviesRecordCount);
            closeDatabaseConnection();
            fw.close();
            HelperFunc.xmlHandlerLog("Finish parsing MovieXML. Data count: " + Integer.toString(starsInMoviesRecordCount) + ".");
            HelperFunc.closeLogFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            //create a new instance of employee
            HelperFunc.printToConsole(tempStarsInMovieRecord);
            HelperFunc.printToConsole("Start of filmc");
            tempStarsInMovieRecord = new StarsInMoviesRecordClass();
            recordIsValid = true;
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
    }



    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("f")){
            HelperFunc.printToConsole("film: " + tempVal);
            tempStarsInMovieRecord.movieId = tempVal;
        }
        else if (qName.equalsIgnoreCase("t")){
            HelperFunc.printToConsole("title: " + tempVal);
            tempStarsInMovieRecord.movieTitle = tempVal;
        }
        else if (qName.equalsIgnoreCase("a")) {
            //add it to the list
            if(!tempVal.equalsIgnoreCase("s a") ) {
                HelperFunc.printToConsole("Actor: " + tempVal);
                tempStarsInMovieRecord.add(tempVal);
            }
        }
        else if (qName.equalsIgnoreCase("filmc")) {
            //add it to the list
            HelperFunc.printToConsole("End of filmc: " + tempVal);
            if(recordIsValid) {
                try {
                    writeStarsInMovieRecord2(tempStarsInMovieRecord);
                    starsInMoviesRecordCount++;
                }
                catch(Exception e){
                    HelperFunc.xmlHandlerLog("Error: " + tempStarsInMovieRecord.toString() + ".");
                }
            }
        }
    }


     private void initializeDatabaseConnection() throws SQLException {
        dbcon = DriverManager.getConnection("jdbc:mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                "mytestuser", "mypassword");
    }

    private void closeDatabaseConnection() throws SQLException {
        dbcon.close();
    }


    private String getMovieSQLId(String movieId, String movieTitle){
        if(movieId == null){
            return movieTitleSQLMap.get(movieTitle);
        }
        else{
            return movieIdXMLSQLMap.get(movieId);
        }
    }

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


    private void writeStarsInMovieRecord2(StarsInMoviesRecordClass currentRecord) throws IOException {
        try {
            HelperFunc.xmlHandlerLog("Start writing StarsInMoviesRecord.");

            String checkDuplicateQuery = "select * from stars_in_movies where starId = ? and movieId = ?";
            String selectStarFromDatabaseQuery = "select * from stars where name = ?";
            String selectMovieFromDatabaseQuery = "select * from movies where title = ?";

            // String filepathMovieRecord = f.getCanonicalPath();

            // Insert genresInMoviesRecord
            String movieId = currentRecord.movieId;
            String movieTitle = currentRecord.movieTitle;
            String movieSQLId = getMovieSQLId(movieId, movieTitle);
            // If movieSQLId not in the main.xml: it might in the database
            // Map<String,String> allmoviemap = getCurrentSQLMovieMap();
            if(movieSQLId == null){
                PreparedStatement statementSelectStar = dbcon.prepareStatement(selectMovieFromDatabaseQuery);
                statementSelectStar.setString(1, movieTitle);
                ResultSet rsMovie = statementSelectStar.executeQuery();
                if(rsMovie.next()){
                    movieSQLId = rsMovie.getString("id");
                    updateMovieMap(movieId, movieTitle, movieSQLId);
                    // System.out.println("Successfully put " + movieId);
                }
                else{
                    HelperFunc.xmlHandlerLog("Error: " + currentRecord.singleNameDupliateString("") + " -> Movie doesn't exist.");
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
                        // System.out.println("Successfully put " + starId);
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
                String line = "[]";// System.getProperty("line.separator");
                StringBuffer str = new StringBuffer();
                str.append(starId + "|" + movieSQLId).append(line);
                fw.write(str.toString());
                fw.flush();

//                    PreparedStatement statementInsertGenreInMovie = dbcon.prepareStatement(insertStarsInMovieQuery);
//                    statementInsertGenreInMovie.setString(1, starId);
//                    statementInsertGenreInMovie.setString(2, movieSQLId);
//                    int retID = statementInsertGenreInMovie.executeUpdate();
//                    if(retID == 0) {
//                        HelperFunc.xmlHandlerLog("Error: " + currentRecord.singleNameDupliateString(starName) + " -> Fail to insert into stars_in_movie movies.");
//                    }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        HelperFunc.xmlHandlerLog("Finish writing StarsInMovieRecordWriter.");
    }

}
