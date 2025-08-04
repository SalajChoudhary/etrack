package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class KeywordCategory implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long keywordCategoryId;
  private String keywordCategory;

}
