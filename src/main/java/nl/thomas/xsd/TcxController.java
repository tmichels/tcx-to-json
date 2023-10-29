package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/convert")
public class TcxController {

    private final Converter converter;

    public TcxController(Converter converter) {
        this.converter = converter;
    }

    @PostMapping("file")
    public TrainingCenterDatabaseT file(@RequestBody String location) throws IOException, JAXBException {
        List<String> lines = Files.readAllLines(Path.of(location));
        String fileContent = String.join("\n", lines);
        return converter.convert(fileContent);
    }

    @PostMapping("file-content")
    public TrainingCenterDatabaseT getFromFileContent(@RequestBody String fileContent) throws JAXBException {
        return converter.convert(fileContent);
    }
}
