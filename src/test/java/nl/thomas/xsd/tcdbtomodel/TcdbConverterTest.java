package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.model.Run;
import nl.thomas.xsd.tcxtotcdb.TcxParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TcdbConverterTest {

    @InjectMocks
    TcdbConverter tcdbConverter;
    
    @Test
    void activitiesNull_convert_emptyList() throws IOException, JAXBException {
        TrainingCenterDatabaseT trainingCenterDatabaseT = getTrainingCenterDatabaseT();
        trainingCenterDatabaseT.setActivities(null);

        List<Run> converted = tcdbConverter.convert(trainingCenterDatabaseT);

        assertThat(converted).isEmpty();
    }

    @Test
    void activityListEmpty_convert_emptyList() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = getTrainingCenterDatabaseT();
        tcdb.getActivities().getActivity().clear();

        List<Run> converted = tcdbConverter.convert(tcdb);

        assertThat(converted).isEmpty();
    }

    @Test
    void activityListWithOneRun_convert_oneRun() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = getTrainingCenterDatabaseT();

        List<Run> converted = tcdbConverter.convert(tcdb);

        assertThat(converted).hasSize(1);
    }

    @Test
    void activityListWithTwoRuns_convert_twoRun() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = getTrainingCenterDatabaseT();
        tcdb.getActivities().getActivity().add(getFirstActivity());

        List<Run> converted = tcdbConverter.convert(tcdb);

        assertThat(converted).hasSize(2);
    }

    @Test
    void activity_id_dateTimeCorrectlySet() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = getTrainingCenterDatabaseT();

        List<Run> converted = tcdbConverter.convert(tcdb);
        Run actualRun = converted.getFirst();

        assertThat(actualRun.getStartUtcDateTime()).isEqualTo(LocalDateTime.of(2024,3,3,6,56,25));
    }

    @Test
    void activity_noId_exception() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = getTrainingCenterDatabaseT();
        getFirstActivity(tcdb).setId(null);

        assertThatThrownBy(() -> tcdbConverter.convert(tcdb))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start date/time was not correctly parsed to XMLGregorianCalendar. Format should be this format: 2024-03-03T06:56:25Z");
    }

    private ActivityT getFirstActivity() throws JAXBException, IOException {
        return getFirstActivity(getTrainingCenterDatabaseT());
    }

    private TrainingCenterDatabaseT getTrainingCenterDatabaseT() throws IOException, JAXBException {
        List<String> strings = Files.readAllLines(Path.of("src/test/java/testfiles/export_ttbin2tcx.tcx"));
        return new TcxParser().parse(String.join("", strings));
    }

    private ActivityT getFirstActivity(TrainingCenterDatabaseT tcdb) {
        return tcdb.getActivities().getActivity().get(0);
    }

}