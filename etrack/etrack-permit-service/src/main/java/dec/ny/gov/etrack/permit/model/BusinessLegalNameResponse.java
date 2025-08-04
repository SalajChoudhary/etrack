package dec.ny.gov.etrack.permit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class BusinessLegalNameResponse {
  private BusinessVerificationResponse responseInfo;
  private BusinessLegalNameInfo legalNameInformation;
}
