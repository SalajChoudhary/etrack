package dec.ny.gov.etrack.gis.model;

public enum PolygonAction {
  
  S("SAVE_ACTION"),
  U("UPDATE_ACTION");
  
  private final String action;
  
  private PolygonAction(String action) {
    this.action = action;
  }
  
  public String getAction() {
    return this.action;
  }
}
