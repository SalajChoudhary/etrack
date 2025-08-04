package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;

import lombok.Data;

public @Data class PurgeArchiveResultDocument implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long documentId;
  private Long edbDistrictId;
  private String markForReview;
  private Long docCategory;
  private Long docSubCategory;
  private Long otherDocSubCategory;
  private String documentName;
  private String docTypeDesc;
  private String docSubTypeDesc;
  private Long projectId;
  private String decId;
  private String decIdFormatted;
  private String facilityName;
  private String municipalityName;
  private Boolean litigationHoldInd; 
}
