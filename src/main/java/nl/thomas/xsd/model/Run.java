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

    private LocalDateTime startUtcDateTime;
    private String creatorName;
    private SportT sport;
    private List<Lap> laps;

}
