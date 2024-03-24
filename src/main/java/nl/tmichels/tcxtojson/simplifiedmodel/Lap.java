package nl.tmichels.tcxtojson.simplifiedmodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.IntensityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TriggerMethodT;

import java.time.ZonedDateTime;
import java.util.List;

public record Lap(
        ZonedDateTime lapStart,
        Double totalTimeSeconds,
        Double distanceMeters,
        Double maximumSpeed,
        Integer calories,
        Short averageHeartRateBpm,
        Short maximumHeartRateBpm,
        IntensityT intensity,
        Short cadence,
        TriggerMethodT triggerMethod,
        Double avgSpeed,
        Short avgRunCadence,
        Short maxRunCadence,
        List<Trackpoint> trackpoints
) {

}
