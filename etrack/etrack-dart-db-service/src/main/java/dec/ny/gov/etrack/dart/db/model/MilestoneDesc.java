package dec.ny.gov.etrack.dart.db.model;

public enum MilestoneDesc {
  PROJECT_RECEIVED("Project Received"),
  INCOMPLETE_SENT("Incomplete Sent"),
  COMPLETENESS_DATE("Completeness Sent"),
  FINAL_DECISION_DATE("Final Decision Date"),
  ADDL_INFO_REQD("Addinitonal Information Required"),
  ADDL_INFO_RCVD("Additional Information Received"),
  SUSPENDED("Suspended"),
  UNSUSPENDED("Unsuspended"),
  EFFECTIVE("Effective(if applicable)"),
  EXPIRATION("Expiration(if applicable)"),
  HEARING_DATE("Hearing Date"),
  ENB_PUBLICATION("ENB Publication"),
  FIVE_DAY_LTR_RCVD("Five Day Letter Received"),
  FIVE_DAY_LTR_RESP("Five Day Letter Response"),
  RESUBMISSION_RCVD("Resubmission Received"),
  DEIS_COMPLETE("DEIS Complete"),
  FEIS_COMPLETE("FEIS Complete"),
  FINDINGS_ISSUED("Findings Issued"),
  COMMENT_DEADLINE("Comment Deadline");
  
  private String value;
  MilestoneDesc(String value) {
    this.value = value;
  }
  public String getValue() {
    return value;
  }
}
