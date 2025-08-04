package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class FacilityDetail implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long facilityId;
  private String facilityName;
  private Long edbDistrictId;
  private String decId;
  private String decIdFormatted;
  private String matchedDecId;
  private FacilityAddress address;
}
