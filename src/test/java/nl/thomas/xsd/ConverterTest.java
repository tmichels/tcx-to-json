package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ConverterTest {

    @InjectMocks
    Converter converter;
    @Mock
    TrackpointExtensionHandler trackpointExtensionHandler;

    @Test
    void invalidContent_convert_jaxbException() throws IOException {
        Path path = Path.of("src/test/java/testfiles/invalidxml.txt");
        String fileContent = String.join("\n", Files.readAllLines(path));

        assertThatThrownBy(() -> converter.convert(fileContent))
                .isInstanceOf(JAXBException.class);
    }

    @Test
    void validContent_convert_object(CapturedOutput capturedOutput) throws IOException, JAXBException {
        Path path = Path.of("src/test/java/testfiles/valid.tcx");
        String fileContent = String.join("", Files.readAllLines(path));

        TrainingCenterDatabaseT trainingCenterDatabaseT = converter.convert(fileContent);

        assertThat(
                trainingCenterDatabaseT
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
                        .toZonedDateTime())
                .isEqualTo(
                        ZonedDateTime.parse("2023-10-01T10:06:53Z"));
        assertThat(capturedOutput.getOut()).contains(
                "Converted text to TrainingCenterDatabaseT object with 2868 trackpoints");
    }
}