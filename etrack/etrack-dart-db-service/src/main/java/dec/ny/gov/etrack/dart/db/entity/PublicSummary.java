package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class PublicSummary implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @JsonProperty("applicantId")
  private Long publicId;
  private Long  edbPublicId;
  private String publicSignedInd;
  private String publicName;
  private String taxpayerId;
  private String displayName;
  private String firstName;
  private String middleName;
  private String lastName;
  private String suffix;
  private String dbaName;
  private String incorpInd;
  private String incorpState;
  private String territoryOrCountry;
  private String addressId;
  @Column(name = "street1")
  private String street1;
  @Column(name = "street2")
  private String street2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String zipExtension;
  private String homePhoneNumber;
  private String businessPhoneNumber;
  private String businessPhoneExt;
  private String faxPhoneNumber;
  private String cellPhoneNumber;
  private String emailAddress;
  private String foreignAddressInd;
//  private Long edbPublicIdHist;
//  private String publicSignedIndHist;
//  private String publicNameHist;
//  private String taxpayerIdHist;
//  private String displayNameHist;
//  private String firstNameHist;
//  private String middleNameHist;
//  private String lastNameHist;
//  private String suffixHist;
//  private String dbaNameHist;
//  private String incorpIndHist;
//  private String incorpStateHist;
//  private String territoryOrCountryHist;
//  private String addressIdHist;
//  @Column(name = "street1_hist")
//  private String street1Hist;
//  @Column(name = "street2_hist")
//  private String street2Hist;
//  private String cityHist;
//  private String stateHist;
//  private String countryHist;
//  private String zipHist;
//  private String zipExtensionHist;
//  private String homePhoneNumberHist;
//  private String businessPhoneNumberHist;
//  private String businessPhoneExtHist;
//  private String faxPhoneNumberHist;
//  private String cellPhoneNumberHist;
//  private String emailAddressHist;
//  private String foreignAddressIndHist;
}
