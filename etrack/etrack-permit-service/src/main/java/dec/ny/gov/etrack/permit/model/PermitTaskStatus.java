package dec.ny.gov.etrack.permit.model;

import lombok.Data;

public @Data class PermitTaskStatus {
//  private Integer projectActivityStatusId;
  private Long projectId;
  private Integer activityStatusId;
  private String completed;
}
