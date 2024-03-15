package nl.thomas.xsd.tcxtotcdb;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class TcxParser {

    public TrainingCenterDatabaseT parse(String tcxContent) throws JAXBException {
        String correctedContent = correctInput(tcxContent);
        TrainingCenterDatabaseT trainingCenterDatabaseT = unMarshal(correctedContent);
        log.info("Parsed text as TrainingCenterDatabaseT object with {} trackpoints",
                TrainingCenterDatabaseExtractor
                        .extractTrackpoints(trainingCenterDatabaseT)
                        .size());
        return trainingCenterDatabaseT;
    }

    private static String correctInput(String tcxContent) {
        String correctedContent = TomTomCorrector.correct(tcxContent);
        correctedContent = Ttbin2TcxCorrector.correct(correctedContent);
        correctedContent = StravaCorrector.correct(correctedContent);
        return correctedContent;
    }

    private TrainingCenterDatabaseT unMarshal(String correctedContent) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext
                .newInstance(TrainingCenterDatabaseT.class, ActivityTrackpointExtensionT.class)
                .createUnmarshaller();
        Source source = new StreamSource(
                new ByteArrayInputStream(
                        correctedContent.getBytes(
                                StandardCharsets.UTF_8)));

        return unmarshaller.unmarshal(source, TrainingCenterDatabaseT.class).getValue();
    }

}
