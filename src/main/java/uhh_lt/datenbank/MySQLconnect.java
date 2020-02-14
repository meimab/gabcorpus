package uhh_lt.datenbank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class MySQLconnect{
    private static Connection con = null;
    private static String dbHost = "basecamp-bigdata";	// Hostname
    private static String dbPort = "3306";		// Port -- Standard: 3306
    private static String dbName = "gabcorpus";	// Datenbankname
    private static String dbUser;;		// Datenbankuser
    private static String dbPass;		// Datenbankpasswort
    private static String dbTable = "posts";		// Posts Tabelle

    public MySQLconnect() {
        // get credentials
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "resources/credentials.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] fields = line.split("=");
                if (fields[0].compareTo("mysql.user") == 0) {
                    dbUser = fields[1];
                }
                if (fields[0].compareTo("mysql.password") == 0) {
                    dbPass = fields[1];
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");    // Datenbanktreiber für JDBC Schnittstellen laden.

            // Verbindung zur JDBC-Datenbank herstellen.
            con = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false", dbUser, dbPass);
        } catch (ClassNotFoundException e) {
            System.out.println("Treiber nicht gefunden");
        } catch (SQLException e) {
            System.out.println("Verbindung nicht moglich");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

        private static Connection getInstance(){
        if(con == null)
            new MySQLconnect();
        return con;
    }

    // Methode zum Auslesen von Text aus der Datenbank, der zusammen mit der post_id verwendet wird, um Watson-Daten zu speichern
    public static HashMap<Integer, String> getText(String datum)  {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = formatter.parse(datum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Statement st = null;
        StringBuilder sb = new StringBuilder();
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
        String sql = ("SELECT text, post_id FROM "+dbTable+" WHERE date(date) between '" +sqlDate+ "' and date_add('"+sqlDate+" 00:00:00',interval 12 hour) AND watson_sentiment IS NULL ORDER BY RAND() LIMIT 10;");
        ResultSet rs = null;
        HashMap<Integer, String> results = new HashMap<>();
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while(rs.next()) {
                int id = rs.getInt("post_id");
                String post_text = rs.getString("text");
                System.out.println(id);
                results.put(id, post_text);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Methode zum Auslesen von Text aus der Datenbank, der zusammen mit der post_id verwendet wird, um Watson-Daten zu speichern
    public static HashMap<Integer, String> getTextNoDate()  {
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = ("SELECT text, post_id FROM "+dbTable+" WHERE post_id in (SELECT post_id FROM (SELECT post_id FROM "+dbTable+" ORDER BY RAND() LIMIT 10) t) AND watson_sentiment IS NULL LIMIT 10;");
        ResultSet rs = null;
        HashMap<Integer, String> results = new HashMap<>();
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while(rs.next()) {
                int id = rs.getInt("post_id");
                String post_text = rs.getString("text");
                System.out.println(id);
                results.put(id, post_text);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Methode zum Auslesen des durchschnittlichen Watson Scores an einem bestimmten Tag
    public static String getWatson(String datum) {
        String message;
        double average_sentiment = 0;
        double average_sadness = 0;
        double average_joy = 0;
        double average_fear = 0;
        double average_disgust = 0;
        double average_anger = 0;
        double index = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = formatter.parse(datum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Statement st = null;
        StringBuilder sb = new StringBuilder();
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
        String sql = ("SELECT watson_sentiment_score, watson_sadness, watson_joy, watson_fear, watson_disgust, watson_anger FROM " + dbTable + " WHERE date(date) between '" + sqlDate + "' and date_add('" + sqlDate + " 00:00:00',interval 12 hour) AND watson_sentiment IS NOT NULL LIMIT 10;");
        ResultSet rs = null;
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                average_sentiment += rs.getDouble("watson_sentiment_score");
                average_sadness += rs.getDouble("watson_sadness");
                average_joy += rs.getDouble("watson_joy");
                average_fear += rs.getDouble("watson_fear");
                average_disgust += rs.getDouble("watson_disgust");
                average_anger += rs.getDouble("watson_anger");
                index++;
            }
            average_sentiment = average_sentiment / index;
            average_sadness = average_sadness / index;
            average_joy = average_joy / index;
            average_fear = average_fear / index;
            average_disgust = average_disgust / index;
            average_anger = average_anger / index;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DecimalFormat df = new DecimalFormat("0.000");
        String newLine = System.getProperty("line.separator");
    if (Double.isNaN(average_sentiment)) {
        message = "There are not enough classified objects to get an average. Please use the 'Classify' method first.";
    }
        else {
        message = "Average Watson NLU data for the specified date:" + newLine + newLine + "Average sentiment: " + df.format(average_sentiment) + newLine + "Average sadness: " + df.format(average_sadness) + newLine + "Average joy: " + df.format(average_joy) + newLine + "Average fear: " + df.format(average_fear) + newLine + "Average disgust: " + df.format(average_disgust) + newLine + "Average anger: " + df.format(average_anger) + newLine + newLine + "What does this mean?" + newLine + "Sentiment: The value is always between -1 and 1 and stands for a positive (0 to 1) or negative (-1 to 0) sentiment in varying intensity." + newLine + "Emotions:  The number stands for the proportion of the emotion. The sum of all emotions is 1.";
    }
        return message;
    }

    // Methode zum Speichern neuer Daten, um den gab Corpus zu importieren
    public static void insertData(int id, String text, java.util.Date date , int likes, int dislikes, int gabscore, boolean nsfw, int user_id){
        try {
            PreparedStatement st = con.prepareStatement("INSERT INTO " + dbTable + " (post_id, text, date, likes, dislikes, gab_score, nsfw, userid) VALUES (?,?,?,?,?,?,?,?);");
            st.setInt(1, id);
            st.setString(2, text);
            st.setTimestamp(3, new Timestamp(date.getTime()));
            st.setInt(4, likes);
            st.setInt(5, dislikes);
            st.setInt(6, gabscore);
            st.setBoolean(7,nsfw);
            st.setInt(8, user_id);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }

    // Methode zum updaten vorhandener Einträge der Datenbank, um die Watson-Scores hinzuzufügen
    public void updateData(int id, String sentiment, double sentiment_score, double sadness, double joy, double fear, double disgust, double anger){
        try {
            PreparedStatement st = con.prepareStatement("UPDATE " + dbTable + " SET watson_sentiment = ?, watson_sentiment_score = ?, watson_sadness = ?, watson_joy = ?, watson_fear = ?, watson_disgust = ?, watson_anger = ? WHERE post_id = ?;");
            st.setString(1, sentiment);
            st.setDouble(2, sentiment_score);
            st.setDouble(3, sadness);
            st.setDouble(4, joy);
            st.setDouble(5, fear);
            st.setDouble(6, disgust);
            st.setDouble(7, anger);
            st.setInt(8, id);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        MySQLconnect con = new MySQLconnect();
        System.out.println(getWatson("2016-10-10"));
    }

    // Schließt die Verbindung zur Datenbank
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}