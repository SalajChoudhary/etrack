package dec.ny.gov.etrack.fmis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class ErrorResponse {
  private String errorCode;
  private String errorMessage;
}
