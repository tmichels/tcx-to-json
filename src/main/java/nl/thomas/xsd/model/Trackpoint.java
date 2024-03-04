package nl.thomas.xsd.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Trackpoint {

    private Long tpId;
    private LocalDateTime utc;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Integer heart;
    private Double distancePastSec;
    private Double totalDistance;
    private Double metersClimbedPastSec;
    private Double metersDescendedPastSec;
    @JsonProperty("runId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "runId")
    @JsonIdentityReference(alwaysAsId = true)
    private Run run;
    @JsonProperty("lapId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "lapId")
    @JsonIdentityReference(alwaysAsId = true)
    private Lap lap;

}
