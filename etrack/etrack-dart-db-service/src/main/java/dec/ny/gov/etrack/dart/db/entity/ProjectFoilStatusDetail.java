package dec.ny.gov.etrack.dart.db.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT_FOIL_DETAIL")
public @Data class ProjectFoilStatusDetail {
  @Id
  private Long foilId;
  private Long projectId;
  private String foilReqNum;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
