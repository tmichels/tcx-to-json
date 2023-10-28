package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrainingCenterDatabaseExtractor {

    static List<TrackpointT> extractTrackpoints(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        if (trainingCenterDatabaseT.getActivities() == null || trainingCenterDatabaseT.getActivities().getActivity() == null) {
            return new ArrayList<>();
        }
        List<ActivityT> activities = trainingCenterDatabaseT.getActivities().getActivity();
        return activities.stream()
                .flatMap(activityT -> getTrackpointsForActivity(activityT).stream())
                .collect(Collectors.toList());
    }

    private static List<TrackpointT> getTrackpointsForActivity(ActivityT activity) {
        List<ActivityLapT> laps = activity.getLap();
        return laps != null ?
                laps.stream()
                        .flatMap(activityLapT -> getTrackpointsForLaps(activityLapT).stream())
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }

    private static List<TrackpointT> getTrackpointsForLaps(ActivityLapT activityLapT) {
        return activityLapT.getTrack().stream()
                .flatMap(trackT -> getTrackpointsForTrack(trackT).stream())
                .collect(Collectors.toList());
    }

    private static List<TrackpointT> getTrackpointsForTrack(TrackT trackT) {
        return trackT.getTrackpoint() != null ?
                trackT.getTrackpoint() :
                new ArrayList<>();
    }

}
