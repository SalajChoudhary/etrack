package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;

import lombok.Data;

public @Data class DocumentArchivePurge implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long docId;
  private Boolean markForReview;
}
