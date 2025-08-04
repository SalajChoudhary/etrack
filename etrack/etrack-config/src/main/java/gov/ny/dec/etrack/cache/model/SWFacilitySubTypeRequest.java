package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;

import lombok.Data;

public @Data class SWFacilitySubTypeRequest implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String facilitySubTypeRegulationCode;
  private Integer facilityTypeId;
  private Integer swFacilitySubTypeId;
  private String facilitySubType;
  private Integer activeInd;
}
