package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView year = view.findViewById(R.id.year);
        TextView director = view.findViewById(R.id.director);
        TextView genres = view.findViewById(R.id.genres);
        TextView stars = view.findViewById(R.id.stars);


        titleView.setText(movie.getName());
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

        return view;
    }
}