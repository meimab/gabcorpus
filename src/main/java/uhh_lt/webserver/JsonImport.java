package uhh_lt.webserver;

import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uhh_lt.datenbank.MySQLconnect;
import uhh_lt.datenverarbeitung.Watson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;



public class JsonImport
{
    public static void main(String[] args) throws Exception
    {
        importCorpus("resources/split/gab-splitaa");
    }

    // Methode zum Importieren des gab Corpus in die Datenbank "posts"
    public static void importCorpus(String file) throws Exception{
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"); //2016-08-10T07:59:06+00:00
        MySQLconnect con = new MySQLconnect();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    file));
            String line = reader.readLine();
            while (line != null) {
                JSONObject jtest = new JSONObject(line);
                // System.out.println(jtest.getInt("id"));
                Date myDate = formatter.parse(jtest.getString("created_at"));
                con.insertData(jtest.getInt("id"), jtest.getString("body"), myDate, jtest.getInt("like_count"), jtest.getInt("dislike_count"), jtest.getInt("score"), jtest.getBoolean("nsfw"), jtest.getJSONObject("user").getInt("id"));

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        con.close();
    }

    //Methode, die den Text von Posts an Watson weiterleitet, die Watson-Daten aufbereitet und mithilfe von updateData aus MySQLconnect in die Datenbank speichert
    public static void importWatson(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "resources/text_id.txt"));
            String line = reader.readLine();
            while (line != null) {
                String text = line;
                int id = Integer.parseInt(reader.readLine());
                AnalysisResults results = Watson.klassifizierer(text);

                        MySQLconnect con = new MySQLconnect();
                // parsing file "JSONExample.json"
                Object obj = null;
                try {
                    obj = new JSONParser().parse(new FileReader("watson.json"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // typecasting obj to JSONObject
                JSONObject jo = (JSONObject) obj;

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 con.updateData(id, jtest.getString("sentiment"), jtest.getDouble("sentiment"), jtest.getDouble("sadness"), jtest.getDouble("joy"), jtest.getDouble("fear"), jtest.getDouble("disgust"), jtest.getDouble("anger"));
 **/