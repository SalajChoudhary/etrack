package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class PermitKeyword implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long permitKeywordId;
  private Long keywordId;
  private Long keywordCategoryId;
  private String permitTypeCode;
  private String startDate;
  private String endDate;
}
