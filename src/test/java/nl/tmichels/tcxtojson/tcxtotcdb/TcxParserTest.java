package nl.tmichels.tcxtojson.tcxtotcdb;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    void tomTomExport_parse_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent("src/test/java/testfiles/export_tomtom.tcx");

        TrainingCenterDatabaseT trainingCenterDatabaseT = tcxParser.parse(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT))
                .isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:53Z"));
        assertThat(capturedOutput.getOut()).contains(
                "Parsed text as TrainingCenterDatabaseT object with 2868 trackpoints");
    }

    @Test
    void garminExport_parse_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent("src/test/java/testfiles/export_garmin.tcx");

        TrainingCenterDatabaseT trainingCenterDatabaseT = tcxParser.parse(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT))
                .isEqualTo(ZonedDateTime.parse("2023-11-02T05:15:29.000Z"));
        assertThat(capturedOutput.getOut()).contains(
                "Parsed text as TrainingCenterDatabaseT object with 637 trackpoints");
    }

    @Test
    void ttbin2tcxExport_parse_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent("src/test/java/testfiles/export_ttbin2tcx.tcx");

        TrainingCenterDatabaseT trainingCenterDatabaseT = tcxParser.parse(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT)).isEqualTo(
                ZonedDateTime.parse("2024-03-03T06:56:27Z"));
        assertThat(
                capturedOutput.getOut()).contains(
                "Parsed text as TrainingCenterDatabaseT object with 1354 trackpoints");
    }

    @Test
    void file_parse_correctorsCalled() throws JAXBException, IOException {
        String fileContent = getFileContent("src/test/java/testfiles/export_tomtom.tcx");
        try ( // mockStatics in try with resources because if not closed they will impact other tests.
                MockedStatic<TomTomCorrector> tomTomCorrectorMock = mockStatic(TomTomCorrector.class);
                MockedStatic<Ttbin2TcxCorrector> ttbin2tcxCorrectorMock = mockStatic(Ttbin2TcxCorrector.class)) {
            when(TomTomCorrector.correct(fileContent)).thenReturn(fileContent);
            when(Ttbin2TcxCorrector.correct(fileContent)).thenReturn(fileContent);

            tcxParser.parse(fileContent);

            tomTomCorrectorMock.verify(() -> TomTomCorrector.correct(fileContent));
            ttbin2tcxCorrectorMock.verify(() -> Ttbin2TcxCorrector.correct(fileContent));
        }
    }

    private static String getFileContent(String first) throws IOException {
        Path path = Path.of(first);
        return String.join("\n", Files.readAllLines(path));
    }

    private static ZonedDateTime getTimeFirstTrackpoint(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        return trainingCenterDatabaseT
                .getActivities()
                .getActivity()
                .get(0)
                .getLap()
                .get(0)
                .getTrack()
                .get(0)
                .getTrackpoint()
                .get(0)
                .getTime() // The class generated on the basis of the SXD is XMLGregorianCalendar
                .toGregorianCalendar()
                .toZonedDateTime();
    }
}