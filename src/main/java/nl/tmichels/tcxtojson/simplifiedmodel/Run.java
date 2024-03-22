package nl.tmichels.tcxtojson.simplifiedmodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;

import java.time.LocalDateTime;
import java.util.List;

public record Run(
        LocalDateTime startUtcDateTime,
        String creatorName,
        SportT sport,
        List<Lap> laps) {
}
