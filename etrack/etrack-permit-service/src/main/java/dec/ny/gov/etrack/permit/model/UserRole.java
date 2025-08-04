package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class UserRole implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String guid;
  private String userId;
  private List<String> roles;
  private List<String> permissions;
}
