package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public @Data class PublicDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long publicId;
  private String publicTypeCode;
  private String publicName;
  private String taxpayerId;
  private Long edbPublicId;
  private String displayName;
  private String firstName;
  private String middleName;
  private String lastName;
  private String suffix;
  private String dbaName;
  private Integer incorpInd;
  private String incorpState;
  private String territoryOrCountry;
  private String pubChgCtr;
  private Integer selectedInEtrackInd;
  private String roleId;
  private String roleTypeId;
  private String employeeRegionCode;
  private String title;
  private String legallyResponsibleTypeCode;
  private String RoleChgCtr;
  private String roleTypeDesc;
  private String publicTypeDesc;
  private Long addressId;
  private Long edbAddressId;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String zipExtension;
  private String careOfName;
  private String attentionName;
  private String homePhoneNumber;
  private String businessPhoneNumber;
  private String businessPhoneExt;
  private String faxPhoneNumber;
  private String cellPhoneNumber;
  private String emailAddress;
  private String changeCounter;
  private Integer businessValidatedInd;
  private Integer validatedInd;
  private Integer foreignAddressInd;
}
