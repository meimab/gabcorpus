package uhh_lt.webserver;

import org.json.JSONObject;
import uhh_lt.datenbank.MySQLconnect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Diese Klasse ist dazu da, um den Gab-Corpus in die MySQL-Datenbank zu importieren.
 */
public class JsonImport
{
    /**
     * Wird benutzt, um den Gab Corpus mit dem Terminal außerhalb von IntelliJ IDEA zu importieren.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println(args[0]);

        importCorpus(args[0]);
    }

    /**
     * Importiert den Gab-Corpus in die MySQL Datenbank, indem die Datei Zeile für Zeile ausgelesen und analysiert wird.
     * @param file String: der Gab-Corpus
     * @throws Exception
     */
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
                Date myDate = formatter.parse(jtest.getString("created_at"));
                con.insertData(jtest.getInt("id"), jtest.getString("body"), myDate, jtest.getInt("like_count"), jtest.getInt("dislike_count"), jtest.getInt("score"), jtest.getBoolean("nsfw"), jtest.getJSONObject("user").getInt("id"));

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Datei konnte nicht ausgelesen werden");;
        }
        con.close();
    }



}