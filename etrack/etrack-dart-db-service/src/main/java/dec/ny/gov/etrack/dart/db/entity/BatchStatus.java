package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public  @Data class BatchStatus implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long applId;
  private String currentStatusCode;
  private Date updateDate;
  private Long districtId;
  private Long batchId;
  private String authTypeCode;
  private String currentStatusDesc;
  private String applTypeCode;
  private String progId;
  private Date suspendedDate;
  private String suspensionReasonCode;
  private String emergencyInd;
  private String gpInd;
}
