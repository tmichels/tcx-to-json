package nl.thomas.xsd.model;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TriggerMethodT;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class Lap {

    private final LocalDateTime lapStartUtc;
    private final Double totalTimeSeconds;
    private final Double maximumSpeed;
    private final Double distanceMeters;
    private final Integer calories;
    private final Short averageHeartRateBpm;
    private final Short maximumHeartRateBpm;
    private final String intensity;
    private final Short cadence;
    private final TriggerMethodT triggerMethod;
    private final List<Trackpoint> trackpoints;
    private final String notes;
    private final List<Object> extensions;

}
