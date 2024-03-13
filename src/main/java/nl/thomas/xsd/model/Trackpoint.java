package nl.thomas.xsd.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Trackpoint {

    private final LocalDateTime utc;
    private final Double latitude;
    private final Double longitude;
    private final Double altitudeMeters;
    private final Double distanceMeters;
    private final Short heartRateBpm;
    private final Short cadence;
    private final Double speed;

}
