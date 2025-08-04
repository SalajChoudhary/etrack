package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class PermitType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String permitType;
  private String permitTypeDesc;
  private String refLink;
}
