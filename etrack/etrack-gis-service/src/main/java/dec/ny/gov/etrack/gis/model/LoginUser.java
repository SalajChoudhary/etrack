package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.Data;

public @Data  class LoginUser implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String userId;
}
