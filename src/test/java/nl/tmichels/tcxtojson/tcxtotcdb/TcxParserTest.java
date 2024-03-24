package nl.tmichels.tcxtojson.tcxtotcdb;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcxParserTest {

    @InjectMocks
    TcxParser tcxParser;

    @Test
    void invalidContent_parse_jaxbException() throws IOException {
        String fileContent = getFileContent("src/test/java/testfiles/invalidxml.txt");

        assertThatThrownBy(() -> tcxParser.parse(fileContent))
                .isInstanceOf(JAXBException.class)
                .hasMessage(null)
                .hasCauseInstanceOf(SAXParseException.class)
                .hasRootCauseMessage("Content is not allowed in prolog.");
    }

    @ParameterizedTest
    @MethodSource("getTestFiles")
    void fileContent_parse_object(String file, int expected, String firstTrackpoinTime, CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent(file);

        TrainingCenterDatabaseT trainingCenterDatabaseT = tcxParser.parse(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT))
                .isEqualTo(ZonedDateTime.parse(firstTrackpoinTime));
        assertThat(capturedOutput.getOut()).contains(String.format(
                "Parsed text as TrainingCenterDatabaseT object with %s trackpoints", expected));
    }

    @Test
    void file_parse_correctorsCalled() throws JAXBException, IOException {
        String fileContent = getFileContent("src/test/java/testfiles/export_tomtom.tcx");
        try ( // mockStatics in try with resources because if not closed they will impact other tests.
              MockedStatic<TomTomCorrector> tomTomCorrectorMock = mockStatic(TomTomCorrector.class);
              MockedStatic<Ttbin2TcxCorrector> ttbin2tcxCorrectorMock = mockStatic(Ttbin2TcxCorrector.class);
              MockedStatic<StravaCorrector> stravaCorrectorMock = mockStatic(StravaCorrector.class)) {
            when(TomTomCorrector.correct(fileContent)).thenReturn(fileContent);
            when(Ttbin2TcxCorrector.correct(fileContent)).thenReturn(fileContent);
            when(StravaCorrector.correct(fileContent)).thenReturn(fileContent);

            tcxParser.parse(fileContent);

            tomTomCorrectorMock.verify(() -> TomTomCorrector.correct(fileContent));
            ttbin2tcxCorrectorMock.verify(() -> Ttbin2TcxCorrector.correct(fileContent));
            stravaCorrectorMock.verify(() -> StravaCorrector.correct(fileContent));
        }
    }

    private static Stream<Arguments> getTestFiles() {
        return Stream.of(
                Arguments.of("src/test/java/testfiles/export_garmin.tcx", 2133, "2024-02-11T10:00:11Z"),
                Arguments.of("src/test/java/testfiles/export_strava.tcx", 9638, "2024-02-11T10:00:04Z"),
                Arguments.of("src/test/java/testfiles/export_tomtom.tcx", 2868, "2023-10-01T10:06:53Z"),
                Arguments.of("src/test/java/testfiles/export_smashrun.tcx", 2860, "2023-04-25T17:45:00.000Z"),
                Arguments.of("src/test/java/testfiles/export_ttbin2tcx.tcx", 1354, "2024-03-03T06:56:27Z"),
                Arguments.of("src/test/java/testfiles/export_garmin_invalid_extensions.tcx", 637, "2023-11-02T05:15:29.000Z")
        );
    }

    private static String getFileContent(String first) throws IOException {
        Path path = Path.of(first);
        return String.join("\n", Files.readAllLines(path));
    }

    private static ZonedDateTime getTimeFirstTrackpoint(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        return trainingCenterDatabaseT
                .getActivities()
                .getActivity()
                .getFirst()
                .getLap()
                .getFirst()
                .getTrack()
                .getFirst()
                .getTrackpoint()
                .getFirst()
                .getTime() // The class generated on the basis of the SXD is XMLGregorianCalendar
                .toGregorianCalendar()
                .toZonedDateTime();
    }
}