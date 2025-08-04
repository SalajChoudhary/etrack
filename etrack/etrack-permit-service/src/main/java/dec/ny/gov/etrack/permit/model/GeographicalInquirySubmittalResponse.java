package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GeographicalInquirySubmittalResponse implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long inqResponseId;
  private String responseSentInd;
  private String responseSentDate;
  private String inquiryCompletedInd;
  private String response;
}
