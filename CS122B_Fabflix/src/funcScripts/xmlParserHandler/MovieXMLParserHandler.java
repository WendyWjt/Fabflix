package funcScripts.xmlParserHandler;

import com.mysql.jdbc.SocketMetadata;
import dataClass.GenresInMoviesRecordClass;
import dataClass.MovieRecordClass;
import funcScripts.HelperFunc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieXMLParserHandler  extends DefaultHandler {
    private List<MovieRecordClass> movieRecord = new ArrayList<MovieRecordClass>();
    private List<GenresInMoviesRecordClass> genresInMoviesRecord = new ArrayList<GenresInMoviesRecordClass>();

    //to maintain context
    private String tempDirector;
    private String tempVal;
    private MovieRecordClass tempMovie;
    private GenresInMoviesRecordClass tempGenreInMovie;
    private boolean recordIsValid = true;

    private int movieRecordCount = 0;


    public List<MovieRecordClass> getMovieRecordList(){
        return movieRecord;
    }


    public List<GenresInMoviesRecordClass> getGenresInMoviesRecord(){
        return genresInMoviesRecord;
    }


    //Event Handlers
    public void startDocument() throws SAXException {
        try {
            HelperFunc.initializeLogFile("MovieXMLParser");
            movieRecordCount = 0;
            // initializeDatabaseConnection();
            HelperFunc.xmlHandlerLog("Start parsing MovieXML.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void endDocument() throws SAXException {
        try {
            HelperFunc.printToConsole(movieRecordCount);
            // closeDatabaseConnection();
            HelperFunc.xmlHandlerLog("Finish parsing MovieXML. Data count: " + Integer.toString(movieRecordCount) + ".");
            HelperFunc.closeLogFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            HelperFunc.printToConsole(tempMovie);
            tempMovie = new MovieRecordClass();
            recordIsValid = true;
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
    }






    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equalsIgnoreCase("dirname")) {
            HelperFunc.printToConsole("Director: " + tempVal);
            tempDirector = tempVal;
        }
        else if (qName.equalsIgnoreCase("fid")){
            HelperFunc.printToConsole("id: " + tempVal);
            tempMovie.id = tempVal;
        }
        else if (qName.equalsIgnoreCase("t")){
            HelperFunc.printToConsole("title: " + tempVal);
            if(tempVal.equalsIgnoreCase("")) {
                recordIsValid = false;
                HelperFunc.xmlHandlerLog("Error: " + tempMovie.toString() + " -> Empty title: " + tempVal);
            }
            else {
                tempMovie.title = tempVal;
            }
        }
        else if (qName.equalsIgnoreCase("altt")){
            HelperFunc.printToConsole("altt: " + tempVal);
            if(tempMovie.title == null && tempVal.length() != 0) {
                tempMovie.title = tempVal;
            }
            else{
                recordIsValid = false;
                HelperFunc.xmlHandlerLog("Error: " + tempMovie.toString() + " -> Empty title: " + tempVal);
            }
        }
        else if (qName.equalsIgnoreCase("year")){
            try {
                HelperFunc.printToConsole("year: " + tempVal);
                tempMovie.year = Integer.parseInt(tempVal);
            }
            catch (NumberFormatException e){
                recordIsValid = false;
                HelperFunc.xmlHandlerLog("Error: " + tempMovie.toString() + " -> Invalid year format: " + tempVal);
            }
        }
        else if (qName.equalsIgnoreCase("cat")){
            HelperFunc.printToConsole("cat: " + tempVal + " | " + HelperFunc.genreMap(tempVal));
            HelperFunc.printToConsole(tempGenreInMovie);
            if(recordIsValid && tempVal.length() != 0) {
                tempGenreInMovie = new GenresInMoviesRecordClass();
                tempGenreInMovie.genreName = HelperFunc.genreMap(tempVal);
                tempGenreInMovie.movieId = tempMovie.id;
                tempGenreInMovie.movieName = tempMovie.title;
                genresInMoviesRecord.add(tempGenreInMovie);
            }
        }
        else if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            HelperFunc.printToConsole("End of film: " + tempVal);
            if(recordIsValid) {
                tempMovie.director = tempDirector;
                movieRecord.add(tempMovie);
                movieRecordCount++;
            }
        }
    }

}
