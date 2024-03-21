package nl.thomas.xsd.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addHeaders("hoi", new Header().required(false)))
                .info(new Info()
                        .title("TCX to Json")
                        .version("0.1")
                        .description(
                                "This application reads .tcx files that comply with the official xsd specifications found " +
                                        "here https://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd, and " +
                                        "returns them as JSON object." +
                                        "<br>" +
                                        "<br>" +
                                        "Input is either the path of a tcx file, or the full content of a tcx file. " +
                                        "Output is either the exact translation of tcx to json, or a simplified and opionated model. " +
                                        "<br>Tested on exports of Strava, Garmin, TomTom, SmashRun and ttbin2tcx (https://github.com/alexspurven/ttbin2tcx), " +
                                        "but should work on all TCX applications that comply with the xsd.")
                        .contact(new Contact()
                                .name("tmichels")
                                .url("https://github.com/tmichels/backend-xsd-reader")
                        )
                );
    }
}
