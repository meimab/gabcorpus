package uhh_lt.datenverarbeitung;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;

public class Watson {

    public static String key = "";
    public static String serviceUrl = "";

    public static void main(String[] args) {
        klassifizierer("Hello Gab! This is my first post!");
    }

    // Methode, die Text aus Posts von Watson analysieren lässt und als AnalysisResponse ausgibt
    public static AnalysisResults klassifizierer(String text){
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