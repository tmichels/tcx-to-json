package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Run;
import nl.thomas.xsd.tcdbtomodel.TcdbRunConverter;
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
    private final TcdbRunConverter tcdbRunConverter;

    public TcxController(TcxParser tcxParser, TcdbRunConverter tcdbRunConverter) {
        this.tcxParser = tcxParser;
        this.tcdbRunConverter = tcdbRunConverter;
    }

    @PostMapping("/translation/path")
    @Operation(summary = "Get literal translation of XML to JSON (same structure as XML)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A string with the file location of a tcx file. Absolute, or relative to the application such as src/test/java/testfiles/export_tomtom.tcx")
    public TrainingCenterDatabaseT fileForTcdb(@RequestBody String location) throws IOException, JAXBException {
        log.info("GET request to read {}", location);
        String fileContent = readFileContent(location);
        return parseContentToTcdb(fileContent);
    }

    @PostMapping("/translation/file-content")
    @Operation(summary = "Get literal translation of XML to JSON (same structure as XML)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The complete content of a tcx file.")
    public TrainingCenterDatabaseT getFromFileContentForTcdb(@RequestBody String fileContent) throws JAXBException {
        log.info("GET request to read text with {} characters", fileContent.length());
        return parseContentToTcdb(fileContent);
    }

    @PostMapping("/simplified/path")
    @Operation(summary = "Get a simplified (opinionated) model of a TCX activity.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A string with the file location of a tcx file. Absolute, or relative to the application such as src/test/java/testfiles/export_tomtom.tcx")
    public List<Run> fileForModel(@RequestBody String location) throws IOException, JAXBException {
        log.info("GET request to read {}", location);
        String fileContent = readFileContent(location);
        TrainingCenterDatabaseT tcdb = parseContentToTcdb(fileContent);
        return convertTcdb(tcdb);
    }

    @PostMapping("/simplified/file-content")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The complete content of a tcx file.")
    @Operation(summary = "Get a simplified (opinionated) model of a TCX activity.")
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
        return tcdbRunConverter.convert(tcdb);
    }
}
