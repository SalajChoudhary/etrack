package dec.ny.gov.etrack.permit.model;

public enum SpatialInquiryCategory {
  
  BOROUGH_DETERMINATION(1),
  JURISDICTION_DETERMINATION(2),
  SEQR_LA_REQ(3),
  PRE_APPLN_REQ(4),
  SERP_CERT(5),
  MGMT_COMPRE_PLAN(6),
  SANITARY_SEWER_EXT(7),
  ENERGY_PROJ(8);

  private final Integer category;
  SpatialInquiryCategory(final Integer category) {
    this.category = category;
  }
  
  public Integer getCategory() {
    return this.category;
  }
}
