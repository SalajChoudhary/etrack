package dec.ny.gov.etrack.dart.db.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT_LITIGATION_HOLD")
public @Data class LitigationHold {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_LITIGATION_HOLD_S")
  @SequenceGenerator(name = "E_PROJECT_LITIGATION_HOLD_S", 
  sequenceName = "E_PROJECT_LITIGATION_HOLD_S", allocationSize = 1)
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
