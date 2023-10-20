package nl.thomas.xsd;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
public class TcxController {

    @PostMapping("/get-file")
    public List<String> get(@RequestBody String location) throws IOException {
        List<String> strings = Files.readAllLines(Path.of(location));
        return strings;
    }
}
