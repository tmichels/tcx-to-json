package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Run;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TcdbRunConverter {

    private final TcdbLapConverter tcdbLapConverter;

    public TcdbRunConverter(TcdbLapConverter tcdbLapConverter) {
        this.tcdbLapConverter = tcdbLapConverter;
    }

    public List<Run> convert(TrainingCenterDatabaseT tcdb) {
        List<Run> runs = tcdb.getActivities() != null ?  // getActivities can be null, see generated class and tests.
                tcdb.getActivities().getActivity().stream()  // getActivity cannot be null, see generated class and tests.
                        .map(this::convertActivityToRun)
                        .toList() :
                new ArrayList<>();
        if (runs.isEmpty()) {
            log.warn("No runs were found in the parsed TrainingCenterDatabaseT");
        }
        return runs;
    }

    private Run convertActivityToRun(ActivityT activityT) {
        return new Run(
                TimeConverter.getStartDateTime(activityT.getId()),
                activityT.getCreator() != null ? activityT.getCreator().getName() : null,
                activityT.getSport(),
                getLaps(activityT));
    }

    private List<Lap> getLaps(ActivityT activityT) {
        return activityT.getLap().stream() // lap cannot be null, see generated class and tests
                .map(tcdbLapConverter::convertLap)
                .toList();
    }

}
