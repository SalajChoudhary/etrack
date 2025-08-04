package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_PROJECT_MILESTONE")
public @Data class DartMilestone implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectMilestoneId;
  private Long projectId;
  private Long batchId;
  private Date projectReceivedDate;
  private Date incompleteSentDate;
  private Date completeSentDate;
  private Date finalDispositionDate;
  private Date additionalInfoRequestDate;
  private Date additionalInfoRecvdDate;
  private Date suspendedDate;
  private String suspensionReason;
  private Date unsuspendedDate;
  private Date authEffectiveDate;
  private Date authExpDate;
  private Date hearingDate;
  private Date enbDate;
  private Date fiveDayLetterRecvdDate;
  private Date fiveDayLetterResponseDate;
  private Date resubmissionRecvdDate;
  private Date deisCompleteDate;
  private Date feisCompleteDate;
  private Date seqrFindingsIssuedDate;
  private Date commentsDeadlineDate;
  private Date createDate;
  private String createdById;
  private Long edbApplId;
  private String edbCurrentStatusCode;
  private Long applId;
  private String edbPermitTypeCode;
  private String edbTransTypeCode;
  private String edbCurrentStatusDesc;
  private String edbGpInd;
  private String edbEaInd;
  private Date updateDate;
  private Date completenessDueDate;
  private Date permitteeRespDueDate;
  private Date dimsrDecisionDueDate;
}
