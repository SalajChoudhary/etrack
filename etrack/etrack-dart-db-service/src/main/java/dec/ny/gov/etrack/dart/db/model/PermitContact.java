package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class PermitContact implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("applicantId")
  private Long publicId;
  @JsonProperty("applicantType")
  private String publicTypeCode;
  private String displayName;
  private Long edbPublicId;
  private Long roleId;
  private String permitAssignedInd;
  private String transType;
}
