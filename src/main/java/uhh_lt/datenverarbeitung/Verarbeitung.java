package uhh_lt.datenverarbeitung;

import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import uhh_lt.datenbank.MySQLconnect;
import uhh_lt.webserver.JsonImport;

import java.sql.ResultSet;

public class Verarbeitung {

    // Zeigt den Durchschnittswert der Sentiment Scores aus dem gewählten Zeitraum an (eventuell durch eine Auswahl von n Posts pro Tag und nicht allen).
    public double averageSentiment(){
        return 0;
    }

    public void classify(String date){
        MySQLconnect con = new MySQLconnect();
        //MySQLconnect.getText(date); 10 beispiele· z.B. hashmap

        // foreeach if: hashmap:
            // get Text
            // Analysewithwatson
        AnalysisResults res = new AnalysisResults();
        double  sentscore = res.getSentiment().getDocument().getScore();
            // update DB
        con.close();
    }

}