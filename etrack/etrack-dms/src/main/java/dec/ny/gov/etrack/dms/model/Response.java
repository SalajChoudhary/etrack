package dec.ny.gov.etrack.dms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Response {
  private String resultCode;
  private String errorCode;
  private String resultMessage;
  private String documentId;
}
