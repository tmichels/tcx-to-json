package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.*;
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

    private final TrackpointExtensionHandler trackpointExtensionHandler;

    public Converter(TrackpointExtensionHandler trackpointExtensionHandler) {
        this.trackpointExtensionHandler = trackpointExtensionHandler;
    }

    public TrainingCenterDatabaseT convert(String tcxContent) throws JAXBException {
        Unmarshaller unmarshaller = createUnmarshaller();
        Source source = createSourceFromString(tcxContent);

        JAXBElement<TrainingCenterDatabaseT> root = unmarshaller.unmarshal(source, TrainingCenterDatabaseT.class);
        TrainingCenterDatabaseT trainingCenterDatabaseT = root.getValue();
        trackpointExtensionHandler.setTrackpointSpeedFromExtension(trainingCenterDatabaseT);
        log.info("Converted text to TrainingCenterDatabaseT object with {} trackpoints",
                TrainingCenterDatabaseExtractor.extractTrackpoints(trainingCenterDatabaseT).size());
        return trainingCenterDatabaseT;
    }

    private Unmarshaller createUnmarshaller() throws JAXBException {
        return JAXBContext.newInstance(TrainingCenterDatabaseT.class).createUnmarshaller();
    }

    private Source createSourceFromString(String tcxContent) {
        InputStream stream = new ByteArrayInputStream(tcxContent.getBytes(StandardCharsets.UTF_8));
        return new StreamSource(stream);
    }

}
