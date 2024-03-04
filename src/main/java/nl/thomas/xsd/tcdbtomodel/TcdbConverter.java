package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Run;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class TcdbConverter {


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
                getStartDateTime(activityT),
                activityT.getCreator().getName(),
                activityT.getSport(),
                getLaps(activityT));
    }

    private LocalDateTime getStartDateTime(ActivityT activityT) {
        XMLGregorianCalendar id = activityT.getId();
        if (id == null) {
            throw new IllegalArgumentException("Start date/time was not correctly parsed to XMLGregorianCalendar. " +
                    "Format should be this format: 2024-03-03T06:56:25Z");
        }
        return ZonedDateTime.parse(id.toString()).toLocalDateTime();
    }

    private List<Lap> getLaps(ActivityT activityT) {
        return new ArrayList<>();
    }

}
