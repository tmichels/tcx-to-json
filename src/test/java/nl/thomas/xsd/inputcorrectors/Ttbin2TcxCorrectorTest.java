package nl.thomas.xsd.inputcorrectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Ttbin2TcxCorrectorTest {

    @Test
    void test(){
        String rawUncorrected = """
                      <Extensions>
                       <x:TPX>
                        <x:Speed>2.94</x:Speed>
                        <x:RunCadence>87.0</x:RunCadence>
                       </x:TPX>
                      </Extensions>
                      <Cadence>87.0</Cadence>
                     </Trackpoint>
                """;

        String actualCorrection = Ttbin2TcxCorrector.correct(rawUncorrected);

        assertThat(actualCorrection).isEqualTo("""
                      <Extensions>
                       <x:TPX>
                        <x:Speed>2.94</x:Speed>
                        <x:RunCadence>87</x:RunCadence>
                       </x:TPX>
                      </Extensions>
                      <Cadence>87</Cadence>
                     </Trackpoint>
                """);
    }

}
