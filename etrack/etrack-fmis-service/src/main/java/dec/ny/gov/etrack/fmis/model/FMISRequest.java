package dec.ny.gov.etrack.fmis.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class FMISRequest {
  @JsonProperty("ETRACKLoad")
  private ETRACKLoad eTrackLoad;
}
