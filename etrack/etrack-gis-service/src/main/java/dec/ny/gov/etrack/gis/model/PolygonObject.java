package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class PolygonObject implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<PolygonFeature> features;
  private JsonNode error;
}
