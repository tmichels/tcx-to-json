package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Run;
import nl.thomas.xsd.tcdbtomodel.TcdbConverter;
import nl.thomas.xsd.tcxtotcdb.TcxParser;
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

    private final TcxParser tcxParser;
    private final TcdbConverter tcdbConverter;

    public TcxController(TcxParser tcxParser, TcdbConverter tcdbConverter) {
        this.tcxParser = tcxParser;
        this.tcdbConverter = tcdbConverter;
    }

    @PostMapping("/raw/file")
    public TrainingCenterDatabaseT fileForTcdb(@RequestBody String location) throws IOException, JAXBException {
        log.info("GET request to read {}", location);
        String fileContent = readFileContent(location);
        return parseContentToTcdb(fileContent);
    }

    @PostMapping("/raw/file-content")
    public TrainingCenterDatabaseT getFromFileContentForTcdb(@RequestBody String fileContent) throws JAXBException {
        log.info("GET request to read text with {} characters", fileContent.length());
        return parseContentToTcdb(fileContent);
    }

    @PostMapping("/model/file")
    public List<Run> fileForModel(@RequestBody String location) throws IOException, JAXBException {
        log.info("GET request to read {}", location);
        String fileContent = readFileContent(location);
        TrainingCenterDatabaseT tcdb = parseContentToTcdb(fileContent);
        return convertTcdb(tcdb);
    }

    @PostMapping("/model/file-content")
    public List<Run> getFromFileContentForModel(@RequestBody String fileContent) throws JAXBException {
        log.info("GET request to read text with {} characters", fileContent.length());
        TrainingCenterDatabaseT tcdb = parseContentToTcdb(fileContent);
        return convertTcdb(tcdb);
    }

    private String readFileContent(String location) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(location));
        return String.join("", lines);
    }

    private TrainingCenterDatabaseT parseContentToTcdb(String fileContent) throws JAXBException {
        return tcxParser.parse(fileContent);
    }

    private List<Run> convertTcdb(TrainingCenterDatabaseT tcdb) {
        return tcdbConverter.convert(tcdb);
    }
}
