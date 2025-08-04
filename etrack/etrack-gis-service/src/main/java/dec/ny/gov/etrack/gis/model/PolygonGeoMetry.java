package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class PolygonGeoMetry implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private ArrayList<ArrayList<ArrayList<Double>>> rings;
}
