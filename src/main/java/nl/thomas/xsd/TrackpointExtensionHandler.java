package nl.thomas.xsd;

import com.garmin.xmlschemas.activityextension.v2.ActivityTrackpointExtensionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
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
import java.util.Optional;

@Slf4j
@Component
public class TrackpointExtensionHandler {

    void setTrackpointSpeedFromExtension(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        List<TrackpointT> trackpoints = TrainingCenterDatabaseExtractor.extractTrackpoints(trainingCenterDatabaseT);
        trackpoints.forEach(this::replaceIncorrectlyParsedExtension);
    }

    private void replaceIncorrectlyParsedExtension(TrackpointT trackpointT) {
        ExtensionsT extensions = trackpointT.getExtensions();
        if (extensions == null || extensions.getAny() == null || extensions.getAny().isEmpty()) {
            log.warn("No extensions found in trackpoint {}", trackpointT.getTime());
            return;
        }
        parseExtension(trackpointT).ifPresent(tp -> extensions.getAny().set(0, tp));
    }

    private Optional<ActivityTrackpointExtensionT> parseExtension(TrackpointT trackpointT) {
        String rawXml = getRawXmlStringForExtension(trackpointT);
        try {
            return Optional.of(convertXmlToObject(trackpointT, rawXml));
        } catch (JAXBException e) {
            log.error("The TrackpointExtension for trackpoint {} is invalid and cannot be processed: \"{}\"", trackpointT.getTime(), rawXml);
            return Optional.empty();
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
     * Rewrites for example the below XML
     * <?xml version="1.0" encoding="UTF-16"?>
     * <x:TPX xmlns:x="http://www.garmin.com/xmlschemas/ActivityExtension/v2" xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
     * <Speed xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">0.0</Speed>
     * </x:TPX>
     *
     * to
     *
     * <x:TPX xmlns:x="http://www.garmin.com/xmlschemas/ActivityExtension/v2">
     * <Speed xmlns="http://www.garmin.com/xmlschemas/ActivityExtension/v2">0.0</Speed>
     * </x:TPX>
     *
     * @param rawXml: Somehow the parser misreads the values of the extensions and interprets them as
     *                <Speed xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">0.0</Speed>
     *                where it should be
     *                <Speed xmlns="http://www.garmin.com/xmlschemas/ActivityExtension/v2">0.0</Speed>
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

    private ActivityTrackpointExtensionT convertXmlToObject(TrackpointT trackpointT, String rawXml) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(ActivityTrackpointExtensionT.class).createUnmarshaller();
        InputStream stream = new ByteArrayInputStream(rawXml.getBytes(StandardCharsets.UTF_8));
        Source source = new StreamSource(stream);

        JAXBElement<ActivityTrackpointExtensionT> root = unmarshaller.unmarshal(source, ActivityTrackpointExtensionT.class);
        ActivityTrackpointExtensionT activityTrackpointExtensionT = root.getValue();
        logIfAllValuesAreNull(trackpointT, rawXml, activityTrackpointExtensionT);
        return activityTrackpointExtensionT;
    }

    private void logIfAllValuesAreNull(TrackpointT trackpointT, String rawXml, ActivityTrackpointExtensionT activityTrackpointExtensionT) {
        if (allValuesOfExtensionAreNull(activityTrackpointExtensionT)) {
            log.warn("ActivityTrackpointExtension for {} was correctly parsed, but all values are null. Raw XML: {}", trackpointT.getTime(), rawXml);
        }
    }

    private boolean allValuesOfExtensionAreNull(ActivityTrackpointExtensionT activityTrackpointExtensionT) {
        return activityTrackpointExtensionT.getExtensions() == null &&
                activityTrackpointExtensionT.getCadenceSensor() == null &&
                activityTrackpointExtensionT.getSpeed() == null &&
                activityTrackpointExtensionT.getWatts() == null &&
                activityTrackpointExtensionT.getRunCadence() == null;
    }
}
