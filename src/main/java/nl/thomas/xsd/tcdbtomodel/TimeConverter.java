package nl.thomas.xsd.tcdbtomodel;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class TimeConverter {

    static LocalDateTime getStartDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            throw new IllegalArgumentException("Date/time was null. Maybe not correctly parsed to XMLGregorianCalendar? " +
                    "Input format of TCX should be like this: 2024-03-03T06:56:25Z");
        }
        return ZonedDateTime.parse(xmlGregorianCalendar.toString()).toLocalDateTime();
    }

}
