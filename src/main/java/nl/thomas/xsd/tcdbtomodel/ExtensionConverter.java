package nl.thomas.xsd.tcdbtomodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import jakarta.xml.bind.JAXBElement;

import java.util.List;

public class ExtensionConverter {

    static List<Object> getJaxbExtensions(ExtensionsT extensions) {
        return extensions == null ?
                List.of() :
                extensions.getAny().stream()
                        .filter(e -> e instanceof JAXBElement<?>)
                        .map(e -> (JAXBElement<?>) e)
                        .map(JAXBElement::getValue)
                        .map(e -> (Object) e)
                        .toList();
    }
}
