package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GeographicalInquiryResponse implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long inqResponseId;
  private String responseSentInd;
  private String inquiryCompletedInd;
  private String responseSentDate;
  private String response;
  
}
