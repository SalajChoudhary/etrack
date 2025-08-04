package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

public @Data class SearchQueryDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long queryId;
  private String queryName;
  private String queryOwner;
  private String resultDetails;
  private String documentSearchType;
  private String comments;
  private String persistenceDataType;
  
  private List<SearchQueryConditionDetail> searchQueryConditions;
   
}