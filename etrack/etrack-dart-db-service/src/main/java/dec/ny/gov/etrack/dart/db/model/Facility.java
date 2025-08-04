package dec.ny.gov.etrack.dart.db.model;

import lombok.Data;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Facility {
  private String roleTypeDesc;
  private Integer rolePrimaryInd;
  private Long districtId;
//  private String projectId;
  private String facilityName;
  private String decId;
  private String decIdFormatted;
  private String locationDirections;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String zipExtension;
  private String phoneNumber;
  private String formattedAddress;
  private String municipality;
  private String street1;
  private String street2;
  private String county;
}
