package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import lombok.Data;

public @Data class MilestoneStatus implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long applId;
  private Long batchId;
  private String milestone;
  private Date milestoneDate;
}
