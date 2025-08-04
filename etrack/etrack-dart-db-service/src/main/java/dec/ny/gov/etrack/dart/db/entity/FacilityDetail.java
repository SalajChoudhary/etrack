package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class FacilityDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectId;
  private String facilityName;
  private String decId;
  private String decIdFormatted;
  private String locationDirections;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String zipExtension;
  private String changeCounter;
  private String phoneNumber;
  private Long edbDistrictId; 
}
