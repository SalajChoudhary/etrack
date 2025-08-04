package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;

public enum SearchPatternEnum implements Serializable {

  S("STARTS_WITH"),
  C("CONTAINS"),
  E("EXACT");
  
  private final String searchType;
  private SearchPatternEnum(final String searchType) {
    this.searchType = searchType;
  }
  
  public String getSearchType() {
    return this.searchType;
  }
}
