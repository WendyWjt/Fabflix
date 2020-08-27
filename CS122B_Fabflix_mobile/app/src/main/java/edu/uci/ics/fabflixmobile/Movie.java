package edu.uci.ics.fabflixmobile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Movie {
    private String name;
    private String year;
    private String director;
    private String json;
    private ArrayList<String> genres= new ArrayList<String>();
    private ArrayList<String> stars = new ArrayList<String>();

    public Movie(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public Movie(JSONObject jsonObject) throws JSONException {
        this.json = jsonObject.toString();
        this.name = jsonObject.getString("movie_title");
        this.year = jsonObject.getString("movie_year");
        this.director = jsonObject.getString("movie_director");
        JSONObject starobject = jsonObject.getJSONObject("movie_stars");
        JSONObject genreobject = jsonObject.getJSONObject("movie_genres");
        for(int i = 0; i<starobject.length();i++){
            this.stars.add(starobject.getJSONObject(Integer.toString(i+1)).getString("name"));
        }
        for(int i = 0; i<genreobject.length();i++){
            this.genres.add(genreobject.getJSONObject(Integer.toString(i+1)).getString("name"));
        }
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {return director;}

    public String getJson() {return json;}

    public ArrayList getStars() {return stars;}

    public ArrayList getGenres() {return genres;}
}