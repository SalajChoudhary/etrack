package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class FacilityLRP implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String decId;
  private String edbDistrictId;
  private String facilityname;
  private List<String> activeLRPs;
}
