import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.*;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
import java.sql.*;

public class SAXParserMovies extends DefaultHandler {

    List<Movies> noGenre;
    List<Movies> duplicates;
    List<String> invalidTags;
    private String tempVal;

    private String director;
    //to maintain context
    private Movies tempMovie;
    Connection conn;
    PreparedStatement batch;

    PreparedStatement batchGenre;
    PreparedStatement batchGenre1;
    PreparedStatement batchRating;

    List<String> movieTitles;
    List<Integer> movieIDs;
    List<String> movieDir;
    List<Integer> movieYear;
    List<String> genreNames;
    List<Integer> genreIDs;
    List<String> ratingMovies;

    int insert = 0;

    public SAXParserMovies(Connection connection) {
        noGenre = new ArrayList<Movies>();
        duplicates = new ArrayList<Movies>();
        invalidTags = new ArrayList<String>();
        conn = connection;
        try {
            batch = conn.prepareStatement("insert into movies (id, title, year, director)\n" +
                    "values (?, ?, ?, ?);");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            batchRating = conn.prepareStatement("insert into ratings (movieId, rating, numVotes)\n" +
                    "values (?, 0, 0);");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            batchGenre = conn.prepareStatement("insert into genres (id, name)\n" +
                    "values (?, ?);");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            batchGenre1 = conn.prepareStatement("insert into genres_in_movies (genreId, movieId)\n" +
                    "values (?, ?);");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        movieTitles = new ArrayList<String>();
        movieIDs = new ArrayList<Integer>();
        movieDir = new ArrayList<String>();
        movieYear = new ArrayList<Integer>();
        genreNames = new ArrayList<String>();
        genreIDs = new ArrayList<Integer>();
        ratingMovies = new ArrayList<String>();
        populateLists();

    }
    public void populateLists(){
        try {
            PreparedStatement statement = conn.prepareStatement("select * from movies");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                movieIDs.add(Integer.parseInt(rs.getString("id").substring(2)));
                movieTitles.add(rs.getString("title"));
                movieDir.add(rs.getString("director"));
                movieYear.add(rs.getInt("year"));
            }
            rs.close();
            statement.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            PreparedStatement statement = conn.prepareStatement("select * from genres");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                genreIDs.add(rs.getInt("id"));
                genreNames.add(rs.getString("name"));
            }
            rs.close();
            statement.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            PreparedStatement statement = conn.prepareStatement("select * from ratings");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ratingMovies.add(rs.getString("movieId"));
            }
            rs.close();
            statement.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }
    public void runExample() throws SQLException, IOException {
        parseDocument();
        batch.executeBatch();
        batchGenre1.executeBatch();
        batchRating.executeBatch();

        batch.close();
        batchGenre.close();
        batchGenre1.close();
        batchRating.close();
//        conn.close();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("stanford-movies/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() throws IOException {
        File folder = new File("Inconsistency Reports");
        folder.mkdir();
        File myObj = new File(folder, "NoGenre.txt");
        FileWriter writer = new FileWriter(myObj);
        writer.write("Number of No Genre Movies '" + noGenre.size() + "'.");

        Iterator<Movies> it = noGenre.iterator();
        while (it.hasNext()) {
            writer.write("\n" + it.next().toString());
        }

        File myObj1 = new File(folder,"MovieDuplicates.txt");
        writer = new FileWriter(myObj1);

            writer.write("\nNumber of Duplicate Movies '" + duplicates.size() + "'.");

            Iterator<Movies> it1 = duplicates.iterator();
            while (it1.hasNext()) {
                writer.write("\n" + it1.next().toString());
            }
        File myObj2 = new File(folder,"InvalidTagsForMovies.txt");
        writer = new FileWriter(myObj2);
        writer.write("\nNumber of Invalid Tags '" + invalidTags.size() + "'.");

        for(String tag: invalidTags){
            writer.write("\n" + tag);
        }
    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMovie = new Movies(null, null, 0, new ArrayList<String>());
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            if(tempMovie.getGenres().size() != 0){
                tempMovie.setDir(director);
                // add movie to table
                addTable(tempMovie);
            } else{
                noGenre
                        .add(tempMovie);
            }
        } else if (qName.equalsIgnoreCase("year")) {
            try{
                tempMovie.setYear(Integer.parseInt(tempVal.strip()));
            } catch (Exception e){
                invalidTags.add("Invalid value " + tempVal + " for tag " + qName + " for movie " + tempMovie.getTitle());
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            int before = tempMovie.getGenres().size();
            tempMovie.setGenre(tempVal.strip());
            int after = tempMovie.getGenres().size();
            if(after == before){
                invalidTags.add("Invalid value " + tempVal + " for tag " + qName + " for movie " + tempMovie.getTitle());
            }
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("dirname")) {
            director = tempVal;
        }

    }
    public void addTable(Movies m){

        // Get a connection from dataSource and let resource manager close the connection after usage.
        // Incorporate mySQL driver
        try {

            // is the movie already in the table
            String t = m.getTitle();
            String d = m.getDir();
            int y = m.getYear();
            if(t.indexOf("'") == -1){
                // check if this movie exists in the table
                boolean flag = false;
                int index = movieTitles.indexOf(t);
                if(index != -1){
                    for(int i = index; i < movieTitles.size(); i++){
                        if(movieTitles.get(i).equals(t) && d.equals(movieDir.get(i)) && y == movieYear.get(i)){
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    String id = getMovieID();
                    // insert the movie into the table
                    batch.setString(1, id);
                    batch.setString(2, t);
                    batch.setObject(3, y);
                    batch.setString(4, d);

                    batch.addBatch();
                    insert++;

                    movieDir.add(d);
                    movieYear.add(y);
                    movieTitles.add(t);
                    movieIDs.add(Integer.parseInt(id.substring(2)));

                    if(insert % 1000 == 0 && insert != 0){
                        batch.executeBatch();
                        batch.clearBatch();
                    }

                    // make function that adds genre and movie to genres_in_movie table and insert under this line
                    // grab genre by using query that get genreId based on the genre and then use the movieId above and add ti
                    addGenres(m.getGenres()); // insert into the genre table if there are new genres
                    insertGIM(m.getGenres(), id); // insert into genres_in_movies
                    addRating(id);

                } else{
                    duplicates.add(m);
                }

            }

        } catch (Exception e) {

        }
    }

    public void insertGIM(List<String> genres, String movieID){

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try{
            for(String gen:genres){
                int gID = genreIDs.get(genreNames.indexOf(gen));
                String query = "insert into genres_in_movies (genreId, movieId)\n" +
                        "values (?, ?);";

                batchGenre1.setObject(1, gID);
                batchGenre1.setString(2, movieID);

                batchGenre1.addBatch();

                if(insert % 1000 == 0 && insert != 0){
                    batchGenre1.executeBatch();
                    batchGenre1.clearBatch();
                }
            }
        } catch (Exception e) {

        }
    }
    public void addGenres(List<String> genres){
        try{

            for(String gen:genres){
                // check if this genre exists in the table
                if(!genreNames.contains(gen)){
                    int id = Collections.max(genreIDs) + 1;
                    // insert the movie into the table
                    batchGenre.setObject(1, id);
                    batchGenre.setString(2, gen);
                    genreIDs.add(id);
                    genreNames.add(gen);

                    batchGenre.executeUpdate();
                }
            }
        } catch (Exception e) {

        }
    }

    public String getMovieID(){
        int id = Collections.max(movieIDs) + 1;
        String ret = "tt0" + Integer.toString(id);

        return ret;
    }

    public void addRating(String movieId){
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try {
            // is the movie already in the table

            if (!ratingMovies.contains(movieId)) {
                // either the title, director, or year is different therefore add it to the table
                // insert the movie into the table
                batchRating.setString(1, movieId);
                batchRating.addBatch();
                ratingMovies.add(movieId);

                if(insert % 1000 == 0 && insert != 0){
                    batchRating.executeBatch();
                    batchRating.clearBatch();
                }

            }


        } catch (Exception e) {
        }
    }
//    public static void main(String[] args) throws SQLException {
//        SAXParserMovies spe = new SAXParserMovies();
//        spe.runExample();
//    }

}

