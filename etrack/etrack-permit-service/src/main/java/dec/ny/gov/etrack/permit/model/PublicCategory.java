package dec.ny.gov.etrack.permit.model;

public enum PublicCategory {

  C("CONTACT_AGENT"),
  P("PUBLIC"),
  O("PROPERTY_OWNER");
  
  private final String category;
  private PublicCategory(final String category) {
    this.category = category;
  }
  
  public String getCategory() {
    return this.category;
  }
}
