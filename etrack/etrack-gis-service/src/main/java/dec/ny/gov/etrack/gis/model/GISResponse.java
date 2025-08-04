package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GISResponse implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String objectId;
  private boolean success;
  private GISErrorResponse error;
  
}
