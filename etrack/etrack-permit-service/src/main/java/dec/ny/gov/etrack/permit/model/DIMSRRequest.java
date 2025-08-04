package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class DIMSRRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long edbDistrictId;
  private String facilityName;
  private String decId;
  private String projectDesc;
  private String intentMailingDate;
  private String proposedEffDate;
  private String assignedAnalystName;
  private String analystAssignedId;
  private Long analystRoleId;
  private List<DIMSRPermit> permits;
  private Long projectId;
}
