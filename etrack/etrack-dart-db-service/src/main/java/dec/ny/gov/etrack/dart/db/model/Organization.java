package dec.ny.gov.etrack.dart.db.model;

import lombok.Data;

public @Data class Organization {
  private String busOrgName;
  private String isIncorporated;
  private String taxPayerId;
  private String incorporationState;
  private String incorporateCountry;
  private String businessVerified;
}
