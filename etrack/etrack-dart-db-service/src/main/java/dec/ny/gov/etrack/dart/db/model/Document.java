package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

public @Data class Document implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long documentReviewId;
  private String documentTitle;
  private String description;
  private String uploadInd;
  private Long documentId;
  private String uploadDate;
  private Date uploadDateFormat;
  private String releasableCode;
  private Integer documentType;
  private Integer documentSubType;
//  private Long supportDocRefId;
  private Long documentTitleId;
  private String docReviewerId;
  private String docReviewerName;
  private String reviewAssignedDate;
  private String reviewDueDate;
  private String docReviewedInd;
  private String refDocumentDesc;
  private Integer fileCount;
  private Long correspondenceId;
  private List<String> documentTitles;
}
