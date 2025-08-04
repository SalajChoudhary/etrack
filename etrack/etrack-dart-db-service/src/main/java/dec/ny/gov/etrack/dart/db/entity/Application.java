package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public  @Data class Application implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long applicationId;
  private Long projectId;
  private String permitTypeCode;
  private String permitTypeDesc;
  private String transTypeCode;
  private String gpInd;
  private String eaInd;
  private Long batchIdEdb;
  private String progId;
  private String relatedRegPermit;
}
