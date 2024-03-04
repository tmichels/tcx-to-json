package nl.thomas.xsd.tcxtotcdb;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.*;

import java.util.ArrayList;
import java.util.List;

public abstract class TrainingCenterDatabaseExtractor {

    static List<TrackpointT> extractTrackpoints(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        List<ActivityLapT> activityLapTS = extractLaps(trainingCenterDatabaseT);
        if (activityLapTS == null) {
            return new ArrayList<>();
        }
        return getTrackpointsForLaps(activityLapTS);
    }

    private static List<ActivityLapT> extractLaps(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        if (trainingCenterDatabaseT.getActivities() == null || trainingCenterDatabaseT.getActivities().getActivity() == null) {
            return new ArrayList<>();
        }
        List<ActivityT> activities = trainingCenterDatabaseT.getActivities().getActivity();
        return activities.stream()
                .flatMap(activityT -> activityT.getLap().stream())
                .toList();
    }

    private static List<TrackpointT> getTrackpointsForLaps(List<ActivityLapT> laps) {
        return laps.stream()
                .flatMap(activityLapT -> getTrackpointsForLap(activityLapT).stream())
                .toList();
    }

    private static List<TrackpointT> getTrackpointsForLap(ActivityLapT activityLapT) {
        return activityLapT.getTrack().stream()
                .flatMap(trackT -> getTrackpointsForTrack(trackT).stream())
                .toList();
    }

    private static List<TrackpointT> getTrackpointsForTrack(TrackT trackT) {
        return trackT.getTrackpoint() != null ?
                trackT.getTrackpoint() :
                new ArrayList<>();
    }

}
