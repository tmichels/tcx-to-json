package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityLapExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateInBeatsPerMinuteT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import lombok.extern.slf4j.Slf4j;
import nl.tmichels.tcxtojson.simplifiedmodel.Lap;
import nl.tmichels.tcxtojson.simplifiedmodel.Trackpoint;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TcdbLapConverter {

    private final TcdbTrackpointConverter tcdbTrackpointConverter;

    public TcdbLapConverter(TcdbTrackpointConverter tcdbTrackpointConverter) {
        this.tcdbTrackpointConverter = tcdbTrackpointConverter;
    }

    Lap convertLap(ActivityLapT activityLapT) {
        Optional<ActivityLapExtensionT> activityLapExtensionT = getLapExtension(activityLapT);
        return new Lap(
                getStartDateTime(activityLapT),
                activityLapT.getTotalTimeSeconds(),
                activityLapT.getDistanceMeters(),
                activityLapT.getMaximumSpeed(),
                activityLapT.getCalories(),
                getOptionalHrBpm(activityLapT.getAverageHeartRateBpm()),
                getOptionalHrBpm(activityLapT.getMaximumHeartRateBpm()),
                activityLapT.getIntensity() != null ? activityLapT.getIntensity().value() : null,
                activityLapT.getCadence(),
                activityLapT.getTriggerMethod(),
                activityLapExtensionT.map(ActivityLapExtensionT::getAvgSpeed).orElse(null),
                activityLapExtensionT.map(ActivityLapExtensionT::getAvgRunCadence).orElse(null),
                activityLapExtensionT.map(ActivityLapExtensionT::getMaxRunCadence).orElse(null),
                getTrackpoints(activityLapT)
        );
    }

    private Optional<ActivityLapExtensionT> getLapExtension(ActivityLapT activityLapT) {
        List<Object> allExtensions = ExtensionConverter.getJaxbExtensions(activityLapT.getExtensions());
        List<ActivityLapExtensionT> lapExtension = ExtensionConverter.filterExtensionsOfType(allExtensions, ActivityLapExtensionT.class);
        if (lapExtension.size() < allExtensions.size()) {
            log.warn("Found unexpected type(s) of Lap extensions: {}", allExtensions.stream().map((Object o) -> o.getClass().getName()).toList());
        }
        if (lapExtension.isEmpty()) {
            log.info("No LapExtension for lap with start time {}", activityLapT.getStartTime());
            return Optional.empty();
        }
        if (lapExtension.size() > 1) {
            log.warn("Unexpected amount of {} LapExtensionTs for Lap {}", lapExtension.size(), activityLapT.getStartTime());
        }
        return Optional.of(lapExtension.getFirst());
    }

    private LocalDateTime getStartDateTime(ActivityLapT activityLapT) {
        XMLGregorianCalendar startTime = activityLapT.getStartTime();
        if (startTime == null) {
            log.warn(
                    "Lap with distance {} and {} total time seconds has no start time.",
                    activityLapT.getDistanceMeters(),
                    activityLapT.getTotalTimeSeconds());
            return null;
        }
        return TimeConverter.convert(activityLapT.getStartTime());
    }

    private Short getOptionalHrBpm(HeartRateInBeatsPerMinuteT heartRateBpm) {
        return heartRateBpm == null ? null : heartRateBpm.getValue();
    }

    private List<Trackpoint> getTrackpoints(ActivityLapT activityLapT) {
        return activityLapT.getTrack().stream() // Cannot be null, see generated class and tests.
                .map(TrackT::getTrackpoint) // Cannot be null, see generated class and tests.
                .flatMap(Collection::stream)
                .map(tcdbTrackpointConverter::convertTrackpoint)
                .toList();
    }

}
