package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

public @Data class EmailContent implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long correspondenceId;
  private Long projectId;
  private String subShortDesc;
  private String emailSubject;
  private String fromEmailAdr;
  private String toEmailAdr;
  private String ccEmailAdr;
  private List<String> emailContent;
  private String emailStatus;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Long topicId;
  private String emailRead;
  private String deletedInd;
  private String replyFromAdr;
}
