package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class ProjectNoteView implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long projectNoteId;
  private Long projectId;
  private Integer actionTypeCode;
  private String actionTypeDesc;
  private String actionDate;
  private String actionNote;
  private String comments;
  private String systemGenerated;
  private List<String> missingReqdDoc;
  private String cancelledUserId;
}
