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
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
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

public class SAXParserStars extends DefaultHandler {

    List<Stars> duplicates;
    private String tempVal;

    Connection conn;
    PreparedStatement batch;

    List<String> starName;
    List<Integer> starIDs;
    List<Integer> starYear;

    int first = 1;
    //to maintain context
    int insert = 0;

    //to maintain context
    private Stars tempStar;

    public SAXParserStars(Connection connection) {
        duplicates = new ArrayList<Stars>();
        conn = connection;

        try {
            batch = conn.prepareStatement("insert into stars (id, name, birthYear)\n" +
                    "values (?, ?, ?);");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        starName = new ArrayList<String>();
        starIDs = new ArrayList<Integer>();
        starYear = new ArrayList<Integer>();
        populateList();
    }
    public void populateList(){
        try {
            PreparedStatement statement = conn.prepareStatement("select * from stars");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                starIDs.add(Integer.parseInt(rs.getString("id").substring(2)));
                starName.add(rs.getString("name"));
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
            sp.parse("stanford-movies/actors63.xml", this);

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
            File myObj = new File(folder, "DuplicateStars.txt");
            FileWriter writer = new FileWriter(myObj);
            writer.write("\nNumber of Duplicate Stars '" + duplicates.size() + "'.");

            Iterator<Stars> it1 = duplicates.iterator();
            while (it1.hasNext()) {
                writer.write(it1.next().toString());
            }

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempStar = new Stars("", 0);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            addTable(tempStar);
        } else if (qName.equalsIgnoreCase("dob")) {
            try{
                tempStar.setYear(Integer.parseInt(tempVal.strip()));
            } catch (Exception e){
                tempStar.setYear(0);
            }
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setStagename(tempVal.strip());
        }

    }
    public void addTable(Stars star){

        // Get a connection from dataSource and let resource manager close the connection after usage.
        // Incorporate mySQL driver

        try {
            // is the movie already in the table
            String t = star.getStagename();
            int y = star.getYear();
            int index = starName.indexOf(t);
            boolean flag = false;
            if(index != -1){
                for(int i = index; i < starName.size(); i++){
                    if(starName.get(i).equals(t) && starYear.get(index) == y){
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                // either the title, director, or year is different therefore add it to the table
                String id = getStarID();
                batch.setString(1, id);
                batch.setString(2, t);
                starYear.add(y);
                starName.add(t);
                starIDs.add(Integer.parseInt(id.substring(2)));
                // insert the movie into the table
                if(y == 0){
                    batch.setObject(3, null);
                } else{
                    batch.setObject(3, y);
                }

                batch.addBatch();
                insert++;

//                if(insert == 1000){
//                    batch.executeBatch();
//                    batch.clearBatch();
//                    insert = 0;
//                }
            } else{
                duplicates.add(star);
            }

        } catch (Exception e) {

        }
    }
    public String getStarID(){
        int id = Collections.max(starIDs) + 1;
        String ret= "nm";
        for(int i = Integer.toString(id).length(); i < 7; i++){
            ret += "0";
        }
        ret += Integer.toString(id);

        return ret;

    }
//    public static void main(String[] args) throws SQLException {
//        SAXParserStars spe = new SAXParserStars();
//        spe.runExample();
//    }

}

