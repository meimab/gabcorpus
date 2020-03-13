package uhh_lt.datenbank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Diese Klasse stellt eine Verbindung zu MySQL her und kann Daten in die Datenbank speichern, bestehende Zeilen aktualisieren und Daten auslesen.
 */
public class MySQLconnect{
    private static Connection con = null;
    private static String dbHost;	// Hostname
    private static String dbPort = "3306";		// Port -- Standard: 3306
    private static String dbName;	// Datenbankname
    private static String dbUser;;		// Datenbankuser
    private static String dbPass;		// Datenbankpasswort
    private static String dbTable = "posts";		// Posts Tabelle

    /**
     * Stellt eine Verbindung zur Datenbank her.
     */
    public MySQLconnect() {
        // get credentials
        InputStream is = MySQLconnect.class.getResourceAsStream("/credentials.txt");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        try {
            String line = reader.readLine();
            while (line != null) {
                String[] fields = line.split("=");
                if (fields[0].compareTo("mysql.user") == 0) {
                    dbUser = fields[1];
                }
                if (fields[0].compareTo("mysql.password") == 0) {
                    dbPass = fields[1];
                }
                if (fields[0].compareTo("mysql.host") == 0) {
                    dbHost = fields[1];
                }
                if (fields[0].compareTo("mysql.database") == 0) {
                    dbName = fields[1];
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Abfrage hat nicht funktioniert");
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

    /**
     * Schließt die Verbindung zur Datenbank
     */
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("Verbindung konnte nicht geschlossen werden");
        }
    }

    /**
     * Methode, die den Text von 10 noch nicht von Watson klassifizierten Posts eines gewählten Datums aus der Datenbank liest und zusammen mit der zugehörigen ID in eine Hashmap speichert. Wird von classify und classifyNoDate in Verarbeitung benutzt, um Watson-Daten in die Datenbank zu speichern.
     * @param datum String: Datum im Format YYYY-MM-DD
     * @return results HashMap: Kombination von ids und Text aus Posts
     */
    public static HashMap<Integer, String> getText(String datum)  {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = formatter.parse(datum);
        } catch (ParseException e) {
            System.out.println("Datum konnte nicht geparst werden");
        }
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            System.out.println("Statement konnte nicht erstellt werden");
        }
        java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
        String sql = ("SELECT text, post_id FROM "+dbTable+" WHERE date(date) between '" +sqlDate+ "' and date_add('"+sqlDate+" 00:00:00',interval 12 hour) AND watson_sentiment IS NULL ORDER BY RAND() LIMIT 10;");
        ResultSet rs = null;
        HashMap<Integer, String> results = new HashMap<>();
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Anfrage konnte nicht ausgeführt werden");;
        }
        try {
            while(rs.next()) {
                int id = rs.getInt("post_id");
                String post_text = rs.getString("text");
                results.put(id, post_text);
            }
        } catch (SQLException e) {
            System.out.println("Auf ResultSet konnte nicht zugegriffen werden");
        }
        return results;
    }

    /**
     * Gibt die id, den Text und die Watson-Werte von 3 Posts aus
     * @param datum String: Datum im Format YYYY-MM-DD
     * @return
     */
    public static ResultSet getExamplePosts(String datum)  {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = formatter.parse(datum);
        } catch (ParseException e) {
            System.out.println("Datum konnte nicht geparst werden");
        }
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            System.out.println("Statement konnte nicht erstellt werden");
        }
        java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
        String sql = ("SELECT text, post_id, watson_sentiment, watson_sentiment_score, watson_sadness, watson_joy, watson_fear, watson_disgust, watson_anger FROM "+dbTable+" WHERE date(date) between '" +sqlDate+ "' and date_add('"+sqlDate+" 00:00:00',interval 12 hour) AND watson_sentiment IS NOT NULL LIMIT 3;");
        ResultSet rs = null;
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Anfrage konnte nicht ausgeführt werden");;
        }

        return rs;
    }

    /**
     * Methode, die den Text von 10 zufälligen noch nicht von Watson klassifizierten Posts aus der Datenbank liest und zusammen mit der zugehörigen ID in eine Hashmap speichert. Wird von classify und classifyNoDate in Verarbeitung benutzt, um Watson-Daten in die Datenbank zu speichern.
     * @return results HashMap: Kombination von ids und Text aus Posts
     */
    public static HashMap<Integer, String> getTextNoDate()  {
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            System.out.println("Statement konnte nicht erstellt werden");
        }
        String sql = ("SELECT text, post_id FROM "+dbTable+" WHERE post_id in (SELECT post_id FROM (SELECT post_id FROM "+dbTable+" ORDER BY RAND() LIMIT 10) t) AND watson_sentiment IS NULL LIMIT 10;");
        ResultSet rs = null;
        HashMap<Integer, String> results = new HashMap<>();
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Anfrage konnte nicht ausgeführt werden");;
        }
        try {
            while(rs.next()) {
                int id = rs.getInt("post_id");
                String post_text = rs.getString("text");
                System.out.println(id);
                results.put(id, post_text);
            }
        } catch (SQLException e) {
            System.out.println("Auf ResultSet konnte nicht zugegriffen werden");
        }
        return results;
    }

    /**
     * Liest Watson Daten aus der Datenbank aus und errechnet Durchschnittswerte für den Sentiment Score und die Emotions.
     * @param datum String: Datum im Format YYYY-MM-DD
     * @return ein double Array mit den Watson Werten in der Reihenfolge Sentiment, Sadness, Joy, Fear, Disgust, Anger
     */
    public double[] getWatson(String datum) {
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
            System.out.println("Datum konnte nicht geparst werden");
        }
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            System.out.println("Statement konnte nicht erstellt werden");
        }
        java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
        String sql = ("SELECT watson_sentiment_score, watson_sadness, watson_joy, watson_fear, watson_disgust, watson_anger FROM " + dbTable + " WHERE date(date) between '" + sqlDate + "' and date_add('" + sqlDate + " 00:00:00',interval 12 hour) AND watson_sentiment IS NOT NULL;");
        ResultSet rs = null;
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Anfrage konnte nicht ausgeführt werden");;
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
            System.out.println("Auf ResultSet konnte nicht zugegriffen werden");
        }
        double[] results = {average_sentiment, average_sadness, average_joy, average_fear, average_disgust, average_anger};

        return results;
    }

    /**
     * Speichert Daten in die MySQL-Datenbank. Hauptsächlicher Use-Case ist der Import vom Gab-Corpus.
     * @param id int: post_id der Datenbank
     * @param text String: Text vom Post
     * @param date String: Datum im Format YYYY-MM-DD
     * @param likes int: Anzahl der Likes
     * @param dislikes int: Anzahl der Dislikes
     * @param gabscore int: Gab-eigener Score, kann negativ sein
     * @param nsfw boolean: Zeigt an, ob ein Post nsfw ist
     * @param user_id int: id des Users
     */
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

    /**
     * Aktualisiert bestehende Einträge der Datenbank mit den Watson-Werten. Wird von classify und classifyNoDate in Verarbeitung benutzt, um Watson-Daten in die Datenbank zu speichern.
     * @param id int: post_id der Datenbank
     * @param sentiment String: positive, negative, neutral
     * @param sentiment_score double: -1 bis 1
     * @param sadness double 0 bis 1
     * @param joy double 0 bis 1
     * @param fear double 0 bis 1
     * @param disgust double 0 bis 1
     * @param anger double 0 bis 1, Summe aus sadness, joy, fear, disgust & anger ist 1
     */
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

}