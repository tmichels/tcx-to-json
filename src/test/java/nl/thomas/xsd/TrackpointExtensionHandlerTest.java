package nl.thomas.xsd;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({OutputCaptureExtension.class})
@SpringBootTest
class TrackpointExtensionHandlerTest {

    private final String FILE_LOCATION_OF_FILE_WITH_INVALID_EXTENSIONS = "src/test/java/testfiles/invalid_extensions.tcx";
    private final int TRACKPOINT_WITH_MISSING_EXTENSION = 0;
    private final int TRACKPOINT_WITH_UNEXPECTED_EXTENSION = 1;
    private final int TRACKPOINT_WITH_UNEXPECTED_NODE = 2;
    private final int TRACKPOINT_WITH_MISSING_NODE = 3;
    private final int TRACKPOINT_WITH_ADDITIONAL_NODE = 6;
    private final int TRACKPOINT_WITH_EMPTY_EXTENSION = 4;

    @Autowired
    TrackpointExtensionHandler trackpointExtensionHandler;
    @Autowired
    TcxController tcxController;

    @Test
    void noExtension(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = getTrackpoints().get(TRACKPOINT_WITH_MISSING_EXTENSION);

        assertThat(capturedOutput.getOut()).contains("No extensions found in trackpoint 2023-10-01T10:06:53Z");
        assertThat(trackpointT.getTime().toGregorianCalendar().toZonedDateTime()).isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:53Z"));
        assertThat(trackpointT.getExtensions()).isNull();
    }

    @Test
    void unexpectedExtension(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = getTrackpoints().get(TRACKPOINT_WITH_UNEXPECTED_EXTENSION);

        assertThat(capturedOutput.getOut()).contains("The TrackpointExtension for trackpoint 2023-10-01T10:06:54Z is invalid and cannot be processed: \"<?xml version=\"1.0\" encoding=\"UTF-16\"?><x:OTHER xmlns:x=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Speed xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\">0.0</Speed></x:OTHER>\"");
        assertThat(trackpointT.getTime().toGregorianCalendar().toZonedDateTime()).isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:54Z"));
        assertThat(trackpointT.getExtensions().getAny().get(0).toString()).hasToString("[x:OTHER: null]");
    }

    @Test
    void unexpectedNode(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = getTrackpoints().get(TRACKPOINT_WITH_UNEXPECTED_NODE);

        assertThat(capturedOutput.getOut()).contains("ActivityTrackpointExtension for 2023-10-01T10:06:55Z was correctly parsed, but all values are null. Raw XML: <x:TPX xmlns:x=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\"><NO_SPEED xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\">3.17</NO_SPEED></x:TPX>");
        assertThat(trackpointT.getTime().toGregorianCalendar().toZonedDateTime()).isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:55Z"));
        assertThat(((ActivityTrackpointExtensionT) trackpointT.getExtensions().getAny().get(0)).getSpeed()).isNull();
    }

    @Test
    void missingNode(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = getTrackpoints().get(TRACKPOINT_WITH_MISSING_NODE);

        assertThat(capturedOutput.getOut()).contains("The TrackpointExtension for trackpoint 2023-10-01T10:06:56Z is invalid and cannot be processed: \"<?xml version=\"1.0\" encoding=\"UTF-16\"?><x:TPX xmlns:x=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
        assertThat(trackpointT.getTime().toGregorianCalendar().toZonedDateTime()).isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:56Z"));
        assertThat(trackpointT.getExtensions().getAny()).hasToString("[[x:TPX: null]]");
    }

    @Test
    void additionalNode(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = getTrackpoints().get(TRACKPOINT_WITH_ADDITIONAL_NODE);

        assertThat(capturedOutput.getOut()).doesNotContain("2023-10-01T10:06:59Z");
        assertThat(trackpointT.getTime().toGregorianCalendar().toZonedDateTime()).isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:59Z"));
        assertThat(((ActivityTrackpointExtensionT) trackpointT.getExtensions().getAny().get(0)).getSpeed()).isEqualTo(3.42);
    }

    @Test
    void emptyExtension(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = getTrackpoints().get(TRACKPOINT_WITH_EMPTY_EXTENSION);

        assertThat(capturedOutput.getOut()).contains("No extensions found in trackpoint 2023-10-01T10:06:57Z");
        assertThat(trackpointT.getTime().toGregorianCalendar().toZonedDateTime()).isEqualTo(ZonedDateTime.parse("2023-10-01T10:06:57Z"));
        assertThat(trackpointT.getExtensions().getAny()).isEmpty();
    }

    private List<TrackpointT> getTrackpoints() throws IOException, JAXBException {
        TrainingCenterDatabaseT trainingCenterDatabaseT = tcxController.file(FILE_LOCATION_OF_FILE_WITH_INVALID_EXTENSIONS);
        return trainingCenterDatabaseT
                .getActivities()
                .getActivity()
                .get(0)
                .getLap()
                .get(0)
                .getTrack()
                .get(0)
                .getTrackpoint();
    }

}