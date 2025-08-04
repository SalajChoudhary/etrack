package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class SWFacTypeSubTypeDocument {
  @Id
  private String uniqueId;
  private Integer swFacilityTypeId;
  private String facilityTypeDesc;
  private Integer swFacilitySubTypeId;
  private String subTypeDescription;
}
