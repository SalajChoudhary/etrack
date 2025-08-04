package dec.ny.gov.etrack.permit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class FacilityDetail {
  @Id
  @Column(name="PROJECT_ID")
  private String projectId;
  @Column(name="FACILITY_NAME")
  private String facilityName;
  @Column(name="DEC_ID")
  private String decId;
  @Column(name="DEC_ID_FORMATTED")
  private String decIdFormatted;
//  @Column(name="LOCATION_DIRECTIONS")
//  private String locationDirections;
  @Column(name="CITY")
  private String city;
  @Column(name="STATE")
  private String state;
  @Column(name="COUNTRY")
  private String country;
  @Column(name="ZIP")
  private String zip;
  @Column(name="ZIP_EXTENSION")
  private String zipExtension;
  @Column(name="CHANGE_COUNTER")
  private String changeCounter;
  @Column(name="PHONE_NUMBER")
  private String phoneNumber;
}
