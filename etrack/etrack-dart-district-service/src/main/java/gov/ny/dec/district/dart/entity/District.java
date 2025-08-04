package gov.ny.dec.district.dart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class District {
  @Id
  @Column(name = "DISTRICT_ID")
  private Long districtId;
  @Column(name = "FACILITY_NAME")
  private String facilityName;
  @Column(name = "DEC_ID")
  private String decId;
  @Column(name = "DEC_ID_FORMATTED")
  private String decIdFormatted;
  @Column(name = "MATCH_CURRENT_IND")
  private String matchCurrentInd;
  @Column(name = "MUNICIPALITY_NAME")
  private String municipalityName;
}
