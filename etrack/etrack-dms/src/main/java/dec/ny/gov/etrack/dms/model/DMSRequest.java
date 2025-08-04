package dec.ny.gov.etrack.dms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class DMSRequest {

  @JsonProperty("userId")
  private String userId;
  @JsonProperty("clientId")
  private String clientId;
  private String documentId;
  private String guid;
  private String fileName;
  private Boolean includeContentMetaData;
  private String searchQueryCondition;
  private SearchScope searchScope;
  private Integer elementSeqNumber;
  private String contextId;
}
