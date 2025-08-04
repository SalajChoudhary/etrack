package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class InvoiceStatusOutput implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("RESPONSEMSG")
  private String responseMessage;
}
