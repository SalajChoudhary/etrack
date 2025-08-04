package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class UserAssignment implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long projectId;
  private String userAssigned;
  private String analystName;
  private String comments;
  private Long analystRoleId;
}
