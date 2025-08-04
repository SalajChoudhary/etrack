package dec.ny.gov.etrack.dms.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import dec.ny.gov.etrack.dms.request.MetadataProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class SdisRequestObject {
  private String userId;
  private String clientId;
  private String documentId;
  private Integer attachmentFilesCount;
  private List<MetadataProperty> metadataProperties;
}
