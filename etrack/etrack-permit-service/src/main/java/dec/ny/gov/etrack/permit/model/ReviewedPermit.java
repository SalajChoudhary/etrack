package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ReviewedPermit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long applicationId;
  private String permitTypeCode;
  private Long edbApplnId;
  private Long batchId;
  private String programId;
  private String transType;
  private String modifiedTransType;
  private String batchGroup;
  private Integer trackingInd;
}
