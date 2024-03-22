package nl.tmichels.tcxtojson.tcxtotcdb;

import java.util.regex.Pattern;

public final class TomTomCorrector {

    private TomTomCorrector() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Exports from TomTom contain a systematic error: the objects in the ActivityExtensionv2.xsd schema are not prefixed
     * with the reference to that namespace. E.g. instead of <x:Speed>, TomTom export contains <Speed>.
     */
    static String correct(String tcxContent) {
        return correctSpeedTag(tcxContent);
    }

    private static String correctSpeedTag(String tcxContent) {
        String withCorrectedPrefix = replaceRegex(
                tcxContent,
                "<Extensions>\n* *<x:TPX>\n* *<Speed>",
                "<Extensions><x:TPX><x:Speed>");
        String withCorrectedPrefixAndSuffix = replaceRegex(
                withCorrectedPrefix,
                "</Speed>\n* *</x:TPX>\n* *</Extensions>",
                "</x:Speed></x:TPX></Extensions>");
        return withCorrectedPrefixAndSuffix;
    }

    private static String replaceRegex(String tcxContent, String regex, String replacement) {
        Pattern compile = Pattern.compile(regex);
        return compile.matcher(tcxContent).replaceAll(replacement);
    }
}
