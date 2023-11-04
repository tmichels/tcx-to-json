package nl.thomas.xsd;

import jakarta.xml.bind.JAXBException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@SpringBootApplication
public class XsdApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XsdApplication.class)
                .run(args);
    }

    @Component
    static class DevCommandLineRunner implements CommandLineRunner {

        private final TcxController tcxController;

        DevCommandLineRunner(TcxController tcxController) {
            this.tcxController = tcxController;
        }


        @Override
        public void run(String... args) throws JAXBException, IOException {
            tcxController.file("src/test/java/testfiles/tomtom_export.tcx");
        }
    }

}
