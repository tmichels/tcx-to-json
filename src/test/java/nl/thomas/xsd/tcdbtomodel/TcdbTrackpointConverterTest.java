package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcdbTrackpointConverterTest {

    @InjectMocks
    TcdbTrackpointConverter tcdbTrackpointConverter;

    @Test
    void ttbin2tcxFile_convert_trackpoints() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_ttbin2tcx.tcx");

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints).hasSize(131);
    }

    @Test
    void noTrack_convert_empty() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_garmin_track_deleted.tcx");

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints).isEmpty();
    }

    @Test
    void positionNull_convert_latLngNull(CapturedOutput capturedOutput) throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstTrackpoint(activityLapT).setPosition(null);

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.getFirst().getLatitude()).isNull();
        assertThat(trackpoints.getFirst().getLongitude()).isNull();
        assertThat(capturedOutput.getOut()).contains("No latitude and longitude available for trackpoint on 2024-03-03T06:56:27Z");
    }

    @Test
    void noExtensions_convert_noSpeedAndCadence(CapturedOutput capturedOutput) throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_garmin_invalid_extensions.tcx");

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.get(3).getSpeed()).isNull();
        assertThat(trackpoints.get(3).getCadence()).isNull();
        assertThat(capturedOutput.getOut()).contains("No TrackpointExtension for trackpoint on 2023-11-02T05:15:38.000Z");
    }

    @Test
    void multipleExtensions_convert_valuesOfFirst(CapturedOutput capturedOutput) throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_garmin_invalid_extensions.tcx");

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.get(1).getSpeed()).isEqualTo(2.3);
        assertThat(trackpoints.get(1).getCadence()).isEqualTo((short) 29);
        assertThat(capturedOutput.getOut()).contains("Unexpected amount of 2 ActivityTrackpointExtensionT for Trackpoint on 2023-11-02T05:15:31.000Z");
    }

    @Test
    void otherExtensions_convert_valuesOfRelevant(CapturedOutput capturedOutput) throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_garmin_invalid_extensions.tcx");

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.get(2).getSpeed()).isEqualTo(4.2);
        assertThat(trackpoints.get(2).getCadence()).isEqualTo((short) 83);
        assertThat(capturedOutput.getOut()).contains(
                "Unexpected type of Trackpoint extensions was found for Trackpoint on 2023-11-02T05:15:35.000Z: [" +
                        "com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT, " +
                        "com.garmin.xmlschemas.activityextension.v2.ActivityLapExtensionT]");
    }

    @Test
    void heartRateBpmNull_convert_null() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_tomtom.tcx");
        TestActivityProvider.getFirstTrackpoint(activityLapT).setHeartRateBpm(null);

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.getFirst().getHeartRateBpm()).isNull();
    }

    @Test
    void cadenceInTpAndTpExtension_convert_tp(CapturedOutput capturedOutput) throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_tomtom.tcx");
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint(activityLapT);
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence((short) 3);
        firstTpExtension.setRunCadence((short) 4);

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.getFirst().getCadence()).isEqualTo((short) 3);
        assertThat(capturedOutput.getOut()).contains("Conflicting values for cadence in trackpoint on 2023-10-01T10:06:53Z: extension: 4, trackpoint value: 3");
    }

    @Test
    void cadenceInTpAndNotTpExtension_convert_tp() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_tomtom.tcx");
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint(activityLapT);
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence((short) 3);
        firstTpExtension.setRunCadence(null);

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.getFirst().getCadence()).isEqualTo((short) 3);
    }

    @Test
    void cadenceNotInTpAndNotTpExtension_convert_tp() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_tomtom.tcx");
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint(activityLapT);
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence(null);
        firstTpExtension.setRunCadence(null);

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.getFirst().getCadence()).isNull();
    }

    @Test
    void cadenceNotInTpAndTpExtension_convert_tp() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_tomtom.tcx");
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint(activityLapT);
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence(null);
        firstTpExtension.setRunCadence((short) 4);

        List<Trackpoint> trackpoints = tcdbTrackpointConverter.convertTrackpoints(activityLapT);

        assertThat(trackpoints.getFirst().getCadence()).isEqualTo((short) 4);
    }
}