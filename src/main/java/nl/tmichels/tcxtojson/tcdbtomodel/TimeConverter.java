package nl.tmichels.tcxtojson.tcdbtomodel;

import lombok.extern.slf4j.Slf4j;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Slf4j
public class TimeConverter {

    private TimeConverter() {
        // Util class not to be instantiated
    }

    static LocalDateTime convert(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            throw new NullPointerException("Date/time was null. Input format of TCX should be like this: " +
                    "2024-03-03T06:56:25Z or this 2024-03-03T06:56:25");
        }
        setUtcTimeZoneIfMissing(xmlGregorianCalendar);
        return ZonedDateTime.parse(xmlGregorianCalendar.toString()).toLocalDateTime();
    }

    /**
     * Needed because Smashrun exports TCX files without timezone: their xml contains format like 2024-03-03T06:56:25.
     */
    private static void setUtcTimeZoneIfMissing(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            log.warn("Provided XML date {} contains no timezone indication, UTC is assumed.", xmlGregorianCalendar);
            xmlGregorianCalendar.setTimezone(0); // Set UTC timezone
        }
    }

}
