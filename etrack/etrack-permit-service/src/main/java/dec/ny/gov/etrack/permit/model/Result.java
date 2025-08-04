package dec.ny.gov.etrack.permit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class Result {
  private String resultCode;
  private String resultMessage;
}
