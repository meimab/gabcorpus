package uhh_lt.datenverarbeitung;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Diese Klasse stellt eine Verbindung zum Watson NLU-Service her und analysiert Text.
 */
public class Watson {

    public static String key = "";
    public static String serviceUrl = "";

    /**
     * Lässt Text aus Posts von Watson analysieren und gibt das Ergebnis als AnalysisResponse aus. Wird von classify und classifyNoDate in Verarbeitung benutzt, um Watson-Daten in die Datenbank zu speichern.
     * @param text String: Text vom Post
     * @return response AnalysisResults: Watson-Daten, auf die in Klasse Verarbeitung zugegriffen wird
     */
    public static AnalysisResults klassifizierer(String text){

        InputStream is = Watson.class.getResourceAsStream("/credentials.txt");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        try {

            String line = reader.readLine();
            while (line != null) {
                String[] fields = line.split("=");
                if (fields[0].compareTo("watson.key") == 0) {
                    key = fields[1];
                }
                if (fields[0].compareTo("watson.url") == 0) {
                    serviceUrl = fields[1];
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Zeilen konnte nicht ausgelesen werden");
        }

        IamAuthenticator authenticator = new IamAuthenticator(key);
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

        AnalysisResults response = service
                .analyze(analyzeOptions)
                .execute().getResult();

        return response;
    }

}