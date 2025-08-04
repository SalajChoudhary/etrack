package dec.ny.gov.etrack.permit.model;

public enum ActivityTaskStatus {
  
  SEL_PROJ_LOC(1), APPLICANT_INFO(2), PROJECT_INFO(3), UPLOAD_DOC(4), SIGNATURE(5),
  LOC_DETAIL_VAL(6),
  APPLICANT_VAL(7),
  PROP_OWNER_VAL(8),
  CONT_AGENT_VAL(9),
  PERMIT_SUMMARY(10),
  PROJ_DESC(11),
  ASSIGN_CONTACT(12);

  private Integer activityStatus;
  
  private ActivityTaskStatus(Integer activityStatus) {
    this.activityStatus = activityStatus;
  }
  
  public Integer getActivityStatus() {
    return this.activityStatus;
  }
}
