package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class DartPermit implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long applId;
  private String trackedId;
  private String trackedIdFormatted;
  private String emergencyInd;
  private String gpInd;
  private Long districtId;
  private Long projectId;
  private Long gpAuthId;
  private String gpExtendedDate;
  private String gpPermitType;
  private String gpPermitTypeFormatted;
  private String gpPermitDesc;
  private Integer renOrderNum;
  private Integer modOrderNum;
  private String transType;
  private String permitType;
  private String permitDesc;
  private String receivedDate;
  private Integer trackingInd;
  private Long batchId;
  private String projectDesc;
  private String processingOfficeDistrictId;
  private String applCompletenessCode;
  private String dueDate;
  private Integer region;
  private String sapaInd;
  private String sapaDate;
  private String sapaSuffDate;
  private String applDispositionCode;
  private Integer maxPermitTerm;
  private Date nonRenewableEffDate; 
  private Date startDate;
  private Date expiryDate;
  private String renewedInd;
}
