package dec.ny.gov.etrack.dart.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class InputParameters {
  @JsonProperty("X_USER_NAME")
  private String xUserName;
}
