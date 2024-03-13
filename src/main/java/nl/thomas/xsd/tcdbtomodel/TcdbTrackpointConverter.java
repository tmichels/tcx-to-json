package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import lombok.extern.slf4j.Slf4j;
import nl.thomas.xsd.model.Trackpoint;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class TcdbTrackpointConverter {

    public List<Trackpoint> convertTrackpoints(ActivityLapT activityLapT) {
        return activityLapT.getTrack().stream() // Cannot be null, see generated class.
                .map(TrackT::getTrackpoint) // Cannot be null, see generated class.
                .flatMap(Collection::stream)
                .map(this::convertTp)
                .toList();
    }

    private Trackpoint convertTp(TrackpointT trackpointT) {
        PositionT position = trackpointT.getPosition();
        ActivityTrackpointExtensionT trackpointExtensionT = getTrackpointExtension(trackpointT);


        return new Trackpoint(
                TimeConverter.getStartDateTime(trackpointT.getTime()),
                position != null ? position.getLatitudeDegrees() : null,
                position != null ? position.getLongitudeDegrees() : null,
                trackpointT.getAltitudeMeters(),
                trackpointT.getDistanceMeters(),
                trackpointT.getHeartRateBpm().getValue(),
                getCadence(trackpointT, trackpointExtensionT),
                trackpointExtensionT.getSpeed()
        );
    }

    private static Short getCadence(TrackpointT trackpointT, ActivityTrackpointExtensionT trackpointExtensionT) {
        Short cadenceFromTp = trackpointT.getCadence();
        Short cadenceFromExt = trackpointExtensionT.getRunCadence();

        if (cadenceFromTp == null && cadenceFromExt != null) {
            return cadenceFromExt;
        } else {
            return cadenceFromTp;
        }
    }

    private static ActivityTrackpointExtensionT getTrackpointExtension(TrackpointT trackpointT) {
        List<Object> jaxbExtensions = ExtensionConverter.getJaxbExtensions(trackpointT.getExtensions());
        List<ActivityTrackpointExtensionT> tpExtension = jaxbExtensions.stream()
                .filter(o -> o instanceof ActivityTrackpointExtensionT)
                .map(a -> (ActivityTrackpointExtensionT) a)
                .toList();
        if (tpExtension.size() != 1) {
            log.warn("Unexpected amount of {} ActivityTrackpointExtensionT for Trackpoint {}", tpExtension.size(), trackpointT);
        }
        if (tpExtension.size() < jaxbExtensions.size()) {
            log.warn("Unexpected type of Trackpoint extensions was found: {}", jaxbExtensions.stream());
        }
        return tpExtension.getFirst();
    }

}
