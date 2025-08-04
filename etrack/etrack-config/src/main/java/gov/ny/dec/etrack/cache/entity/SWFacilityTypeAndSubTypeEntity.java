package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import lombok.Data;

public @Data class SWFacilityTypeAndSubTypeEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Integer swFacilityTypeId;
  private String facilityTypeDesc;
  private String ftReg;
  private Integer swFacilitySubTypeId;
  private String subReg;
  private String subTypeDescription;
  private Integer activeInd;
}
