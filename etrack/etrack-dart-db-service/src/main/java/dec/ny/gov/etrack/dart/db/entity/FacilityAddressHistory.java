package dec.ny.gov.etrack.dart.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
//@Table(name = "E_FACILITY_ADDRESS_H")
public @Data class FacilityAddressHistory {
  
  @Id
  private Long facilityAddressHId;
  private Long hProjectId;
  private String hLocationDirections;
  private String hCity;
  private String hState;
  private String hCountry;
  private String hZip;
  private String hZipExtension;
//  private String hEdbAddressId;
  private String hPhoneNumber;
  @Column(name="h_street1")
  private String hStreet1;
  @Column(name="h_street2")
  private String hStreet2;
}
