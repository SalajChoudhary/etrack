package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class CurrentStatus implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String status;
  private String statusDate;
  private String reason;
  private Long batchId;
}
