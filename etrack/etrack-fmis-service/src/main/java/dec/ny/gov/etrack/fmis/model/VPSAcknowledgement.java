package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class VPSAcknowledgement implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("TransactionID")
  private String transactionId;
  @JsonProperty("ConfAcknowledge")
  private Integer acknowledgeInd;
}
