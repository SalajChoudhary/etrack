package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class DIMSRPermit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String permitTypeCode;
  private String transType;
  private Long edbApplnId;
  private Long batchId;
  private String programId;
  private Integer edbTrackingInd;
}
