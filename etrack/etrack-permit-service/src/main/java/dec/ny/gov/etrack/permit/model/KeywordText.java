package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class KeywordText implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long keywordId;
  private String keywordText;
  private Long keywordCategoryId;
  private String keywordCategory;
  private Integer activeInd;
  private String startDate;
  private String endDate;
}
