package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

public @Data class AddressCandidate implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private SpatialReference spatialReference;
  private List<JsonNode> candidates;
}
