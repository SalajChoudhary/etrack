package dec.ny.gov.etrack.permit.model;


public enum PolygonStatus {
  APPLICANT_SCRATCH(1),
  ANALYST_SCRATCH(2),
  APPLICANT_SUBMITTED(3),
  ANALYST_APPROVED(4),
  GIS_REGULATED(5);
  
  private int status;
  PolygonStatus(int i) {
    this.status = i;
  }
  
  public int getStatus() {
    return status;
  }
  
  public static PolygonStatus getValue(int value) {
    String enumVal = null;
    switch (value) {
      case 1:
        enumVal = "APPLICANT_SCRATCH";
      break;
      case 2:
        enumVal = "ANALYST_SCRATCH";
        break;
      case 3:
        enumVal = "APPLICANT_SUBMITTED";
        break;
      case 4:
        enumVal = "ANALYST_APPROVED";
        break;
      case 5:
        enumVal = "GIS_REGULATED";
        break;        
    }
    return PolygonStatus.valueOf(enumVal);
  }
}
