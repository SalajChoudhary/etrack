package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ProjectPolygon implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectId;
  private Long edbDistrictId;
  private Integer approvedPolygonChangeInd;
  private Integer polygonGisId;
}
