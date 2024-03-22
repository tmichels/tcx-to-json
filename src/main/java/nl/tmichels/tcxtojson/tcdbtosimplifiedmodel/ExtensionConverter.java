package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ExtensionsT;
import jakarta.xml.bind.JAXBElement;

import java.util.List;

public final class ExtensionConverter {

    private ExtensionConverter() {
        throw new IllegalStateException("Utility class");
    }

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

    static <T> List<T> filterExtensionsOfType(List<Object> jaxbExtensions, Class<T> type) {
        return jaxbExtensions.stream()
                .filter(type::isInstance)
                .map(a -> (T) a)
                .toList();
    }
}
