package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_ACTIVITY_TASK_STATUS_CODE")
public @Data class ActivityTaskStatus {
  @Id
  private Integer activityStatusId;
  private String activityCode;
  private String taskCode;
}
