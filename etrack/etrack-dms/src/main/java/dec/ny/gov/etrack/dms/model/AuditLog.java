package dec.ny.gov.etrack.dms.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class AuditLog {

  private String documentId;
  private String projectId;
  private String applicationId;
  private String decId;
  private String districtId;
  private String methodName;
  private String eventName;
  private List<String> fileName;

  public String toString() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}
