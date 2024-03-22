package nl.tmichels.tcxtojson.tcxtotcdb;

public final class StravaCorrector {

    private StravaCorrector() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Strava TCX exports contain empty spaces as prefix. Without removing it, a SAXParseException will be thrown.
     */
    static String correct(String tcxContent) {
        return tcxContent.trim();
    }
}
