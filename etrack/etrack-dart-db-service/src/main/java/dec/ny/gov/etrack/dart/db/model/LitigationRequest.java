package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class LitigationRequest implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long litigationHoldId;
  private String litigationStartDate;
  private String litigationEndDate;
  private String holdInd;
}
