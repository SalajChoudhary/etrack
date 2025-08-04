package dec.ny.gov.etrack.dms.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class IngestionRequest {
  private Integer attachmentFilesCount;
  private String clientId;
  private String userId;
  private String guid;
  private String contextId;
  @JsonProperty("metadataProperties")
  private Map<String, String> metaDataProperties;
}
