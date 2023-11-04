package nl.thomas.xsd;

import java.util.regex.Pattern;

public class TomTomCorrector {

    /**
     * Exports from TomTom contain a systematic error: the objects in the ActivityExtensionv2.xsd schema are not prefixed
     * with the reference to that namespace. E.g. instead of <x:Speed>, TomTom export contains <Speed>.
     */
    public static String correct(String tcxContent) {
        return correctSpeedTag(tcxContent);
    }

    private static String correctSpeedTag(String tcxContent) {
        String withCorrectedPrefix = replaceRegex(tcxContent, "<Extensions>\n* *<x:TPX>\n* *<Speed>", "<Extensions><x:TPX><x:Speed>");
        return replaceRegex(withCorrectedPrefix, "</Speed>\n* *</x:TPX>\n* *</Extensions>", "</x:Speed></x:TPX></Extensions>");
    }

    private static String replaceRegex(String tcxContent, String regex, String replacement) {
        Pattern compile = Pattern.compile(regex);
        return compile.matcher(tcxContent).replaceAll(replacement);
    }
}
