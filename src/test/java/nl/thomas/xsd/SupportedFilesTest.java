package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Run;
import nl.thomas.xsd.tcdbtomodel.TcdbRunConverter;
import nl.thomas.xsd.tcxtotcdb.TcxParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SupportedFilesTest {

    @Autowired
    TcxParser tcxParser;
    @Autowired
    TcdbRunConverter tcdbRunConverter;

    @ParameterizedTest
    @MethodSource("getTestFiles")
    void files_parseAndConvert_noExceptions(String testFile, long expectedNrOfTrackpoints) throws IOException, JAXBException {
        String lines = String.join("", Files.readAllLines(Path.of(testFile)));
        TrainingCenterDatabaseT tcdb = tcxParser.parse(lines);
        List<Run> run = tcdbRunConverter.convert(tcdb);

        long count = run.getFirst().getLaps().stream().flatMap(l -> l.getTrackpoints().stream()).count();

        assertThat(count).isEqualTo(expectedNrOfTrackpoints);
    }


    public static Stream<Arguments> getTestFiles() {
        return Stream.of(
            Arguments.of("src/test/java/testfiles/export_garmin.tcx", 637),
            Arguments.of("src/test/java/testfiles/export_strava.tcx",9638),
            Arguments.of("src/test/java/testfiles/export_tomtom.tcx",2868),
            Arguments.of("src/test/java/testfiles/export_ttbin2tcx.tcx",1354),
            Arguments.of("src/test/java/testfiles/export_garmin_invalid_extensions.tcx",637),
            Arguments.of("src/test/java/testfiles/export_garmin_track_deleted.tcx",493)
        );
    }
}