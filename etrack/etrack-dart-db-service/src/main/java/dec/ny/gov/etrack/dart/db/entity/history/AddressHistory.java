package dec.ny.gov.etrack.dart.db.entity.history;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public @Data class AddressHistory {
  @Id
  private Long hAddressId;
  private String hStreet1;
  private Integer street1ChgInd;
  private String hStreet2;
  private Integer street2ChgInd;
  private String hCity;
  private Integer cityChgInd;
  private String hState;
  private Integer stateChgInd;
  private String hCountry;
  private Integer countryChgInd;
  private String hZip;
  private Integer zipChgInd;
  private String hZipExt;
  private Integer zipExtChgInd;
  private String hCareOfName;
  private Integer hCareOfNameChgInd;
  private String hAttentionName;
  private Integer hAttentionNameChgInd;
  private String hHomePhoneNumber;
  private Integer hHomePhoneNumberChgInd;
  private String hBusPhoneNumber;
  private Integer busPhoneNumberChgInd;
  private String hBusExt;
  private Integer busPhoneExtChgInd;
  private String hFaxNumber;
  private Integer faxNumberChgInd;
  private String hCellNumber;
  private Integer cellNumberChgInd;
  private String hEmailAddr;
  private Integer emailAddressChgInd;
  private String hForeignAddrInd;
  private Integer foreighAddressIndChgInd;
}
