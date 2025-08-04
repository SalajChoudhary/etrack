package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

public @Data class IngestionRequest implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Integer attachmentFilesCount;
  private String clientId;
  private String userId;
  private String guid;
  private Map<String, Object> metadataProperties;
  private Map<String, String> fileDates;
}
