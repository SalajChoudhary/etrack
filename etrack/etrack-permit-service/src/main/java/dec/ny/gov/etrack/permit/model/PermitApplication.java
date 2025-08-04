package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class PermitApplication implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long applicationId;
  private Long edbApplicationId;
  private Long edbDistrictId;
//  private Long applicantId;
//  private Long edbApplicantId;
//  private String displayName;
//  private Long contactRoleId;
//  private String contactAssignedInd;
  private String permitTypeCode;
  private String permitTypeDesc;
  private String transType;
  private List<ApplicationPermitDetail> contacts;
}
