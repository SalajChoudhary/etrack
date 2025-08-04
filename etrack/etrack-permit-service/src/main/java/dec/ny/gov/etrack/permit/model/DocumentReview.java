package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class DocumentReview implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String reviewerId;
  private String reviewerName;
  private List<Long> documentIds;
  private String dateAssigned;
  private String dueDate;
  private String reviewerEmail;
  private Long resultId;
  private String archiveDocInd;
  private String archiveType;
  private List<DocumentArchivePurge> documents;
  private Long reviewerRoleId;
}
