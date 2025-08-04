package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GeographicalInquiryNoteView implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long inquiryNoteId;
  private Long inquiryId;
  private Integer actionTypeCode;
  private String actionDate;
  private String actionNote;
  private String comments;
}
