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
@Table(name="E_DOCUMENT_REVIEW")
public @Data class DocumentReviewEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_DOCUMENT_REVIEW_S")
  @SequenceGenerator(name = "E_DOCUMENT_REVIEW_S", sequenceName = "E_DOCUMENT_REVIEW_S", allocationSize = 1)
  private Long documentReviewId;
  private Long documentId;
  private Long correspondenceId;
  private Date createDate;
  private String createdById;
  private Date modifiedDate;
  private String modifiedById;
  private String docReviewerId;
  private String docReviewerName;
  private Integer docReviewedInd;
  private Date reviewAssignedDate;
  private Date reviewDueDate;
  private Long assignedReviewerRoleId;
}
