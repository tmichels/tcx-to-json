package nl.tmichels.tcxtojson.tcxtotcdb;


public abstract class Ttbin2TcxCorrector {

    /**
     * The tcx files generated by <a href="https://github.com/alexspurven/ttbin2tcx/">ttbin2tcx</a> contain incorrect cadence formatting.
     * According to the XSD, this should be an unsignedByte, but the TCX files contain a double that always ends on ".0".
     */
    static String correct(String tcxContent) {
        return correctCadenceFormatting(tcxContent);
    }

    private static String correctCadenceFormatting(String tcxContent) {
        return tcxContent
                .replaceAll(".0</Cadence>", "</Cadence>")
                .replaceAll(".0</x:RunCadence>", "</x:RunCadence>");
    }
}
