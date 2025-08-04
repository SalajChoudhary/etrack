package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

public @Data class ReviewedPermitData implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Map<String, List<ReviewedPermit>> reviewedPermits;
  
}
