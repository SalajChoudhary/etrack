package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class PolygonAttributes implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("OBJECTID")
  private Long objectId;
  @JsonProperty("VALIDATED_LOCATION")
  private Integer validatedLocation;
}
