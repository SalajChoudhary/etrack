package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class PaymentCustomField implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @JsonProperty("FieldName")
  private String fieldName;
  @JsonProperty("FieldValue")
  private String fieldValue;
}
