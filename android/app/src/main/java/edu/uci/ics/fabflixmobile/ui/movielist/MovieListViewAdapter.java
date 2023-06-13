package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.ViewGroup;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
//    private static class ViewHolder {
//        TextView title;
//        TextView subtitle;
//    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.movielist_row, parent, false);

        Movie movie = movies.get(position);
        // Title for bigger font, Subtitle for smaller font
        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subtitle);
        // Append movie name to title
        titleView.setText(movie.getName());
        // Add to the list for each movie
        String movie_details = movie.getYear() + " " +
                "\nDirector: " + movie.getDirector() +
                "\nGenres: " + movie.getGenres() +
                "\nStars: " + movie.getStars()
                + "\nRating: " + movie.getRating();
        subtitleView.setText(movie_details);

        return view;
    }
}