package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateInBeatsPerMinuteT;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Lap;
import nl.thomas.xsd.model.Trackpoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TcdbLapConverter {

    List<Lap> convertLaps(ActivityT activityT) {
        return activityT.getLap().stream()
                .map(this::convertLap)
                .toList();
    }

    private Lap convertLap(ActivityLapT activityLapT) {
        return new Lap(
                TimeConverter.getStartDateTime(activityLapT.getStartTime()),
                activityLapT.getTotalTimeSeconds(),
                activityLapT.getMaximumSpeed(),
                activityLapT.getDistanceMeters(),
                activityLapT.getCalories(),
                getOptionalHrBpm(activityLapT.getAverageHeartRateBpm()),
                getOptionalHrBpm(activityLapT.getMaximumHeartRateBpm()),
                activityLapT.getIntensity().value(),
                activityLapT.getCadence(),
                activityLapT.getTriggerMethod(),
                getTrackpoints(),
                activityLapT.getNotes(),
                getJaxbExtensions(activityLapT.getExtensions())
        );
    }

    private Short getOptionalHrBpm(HeartRateInBeatsPerMinuteT heartRateBpm) {
        return heartRateBpm == null ? null : heartRateBpm.getValue();
    }

    private List<Trackpoint> getTrackpoints() {
        return new ArrayList<>();
    }

    private List<Object> getJaxbExtensions(ExtensionsT extensions) {
        return extensions == null ?
                List.of() :
                extensions.getAny().stream()
                        .filter(e -> e instanceof JAXBElement<?>)
                        .map(e -> (JAXBElement<?>) e)
                        .map(JAXBElement::getValue)
                        .map(e -> (Object) e)
                        .toList();
    }
}
