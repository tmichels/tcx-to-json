package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.*;
import jakarta.xml.bind.JAXBException;
import nl.tmichels.tcxtojson.simplifiedmodel.Run;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcdbRunConverterTest {

    @InjectMocks
    TcdbRunConverter tcdbRunConverter;
    @Mock
    TcdbLapConverter tcdbLapConverter;

    @Test
    void newTcdb_convert_emptyRunList(CapturedOutput capturedOutput) {
        TrainingCenterDatabaseT trainingCenterDatabaseT = new TrainingCenterDatabaseT();
        List<Run> convert = tcdbRunConverter.convert(trainingCenterDatabaseT);
        assertThat(convert).isEmpty();
        assertThat(capturedOutput.getOut()).contains("No runs were found in the parsed TrainingCenterDatabaseT");
    }

    @Test
    void newTcdb_getActivities_activitiesCanBeNull() {
        TrainingCenterDatabaseT trainingCenterDatabaseT = new TrainingCenterDatabaseT();
        ActivityListT activities = trainingCenterDatabaseT.getActivities();
        assertThat(activities).isNull();
    }

    @Test
    void newTcdbActivityList_getActivities_activityCannotBeNull() {
        ActivityListT activityListT = new ActivityListT();
        List<ActivityT> activity = activityListT.getActivity();
        assertThat(activity).isEmpty();
    }

    @Test
    void newTcdbActivity_getLaps_lapsCannotBeNull() {
        ActivityT activityT = new ActivityT();
        List<ActivityLapT> lap = activityT.getLap();
        assertThat(lap).isEmpty();
    }

    @Test
    void newTcdbLap_getTracks_tracksCannotBeNull() {
        ActivityLapT activityLapT = new ActivityLapT();
        List<TrackT> track = activityLapT.getTrack();
        assertThat(track).isEmpty();
    }

    @Test
    void newTcdbTrack_getTrackpoints_trackpointsCannotBeNull() {
        TrackT trackT = new TrackT();
        List<TrackpointT> trackpoints = trackT.getTrackpoint();
        assertThat(trackpoints).isEmpty();
    }

    @Test
    void newTcdbActivity_getCreator_creatorCanBeNull() {
        ActivityT activityT = new ActivityT();
        AbstractSourceT creator = activityT.getCreator();
        assertThat(creator).isNull();
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

        assertThat(actualRun.start()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2024, 3, 3, 6, 56, 25), ZoneId.of(ZoneOffset.UTC.toString())));
    }

    @Test
    void activity_noId_exception() throws IOException, JAXBException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_ttbin2tcx.tcx");
        TestActivityProvider.getFirstActivity(tcdb).setId(null);

        assertThatThrownBy(() -> tcdbRunConverter.convert(tcdb))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Date/time was null. Input format of TCX should be like this: 2024-03-03T06:56:25Z or this 2024-03-03T06:56:25");
    }

    @Test
    void garminRun_convert_expectedValues() throws JAXBException, IOException {
        TrainingCenterDatabaseT tcdb = TestActivityProvider.getTrainingCenterDatabaseT("export_garmin.tcx");

        Run converted = tcdbRunConverter.convert(tcdb).getFirst();

        assertThat(converted.start()).isEqualTo("2023-11-02T05:15:29.000Z");
        assertThat(converted.creatorName()).isEqualTo("Forerunner 245 Music");
        assertThat(converted.sport()).isEqualTo(SportT.RUNNING);
        assertThat(converted.laps()).hasSize(6);
    }

}