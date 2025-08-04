package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class SchemaProperty implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private QuestionSectionProperty questionsOrSections;
}
