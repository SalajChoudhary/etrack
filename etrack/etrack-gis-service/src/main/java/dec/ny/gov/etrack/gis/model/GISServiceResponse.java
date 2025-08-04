package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class GISServiceResponse implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<GISResponse> addResults;
  private List<GISResponse> updateResults;
}
