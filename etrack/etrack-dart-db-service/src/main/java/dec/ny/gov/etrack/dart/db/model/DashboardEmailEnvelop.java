package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class DashboardEmailEnvelop implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long projectId;
  private String facilityName;
  private Integer unreadCount;
}
