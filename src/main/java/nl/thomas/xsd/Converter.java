package nl.thomas.xsd;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class Converter {

    public TrainingCenterDatabaseT convert(String tcxContent) throws JAXBException {
        String correctedContent = TomTomCorrector.correct(tcxContent);
        Unmarshaller unmarshaller = createUnmarshaller();
        Source source = createSourceFromString(correctedContent);

        JAXBElement<TrainingCenterDatabaseT> root = unmarshaller.unmarshal(source, TrainingCenterDatabaseT.class);
        TrainingCenterDatabaseT trainingCenterDatabaseT = root.getValue();
        log.info("Converted text to TrainingCenterDatabaseT object with {} trackpoints",
                TrainingCenterDatabaseExtractor.extractTrackpoints(trainingCenterDatabaseT).size());
        return trainingCenterDatabaseT;
    }

    private Unmarshaller createUnmarshaller() throws JAXBException {
        return JAXBContext.newInstance(TrainingCenterDatabaseT.class, ActivityTrackpointExtensionT.class, ExtensionsT.class).createUnmarshaller();
    }

    private Source createSourceFromString(String tcxContent) {
        InputStream stream = new ByteArrayInputStream(tcxContent.getBytes(StandardCharsets.UTF_8));
        return new StreamSource(stream);
    }

}
