package nl.thomas.xsd;

import jakarta.xml.bind.JAXBException;
import nl.thomas.xsd.tcdbtomodel.TcdbRunConverter;
import nl.thomas.xsd.tcxtotcdb.TcxParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.nio.file.Path;

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
    private TcdbRunConverter tcdbRunConverter;

    @Test
    void path_pathToTcdb_contentToParser(CapturedOutput capturedOutput) throws JAXBException, IOException {
        String relativePath = "src/test/java/testfiles/invalidxml.txt";

        tcxController.pathToTcdb(relativePath);

        assertThat(capturedOutput.getOut()).contains("Received POST request to read " + Path.of(relativePath).toAbsolutePath());
        verify(tcxParser).parse("Dit bestandis geenTCX bestand");
        verifyNoInteractions(tcdbRunConverter);
    }

    @Test
    void content_contentToTcdb_contentToParser(CapturedOutput capturedOutput) throws JAXBException {
        tcxController.fileContentToTcdb("dit is TCX tekst");

        assertThat(capturedOutput.getOut()).contains("Received POST request to read text with 16 characters");
        verify(tcxParser).parse("dit is TCX tekst");
        verifyNoInteractions(tcdbRunConverter);
    }

    @Test
    void path_pathToSimplified_contentToParser(CapturedOutput capturedOutput) throws JAXBException, IOException {
        String relativePath = "src/test/java/testfiles/invalidxml.txt";

        tcxController.pathToSimplified(relativePath);

        assertThat(capturedOutput.getOut()).contains("Received POST request to read " + Path.of(relativePath).toAbsolutePath());
        verify(tcxParser).parse("Dit bestandis geenTCX bestand");
        verify(tcdbRunConverter).convert(any());
    }

    @Test
    void content_contentToSimplified_contentToParser(CapturedOutput capturedOutput) throws JAXBException {
        tcxController.contentToSimplified("dit is TCX tekst");

        assertThat(capturedOutput.getOut()).contains("Received POST request to read text with 16 characters");
        verify(tcxParser).parse("dit is TCX tekst");
        verify(tcdbRunConverter).convert(any());
    }

}