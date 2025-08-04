package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class FacilityAddress implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String zipExtension;
  private String phoneNumber;
}
