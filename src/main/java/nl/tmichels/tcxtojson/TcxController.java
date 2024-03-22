package nl.tmichels.tcxtojson;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import nl.tmichels.tcxtojson.simplifiedmodel.Run;
import nl.tmichels.tcxtojson.tcdbtosimplifiedmodel.TcdbRunConverter;
import nl.tmichels.tcxtojson.tcxtotcdb.TcxParser;
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description =
            "A string with the file path to a tcx file. Absolute, or relative to the " +
                    "application such as src/test/java/testfiles/export_tomtom.tcx. Configure your volumes when run in " +
                    "Docker and use path available in Docker container.")
    public TrainingCenterDatabaseT pathToTcdb(@RequestBody String path) throws IOException, JAXBException {
        Path absolutePath = Path.of(path).toAbsolutePath();
        log.info("Received POST request to read {}", absolutePath);
        String fileContent = readFileContent(absolutePath);
        return parseContentToTcdb(fileContent);
    }

    @PostMapping("/translation/file-content")
    @Operation(summary = "Get literal translation of XML to JSON (same structure as XML)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The complete content of a tcx file.")
    public TrainingCenterDatabaseT fileContentToTcdb(@RequestBody String fileContent) throws JAXBException {
        log.info("Received POST request to read text with {} characters", fileContent.length());
        return parseContentToTcdb(fileContent);
    }

    @PostMapping("/simplified/path")
    @Operation(summary = "Get a simplified (opinionated) model of a TCX activity.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description =
            "A string with the file path to a tcx file. Absolute, or relative to the " +
                    "application such as src/test/java/testfiles/export_tomtom.tcx. Configure your volumes when run in " +
                    "Docker and use path available in Docker container.")
    public List<Run> pathToSimplified(@RequestBody String path) throws IOException, JAXBException {
        Path absolutePath = Path.of(path).toAbsolutePath();
        log.info("Received POST request to read {}", absolutePath);
        String fileContent = readFileContent(absolutePath);
        TrainingCenterDatabaseT tcdb = parseContentToTcdb(fileContent);
        return convertTcdb(tcdb);
    }

    @PostMapping("/simplified/file-content")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The complete content of a tcx file.")
    @Operation(summary = "Get a simplified (opinionated) model of a TCX activity.")
    public List<Run> contentToSimplified(@RequestBody String fileContent) throws JAXBException {
        log.info("Received POST request to read text with {} characters", fileContent.length());
        TrainingCenterDatabaseT tcdb = parseContentToTcdb(fileContent);
        return convertTcdb(tcdb);
    }

    private String readFileContent(Path absolutePath) throws IOException {
        List<String> lines = Files.readAllLines(absolutePath);
        return String.join("", lines);
    }

    private TrainingCenterDatabaseT parseContentToTcdb(String fileContent) throws JAXBException {
        return tcxParser.parse(fileContent);
    }

    private List<Run> convertTcdb(TrainingCenterDatabaseT tcdb) {
        return tcdbRunConverter.convert(tcdb);
    }
}
