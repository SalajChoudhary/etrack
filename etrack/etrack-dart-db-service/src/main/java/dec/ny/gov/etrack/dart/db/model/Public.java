package dec.ny.gov.etrack.dart.db.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Public {
  @JsonProperty("applicantId")
  private Long publicId;
  private Long projectId;
  private String displayName;
  private List<Facility> facilities;
  private String role;
  private String acknowledgeInd;
  private Integer lrpCode;
}
