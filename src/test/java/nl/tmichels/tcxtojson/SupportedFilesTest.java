package nl.tmichels.tcxtojson;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.tmichels.tcxtojson.simplifiedmodel.Run;
import nl.tmichels.tcxtojson.tcdbtosimplifiedmodel.TcdbRunConverter;
import nl.tmichels.tcxtojson.tcxtotcdb.TcxParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SupportedFilesTest {

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

        long count = run.getFirst().laps().stream().mapToLong(l -> l.trackpoints().size()).sum();

        assertThat(count).isEqualTo(expectedNrOfTrackpoints);
    }


    private static Stream<Arguments> getTestFiles() {
        return Stream.of(
                Arguments.of("src/test/java/testfiles/export_garmin.tcx", 2133),
                Arguments.of("src/test/java/testfiles/export_strava.tcx", 9638),
                Arguments.of("src/test/java/testfiles/export_tomtom.tcx", 2868),
                Arguments.of("src/test/java/testfiles/export_smashrun.tcx", 2860),
                Arguments.of("src/test/java/testfiles/export_ttbin2tcx.tcx", 1354),
                Arguments.of("src/test/java/testfiles/export_garmin_invalid_extensions.tcx", 637),
                Arguments.of("src/test/java/testfiles/export_garmin_track_deleted.tcx", 493)
        );
    }
}
