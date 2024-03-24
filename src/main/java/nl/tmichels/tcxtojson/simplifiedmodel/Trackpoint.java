package nl.tmichels.tcxtojson.simplifiedmodel;

import java.time.ZonedDateTime;

public record Trackpoint(
        ZonedDateTime timeStamp,
        Double latitude,
        Double longitude,
        Double altitudeMeters,
        Double distanceMeters,
        Short heartRateBpm,
        Short cadence,
        Double speed
) {

}
