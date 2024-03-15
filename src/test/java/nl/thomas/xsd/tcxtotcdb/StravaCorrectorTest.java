package nl.thomas.xsd.tcxtotcdb;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StravaCorrectorTest {

    @Test
    void prefixInStravaExport_corrector_noPrefix() {
        String export = "          <?xml version=\"1.0\" encoding=\"utf-8\"?>";
        String expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

        String corrected = StravaCorrector.correct(export);

        assertThat(corrected).isEqualTo(expected);
    }

}