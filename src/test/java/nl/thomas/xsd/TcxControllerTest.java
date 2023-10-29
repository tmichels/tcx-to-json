package nl.thomas.xsd;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TcxControllerTest {

    @InjectMocks
    private TcxController tcxController;
    @Mock
    private Converter converter;
    private String TEST_FILE_LOCATION = "/home/thomas/projecten/hardlopen/backend-xsd-reader/src/test/java/testfiles/invalidxml.txt";

    @Test
    void path_readPath_contentToConverter() throws JAXBException, IOException {
        tcxController.file(TEST_FILE_LOCATION);

        verify(converter).convert("""
                Dit bestand
                is geen
                TCX bestand""");
    }

    @Test
    void content_readContent_contentToConverter() throws JAXBException {
        tcxController.getFromFileContent("dit is TCX tekst");

        verify(converter).convert("dit is TCX tekst");
    }

}