package de.image.extractor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"de.image.extractor", "de.image.extractor.service"})
public class ExtractorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExtractorApplication.class, args);
    }

}
