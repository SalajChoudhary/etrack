package dec.ny.gov.etrack.permit.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT_FOIL_DETAIL")
public @Data class ProjectFoilStatusDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_FOIL_DETAIL_S")
  @SequenceGenerator(name = "E_PROJECT_FOIL_DETAIL_S", sequenceName = "E_PROJECT_FOIL_DETAIL_S", allocationSize = 1)
  private Long foilId;
  private Long projectId;
  private String foilReqNum;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
