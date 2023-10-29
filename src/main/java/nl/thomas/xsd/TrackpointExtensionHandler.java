package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@Slf4j
@Component
public class TrackpointExtensionHandler {

    void setTrackpointSpeedFromExtension(TrainingCenterDatabaseT trainingCenterDatabaseT) {
        List<TrackpointT> trackpoints = TrainingCenterDatabaseExtractor.extractTrackpoints(trainingCenterDatabaseT);
        trackpoints.forEach(trackpointT -> trackpointT.setSpeed(getSpeedFromExtensions(trackpointT)));
    }

    private Double getSpeedFromExtensions(TrackpointT trackpointT) {
        XMLGregorianCalendar trackpointTime = trackpointT.getTime();
        if (trackpointT.getExtensions() == null || trackpointT.getExtensions().getAny() == null || trackpointT.getExtensions().getAny().isEmpty()) {
            log.warn("No extensions found in trackpoint {}", trackpointTime);
            return null;
        }
        List<Object> extensions = trackpointT.getExtensions().getAny();
        for (Object extension : extensions) {
            Double speedFromExtension = getSpeedFromExtension((Element) extension, trackpointTime);
            if (speedFromExtension != null) {
                return speedFromExtension;
            }
        }
        return null;
    }

    private Double getSpeedFromExtension(Element element, XMLGregorianCalendar trackpointTime) {
        if ("TPX".equals(element.getLocalName())) {
            return getSpeedFromTPX(element, trackpointTime);
        } else {
            warnForUnexpectedExtensionNodes(element, trackpointTime);
            return null;
        }
    }

    private Double getSpeedFromTPX(Element element, XMLGregorianCalendar trackpointTime) {
        NodeList childNodes = element.getChildNodes();
        int childNodesLength = childNodes.getLength();
        warnForUnexpectedNumberOfTpxNodes(childNodesLength, trackpointTime);
        for (int i = 0; i < childNodesLength; i++) {
            Double speed = getSpeedFromNode(trackpointTime, childNodes, i);
            if (speed != null) {
                return speed;
            }
        }
        return null;
    }

    private Double getSpeedFromNode(XMLGregorianCalendar trackpointTime, NodeList childNodes, int i) {
        Node node = childNodes.item(i);
        if (node.getLocalName().equals("Speed")) {
            return Double.parseDouble(node.getTextContent());
        } else {
            warnForUnexpectedTpxNodeTypes(node, trackpointTime);
            return null;
        }
    }

    private void warnForUnexpectedNumberOfTpxNodes(int childNodesLength, XMLGregorianCalendar trackpointTime) {
        if (childNodesLength != 1) {
            log.warn(
                    "TPX node for Trackpoint {} did not have the expected amount of 1 children, but {} children",
                    trackpointTime,
                    childNodesLength);
        }
    }

    private void warnForUnexpectedTpxNodeTypes(Node node, XMLGregorianCalendar trackpointTime) {
        log.warn(
                "Instead of speed node, the node {} with value {} was found for trackpoint {}",
                node.getNodeName(),
                node.getTextContent(),
                trackpointTime);
    }

    private void warnForUnexpectedExtensionNodes(Element element, XMLGregorianCalendar trackpointTime) {
        log.warn("Trackpoint {} had another other extension \"{}\" than the expected \"x:TPX\" extension",
                trackpointTime,
                element.getNodeName());
    }
}
