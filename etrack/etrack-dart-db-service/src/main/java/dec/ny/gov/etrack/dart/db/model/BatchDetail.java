package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;


public @Data class BatchDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long batchNumber;
  private List<String> permitTransTypeApplId;
  private String gpInd;
  private String eaInd;
  private String gpPermitTypeCode;
  private List<MileStoneStatus> milestones;
}
