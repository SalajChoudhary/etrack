package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApplicationPermitForm implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Integer formTransRuleId;
  private Long applicationId;
  private Integer permitFormId;
  private String permitFormName;
  private String permitFormDesc;
  private Long edbApplId;
  private Long edbDistrictId;
  private Long contactAssignedId;
  private Integer permitCategoryId;
  private String permitTypeCode;
  private String transTypeCode;
  private String edbTransTypeCode;
  private String permitTypeDesc;
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
  private Integer userSelRenInd;
  private Integer chgOriginalProjectInd;
  private String effectiveEndDate;
  private String effectiveStartDate;
  private String batchGroupEtrack;
}
