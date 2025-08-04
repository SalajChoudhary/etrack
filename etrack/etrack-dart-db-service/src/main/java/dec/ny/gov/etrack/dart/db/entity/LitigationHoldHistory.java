package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT_LITIGATION_HOLD_H")
public @Data class LitigationHoldHistory implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name="LITIGATION_HOLD_H_ID")
  private Long litigationHoldHId;
  private Long litigationHoldId;
  private Long projectId;
  private Integer litigationHoldInd;
  private Date LitigationHoldStartDate;
  private Date litigationHoldEndDate;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
