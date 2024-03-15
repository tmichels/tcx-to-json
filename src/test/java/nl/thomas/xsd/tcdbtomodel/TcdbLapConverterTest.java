package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Trackpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcdbLapConverterTest {

    @InjectMocks
    TcdbLapConverter tcdbLapConverter;
    @Mock
    TcdbTrackpointConverter tcdbTrackpointConverter;

    @Test
    void avgHeartRateBpmNull_convert_null() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_ttbin2tcx.tcx");
        firstLap.setAverageHeartRateBpm(null);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
    }

    @Test
    void maxHeartRateBpmNull_convert_null() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin.tcx");
        firstLap.setMaximumHeartRateBpm(null);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getMaximumHeartRateBpm()).isNull();
    }

    @Test
    void extensionsNull_convert_noDataFromLapExtension() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_ttbin2tcx.tcx");
        firstLap.setExtensions(null);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
    }

    @Test
    void emptyExtensions_convert_noDataFromLapExtension() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_ttbin2tcx.tcx");
        firstLap.setExtensions(new ExtensionsT());

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
    }

    @Test
    void cadenceNull_convert_null() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin.tcx");
        firstLap.setCadence(null);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getCadence()).isNull();
    }

    @Test
    void cadence_convert_cadence() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin.tcx");
        firstLap.setCadence((short) 82);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getCadence()).isEqualTo((short) 82);
    }

    @Test
    void trackNull_convert_null() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin_track_deleted.tcx");
        firstLap.setCadence(null);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getTrackpoints()).isEmpty();
    }

    @Test
    void notesNull_convert_null() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin.tcx");
        firstLap.setNotes(null);

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getNotes()).isNull();
    }

    @Test
    void lapExtension_convert_dataFromLapExtension() throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin.tcx");

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getAvgSpeed()).isEqualTo(2.2279999256134033);
        assertThat(converted.getAvgRunCadence()).isEqualTo((short) 81);
        assertThat(converted.getMaxRunCadence()).isEqualTo((short) 114);
    }

    @Test
    void noLapExtension_convert_noDataFromLapExtension(CapturedOutput capturedOutput) throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_tomtom.tcx");

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getMaxRunCadence()).isNull();
        assertThat(capturedOutput.getOut()).contains(
                "No LapExtension for lap with start time 2023-10-01T10:06:53Z"
        );
    }

    @Test
    void unexpectedLapExtension_convert_noDataFromLapExtension(CapturedOutput capturedOutput) throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin_invalid_extensions.tcx");

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getLast());

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
        assertThat(capturedOutput.getOut()).contains(
                "Found unexpected type(s) of Lap extensions: [com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT]",
                "No LapExtension for lap with start time 2023-11-02T05:56:58.000Z"
        );
    }

    @Test
    void multipleLapExtension_convert_dataFromFirstDuplicate(CapturedOutput capturedOutput) throws IOException, JAXBException {
        ActivityLapT firstLap = TestActivityProvider.getFirstLap("export_garmin_invalid_extensions.tcx");

        Lap converted = tcdbLapConverter.convertLap(firstLap);

        assertThat(converted.getAvgSpeed()).isEqualTo(2.2279999256134033);
        assertThat(converted.getAvgRunCadence()).isEqualTo((short) 81);
        assertThat(converted.getMaxRunCadence()).isEqualTo((short) 114);
        assertThat(capturedOutput.getOut()).contains(
                "Unexpected amount of 2 LapExtensionTs for Lap 2023-11-02T05:15:29.000Z"
        );
    }

    @Test
    void ttbin2tcxFile_convert_trackpoints() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_ttbin2tcx.tcx");

        List<Trackpoint> trackpoints = tcdbLapConverter.convertLap(activityLapT).getTrackpoints();

        assertThat(trackpoints).hasSize(131);
    }

    @Test
    void noTrack_convert_empty() throws JAXBException, IOException {
        ActivityLapT activityLapT = TestActivityProvider.getFirstLap("export_garmin_track_deleted.tcx");

        List<Trackpoint> trackpoints = tcdbLapConverter.convertLap(activityLapT).getTrackpoints();

        assertThat(trackpoints).isEmpty();
    }

}