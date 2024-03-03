package nl.thomas.xsd;

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
class ConverterTest {

    @InjectMocks
    Converter converter;

    @Test
    void invalidContent_convert_jaxbException() throws IOException {
        String fileContent = getFileContent("src/test/java/testfiles/invalidxml.txt");

        assertThatThrownBy(() -> converter.convert(fileContent))
                .isInstanceOf(JAXBException.class)
                .hasMessage(null)
                .hasCauseInstanceOf(SAXParseException.class)
                .hasRootCauseMessage("Content is not allowed in prolog.");
    }

    @Test
    void tomTomExport_convert_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent("src/test/java/testfiles/export_tomtom.tcx");

        TrainingCenterDatabaseT trainingCenterDatabaseT = converter.convert(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT))
                .isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:53Z"));
        assertThat(capturedOutput.getOut()).contains(
                "Converted text to TrainingCenterDatabaseT object with 2868 trackpoints");
    }

    @Test
    void garminExport_convert_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent("src/test/java/testfiles/export_garmin.tcx");

        TrainingCenterDatabaseT trainingCenterDatabaseT = converter.convert(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT))
                .isEqualTo(ZonedDateTime.parse("2023-11-02T05:15:29.000Z"));
        assertThat(capturedOutput.getOut()).contains(
                "Converted text to TrainingCenterDatabaseT object with 637 trackpoints");
    }

    @Test
    void ttbin2tcxExport_convert_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        String fileContent = getFileContent("src/test/java/testfiles/export_ttbin2tcx.tcx");

        TrainingCenterDatabaseT trainingCenterDatabaseT = converter.convert(fileContent);

        assertThat(
                getTimeFirstTrackpoint(trainingCenterDatabaseT)).isEqualTo(
                ZonedDateTime.parse("2024-03-03T06:56:27Z"));
        assertThat(
                capturedOutput.getOut()).contains(
                "Converted text to TrainingCenterDatabaseT object with 1354 trackpoints");
    }

    @Test
    void file_convert_tomTomCorrectorCalled() throws JAXBException, IOException {
        String fileContent = getFileContent("src/test/java/testfiles/export_tomtom.tcx");
        MockedStatic<TomTomCorrector> correctorMock = mockStatic(TomTomCorrector.class);
        when(TomTomCorrector.correct(fileContent)).thenReturn(fileContent);

        converter.convert(fileContent);

        correctorMock.verify(() -> TomTomCorrector.correct(fileContent));
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