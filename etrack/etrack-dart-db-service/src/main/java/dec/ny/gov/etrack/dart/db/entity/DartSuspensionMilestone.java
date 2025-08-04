package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_PROJECT_MILESTONE_SUSPENSION")
public @Data class DartSuspensionMilestone implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectMilestoneSuspensionId;
  private Long projectId;
  private Long applId;
  private Date suspendedDate;
  private Date unsuspendedDate;
  private String suspensionReason;
  private Long batchId;
  private Date createDate;
  private String createdById;
}
