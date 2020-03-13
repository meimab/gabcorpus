package uhh_lt.datenverarbeitung;

import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import uhh_lt.datenbank.MySQLconnect;
import java.util.HashMap;

/**
 * Diese Klasse verwendet Methoden aus Watson und MySQLconnect, um bestehende Einträge der Datenbank um Watson-Werte zu erweitern..
 */
public class Verarbeitung {

    /**
     * Benutzt Watson und MySQLconnect, um Watson-Daten (Sentiment, Sentiment Score & Emotions) in 10 Einträge der Datenbank eines bestimmten Datums zu speichern.
     * @param date String: Datum im Format YYYY-MM-DD
     */
    public void classify(String date){
        MySQLconnect con = new MySQLconnect();
        HashMap<Integer,String> posts = con.getText(date);

        for (int id: posts.keySet()) {

            try {
                AnalysisResults results = Watson.klassifizierer(posts.get(id));
                String sentiment = results.getSentiment().getDocument().getLabel();
                double sentiment_score = results.getSentiment().getDocument().getScore();
                double sadness = results.getEmotion().getDocument().getEmotion().getSadness();
                double joy = results.getEmotion().getDocument().getEmotion().getJoy();
                double fear = results.getEmotion().getDocument().getEmotion().getFear();
                double disgust = results.getEmotion().getDocument().getEmotion().getDisgust();
                double anger = results.getEmotion().getDocument().getEmotion().getAnger();
                con.updateData(id, sentiment, sentiment_score, sadness, joy, fear, disgust, anger);
            } catch (Exception e) {
                System.err.println(e.getMessage() + "\t" + id);
            }
        }
        con.close();
    }

    /**
     * Benutzt Watson und MySQLconnect, um Watson-Daten (Sentiment, Sentiment Score & Emotions) in 10 zufällige Einträge der Datenbank zu speichern.
     */
    public void classifyNoDate(){
        MySQLconnect con = new MySQLconnect();
        HashMap<Integer,String> posts = con.getTextNoDate();

        for (int id: posts.keySet()) {
            try {
                AnalysisResults results = Watson.klassifizierer(posts.get(id));
                String sentiment = results.getSentiment().getDocument().getLabel();
                double sentiment_score = results.getSentiment().getDocument().getScore();
                double sadness = results.getEmotion().getDocument().getEmotion().getSadness();
                double joy = results.getEmotion().getDocument().getEmotion().getJoy();
                double fear = results.getEmotion().getDocument().getEmotion().getFear();
                double disgust = results.getEmotion().getDocument().getEmotion().getDisgust();
                double anger = results.getEmotion().getDocument().getEmotion().getAnger();
                con.updateData(id, sentiment, sentiment_score, sadness, joy, fear, disgust, anger);
            } catch (Exception e) {
                System.err.println(e.getMessage() + "\t" + id);
            }

        }
        con.close();
    }

}