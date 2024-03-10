package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.tcxtotcdb.TcxParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestActivityProvider {

    static ActivityLapT getFirstLap(TrainingCenterDatabaseT tcdb) {
        return getFirstActivity(tcdb).getLap().getFirst();
    }

    static ActivityT getFirstActivity(String testFileName) throws JAXBException, IOException {
        return getFirstActivity(getTrainingCenterDatabaseT(testFileName));
    }

    static TrainingCenterDatabaseT getTrainingCenterDatabaseT(String testFileName) throws IOException, JAXBException {
        List<String> strings = Files.readAllLines(Path.of("src/test/java/testfiles/" + testFileName));
        return new TcxParser().parse(String.join("", strings));
    }

    static ActivityT getFirstActivity(TrainingCenterDatabaseT tcdb) {
        return tcdb.getActivities().getActivity().get(0);
    }
}
