package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import lombok.Data;

public @Data class Facility implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String facilityName;
  private String streetAddress;
  private String details;
  private String taxmap;
  private String direcions;
  private String streamwaterbody;
}
