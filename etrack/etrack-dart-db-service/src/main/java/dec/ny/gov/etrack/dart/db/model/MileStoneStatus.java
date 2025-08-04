package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class MileStoneStatus implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String milestoneStatus;
  private String milestoneDate;
  private Date milestoneDateFormat;
}
