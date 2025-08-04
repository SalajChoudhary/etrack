package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class EmailContent implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<String> toEmailId;
  private List<String> ccEmailId;
  private String fromEmailId;
  private String subject;
  private String emailBody;
  private String emailType;
  private String template;
  private Long topicId;
  private Long emailCorrespondenceId;
  private List<String> existingContents;
}
