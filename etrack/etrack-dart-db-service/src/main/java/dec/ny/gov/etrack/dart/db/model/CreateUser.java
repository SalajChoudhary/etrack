package dec.ny.gov.etrack.dart.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class CreateUser {
  @JsonProperty("@xmlns")
  private String xmlns;
  
}
