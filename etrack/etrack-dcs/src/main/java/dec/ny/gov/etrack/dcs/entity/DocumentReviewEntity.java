package dec.ny.gov.etrack.dcs.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class DocumentReviewEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long documentReviewId;
  private Long documentId;
  private String documentDesc;
  private String documentNm;
  private String docReviewerId;
  private String docReviewerName;
  private Integer docReviewedInd;
  private Date reviewAssignedDate;
  private Date reviewDueDate;
  private Long correspondenceId;
  private Date createDate;
}