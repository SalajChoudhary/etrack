package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class AvailTransType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String type;
  private String code;
}
