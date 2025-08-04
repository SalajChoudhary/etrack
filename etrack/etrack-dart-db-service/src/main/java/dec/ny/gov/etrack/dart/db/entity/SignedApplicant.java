package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
public @Data class SignedApplicant implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @JsonProperty("applicantId")
  private String publicId;
  @JsonProperty("applicantType")
  private String publicTypeCode;
  private String displayName;
  private Long edbPublicId;
  private Long projectId;
  private String role;
  private Integer publicSignedInd;
  private Integer legallyResponsibleTypeCode;
}
