package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@ToString
public @Data class InvoiceResponseBody implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("OutputParameters")
  private Map<String, String> outputParameters;
}
