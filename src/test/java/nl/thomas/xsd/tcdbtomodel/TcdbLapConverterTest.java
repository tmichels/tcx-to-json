package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Lap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TcdbLapConverterTest {

    @InjectMocks
    TcdbLapConverter tcdbLapConverter;

    @Test
    void avgHeartRateBpmNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstLap(tcdb).setAverageHeartRateBpm(null);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getAverageHeartRateBpm()).isNull();
    }

    @Test
    void maxHeartRateBpmNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setMaximumHeartRateBpm(null);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getMaximumHeartRateBpm()).isNull();
    }

    @Test
    void extensionsNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstLap(tcdb).setExtensions(null);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getExtensions()).isNull();
    }

    @Test
    void emptyExtensions_convert_emptyList() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstLap(tcdb).setExtensions(new ExtensionsT());

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getExtensions()).isEmpty();
    }

    @Test
    void cadenceNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setCadence(null);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getCadence()).isNull();
    }

    @Test
    void cadence_convert_cadence() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setCadence((short) 82);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getCadence()).isEqualTo((short) 82);
    }

    @Test
    void trackNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin_track_deleted.tcx");
        TestActivityProvider.getFirstLap(tcdb).setCadence(null);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getTrackpoints()).isEmpty();
    }

    @Test
    void notesNull_convert_null() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");
        TestActivityProvider.getFirstLap(tcdb).setNotes(null);

        Lap converted = tcdbLapConverter.convertLaps(tcdb.getActivities().getActivity().getFirst()).getFirst();

        assertThat(converted.getNotes()).isNull();
    }

}