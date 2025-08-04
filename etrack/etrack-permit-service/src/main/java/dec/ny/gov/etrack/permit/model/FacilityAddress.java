package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class FacilityAddress implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String street1;
  private String street2;
//  private String locationDirections;
  private String city;
  private String state;  
  private String country;
  private String zip;
  private String zipExtension;
//  private Long edbAddressId;
  private String phoneNumber;
}
