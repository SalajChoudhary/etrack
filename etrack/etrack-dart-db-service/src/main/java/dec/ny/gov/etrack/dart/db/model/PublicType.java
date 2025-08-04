package dec.ny.gov.etrack.dart.db.model;

public enum PublicType {

  I("INDIVIDUAL"),
  P("SOLE_PROPRIETOR"),
  X("INCORPORATED_BIZ"),
  T("TRUST_OR_ASSOCIATION"),
  C("CORPN_PARTNER"),
  F("FEDERAL_AGENCY"),
  S("STATE_AGENCY"),
  M("MUNI_OR_COUNTY");

  private final String pType;
  private PublicType(final String pType) {
    this.pType = pType;
  }
  
  public String getPType() {
    return this.pType;
  }
  
}
