package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Run;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TcdbRunConverterTest {

    @InjectMocks
    TcdbRunConverter tcdbRunConverter;
    @Mock
    TcdbLapConverter tcdbLapConverter;
    
    @Test
    void activitiesNull_convert_emptyList() throws IOException, JAXBException {
        TrainingCenterDatabaseT trainingCenterDatabaseT = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        trainingCenterDatabaseT.setActivities(null);

        List<Run> converted = tcdbRunConverter.convert(trainingCenterDatabaseT);

        assertThat(converted).isEmpty();
    }

    @Test
    void activityListEmpty_convert_emptyList() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        tcdb.getActivities().getActivity().clear();

        List<Run> converted = tcdbRunConverter.convert(tcdb);

        assertThat(converted).isEmpty();
    }

    @Test
    void activityListWithOneRun_convert_oneRun() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");

        List<Run> converted = tcdbRunConverter.convert(tcdb);

        assertThat(converted).hasSize(1);
    }

    @Test
    void activityListWithTwoRuns_convert_twoRun() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        tcdb.getActivities().getActivity().add(TestActivityProvider.getFirstActivity("export_ttbin2tcx.tcx"));

        List<Run> converted = tcdbRunConverter.convert(tcdb);

        assertThat(converted).hasSize(2);
    }

    @Test
    void activity_id_dateTimeCorrectlySet() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");

        List<Run> converted = tcdbRunConverter.convert(tcdb);
        Run actualRun = converted.getFirst();

        assertThat(actualRun.getStartUtcDateTime()).isEqualTo(LocalDateTime.of(2024,3,3,6,56,25));
    }

    @Test
    void activity_noId_exception() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstActivity(tcdb).setId(null);

        assertThatThrownBy(() -> tcdbRunConverter.convert(tcdb))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date/time was null. Maybe not correctly parsed to XMLGregorianCalendar? Input format of TCX should be like this: 2024-03-03T06:56:25Z");
    }

    @Test
    void garminRun_convert_expectedValues() throws JAXBException, IOException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");

        Run converted = tcdbRunConverter.convert(tcdb).getFirst();

        assertThat(converted.getStartUtcDateTime()).isEqualTo("2023-11-02T05:15:29.000");
        assertThat(converted.getCreatorName()).isEqualTo("Forerunner 245 Music");
        assertThat(converted.getSport()).isEqualTo(SportT.RUNNING);
        assertThat(converted.getLaps()).hasSize(6);
    }

}