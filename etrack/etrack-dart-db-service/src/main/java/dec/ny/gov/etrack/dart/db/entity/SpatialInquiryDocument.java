package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import lombok.Data;

public @Data class SpatialInquiryDocument implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String documentTitle;
  private Long documentSubTypeTitleId;
  private Integer documentSubTypeId;
  private Integer uploadedInd;
  private Integer reqdDocumentInd;
  private Integer documentTypeId;
  private String refDocumentDesc;
  private Long documentId;
  private String refDocument;
  private String documentStateCode;
}
