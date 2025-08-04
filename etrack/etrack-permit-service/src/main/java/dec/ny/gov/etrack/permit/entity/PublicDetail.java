package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import javax.persistence.Column;
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
  @Column(name="PUBLIC_ID")
  private String publicId;
  @Column(name="PUBLIC_TYPE_CODE")
  private String publicTypeCode;
  @Column(name="PUBLIC_NAME")
  private String publicName;
  @Column(name="TAXPAYER_ID")
  private String taxPayerId;
  @Column(name="EDB_PUBLIC_ID")
  private String edbPublicId;
  @Column(name="DISPLAY_NAME")
  private String displayName;
  @Column(name="FIRST_NAME")
  private String firstName;
  @Column(name="MIDDLE_NAME")
  private String middleName;
  @Column(name="LAST_NAME")
  private String lastName;
  @Column(name="SUFFIX")
  private String suffix;
  @Column(name="DBA_NAME")
  private String dbaName;
  @Column(name="INCORP_IND")
  private String incorpInd;
  @Column(name="INCORP_STATE")
  private String incorpState;
  @Column(name="TERRITORY_OR_COUNTRY")
  private String territoryOrCountry;
  @Column(name="PUB_CHG_CTR")
  private String pubChgCtr;
  @Column(name="SELECTED_IN_ETRACK_IND")
  private String selectInEtrackInd;
  @Column(name="ROLE_ID")
  private String roledId;
  @Column(name="ROLE_TYPE_ID")
  private String roleTypeId;
  @Column(name="EMPLOYEE_REGION_CODE")
  private String employedeRegionCode;
  @Column(name="TITLE")
  private String title;
  @Column(name="LEGALLY_RESPONSIBLE_TYPE_CODE")
  private String legallyResponsibleTypeCode;
  @Column(name="ROLE_CHG_CTR")
  private String RoleChgCtr;
  @Column(name="ROLE_TYPE_DESC")
  private String roleTypeDesc;
  @Column(name="PUBLIC_TYPE_DESC")
  private String publicTypeDesc;
  @Column(name="STREET1")
  private String street1;
  @Column(name="STREET2")
  private String street2;
  @Column(name="CITY")
  private String city;
  @Column(name="STATE")
  private String state;
  @Column(name="COUNTRY")
  private String country;
  @Column(name="ZIP")
  private String zip;
  @Column(name="ZIP_EXTENSION")
  private String zipExtension;
  @Column(name="CARE_OF_NAME")
  private String careOfName;
  @Column(name="ATTENTION_NAME")
  private String attentionName;
  @Column(name="HOME_PHONE_NUMBER")
  private String homePhoneNumber;
  @Column(name="BUSINESS_PHONE_NUMBER")
  private String businessPhoneNumber;
  @Column(name="BUSINESS_PHONE_EXT")
  private String businessPhoneExt;
  @Column(name="FAX_PHONE_NUMBER")
  private String faxPhoneNumber;
  @Column(name="CELL_PHONE_NUMBER")
  private String cellPhoneNumber;
  @Column(name="EMAIL_ADDRESS")
  private String emailAddress;
  @Column(name="CHANGE_COUNTER")
  private String changeCounter;

}
