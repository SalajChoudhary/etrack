package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class SpatialReference implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long wkid;
  private Long latestWkid;
}
