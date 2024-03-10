package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Run;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class TcdbRunConverter {

    private final TcdbLapConverter tcdbLapConverter;

    public TcdbRunConverter(TcdbLapConverter tcdbLapConverter) {
        this.tcdbLapConverter = tcdbLapConverter;
    }


    public List<Run> convert(TrainingCenterDatabaseT tcdb) {
        return getRuns(tcdb);
    }

    private List<Run> getRuns(TrainingCenterDatabaseT tcdb) {
        return getActivityTStream(tcdb)
                .map(this::convertActivityToRun)
                .toList();
    }

    private static Stream<ActivityT> getActivityTStream(TrainingCenterDatabaseT tcdb) {
        try {
            return tcdb.getActivities().getActivity().stream();
        } catch (NullPointerException e) {
            return Stream.of();
        }
    }

    private Run convertActivityToRun(ActivityT activityT) {
        return new Run(
                TimeConverter.getStartDateTime(activityT.getId()),
                activityT.getCreator().getName(),
                activityT.getSport(),
                getLaps(activityT));
    }

    private List<Lap> getLaps(ActivityT activityT) {
        return tcdbLapConverter.convertLaps(activityT);
    }

}
