package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public  @Data class SWFacilityType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Integer swFacilityType;
  private List<SWFacilitySubType> swFacilitySubTypes;
}
