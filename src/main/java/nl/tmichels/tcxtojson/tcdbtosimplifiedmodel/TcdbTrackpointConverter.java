package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import lombok.extern.slf4j.Slf4j;
import nl.tmichels.tcxtojson.simplifiedmodel.Trackpoint;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TcdbTrackpointConverter {

    Trackpoint convertTrackpoint(TrackpointT trackpointT) {
        Optional<PositionT> position = getPosition(trackpointT);
        ActivityTrackpointExtensionT trackpointExtension = getTrackpointExtension(trackpointT);

        return new Trackpoint(
                getTime(trackpointT),
                position.isPresent() ? position.get().getLatitudeDegrees() : null,
                position.isPresent() ? position.get().getLongitudeDegrees() : null,
                trackpointT.getAltitudeMeters(),
                trackpointT.getDistanceMeters(),
                trackpointT.getHeartRateBpm() == null ? null : trackpointT.getHeartRateBpm().getValue(),
                getCadence(trackpointT, trackpointExtension),
                trackpointExtension.getSpeed()
        );
    }

    private Optional<PositionT> getPosition(TrackpointT trackpointT) {
        PositionT position = trackpointT.getPosition();
        if (position == null) {
            log.warn("No latitude and longitude available for trackpoint on {}", trackpointT.getTime());
            return Optional.empty();
        }
        return Optional.of(position);
    }

    private ActivityTrackpointExtensionT getTrackpointExtension(TrackpointT trackpointT) {
        List<Object> allExtensions = ExtensionConverter.getJaxbExtensions(trackpointT.getExtensions());
        List<ActivityTrackpointExtensionT> atpExtensions = ExtensionConverter.filterExtensionsOfType(allExtensions, ActivityTrackpointExtensionT.class);
        if (atpExtensions.isEmpty()) {
            log.warn("No TrackpointExtension for trackpoint on {}", trackpointT.getTime());
            return new ActivityTrackpointExtensionT();
        }
        if (atpExtensions.size() > 1) {
            log.warn("Unexpected amount of {} ActivityTrackpointExtensionT for Trackpoint on {}", atpExtensions.size(), trackpointT.getTime());
        }
        if (atpExtensions.size() < allExtensions.size()) {
            log.warn("Unexpected type of Trackpoint extensions was found for Trackpoint on {}: {}", trackpointT.getTime(), allExtensions.stream().toList().stream().map((Object o) -> o.getClass().getName()).toList());
        }
        return atpExtensions.getFirst();
    }

    private ZonedDateTime getTime(TrackpointT trackpointT) {
        if (trackpointT.getTime() == null) {
            log.warn("Trackpoint with distance {} has no timeStamp", trackpointT.getDistanceMeters());
            return null;
        }
        return TimeConverter.convert(trackpointT.getTime());
    }

    private Short getCadence(TrackpointT trackpointT, ActivityTrackpointExtensionT trackpointExtensionT) {
        if (trackpointExtensionT.getRunCadence() != null && trackpointT.getCadence() != null && !trackpointExtensionT.getRunCadence().equals(trackpointT.getCadence())) {
            log.warn("Conflicting values for cadence in trackpoint on {}: extension: {}, trackpoint value: {}",
                    trackpointT.getTime(),
                    trackpointExtensionT.getRunCadence(),
                    trackpointT.getCadence());
        }

        if (trackpointT.getCadence() == null && trackpointExtensionT.getRunCadence() != null) {
            return trackpointExtensionT.getRunCadence();
        } else {
            return trackpointT.getCadence();
        }
    }

}
