package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityLapExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateInBeatsPerMinuteT;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Trackpoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
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
        Optional<ActivityLapExtensionT> activityLapExtensionT = getLapExtension(activityLapT);
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
                activityLapT.getNotes(),
                activityLapExtensionT.map(ActivityLapExtensionT::getAvgSpeed).orElse(null),
                activityLapExtensionT.map(ActivityLapExtensionT::getAvgRunCadence).orElse(null),
                activityLapExtensionT.map(ActivityLapExtensionT::getMaxRunCadence).orElse(null),
                getTrackpoints(activityLapT)
        );
    }

    private Optional<ActivityLapExtensionT> getLapExtension(ActivityLapT activityLapT) {
        List<Object> allExtensions = ExtensionConverter.getJaxbExtensions(activityLapT.getExtensions());
        List<ActivityLapExtensionT> lapExtension = ExtensionConverter.filterExtensionsOfType(allExtensions, ActivityLapExtensionT.class);
        if (lapExtension.isEmpty()) {
            log.debug("No LapExtension for lap {}", activityLapT);
            return Optional.empty();
        }
        if (lapExtension.size() > 1) {
            log.warn("Unexpected amount of {} LapExtensionTs for Lap {}", lapExtension.size(), activityLapT);
        }
        if (lapExtension.size() < allExtensions.size()) {
            log.warn("Found unexpected type(s) of Lap extensions: {}", allExtensions.stream().map((Object o) -> o.getClass().getName()).toList());
        }
        return Optional.of(lapExtension.getFirst());
    }

    private Short getOptionalHrBpm(HeartRateInBeatsPerMinuteT heartRateBpm) {
        return heartRateBpm == null ? null : heartRateBpm.getValue();
    }

    private List<Trackpoint> getTrackpoints(ActivityLapT activityLapT) {
        return tcdbTrackpointConverter.convertTrackpoints(activityLapT);
    }

}