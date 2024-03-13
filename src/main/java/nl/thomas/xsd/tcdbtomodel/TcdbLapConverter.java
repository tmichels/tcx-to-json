package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateInBeatsPerMinuteT;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Trackpoint;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TcdbLapConverter {

    private final TcdbTrackpointConverter tcdbTrackpointConverter;

    public TcdbLapConverter(TcdbTrackpointConverter tcdbTrackpointConverter) {
        this.tcdbTrackpointConverter = tcdbTrackpointConverter;
    }

    List<Lap> convertLaps(ActivityT activityT) {
        return activityT.getLap().stream()
                .map(this::convertLap)
                .toList();
    }

    private Lap convertLap(ActivityLapT activityLapT) {
        return new Lap(
                TimeConverter.getStartDateTime(activityLapT.getStartTime()),
                activityLapT.getTotalTimeSeconds(),
                activityLapT.getMaximumSpeed(),
                activityLapT.getDistanceMeters(),
                activityLapT.getCalories(),
                getOptionalHrBpm(activityLapT.getAverageHeartRateBpm()),
                getOptionalHrBpm(activityLapT.getMaximumHeartRateBpm()),
                activityLapT.getIntensity().value(),
                activityLapT.getCadence(),
                activityLapT.getTriggerMethod(),
                getTrackpoints(activityLapT),
                activityLapT.getNotes(),
                ExtensionConverter.getJaxbExtensions(activityLapT.getExtensions())
        );
    }

    private Short getOptionalHrBpm(HeartRateInBeatsPerMinuteT heartRateBpm) {
        return heartRateBpm == null ? null : heartRateBpm.getValue();
    }

    private List<Trackpoint> getTrackpoints(ActivityLapT activityLapT) {
        return tcdbTrackpointConverter.convertTrackpoints(activityLapT);
    }
}
