package nl.thomas.xsd.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Lap {

    private Long lapId;
    private Integer lapNr;
    private LocalDateTime lapStartUtc;
    private Double speedMax;
    private String triggerMethod;
    private Integer calories;
    @JsonProperty("runId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "runId")
    @JsonIdentityReference(alwaysAsId = true)
    private Run run;
    @JsonIgnore
    private List<Trackpoint> trackpoints;

}
