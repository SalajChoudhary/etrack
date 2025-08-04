package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class SpatialInquiryDocumentReview implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long giDocumentReviewId;
  private Long documentId;
  private Long inquiryId;
  private String documentDesc;
  private String documentNm;
  private String docReviewerId;
  private String docReviewerName;
  private Integer docReviewedInd;
  private Date reviewAssignedDate;
  private Date reviewDueDate;
  private Date createDate;
  private Long reviewGroupId;
}
