package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ActiveAdditionalPermit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long edbApplicationId;
  private String permitTypeCode;
  private String programId;
  private Long batchId;
}
