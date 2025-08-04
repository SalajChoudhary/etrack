package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

public @Data class SupportDocument implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long districtId;
  private Map<String, PermitType> permitTypes;
  private List<Document> requiredDoc;
  private List<Document> relatedDoc;
  private List<Document> shpaDoc;
  private List<Document> seqrDoc;
  private String validatedInd;
  private Integer eaInd;
  private Integer seqrInd;
}
