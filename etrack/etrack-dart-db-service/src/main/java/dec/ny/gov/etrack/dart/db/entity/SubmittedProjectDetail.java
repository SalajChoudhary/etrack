package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

public @Data class SubmittedProjectDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Integer mailInInd;
  private BigDecimal total;
}
