package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import java.util.ArrayList;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    String title;
    int pageNumber;
    int max;
    int currentPage = 1;
    int moviesPerPage = 10;
    private ArrayList<Movie> movieList;
//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "cs122b_project2_war";
//    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    // HTTPS Code, Comment Out Top Code
    private final String host = "3.141.98.110";
    private final String port = "8443";
    private final String domain = "cs122b-project2";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private Button prevPage;
    private Button nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        pageNumber = intent.getIntExtra("pageNumber", 1);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        Log.d("urlTest", String.valueOf(baseURL + "/api/movies?sort=null&genre=null&title=" + title + "&director=&year=&name=&letter="));
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/movies?sort=null&genre=null&title=" + title + "&director=&year=&name=&letter=",
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        Log.d("response", String.valueOf(response));
                        // Calculate the start and end for movie display on the currentPage
                        int startIndex = (pageNumber - 1) * moviesPerPage;
                        int endIndex = Math.min(startIndex + moviesPerPage, jsonArray.length());

                        Log.d("startIndex", String.valueOf(startIndex));
                        Log.d("endIndex", String.valueOf(endIndex));

                        movieList = new ArrayList<>();

                        for (int i = startIndex; i < endIndex; i++) {
                            JSONObject movieObject = jsonArray.getJSONObject(i);

                            String movieTitle = movieObject.getString("movieTitle");
                            short movieYear = (short) Integer.parseInt(movieObject.getString("movieYear"));
                            String movieDirector = movieObject.getString("movieDirector");
                            String movieRating = movieObject.getString("movieRating");
                            String movieStars = movieObject.getString("movieStars");
                            String movieGenre = movieObject.getString("movieGenre");

                            Movie movie = new Movie(movieTitle, movieYear, movieDirector, movieRating, movieStars, movieGenre);
                            movieList.add(movie);
                        }
                        ListView listView = findViewById(R.id.list);
                        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movieList);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movieList.get(position);
                            // Create intent to send movieName as id to SingleMovieList
                            Intent listPage = new Intent(MovieListActivity.this, SingleMovieList.class);
                            listPage.putExtra("id", movie.getName());
                            startActivity(listPage);

                    });

                } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    },
                error -> {
                    // error
                    Log.d("list.error", error.toString());
                });
        queue.add(searchRequest);

        prevPage = findViewById(R.id.prev);
        // Gray out button when page = 1
        prevPage.setEnabled(pageNumber > 1);
        prevPage.setOnClickListener(view -> prevPage());

        nextPage = findViewById(R.id.next);
        nextPage.setOnClickListener(view -> nextPage());
    }

    public void prevPage(){
        if (pageNumber > 1) {
            Intent changePage = new Intent(MovieListActivity.this, MovieListActivity.class);
            changePage.putExtra("title", title);
            changePage.putExtra("pageNumber", pageNumber - 1);
            startActivity(changePage);
        }
    }
    public void nextPage(){
        Intent changePage = new Intent(MovieListActivity.this, MovieListActivity.class);
        changePage.putExtra("title", title);
        changePage.putExtra("pageNumber", pageNumber + 1);
        startActivity(changePage);

    }
}