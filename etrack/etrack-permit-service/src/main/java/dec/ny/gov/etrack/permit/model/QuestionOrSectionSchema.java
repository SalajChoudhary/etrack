package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class QuestionOrSectionSchema implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String type;
  private String title;
  private String description;
  private Map<String, QuestionSectionProperty> properties;
}