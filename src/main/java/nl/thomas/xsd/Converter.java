package nl.thomas.xsd;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class Converter {

    public TrainingCenterDatabaseT convert(String tcxContent) throws JAXBException {
        Unmarshaller unmarshaller = createUnmarshaller();
        Source source = createSourceFromString(tcxContent);

        JAXBElement<TrainingCenterDatabaseT> root = unmarshaller.unmarshal(source, TrainingCenterDatabaseT.class);
        return root.getValue();
    }

    private Unmarshaller createUnmarshaller() throws JAXBException {
        return JAXBContext.newInstance(TrainingCenterDatabaseT.class).createUnmarshaller();
    }

    private Source createSourceFromString(String tcxContent) {
        InputStream stream = new ByteArrayInputStream(tcxContent.getBytes(StandardCharsets.UTF_8));
        return new StreamSource(stream);
    }

}
