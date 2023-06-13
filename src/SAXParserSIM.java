import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
import java.sql.*;

public class SAXParserSIM extends DefaultHandler {

    List<StarsInMovie> duplicates;
    List<String> noMatch;
    private String tempVal;
    private String director;

    //to maintain context
    private StarsInMovie tempSIM;

    Connection conn;
    int insert = 0;
    PreparedStatement batch;
    PreparedStatement sID;
    PreparedStatement exist;
    List<Integer> starsIDs;
    List<Integer> movieIDs;
    List<String> movieDir;
    List<String> movieTitles;
    List<String> starsNames;

    List<Integer> existStar;
    List<Integer> existMovie;
    int flag = 0;

    String mID;
    String mT;

    PreparedStatement movie;
    public SAXParserSIM(Connection connection) throws SQLException {
        duplicates = new ArrayList<StarsInMovie>();
        noMatch = new ArrayList<String>();
        conn = connection;
        try {
            batch = conn.prepareStatement("insert into stars_in_movies (starId, movieId)\n" +
                    "values (?, ?);");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        starsIDs = new ArrayList<Integer>();
        movieIDs = new ArrayList<Integer>();
        movieDir = new ArrayList<String>();
        starsNames = new ArrayList<String>();
        movieTitles = new ArrayList<String>();
        existMovie = new ArrayList<Integer>();
        existStar = new ArrayList<Integer>();
        populateList();
    }

    public void populateList(){
        try {
            PreparedStatement statement = conn.prepareStatement("select * from stars");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                starsIDs.add(Integer.parseInt(rs.getString("id").substring(2)));
                starsNames.add(rs.getString("name"));
            }
            rs.close();
            statement.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            PreparedStatement statement = conn.prepareStatement("select * from movies");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                movieIDs.add(Integer.parseInt(rs.getString("id").substring(2)));
                movieDir.add(rs.getString("director"));
                movieTitles.add(rs.getString("title"));
            }
            rs.close();
            statement.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        try {
            PreparedStatement statement = conn.prepareStatement("select * from stars_in_movies;\n");
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                existStar.add(Integer.parseInt(rs.getString("starId").substring(2)));
                existMovie.add(Integer.parseInt(rs.getString("movieId").substring(2)));
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
        batch.close();
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
            sp.parse("stanford-movies/casts124.xml", this);

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
        File myObj2 = new File(folder,"DuplicateStarsInMovies.txt");
        FileWriter writer = new FileWriter(myObj2);
        writer.write("\nNumber of Duplicate Stars in Movies '" + duplicates.size() + "'.");

        Iterator<StarsInMovie> it1 = duplicates.iterator();
        while (it1.hasNext()) {
            writer.write(it1.next().toString());
        }

        File myObj = new File(folder,"StarMovieDoesNotExist.txt");
        writer = new FileWriter(myObj);
        writer.write("\nNumber of Stars or Movies That Don't Exist '" + noMatch.size() + "'.");

        Iterator<String> it = noMatch.iterator();
        while (it.hasNext()) {
            writer.write(it.next().toString());
        }


    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of employee
            tempSIM = new StarsInMovie("", "", "");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("m")) {
            tempSIM.setDir(director);
            addTable(tempSIM);
        } else if (qName.equalsIgnoreCase("t")) {
            if(!tempVal.equals(mT)){
                mT = tempVal;
                flag = 1;
            } else{
                flag = 0;
            }
            tempSIM.setTitle(tempVal.strip());
        } else if (qName.equalsIgnoreCase("is")) {
            director = tempVal.strip();
        } else if (qName.equalsIgnoreCase("a")) {
            tempSIM.setStagename(tempVal.strip());
        }

    }
    public void addTable(StarsInMovie sim){

        // Get a connection from dataSource and let resource manager close the connection after usage.
        // Incorporate mySQL driver

        String star = sim.getStagename();
        String title = sim.getTitle();
        String dir = sim.getDir();

        String starID = getStarID(star);
        String movieID = getMovieID(title, dir);
        if(starID == "" || movieID == ""){
            String error = star +" does not exist or " + title + " does not exist with the director " + director;
            noMatch.add(error);
        } else{
            try {
                int index = existStar.indexOf(Integer.parseInt(starID.substring(2)));
                boolean flag = false;
                if(index != -1){
                    for(int i = index; i < existStar.size(); i++){
                        String st= "nm";
                        for(int j = Integer.toString(existStar.get(index)).length(); j < 7; j++){
                            st += "0";
                        }
                        st += Integer.toString(existStar.get(index));

                        String mov = "tt0" + existMovie.get(i);
                        if(st.equals(starID) && mov.equals(movieID)){
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    // either the title, director, or year is different therefore add it to the table
                    // insert the movie into the table
                    batch.setString(1, starID);
                    batch.setString(2, movieID);

                    batch.addBatch();
                    int size = batch.getFetchSize();
                    existMovie.add(Integer.parseInt(movieID.substring(2)));
                    existStar.add(Integer.parseInt(starID.substring(2)));
                    insert++;
//                    if(insert == 1000){
//                        insert = 0;
//                        batch.executeBatch();
//                        batch.clearBatch();
//                    }

                } else{
                    duplicates.add(sim);
                }


            } catch (Exception e) {

            }
        }
    }
    public String getStarID(String name){
        if(!starsNames.contains(name)){
            return "";
        }

        int val = starsIDs.get(starsNames.indexOf(name));
        String ret= "nm";
        for(int i = Integer.toString(val).length(); i < 7; i++){
            ret += "0";
        }
        ret += Integer.toString(val);

        return ret;
    }

    public String getMovieID(String title, String director){
        String id = "";
        for(int i = 0; i < movieIDs.size(); i++){
            if(title.equals(movieTitles.get(i)) && director.equals(movieDir.get(i))){
                id = "tt0" + movieIDs.get(i);
                return id;
            }
        }
        return id;
    }
//    public static void main(String[] args) throws SQLException {
//        SAXParserSIM spe = new SAXParserSIM();
//        spe.runExample();
//    }

}

