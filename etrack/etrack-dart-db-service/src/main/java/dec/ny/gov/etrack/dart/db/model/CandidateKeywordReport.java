package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import lombok.Data;

public @Data class CandidateKeywordReport implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String keyword;
  private Integer totalUsage;
  private LinkedHashMap<Integer, Integer> regions;
}
