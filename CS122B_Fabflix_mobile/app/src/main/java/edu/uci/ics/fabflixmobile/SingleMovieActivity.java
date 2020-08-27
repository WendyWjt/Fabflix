package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class SingleMovieActivity extends Activity {

    private TextView title;
    private TextView director;
    private TextView year;
    private TextView genres;
    private TextView stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movielist);
        title = findViewById(R.id.title);
        director = findViewById(R.id.director);
        year = findViewById(R.id.year);
        genres = findViewById(R.id.genres);
        stars = findViewById(R.id.stars);
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String movies1 = bundle.getString("movie");
        try {
            JSONObject json = new JSONObject(movies1);
            Movie movie = new Movie(json);
            title.setText(movie.getName());
            year.setText(movie.getYear());// need to cast the year to a string to set the label
            director.setText(movie.getDirector());
            String genre = "";
            for (int i=0;i<Math.min(3,movie.getGenres().size());i++){
                genre += movie.getGenres().get(i);
                genre += ";\t";
            }
            genres.setText(genre);
            String star = "";
            for (int i=0;i<Math.min(3,movie.getStars().size());i++){
                star += movie.getStars().get(i);
                star += ";\t";
            }
            stars.setText(star);

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        try {
//            JSONObject jsonObject2 = moviesarray.getJSONObject(6);
//            Movie mm = new Movie(jsonObject2);
//            String genre = "";
//            for (int i=0;i<3;i++){
//                genre += mm.getGenres().get(i);
//                genre += "\n";
//                ArrayList<String> test = mm.getGenres();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        //this should be retrieved from the database and the backend server

    }
}