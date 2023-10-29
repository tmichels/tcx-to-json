package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/convert")
public class TcxController {

    private final Converter converter;

    public TcxController(Converter converter) {
        this.converter = converter;
    }

    @PostMapping("file")
    public TrainingCenterDatabaseT file(@RequestBody String location) throws IOException, JAXBException {
        log.info("GET request to read {}", location);
        List<String> lines = Files.readAllLines(Path.of(location));
        String fileContent = String.join("", lines);
        return converter.convert(fileContent);
    }

    @PostMapping("file-content")
    public TrainingCenterDatabaseT getFromFileContent(@RequestBody String fileContent) throws JAXBException {
        log.info("GET request to read text with {} characters", fileContent.length());
        return converter.convert(fileContent);
    }
}
