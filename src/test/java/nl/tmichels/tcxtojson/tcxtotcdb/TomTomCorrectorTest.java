package nl.tmichels.tcxtojson.tcxtotcdb;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TomTomCorrectorTest {

    @Test
    void test() {
        String content = """
                <Extensions>
                              <x:TPX>
                                <Speed>0.0</Speed>
                              </x:TPX>
                            </Extensions>""";

        String corrected = TomTomCorrector.correct(content);

        assertThat(corrected).isEqualTo("<Extensions><x:TPX><x:Speed>0.0</x:Speed></x:TPX></Extensions>");
    }

}