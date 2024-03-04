package nl.thomas.xsd.model;

import lombok.Data;

@Data
public class Location {

    private Long locationId;
    private Double longitude;
    private Double latitude;
    private String city;
    private String street;
    private String countryCode;
    private String pitch;
    private String mapUrl;
    private String readableLocation;
    private String timeZone;

}
