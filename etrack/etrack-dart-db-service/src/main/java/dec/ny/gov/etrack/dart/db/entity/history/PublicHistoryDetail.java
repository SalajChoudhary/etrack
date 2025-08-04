package dec.ny.gov.etrack.dart.db.entity.history;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public @Data class PublicHistoryDetail {
  @Id
  private Long hPublicId;
  private String hPublicTypeCode;
  private Integer publicTypeChgInd;
  private String hPublicName;
  private Integer publicNameChgInd;
  private String hTaxpayerId;
  private Integer taxpayerIdChgInd;
  private Long hEdbPublicId;
  private String hDisplayName;
  private Integer dispNameChgInd;
  private String hFirstName;
  private Integer fnChgInd;
  private String hMiddleName;
  private Integer mnChgInd;
  private String hLastName;
  private Integer lnChgInd;
  private String hSuffix;
  private Integer sfxChgInd;
  private String hDbaName;
  private Integer dbaChgInd;
  private Integer hIncorpInd;
  private Integer incorpIndChgInd;
  private String hIncorpState;
  private Integer incorpStateChgInd;
  private Integer hBusinessValidatedInd;
  private Integer busValIndChgInd;
  private String hTerritoryOrCountry;
  private Integer terrCountryChgInd;
  private String hSelectedInEtrackInd;
  private Integer selEtrackIndChgInd;
}
