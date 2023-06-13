package edu.uci.ics.fabflixmobile.ui.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import org.json.JSONArray;
import org.json.JSONException;



public class SingleMovieList extends AppCompatActivity {

    String starId;
    String stars = "";
    String genres = "";
    TextView movie_title;
    TextView movie_stars;
    TextView movie_year;
    TextView movie_director;
    TextView movie_genres;
    TextView movie_rating;
    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "cs122b_project2_war";
//    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    // HTTPS Code, Comment Out Top Code
    private final String host = "3.141.98.110";
    private final String port = "8443";
    private final String domain = "cs122b-project2";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize xml layout
        setContentView(R.layout.activity_singlemovie);
        // intent used for retrieving values from MovieListActivity
        Intent intent = getIntent();
        // xml ids for editing
        movie_title = findViewById(R.id.title);
        movie_stars = findViewById(R.id.stars);
        movie_year = findViewById(R.id.year);
        movie_director = findViewById(R.id.director);
        movie_genres = findViewById(R.id.genres);
        movie_rating = findViewById(R.id.rating);

        // id used to identify single movie, should be movieName, reference single_movie.js
        starId = intent.getStringExtra("id");
        getMovieInfo();
    }

    public void getMovieInfo() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest movieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + starId,
                response -> {
                    try{
                        JSONArray result = new JSONArray(response);
                        Log.d("Check For Response", response);
                        String director = result.getJSONObject(0).getString("movieDirector");
                        String rating = result.getJSONObject(0).getString("movieRating");
                        String year = result.getJSONObject(0).getString("movieYear");
                        String title = result.getJSONObject(0).getString("movieTitle");
                        String movieStars = result.getJSONObject(0).getString("movieStars");
                        String genres = result.getJSONObject(0).getString("movieGenre");


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

                        // Add info to the single movie page
                        movie_title.setText(title);

                        // Display for Year
                        String yearDisplay = "Release Year: " + year;
                        movie_year.setText(yearDisplay);

                        // Display for Director
                        String directorDisplay = "Director: " + director;
                        movie_director.setText(directorDisplay);

                        // Display for Genres
                        String genresDisplay = "Genres: " + genres;
                        movie_genres.setText(genresDisplay);

                        // Display for Rating
                        String ratingDisplay = "Rating: " + rating;
                        movie_rating.setText(ratingDisplay);

                        // Display for stars
                        String movie_stars_list = "Stars: " + stars;
                        movie_stars.setText(movie_stars_list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
        };

        // important: queue.add is where the login request is actually sent
        queue.add(movieRequest);

    }
}