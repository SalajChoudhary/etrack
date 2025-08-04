package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;

import lombok.Data;

public @Data class SWFacilitySubType implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String facilitySubTypeRegulationCode;
  private String facilityTypeRegulationCode;
  private Integer facilityTypeId;
  private String facilityType;
  private Integer swFacilitySubTypeId;
  private String facilitySubType;
  private Integer activeInd;
}
