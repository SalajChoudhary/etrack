package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT")
public @Data class Project implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_S")
  @SequenceGenerator(name = "E_PROJECT_S", sequenceName = "E_PROJECT_S", allocationSize = 1)
  private Long projectId;
  private String projectDesc;
  private Integer mailInInd;
  private Integer applicantTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String proposedUseCode;
  private String projectInitiatedUserId;  
//  private String bridgeIdNumber;
  private Integer constrnType;
  private Date proposedStartDate;
  private Date estmtdCompletionDate;
  private String strWaterbodyName;
  private String wetlandIds;
  private Integer validatedInd;
  private Date receivedDate;
  private Integer seqrInd;
  private String damType;
  private Integer foilReqInd;
  private Integer dimsrInd;
  private Date intentMailingDate;
  private Date proposedEffDate;
  private String assignedAnalystName;
  private String analystAssignedId;
  private Date analystAssignedDate;
  private Integer approvedPolygonChangeInd;
  private Integer rejectedInd;
  private String rejectedReason;
  private Integer originalSubmittalInd;
  private Integer dartProcessingCompleteInd;
  private Long assignedAnalystRoleId;
}
