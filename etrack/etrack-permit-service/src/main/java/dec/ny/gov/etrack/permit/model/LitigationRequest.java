package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class LitigationRequest implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String litigationStartDate;
  private String litigationEndDate;
  private String holdInd;
  private String modified;
  private Long litigationHoldId;
}
