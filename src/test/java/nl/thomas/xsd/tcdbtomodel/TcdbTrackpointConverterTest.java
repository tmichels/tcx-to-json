package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Trackpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcdbTrackpointConverterTest {

    @InjectMocks
    TcdbTrackpointConverter tcdbTrackpointConverter;

    @Test
    void positionNull_convert_latLngNull(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT firstTrackpoint = TestActivityProvider.getFirstTrackpoint("export_ttbin2tcx.tcx");
        firstTrackpoint.setPosition(null);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTrackpoint);

        assertThat(trackpoint.getLatitude()).isNull();
        assertThat(trackpoint.getLongitude()).isNull();
        assertThat(capturedOutput.getOut()).contains("No latitude and longitude available for trackpoint on 2024-03-03T06:56:27Z");
    }

    @Test
    void noExtensions_convert_noSpeedAndCadence(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = TestActivityProvider.getNthTrackpoint("export_garmin_invalid_extensions.tcx", 3);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint.getSpeed()).isNull();
        assertThat(trackpoint.getCadence()).isNull();
        assertThat(capturedOutput.getOut()).contains("No TrackpointExtension for trackpoint on 2023-11-02T05:15:38.000Z");
    }

    @Test
    void multipleExtensions_convert_valuesOfFirst(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = TestActivityProvider.getNthTrackpoint("export_garmin_invalid_extensions.tcx", 1);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint.getSpeed()).isEqualTo(2.3);
        assertThat(trackpoint.getCadence()).isEqualTo((short) 29);
        assertThat(capturedOutput.getOut()).contains("Unexpected amount of 2 ActivityTrackpointExtensionT for Trackpoint on 2023-11-02T05:15:31.000Z");
    }

    @Test
    void otherExtensions_convert_valuesOfRelevant(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = TestActivityProvider.getNthTrackpoint("export_garmin_invalid_extensions.tcx", 2);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint.getSpeed()).isEqualTo(4.2);
        assertThat(trackpoint.getCadence()).isEqualTo((short) 83);
        assertThat(capturedOutput.getOut()).contains(
                "Unexpected type of Trackpoint extensions was found for Trackpoint on 2023-11-02T05:15:35.000Z: [" +
                        "com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT, " +
                        "com.garmin.xmlschemas.activityextension.v2.ActivityLapExtensionT]");
    }

    @Test
    void heartRateBpmNull_convert_null() throws JAXBException, IOException {
        TrackpointT firstTrackpoint = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        firstTrackpoint.setHeartRateBpm(null);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTrackpoint);

        assertThat(trackpoint.getHeartRateBpm()).isNull();
    }

    @Test
    void cadenceInTpAndTpExtension_convert_tp(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence((short) 3);
        firstTpExtension.setRunCadence((short) 4);

        Trackpoint trackpoints = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoints.getCadence()).isEqualTo((short) 3);
        assertThat(capturedOutput.getOut()).contains("Conflicting values for cadence in trackpoint on 2023-10-01T10:06:53Z: extension: 4, trackpoint value: 3");
    }

    @Test
    void cadenceInTpAndNotTpExtension_convert_tp() throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence((short) 3);
        firstTpExtension.setRunCadence(null);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoint.getCadence()).isEqualTo((short) 3);
    }

    @Test
    void cadenceNotInTpAndNotTpExtension_convert_tp() throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence(null);
        firstTpExtension.setRunCadence(null);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoint.getCadence()).isNull();
    }

    @Test
    void cadenceNotInTpAndTpExtension_convert_tp() throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence(null);
        firstTpExtension.setRunCadence((short) 4);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoint.getCadence()).isEqualTo((short) 4);
    }
}