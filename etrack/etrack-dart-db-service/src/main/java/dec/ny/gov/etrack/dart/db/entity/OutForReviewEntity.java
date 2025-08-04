package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class OutForReviewEntity implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long applicationId;
  private Long projectId;
  private Integer eaInd;
  private String facilityName;
  private String street1;
  private String city;
  private String state;
  private String zip;
  private String permitTypeCode;
  private String permitTypeDesc;
  private Long edbDistrictId;
  private Long batchId;
  private String progId;
  private String trackedIdFormatted;
  private Integer generalPermitInd;
  private String docReviewerName;
  private Date reviewDueDate;
  private String supervisorNm;
  private String appStatus;
}
