package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
//@Table(name = "E_SPATIAL_INQ_DOCUMENT")
public @Data class SpatialInqDocumentEntity implements Serializable {

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
  private Long inquiryId;
  private String documentNm;
  private Long refDocumentId;
  private String refDocumentDesc;
  private Integer addlDocInd;
  private Integer fileCount;
}
