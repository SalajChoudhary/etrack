package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class ProjectNoteView implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long projectNoteId;
  private Integer actionType;
  private String actionDate;
  private String actionNote;
  private String comments;
  private String updatedDate;
  private String createDate;
  private String systemGenerated;
  private String actionTypeDesc;
  private String updatedBy;
  private List<String> missingReqdDoc;
  private PaymentActionNote paymentActionNote;
  private String cancelledUserId;
  private String cancelledUserName;
  private String reason;
}