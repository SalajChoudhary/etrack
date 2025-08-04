package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class SearchQueryConditions implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long queryId;
  private String queryName;
  private String queryOwner;
  
  private Long searchQueryConditionId;
  private String conditionOperator;
  private Long  searchAttributeId;
  private String comparisonOperator;
  private String comparisonValue;
  private Integer searchEntityCode;
  private String resultDetails;
  private String documentSearchType;
  private String comments;
   
}