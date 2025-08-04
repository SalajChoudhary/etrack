package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ProjectType implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String type;
  private Long fee;

}
