package edu.uci.ics.fabflixmobile.data.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;
    final String director;
    public String rating;
    final String stars;
    final String genres;

    public Movie(String name, short year, String director, String rating, String movieStars, String genres) throws JSONException {
        this.name = name;
        this.year = year;
        this.director = director;
        this.rating = rating;

        // Split the movieStars string into individual star names
        String[] starNames = movieStars.split(", ");

        // Build the stars string with comma-separated star names
        StringBuilder starsBuilder = new StringBuilder();
        for (int i = 0; i < starNames.length; i++) {
            if (i > 0) {
                starsBuilder.append(", ");
            }
            starsBuilder.append(starNames[i]);
        }
        this.stars = starsBuilder.toString();

        // Split the genres string into individual genres
        String[] genreNames = genres.split(", ");

        // Build the stars string with comma-separated star names
        StringBuilder genreBuilder = new StringBuilder();
        for (int i = 0; i < genreNames.length; i++) {
            if (i > 0) {
                genreBuilder.append(", ");
            }
            genreBuilder.append(genreNames[i]);
        }
        this.genres = genreBuilder.toString();
    }

    public String getName() {
        return name;
    }
    public String getRating() {
        return rating;
    }
    public String getDirector(){
        return director;
    }
    public String getStars(){
        return stars;
    }
    public String getGenres(){
        return genres;
    }

    public short getYear() {
        return year;
    }
}