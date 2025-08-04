package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;

import lombok.Data;

public @Data class SWFacilityTypeRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Integer swFacilityTypeId;
  private String regulationCode;
  private String facilityType;
  private Integer activeInd;
}
