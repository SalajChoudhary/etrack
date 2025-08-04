package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

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
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date modifiedDate;
  private String persistenceDataType;
  private List<SearchQueryConditionDetail> searchQueryConditions;
   
}