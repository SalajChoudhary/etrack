package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class ProjectSubmittedRetrievalCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String startDate;
  private String endDate;
  private Integer region;
  private List<String> permitTypes;
  private List<String> transTypes;
}
