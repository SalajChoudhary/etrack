package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import lombok.Data;

public @Data class Account implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String revenueAcct;
  private Long amount;
}
