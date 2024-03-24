package nl.tmichels.tcxtojson.simplifiedmodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;

import java.time.ZonedDateTime;
import java.util.List;

public record Run(
        ZonedDateTime start,
        String creatorName,
        SportT sport,
        List<Lap> laps) {
}
