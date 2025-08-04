package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ProjectPolygon implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long projectId;
  private Long edbDistrictId;
  private Integer approvedPolygonChangeInd;
  private Integer polygonGisId;
}
