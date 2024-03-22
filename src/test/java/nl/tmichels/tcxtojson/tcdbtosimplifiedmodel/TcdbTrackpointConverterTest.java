package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import nl.tmichels.tcxtojson.simplifiedmodel.Trackpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import javax.xml.datatype.XMLGregorianCalendar;
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

        assertThat(trackpoint.latitude()).isNull();
        assertThat(trackpoint.longitude()).isNull();
        assertThat(capturedOutput.getOut()).contains("No latitude and longitude available for trackpoint on 2024-03-03T06:56:27Z");
    }

    @Test
    void noExtensions_convert_noSpeedAndCadence(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = TestActivityProvider.getNthTrackpoint("export_garmin_invalid_extensions.tcx", 3);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint.speed()).isNull();
        assertThat(trackpoint.cadence()).isNull();
        assertThat(capturedOutput.getOut()).contains("No TrackpointExtension for trackpoint on 2023-11-02T05:15:38.000Z");
    }

    @Test
    void multipleExtensions_convert_valuesOfFirst(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = TestActivityProvider.getNthTrackpoint("export_garmin_invalid_extensions.tcx", 1);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint.speed()).isEqualTo(2.3);
        assertThat(trackpoint.cadence()).isEqualTo((short) 29);
        assertThat(capturedOutput.getOut()).contains("Unexpected amount of 2 ActivityTrackpointExtensionT for Trackpoint on 2023-11-02T05:15:31.000Z");
    }

    @Test
    void otherExtensions_convert_valuesOfRelevant(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT trackpointT = TestActivityProvider.getNthTrackpoint("export_garmin_invalid_extensions.tcx", 2);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint.speed()).isEqualTo(4.2);
        assertThat(trackpoint.cadence()).isEqualTo((short) 83);
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

        assertThat(trackpoint.heartRateBpm()).isNull();
    }

    @Test
    void cadenceInTpAndTpExtension_convert_tp(CapturedOutput capturedOutput) throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence((short) 3);
        firstTpExtension.setRunCadence((short) 4);

        Trackpoint trackpoints = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoints.cadence()).isEqualTo((short) 3);
        assertThat(capturedOutput.getOut()).contains("Conflicting values for cadence in trackpoint on 2023-10-01T10:06:53Z: extension: 4, trackpoint value: 3");
    }

    @Test
    void cadenceInTpAndNotTpExtension_convert_tp() throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence((short) 3);
        firstTpExtension.setRunCadence(null);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoint.cadence()).isEqualTo((short) 3);
    }

    @Test
    void cadenceNotInTpAndNotTpExtension_convert_tp() throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence(null);
        firstTpExtension.setRunCadence(null);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoint.cadence()).isNull();
    }

    @Test
    void cadenceNotInTpAndTpExtension_convert_tp() throws JAXBException, IOException {
        TrackpointT firstTp = TestActivityProvider.getFirstTrackpoint("export_tomtom.tcx");
        ActivityTrackpointExtensionT firstTpExtension = ((JAXBElement<ActivityTrackpointExtensionT>) firstTp.getExtensions().getAny().getFirst()).getValue();

        firstTp.setCadence(null);
        firstTpExtension.setRunCadence((short) 4);

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(firstTp);

        assertThat(trackpoint.cadence()).isEqualTo((short) 4);
    }

    @Test
    void newTrackpoint_getTime_canBeNull() {
        TrackpointT trackpointT = new TrackpointT();
        XMLGregorianCalendar time = trackpointT.getTime();
        assertThat(time).isNull();
    }

    @Test
    void emptyTrackpoint_convert_nullSafe(CapturedOutput capturedOutput) {
        TrackpointT trackpointT = new TrackpointT();

        Trackpoint trackpoint = tcdbTrackpointConverter.convertTrackpoint(trackpointT);

        assertThat(trackpoint).isNotNull();
        assertThat(capturedOutput.getOut()).contains(
                "No latitude and longitude available for trackpoint on null",
                "No TrackpointExtension for trackpoint on null",
                "Trackpoint with distance null has no time");
    }
}