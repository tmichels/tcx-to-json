package nl.tmichels.tcxtojson.simplifiedmodel;

import java.time.LocalDateTime;

public record Trackpoint(
        LocalDateTime utc,
        Double latitude,
        Double longitude,
        Double altitudeMeters,
        Double distanceMeters,
        Short heartRateBpm,
        Short cadence,
        Double speed
) {

}