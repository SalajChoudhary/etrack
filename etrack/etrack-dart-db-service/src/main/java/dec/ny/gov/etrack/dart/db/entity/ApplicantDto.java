package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
public @Data class ApplicantDto implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @JsonProperty("applicantId")
  private Long publicId;
  @JsonProperty("applicantType")
  private String publicTypeCode;
  private String displayName;
  private String firstName;
  private String lastName;
  private String middleName;
  private Long edbPublicId;
  private Long projectId;
  private String role;
  private Integer publicSignedInd;
  private String modified;
  private Integer validatedInd;
//  private Integer onlineSubmitterInd;
}
