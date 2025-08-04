package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class Address implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String addressLine1;
  private String addressLine2;
  private String city;
  private String state;
  private String postalCodeZip;
  private String countryCode;
  
}
