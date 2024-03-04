package nl.thomas.xsd;

import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.tcdbtomodel.TcdbConverter;
import nl.thomas.xsd.tcxtotcdb.TcxParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TcxControllerTest {

    @InjectMocks
    private TcxController tcxController;
    @Mock
    private TcxParser tcxParser;
    @Mock
    private TcdbConverter tcdbConverter;

    @Test
    void path_readPathForTcdb_contentToParser(CapturedOutput capturedOutput) throws JAXBException, IOException {
        tcxController.fileForTcdb("src/test/java/testfiles/invalidxml.txt");

        assertThat(capturedOutput.getOut()).contains("GET request to read src/test/java/testfiles/invalidxml.txt");
        verify(tcxParser).parse("Dit bestandis geenTCX bestand");
        verifyNoInteractions(tcdbConverter);
    }

    @Test
    void content_readContentForTcdb_contentToParser(CapturedOutput capturedOutput) throws JAXBException {
        tcxController.getFromFileContentForTcdb("dit is TCX tekst");

        assertThat(capturedOutput.getOut()).contains("GET request to read text with 16 characters");
        verify(tcxParser).parse("dit is TCX tekst");
        verifyNoInteractions(tcdbConverter);
    }

    @Test
    void path_readPathForModel_contentToParser(CapturedOutput capturedOutput) throws JAXBException, IOException {
        tcxController.fileForModel("src/test/java/testfiles/invalidxml.txt");

        assertThat(capturedOutput.getOut()).contains("GET request to read src/test/java/testfiles/invalidxml.txt");
        verify(tcxParser).parse("Dit bestandis geenTCX bestand");
        verify(tcdbConverter).convert(any());
    }

    @Test
    void content_readContentForModel_contentToParser(CapturedOutput capturedOutput) throws JAXBException {
        tcxController.getFromFileContentForModel("dit is TCX tekst");

        assertThat(capturedOutput.getOut()).contains("GET request to read text with 16 characters");
        verify(tcxParser).parse("dit is TCX tekst");
        verify(tcdbConverter).convert(any());
    }

}