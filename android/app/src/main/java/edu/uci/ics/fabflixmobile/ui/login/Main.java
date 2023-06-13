package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class Main extends AppCompatActivity{
    private EditText title;
    private TextView message;
    private Button searchInput;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create views for respective xml
        setContentView(R.layout.main);
        title = findViewById(R.id.title);
        searchInput = findViewById(R.id.search);
        message = findViewById(R.id.message);
        //assign a listener to call a function to handle the user request when clicking a button
        searchInput.setOnClickListener(view -> findMovie());
    }


    @SuppressLint("SetTextI18n")
    public void findMovie() {

        message.setText("Trying to search");
        String text = title.getText().toString();

        if (text.isEmpty()){
            String msg = "Search bar empty, input title";
            message.setText(msg);
            return;
        }
        String msg = "searching";
        message.setText(msg);
        Intent movieList = new Intent(Main.this, MovieListActivity.class);
        movieList.putExtra("title", title.getText().toString());
        movieList.putExtra("pageNumber", 1);
        startActivity(movieList);
    }
}

