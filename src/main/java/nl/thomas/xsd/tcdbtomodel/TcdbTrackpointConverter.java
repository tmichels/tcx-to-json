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
import java.util.Optional;

@Component
@Slf4j
public class TcdbTrackpointConverter {

    List<Trackpoint> convertTrackpoints(ActivityLapT activityLapT) {
        return activityLapT.getTrack().stream() // Cannot be null, see generated class.
                .map(TrackT::getTrackpoint) // Cannot be null, see generated class.
                .flatMap(Collection::stream)
                .map(this::convertTp)
                .toList();
    }

    private Trackpoint convertTp(TrackpointT trackpointT) {
        PositionT position = getPosition(trackpointT);
        Optional<ActivityTrackpointExtensionT> trackpointExtensionT = getTrackpointExtension(trackpointT);

        return new Trackpoint(
                TimeConverter.getStartDateTime(trackpointT.getTime()),
                position != null ? position.getLatitudeDegrees() : null,
                position != null ? position.getLongitudeDegrees() : null,
                trackpointT.getAltitudeMeters(),
                trackpointT.getDistanceMeters(),
                trackpointT.getHeartRateBpm() == null ? null : trackpointT.getHeartRateBpm().getValue(),
                getCadence(trackpointT, trackpointExtensionT),
                trackpointExtensionT.map(ActivityTrackpointExtensionT::getSpeed).orElse(null)
        );
    }

    private static PositionT getPosition(TrackpointT trackpointT) {
        PositionT position = trackpointT.getPosition();
        if (position == null) {
            log.warn("No latitude and longitude available for trackpoint on {}", trackpointT.getTime());
        }
        return position;
    }

    private static Optional<ActivityTrackpointExtensionT> getTrackpointExtension(TrackpointT trackpointT) {
        List<Object> allExtensions = ExtensionConverter.getJaxbExtensions(trackpointT.getExtensions());
        List<ActivityTrackpointExtensionT> atpExtensions = ExtensionConverter.filterExtensionsOfType(allExtensions, ActivityTrackpointExtensionT.class);
        if (atpExtensions.isEmpty()) {
            log.warn("No TrackpointExtension for trackpoint on {}", trackpointT.getTime());
            return Optional.empty();
        }
        if (atpExtensions.size() > 1) {
            log.warn("Unexpected amount of {} ActivityTrackpointExtensionT for Trackpoint on {}", atpExtensions.size(), trackpointT.getTime());
        }
        if (atpExtensions.size() < allExtensions.size()) {
            log.warn("Unexpected type of Trackpoint extensions was found for Trackpoint on {}: {}", trackpointT.getTime(), allExtensions.stream().toList().stream().map((Object o) -> o.getClass().getName()).toList());
        }
        return Optional.of(atpExtensions.getFirst());
    }

    private static Short getCadence(TrackpointT trackpointT, Optional<ActivityTrackpointExtensionT> trackpointExtensionT) {
        Short cadenceFromTp = trackpointT.getCadence();
        Short cadenceFromExt = trackpointExtensionT.map(ActivityTrackpointExtensionT::getRunCadence).orElse(null);

        if (cadenceFromExt != null && cadenceFromTp != null && !cadenceFromExt.equals(cadenceFromTp)) {
            log.warn("Conflicting values for cadence in trackpoint on {}: extension: {}, trackpoint value: {}",
                    trackpointT.getTime(),
                    cadenceFromExt,
                    cadenceFromTp);
        }

        if (cadenceFromTp == null && cadenceFromExt != null) {
            return cadenceFromExt;
        } else {
            return cadenceFromTp;
        }
    }

}
