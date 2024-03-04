package nl.thomas.xsd.model;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public record Run(
        LocalDateTime startUtcDateTime,
        String creatorName,
        SportT sport,
        List<Lap> laps) {

}
