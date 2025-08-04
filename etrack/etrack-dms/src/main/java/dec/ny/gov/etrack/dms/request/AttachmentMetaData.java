package dec.ny.gov.etrack.dms.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class AttachmentMetaData {

  private String contentType;
  private String contentSize;
  private String retrievalName;
  private Integer elementSequenceNumber;
}
