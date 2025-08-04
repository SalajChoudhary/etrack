package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class PolygonObjectDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<PolygonAttributes> attributes;
}
