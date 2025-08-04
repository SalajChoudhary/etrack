package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class AssignmentNote implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String analystId;
  private String analystName;
  private Long analystRoleId;
  private String comments;
  
}
