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
@Table(name = "E_PROJECT_ACTIVITY_TASK_STATUS")
public @Data class ProjectActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_ACTIVITY_STATUS_S")
  @SequenceGenerator(name = "E_PROJECT_ACTIVITY_STATUS_S", sequenceName = "E_PROJECT_ACTIVITY_STATUS_S", allocationSize = 1)
  private Integer projectActivityStatusId;
  private Long projectId;
  private Integer activityStatusId;
  private Date startDate;
  private Date completionDate;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
