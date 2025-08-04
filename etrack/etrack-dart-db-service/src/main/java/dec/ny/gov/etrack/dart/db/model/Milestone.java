package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class Milestone implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private List<CurrentStatus> currentStatuses;
//  private String currentStatus;
//  private String statusDate;
//  private String suspendReason;
  private List<BatchDetail> batchDetails;
  private String reviewDate;
}
