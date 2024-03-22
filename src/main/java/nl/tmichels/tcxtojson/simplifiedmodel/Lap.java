package nl.tmichels.tcxtojson.simplifiedmodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TriggerMethodT;

import java.time.LocalDateTime;
import java.util.List;

public record Lap(
        LocalDateTime lapStartUtc,
        Double totalTimeSeconds,
        Double distanceMeters,
        Double maximumSpeed,
        Integer calories,
        Short averageHeartRateBpm,
        Short maximumHeartRateBpm,
        String intensity,
        Short cadence,
        TriggerMethodT triggerMethod,
        Double avgSpeed,
        Short avgRunCadence,
        Short maxRunCadence,
        List<Trackpoint> trackpoints
) {

}
