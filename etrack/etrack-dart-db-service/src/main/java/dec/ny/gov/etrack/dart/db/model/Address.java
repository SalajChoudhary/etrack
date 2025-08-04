package dec.ny.gov.etrack.dart.db.model;

import lombok.Data;

public @Data class Address {
  private Long addressId;
  private String adrType;
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
