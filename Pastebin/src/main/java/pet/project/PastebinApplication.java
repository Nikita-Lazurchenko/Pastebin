package pet.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pet.project.service.PasteService;

@SpringBootApplication
public class PastebinApplication {

    public static void main(String[] args) {
        SpringApplication.run(PastebinApplication.class, args);
    }

}
