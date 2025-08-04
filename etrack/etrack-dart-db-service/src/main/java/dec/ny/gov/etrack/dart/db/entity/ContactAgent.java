package dec.ny.gov.etrack.dart.db.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
public @Data class ContactAgent {
  @Id
  @JsonProperty("applicantId")
  private Long publicId;
  @JsonProperty("applicantType")
  private String publicTypeCode;
  private String displayName;
  private Long edbPublicId;
//  private Long edbDistrictId;
  private Long roleId;
}
