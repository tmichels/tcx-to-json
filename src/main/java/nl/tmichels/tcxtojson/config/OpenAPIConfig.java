package nl.tmichels.tcxtojson.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TCX to Json")
                        .version("1.0")
                        .description(
                                "This application reads .tcx files that comply with the official xml specifications found " +
                                        "here https://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd, and " +
                                        "here https://www8.garmin.com/xmlschemas/ActivityExtensionv2.xsd and returns " +
                                        "them as JSON." +
                                        "<br>" +
                                        "<br>" +
                                        "Input is either the path to a tcx file in the file system, or the full content of a tcx file. " +
                                        "Output is either the exact translation of tcx to json, or a simplified (opinionated) model. " +
                                        "<br>Tested on exports of Strava, Garmin, TomTom, SmashRun and ttbin2tcx (https://github.com/alexspurven/ttbin2tcx), " +
                                        "but should work on all TCX applications that comply with the xsd.")
                        .contact(new Contact()
                                .name("tmichels")
                                .url("https://github.com/tmichels/tcx-to-json")
                        )
                );
    }
}
