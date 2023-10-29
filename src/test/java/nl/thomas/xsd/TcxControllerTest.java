package nl.thomas.xsd;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcxControllerTest {

    @InjectMocks
    private TcxController tcxController;
    @Mock
    private Converter converter;

    @Test
    void path_readPath_contentToConverter(CapturedOutput capturedOutput) throws JAXBException, IOException {
        tcxController.file("src/test/java/testfiles/invalidxml.txt");

        assertThat(capturedOutput.getOut()).contains("GET request to read src/test/java/testfiles/invalidxml.txt");
        verify(converter).convert("Dit bestandis geenTCX bestand");
    }

    @Test
    void content_readContent_contentToConverter(CapturedOutput capturedOutput) throws JAXBException {
        tcxController.getFromFileContent("dit is TCX tekst");

        assertThat(capturedOutput.getOut()).contains("GET request to read text with 16 characters");
        verify(converter).convert("dit is TCX tekst");
    }

}