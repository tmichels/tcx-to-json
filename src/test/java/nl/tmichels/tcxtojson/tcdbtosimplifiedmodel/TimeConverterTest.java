package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
class TimeConverterTest {

    @Test
    void calendarNull_convert_nullPointerException() {
        assertThatThrownBy(() -> TimeConverter.convert(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Date/time was null. Input format of TCX should be like this: " +
                        "2024-03-03T06:56:25Z or this 2024-03-03T06:56:25");
    }

    @Test
    void onlyDate_convert_nullPointerException() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory
                .newInstance()
                .newXMLGregorianCalendar("2024-03-22");

        assertThatThrownBy(() -> TimeConverter.convert(xmlGregorianCalendar))
                .isInstanceOf(DateTimeParseException.class)
                .hasMessage("Text '2024-03-22Z' could not be parsed at index 10");
    }

    @Test
    void calenderWithTimeZoneIndication_convert_converted() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory
                .newInstance()
                .newXMLGregorianCalendar("2024-03-22T12:44:21Z");
        assertThat(xmlGregorianCalendar.getTimezone()).isZero();

        ZonedDateTime converted = TimeConverter.convert(xmlGregorianCalendar);

        assertThat(converted).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2024, 3, 22, 12, 44, 21), ZoneId.of(ZoneOffset.UTC.toString())));
    }

    @Test
    void calenderWithoutTimeZoneIndication_convert_converted(CapturedOutput capturedOutput) throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory
                .newInstance()
                .newXMLGregorianCalendar("2024-03-22T12:44:21");
        assertThat(xmlGregorianCalendar.getTimezone()).isEqualTo(DatatypeConstants.FIELD_UNDEFINED);

        ZonedDateTime converted = TimeConverter.convert(xmlGregorianCalendar);

        assertThat(converted).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2024, 3, 22, 12, 44, 21), ZoneId.of(ZoneOffset.UTC.toString())));
        assertThat(capturedOutput.getOut()).contains("Provided XML date 2024-03-22T12:44:21 contains no timezone indication, UTC is assumed.");
    }

}