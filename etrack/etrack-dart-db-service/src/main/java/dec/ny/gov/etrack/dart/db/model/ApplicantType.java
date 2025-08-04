package dec.ny.gov.etrack.dart.db.model;

public enum ApplicantType {

  P("LRP"),
  O("OWNER"),
  C("CONTACT/AGENT");

  private final String pType;
  private ApplicantType(final String pType) {
    this.pType = pType;
  }
  
  public String getPType() {
    return this.pType;
  }
  
}
