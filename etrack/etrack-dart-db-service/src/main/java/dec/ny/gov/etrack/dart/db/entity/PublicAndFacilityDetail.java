package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class PublicAndFacilityDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long publicId;
  private Long edbDistrictId;
  private String facilityName;
  private String decId;
  private Long addressId; 
  private String street1; 
  private String street2; 
  private String city; 
  private String state; 
  private String country; 
  private String zip; 
  private String homePhoneNumber; 
  private String businessPhoneNumber; 
  private String cellPhoneNumber;
  private String emailAddress;
  private String displayName;
  private String legallyResponsibleTypeCode;
  private Integer ownerRec;
  private Integer roleTypeId;
}
