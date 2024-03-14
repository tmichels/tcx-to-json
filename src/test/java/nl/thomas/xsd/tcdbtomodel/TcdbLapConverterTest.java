package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Lap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcdbLapConverterTest {

    @InjectMocks
    TcdbLapConverter tcdbLapConverter;
    @Mock
    TcdbTrackpointConverter tcdbTrackpointConverter;

    @Test
    void avgHeartRateBpmNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstLap(tcdb).setAverageHeartRateBpm(null);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
    }

    @Test
    void maxHeartRateBpmNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setMaximumHeartRateBpm(null);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getMaximumHeartRateBpm()).isNull();
    }

    @Test
    void extensionsNull_convert_noDataFromLapExtension() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstLap(tcdb).setExtensions(null);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
    }

    @Test
    void emptyExtensions_convert_noDataFromLapExtension() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstLap(tcdb).setExtensions(new ExtensionsT());

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getAvgSpeed()).isNull();
        assertThat(converted.getAvgRunCadence()).isNull();
        assertThat(converted.getMaxRunCadence()).isNull();
    }

    @Test
    void cadenceNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setCadence(null);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getCadence()).isNull();
    }

    @Test
    void cadence_convert_cadence() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setCadence((short) 82);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getCadence()).isEqualTo((short) 82);
    }

    @Test
    void trackNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin_track_deleted.tcx");
        TestActivityProvider.getFirstLap(tcdb).setCadence(null);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getTrackpoints()).isEmpty();
    }

    @Test
    void notesNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setNotes(null);

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getNotes()).isNull();
    }

    @Test
    void lapExtension_convert_dataFromLapExtension() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getAvgSpeed()).isEqualTo(2.2279999256134033);
        assertThat(converted.getAvgRunCadence()).isEqualTo((short) 81);
        assertThat(converted.getMaxRunCadence()).isEqualTo((short) 114);
    }

    @Test
    void noLapExtension_convert_noDataFromLapExtension(CapturedOutput capturedOutput) throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_tomtom.tcx");

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

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
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin_invalid_extensions.tcx");

        Lap converted = tcdbLapConverter.convertLap(tcdb.getActivities().getActivity().getFirst().getLap().getFirst());

        assertThat(converted.getAvgSpeed()).isEqualTo(2.2279999256134033);
        assertThat(converted.getAvgRunCadence()).isEqualTo((short) 81);
        assertThat(converted.getMaxRunCadence()).isEqualTo((short) 114);
        assertThat(capturedOutput.getOut()).contains(
                "Unexpected amount of 2 LapExtensionTs for Lap 2023-11-02T05:15:29.000Z"
        );
    }

}