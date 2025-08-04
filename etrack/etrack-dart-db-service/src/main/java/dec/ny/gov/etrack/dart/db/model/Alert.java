package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

public @Data class Alert implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long alertId;
  private Long projectId;
  private Long inquiryId;
  private String alertDate;
  private Date alertDateFormat;
  private String note;
  private String facilityName;
  private String createdById;
  private String msgRead;
  private String assignmentNote;
}
