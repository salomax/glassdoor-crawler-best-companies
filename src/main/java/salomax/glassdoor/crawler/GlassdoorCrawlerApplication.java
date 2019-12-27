package salomax.glassdoor.crawler;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by marcos.salomao.
 */
@SpringBootApplication
@Log
public class GlassdoorCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlassdoorCrawlerApplication.class, args).close();
        System.exit(0);
    }


}
