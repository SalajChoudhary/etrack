package dec.ny.gov.etrack.permit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class ApplicantAddress {
  private Long addressId;
  private Integer adrType;
  private String streetAdr1;
  private String streetAdr2;
  private String city;
  private String state;
  private String zipCode;
  private String postalCode;
  private String country;
  private String attentionName;
  private Long edbAddressId;
}
