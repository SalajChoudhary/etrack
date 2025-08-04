package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GISErrorResponse implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String code;
  private String description;
}
