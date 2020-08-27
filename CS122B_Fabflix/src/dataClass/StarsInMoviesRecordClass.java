package dataClass;

import java.util.ArrayList;
import java.util.HashMap;

public class StarsInMoviesRecordClass {
    public ArrayList<String> starNameList = new ArrayList<>();
    public String movieTitle = null;
    public String movieId = null;

    public StarsInMoviesRecordClass(String movieTitle, String movieId) {
        this.movieTitle = movieTitle;
        this.movieId = movieId;
    }

    public StarsInMoviesRecordClass(){

    }

    public void add(String starName){
        starNameList.add(starName);
    }

    @Override
    public String toString(){
        String result = "dataClass.StarsInMoviesRecordClass: " + this.movieId + " | " + this.movieTitle + " | \n";
        for(int i = 0; i < starNameList.size(); i++){
            result += starNameList.get(i) + " / ";
        }
        return result;
    }

    public String singleNameDupliateString(String name){
        return "dataClass.StarsInMoviesRecordClass: \" + this.movieId + \" | \" + this.movieTitle + \" | " + name;
    }

}
