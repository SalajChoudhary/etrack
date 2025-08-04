package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import lombok.Data;

public @Data class SpatialInqDocument implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Integer spatialInqCategoryId;
  private String documentTitle;
  private String spatialInqCategoryCode;
  private String spatialInqCategoryDesc;
  private Integer documentType;
  private Integer documentSubType;
  private Integer reqdDocumentInd;
  private String uploadInd;
  private Long documentId;
  private Integer documentTitleId;
}
