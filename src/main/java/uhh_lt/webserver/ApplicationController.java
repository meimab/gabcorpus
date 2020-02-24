package uhh_lt.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uhh_lt.datenbank.MySQLconnect;
import uhh_lt.datenverarbeitung.Verarbeitung;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;


@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class ApplicationController extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationController.class);
    }

    /**
     * Runs the RESTful server.
     *
     * @param args execution arguments
     */
    public static void main(String[] args) {

        SpringApplication.run(ApplicationController.class, args);
    }


    @GetMapping("/")
    String home()
    {
        return "home";
    }


    @RequestMapping("/analysis")
    public String analysis(@RequestParam(value = "date", defaultValue = "") String date, Model model, Model modelPosts)
    {
        MySQLconnect con = new MySQLconnect();
        DecimalFormat df = new DecimalFormat("0.000");
        double[] watsonData = con.getWatson(date);
        model.addAttribute("sentiment", df.format(watsonData[0]));
        model.addAttribute("sadness", df.format(watsonData[1]));
        model.addAttribute("joy", df.format(watsonData[2]));
        model.addAttribute("fear", df.format(watsonData[3]));
        model.addAttribute("disgust", df.format(watsonData[4]));
        model.addAttribute("anger", df.format(watsonData[5]));

        String data = "var datain = [\n" +
                "          ['Emotion', 'Ratio'],\n" +
                "          ['sadness',  "+ watsonData[1] +"],\n" +
                "          ['anger',     "+ watsonData[5] +"],\n" +
                "          ['disgust', "+ watsonData[4] +"],\n" +
                "          ['joy',     "+ watsonData[2] +"],\n" +
                "          ['fear',   "+ watsonData[3] +"]\n" +
                "        ];\n";

        model.addAttribute("data", data);

        ResultSet posts = con.getExamplePosts(date);
        try {
            posts.next();
        } catch (SQLException e) {
            System.out.println("Konnte nicht auf das ResultSet zugreifen");
        }
        try {
            String text1 = posts.getString("text");
            modelPosts.addAttribute("text1", text1);
            double sentiment1 = posts.getDouble("watson_sentiment_score");
            modelPosts.addAttribute("sentiment1", df.format(sentiment1));
            double joy1 = posts.getDouble("watson_joy");
            modelPosts.addAttribute("joy1", df.format(joy1));
            double fear1 = posts.getDouble("watson_fear");
            modelPosts.addAttribute("fear1", df.format(fear1));
            double sadness1 = posts.getDouble("watson_sadness");
            modelPosts.addAttribute("sadness1", df.format(sadness1));
            double disgust1 = posts.getDouble("watson_disgust");
            modelPosts.addAttribute("disgust1", df.format(disgust1));
            double anger1 = posts.getDouble("watson_anger");
            modelPosts.addAttribute("anger1", df.format(anger1));
        } catch (SQLException e) {
            System.out.println("Konnte auf Post nicht zugreifen");
        }
        try {
            posts.next();
        } catch (SQLException e) {
            System.out.println("Konnte nicht auf das ResultSet zugreifen");
        }
        try {
            String text2 = posts.getString("text");
            modelPosts.addAttribute("text2", text2);
            double sentiment2 = posts.getDouble("watson_sentiment_score");
            modelPosts.addAttribute("sentiment2", df.format(sentiment2));
            double joy2 = posts.getDouble("watson_joy");
            modelPosts.addAttribute("joy2", df.format(joy2));
            double fear2 = posts.getDouble("watson_fear");
            modelPosts.addAttribute("fear2", df.format(fear2));
            double sadness2 = posts.getDouble("watson_sadness");
            modelPosts.addAttribute("sadness2", df.format(sadness2));
            double disgust2 = posts.getDouble("watson_disgust");
            modelPosts.addAttribute("disgust2", df.format(disgust2));
            double anger2 = posts.getDouble("watson_anger");
            modelPosts.addAttribute("anger2", df.format(anger2));
        } catch (SQLException e) {
            System.out.println("Konnte auf Post nicht zugreifen");
        }
        try {
            posts.next();
        } catch (SQLException e) {
            System.out.println("Konnte nicht auf das ResultSet zugreifen");
        }
        try {
            String text3 = posts.getString("text");
            modelPosts.addAttribute("text3", text3);
            double sentiment3 = posts.getDouble("watson_sentiment_score");
            modelPosts.addAttribute("sentiment3", df.format(sentiment3));
            double joy3 = posts.getDouble("watson_joy");
            modelPosts.addAttribute("joy3", df.format(joy3));
            double fear3 = posts.getDouble("watson_fear");
            modelPosts.addAttribute("fear3", df.format(fear3));
            double sadness3 = posts.getDouble("watson_sadness");
            modelPosts.addAttribute("sadness3", df.format(sadness3));
            double disgust3 = posts.getDouble("watson_disgust");
            modelPosts.addAttribute("disgust3", df.format(disgust3));
            double anger3 = posts.getDouble("watson_anger");
            modelPosts.addAttribute("anger3", df.format(anger3));
        } catch (SQLException e) {
            System.out.println("Konnte auf Post nicht zugreifen");
        }

        return "analysis";
    }

    @RequestMapping("/randomClassify")
    public String randomClassify()
    {
        Verarbeitung ver = new Verarbeitung();
        ver.classifyNoDate();
        return "redirect:/";
    }


    @RequestMapping("/classify")
    public String classify(@RequestParam(value = "date", defaultValue = "") String date)
    {
        Verarbeitung ver = new Verarbeitung();
        ver.classify(date);
        return "redirect:/analysis?date="+date;
    }


    @RequestMapping("/info")
    public String info()
    {
        return "info";
    }


}