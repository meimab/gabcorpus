package uhh_lt.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class ApplicationController extends SpringBootServletInitializer {


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
    public String analysis(@RequestParam(value = "date", defaultValue = "") String date, Model model)
    {
        String description = "from analysis";

        String newLine = System.getProperty("line.separator");
        description.replaceAll(newLine, "</br />");
        double sentscore = 0.3;
        model.addAttribute("sentimentscore", date);
        return "analysis";
    }


    @RequestMapping("/classify")
    public String classify(@RequestParam(value = "date", defaultValue = "") String date)
    {

        //get date
        // classify by date

        // redirect to analysis

        return "redirect:/analysis?date="+date;
    }


}