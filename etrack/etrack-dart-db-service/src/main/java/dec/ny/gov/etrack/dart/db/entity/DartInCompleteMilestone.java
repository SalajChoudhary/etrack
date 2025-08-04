package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_PROJECT_MILESTONE_INCOMPLETE")
public @Data class DartInCompleteMilestone implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectMilestoneIncompleteId;
  private Long projectId;
  private Long applId;
  private Long batchId;
  private Date incompleteSentDate;
  private Date resubmissionDate;
  private Date createDate;
  private String createdById;
}
