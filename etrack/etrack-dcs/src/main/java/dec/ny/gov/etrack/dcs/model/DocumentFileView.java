package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

public @Data class DocumentFileView implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long documentFileId;
  private Long documentId;
  private String fileName;
  private String fileDate;
}
