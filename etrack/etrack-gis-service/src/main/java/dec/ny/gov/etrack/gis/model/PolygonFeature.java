package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class PolygonFeature implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private PolygonAttributes attributes;
  private PolygonGeoMetry geometry;
}
