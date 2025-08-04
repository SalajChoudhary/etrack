package dec.ny.gov.etrack.dart.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class ErrorResponse {
  private String resultCode;
  private String resultMessage;
}
