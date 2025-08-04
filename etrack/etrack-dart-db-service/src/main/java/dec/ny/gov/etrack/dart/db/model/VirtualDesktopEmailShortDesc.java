package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

public @Data class VirtualDesktopEmailShortDesc implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String emailUserName;
  private String emailUserId;
  private Integer unreadCount;
  private Date mostRecentCorrespondenceDate;
}
