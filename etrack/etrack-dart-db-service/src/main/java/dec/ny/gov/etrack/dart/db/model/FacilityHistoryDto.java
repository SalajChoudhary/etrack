package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class FacilityHistoryDto implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long hProjectId;
  private String hFacilityName;
  private String hDecId;
  private String hDecIdFormatted;
  private String hLocationDirections;
  private String hStreet1;
  private String hStreet2;
  private String hCity;
  private String hState;
  private String hCountry;
  private String hZip;
  private String hZipExtension;
  private String hPhoneNumber;
}
