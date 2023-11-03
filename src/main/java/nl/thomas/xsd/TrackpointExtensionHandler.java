package nl.thomas.xsd;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class TrackpointExtensionHandler {

    void setTrackpointSpeedFromExtension(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        List<TrackpointT> trackpoints = TrainingCenterDatabaseExtractor.extractTrackpoints(trainingCenterDatabaseT);
        trackpoints.forEach(this::replaceIncorrectlyParsedExtension);
    }

    private void replaceIncorrectlyParsedExtension(TrackpointT trackpointT) {
        trackpointT
                .getExtensions()
                .getAny()
                .set(0, parseExtension(trackpointT));
    }

    private ActivityTrackpointExtensionT parseExtension(TrackpointT trackpointT) {
        String rawXml = getRawXmlStringForExtension(trackpointT);
        try {
            return convertXmlToObject(rawXml);
        } catch (JAXBException e) {
            log.error("The TrackpointExtension is invalid and cannot be processed: \"{}\"", rawXml, e);
            return null;
        }
    }

    private String getRawXmlStringForExtension(TrackpointT trackpointT) {
        Element trackpointExtension = (Element) trackpointT.getExtensions().getAny().getFirst();
        Document document = trackpointExtension.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        String rawXml = serializer.writeToString(trackpointExtension);
        return correctRawXml(rawXml);
    }

    /**
     * Rewrites for example the bolow XML
     * <?xml version="1.0" encoding="UTF-16"?>
     * <x:TPX xmlns:x="http://www.garmin.com/xmlschemas/ActivityExtension/v2" xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
     *     <Speed xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">0.0</Speed>
     * </x:TPX>
     *
     * <x:TPX xmlns:x="http://www.garmin.com/xmlschemas/ActivityExtension/v2">
     *     <Speed xmlns="http://www.garmin.com/xmlschemas/ActivityExtension/v2">0.0</Speed>
     * </x:TPX>
     *
     * @param rawXml: Somehow the parser misreads the values of the extensions and interprets them as
     *              <Speed xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">0.0</Speed>
     *              where it should be
     *              <Speed xmlns="http://www.garmin.com/xmlschemas/ActivityExtension/v2">0.0</Speed>
     * @return corrected XML without unnecessary headers (this allows generic replacement of tag in case other extensions are provided)
     */
    private String correctRawXml(String rawXml) {
        return rawXml
                .replace(
                        "<?xml version=\"1.0\" encoding=\"UTF-16\"?><x:TPX xmlns:x=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
                        "<x:TPX xmlns:x=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\">")
                .replace(
                        "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2",
                        "http://www.garmin.com/xmlschemas/ActivityExtension/v2");
    }

    private ActivityTrackpointExtensionT convertXmlToObject(String rawXml) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(ActivityTrackpointExtensionT.class).createUnmarshaller();
        InputStream stream = new ByteArrayInputStream(rawXml.getBytes(StandardCharsets.UTF_8));
        Source source = new StreamSource(stream);

        JAXBElement<ActivityTrackpointExtensionT> root = unmarshaller.unmarshal(source, ActivityTrackpointExtensionT.class);
        return root.getValue();
    }
}
