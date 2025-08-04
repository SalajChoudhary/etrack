package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
public @Data class RegionUserEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private String userId;
  @JsonProperty("managerName")
  private String displayName;
  @JsonProperty("regionId")
  private String employeeRegionCode;
  private String emailAddress;
  private Integer roleTypeId;
}
