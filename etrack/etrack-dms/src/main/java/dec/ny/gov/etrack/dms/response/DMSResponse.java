package dec.ny.gov.etrack.dms.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public abstract @Data class DMSResponse {
  @JsonProperty("resultCode")
  private String resultCode;
  @JsonProperty("resultMessage")
  private String resultMessage;
  @JsonProperty("requestMetadataProperties")
  private List<RequestMetadataProperty> requestMetadataProperties;
}
