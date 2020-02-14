package uhh_lt.datenverarbeitung;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;
import uhh_lt.datenbank.MySQLconnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Watson {

    public static String key = "";
    public static String serviceUrl = "";

    public static void main(String[] args) {
        klassifizierer("Hello Gab! This is my first post!");
    }

    // Methode, die Text aus Posts von Watson analysieren lässt und als AnalysisResponse ausgibt
    public static AnalysisResults klassifizierer(String text){

        InputStream is = Watson.class.getResourceAsStream("/credentials.txt");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        try {

            String line = reader.readLine();
            while (line != null) {
                String[] fields = line.split("=");
                if (fields[0].compareTo("mysql.user") == 0) {
                    key = fields[1];
                }
                if (fields[0].compareTo("mysql.password") == 0) {
                    serviceUrl = fields[1];
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        IamAuthenticator authenticator = new IamAuthenticator(key);
        //Creating the class of the service. Make sure to insert you service username and password.
        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                "2019-07-11", authenticator);

        service.setServiceUrl(serviceUrl);

        EmotionOptions emotionOptions = new EmotionOptions.Builder().build();
        SentimentOptions sentimentOptions = new SentimentOptions.Builder().build();

        Features features = new Features.Builder()
                .emotion(emotionOptions)
                .sentiment(sentimentOptions)
                .build();

        AnalyzeOptions analyzeOptions = new AnalyzeOptions.Builder().text(text).features(features).build();

        //Take the parameters and send them to your service for results.
        AnalysisResults response = service
                .analyze(analyzeOptions)
                .execute().getResult();

        return response;
    }

}