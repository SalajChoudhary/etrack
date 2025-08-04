package dec.ny.gov.etrack.permit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Organization {
  private String busOrgName;
  private String isIncorporated;
  private String taxPayerId;
  private String incorporationState;
  private String incorporateCountry;
  private String businessVerified;
  private String verifiedLegalName;
}
