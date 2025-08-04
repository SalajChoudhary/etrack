package dec.ny.gov.etrack.dms.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class ECMaaSRequest {

  private String userId;
  private String clientId;
  private int attachmentFilesCount;
  private List<MetadataProperty> metadataProperties;

}
