package dec.ny.gov.etrack.dcs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class IngestionResponse {
  private String resultCode;
  private String errorCode;
  private String resultMessage;
  private String documentId;
  private String guid;
  @JsonProperty("ingestionMetaData")
  private IngestionRequest ingestionRequest;

}
