package nl.thomas.xsd.model;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Slf4j
public class Run {

    private final LocalDateTime startUtcDateTime;
    private final String creatorName;
    private final SportT sport;
    private final List<Lap> laps;

}
