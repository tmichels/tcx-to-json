package nl.thomas.xsd.tcdbtomodel;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public abstract class TimeConverter {

    static LocalDateTime getStartDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            throw new IllegalArgumentException("Date/time was null. Maybe not correctly parsed to XMLGregorianCalendar? " +
                    "Input format of TCX should be like this: 2024-03-03T06:56:25Z");
        }
        setUtcTimeZoneIfMissing(xmlGregorianCalendar);
        return ZonedDateTime.parse(xmlGregorianCalendar.toString()).toLocalDateTime();
    }

    private static void setUtcTimeZoneIfMissing(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            xmlGregorianCalendar.setTimezone(0); // Set UTC timezone
        }
    }

}
