package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
//@Table(name = "E_SUPPORT_DOCUMENT")
public @Data class SupportDocumentEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long documentUid;
  private Long documentId;        
  private String ecmaasGuid;
  private String accessByDepOnlyInd;
  private String docReleasableCode;
  private String trackedApplicationId;
  private Integer documentTypeId;
  private Integer documentSubTypeId;
  private String documentStateCode;
  private String documentDesc;
  private Long documentSubTypeTitleId;
  private String docSubTypeOtherTxt;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Long projectId;
  private String documentNm;
  private Long refDocumentId;
  private String refDocumentDesc;
  private Integer addlDocInd;
  private Integer docConfInd;
  private Integer supportDocCategoryCode;
  private Integer fileCount;
  private Integer archiveReviewedInd;
  private Integer purgeReviewedInd;
  private Long arcPrgQueryResultId;
}
