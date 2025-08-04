package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class ETRACKLoad implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("@xmlns")
  private String xmlnsURL;
  @JsonProperty("RESTHeader")
  private FMISHeader fmisHeader;
  @JsonProperty("InputParameters")
  private Invoice inputParameters;
}
