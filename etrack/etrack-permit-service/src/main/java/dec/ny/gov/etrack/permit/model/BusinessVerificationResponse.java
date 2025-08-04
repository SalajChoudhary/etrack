package dec.ny.gov.etrack.permit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class BusinessVerificationResponse {
  private String custTransactionId;
  private String responseMessage;
  private String errorMessage;
}
