package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ReviewCompletionDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long correspondenceId;
  private String reviewerId;
  private Long documentId;
  private Long documentReviewId;
  private String docReviewerName;
}

