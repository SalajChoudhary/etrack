package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ETrackPermit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long applicationId;
  private Long edbApplId;
  private Long edbDistrictId;
  private Long contactAssignedId;
  private Integer permitCategoryId;
  private String permitTypeCode;
  private String transTypeCode;
  private String edbTransTypeCode;
  private String permitTypeDesc;
  private String refLink;
  private Long batchIdEdb;
  private String progId;
  private String progIdFormatted;
  private String formSubmittedInd;
  private Integer trackingInd;
  private Integer edbTrackingInd;
  private Integer userSelNewInd;
  private Integer userSelModInd;
  private Integer userSelExtInd;
  private Integer userSelTransferInd;
  private Integer pendingInd;
  private Integer userSelRenInd;
  private Integer chgOriginalProjectInd;
  private String effectiveEndDate;
  private String effectiveStartDate;
  private String batchGroupEtrack;
  private Long edbAuthId;
}
