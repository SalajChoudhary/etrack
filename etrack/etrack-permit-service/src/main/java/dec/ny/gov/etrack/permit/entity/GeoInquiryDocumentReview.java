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
@Table(name="E_GI_DOCUMENT_REVIEW")
public @Data class GeoInquiryDocumentReview implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_GI_DOCUMENT_REVIEW_S")
  @SequenceGenerator(name = "E_GI_DOCUMENT_REVIEW_S", sequenceName = "E_GI_DOCUMENT_REVIEW_S", allocationSize = 1)
  private Long giDocumentReviewId;
  private Long documentId;
  private String docReviewerId;
  private String docReviewerName;
  private Date reviewAssignedDate;
  private Date reviewDueDate;
  private Integer docReviewedInd;
  private Date createDate;
  private String createdById;
  private Date modifiedDate;
  private String modifiedById;
  private Long reviewGroupId;
  private Long assignedReviewerRoleId;
}
