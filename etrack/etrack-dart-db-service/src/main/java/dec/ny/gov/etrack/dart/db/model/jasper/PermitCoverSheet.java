package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class PermitCoverSheet implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Permit permit;
}
