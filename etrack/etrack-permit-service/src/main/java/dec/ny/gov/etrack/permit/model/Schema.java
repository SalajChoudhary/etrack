package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class Schema implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("$id")
  private String id;
  @JsonProperty("$schema")
  private String schema;
  private String title;
  private String description;
  private String department;
  private String type;
  private SchemaProperty properties;
}
