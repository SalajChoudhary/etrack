package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;


public @Data class ProjectRejectDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String projectId;
  private String facilityName;
  private String rejectReason;
}