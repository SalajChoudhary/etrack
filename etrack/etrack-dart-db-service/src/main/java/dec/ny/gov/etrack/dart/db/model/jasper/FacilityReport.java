package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import lombok.Data;

public @Data class FacilityReport implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String facility;
  private String facilityAddress;
  private String cityStateZip;
  private String taxMapNumber;
  private String streamWaterbodyName;
  private String directions;
}
