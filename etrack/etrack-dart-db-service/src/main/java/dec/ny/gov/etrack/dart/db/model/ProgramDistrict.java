package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ProgramDistrict implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String programDistrictCode;
  private String programDistrictIdentifier;
  private String edbProgramDistrictIdentifier;
}
