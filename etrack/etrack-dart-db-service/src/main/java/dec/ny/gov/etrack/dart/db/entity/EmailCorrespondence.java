package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_EMAIL_CORRESPONDENCE")
public @Data class EmailCorrespondence implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long correspondenceId;
  private Long projectId;
  private String subShortDesc;
  private String emailSubject;
  private String fromEmailAdr;
  private String toEmailAdr;
  private String ccEmailAdr;
  private String emailContent;
  private String emailStatus;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Long topicId;
  private String emailRead;
  private String deletedInd;
  private String emailRqstdUserId;
  private String emailRcvdUserId;
  private String emailRcvdUserName;
  private String emailSendUserName;
}
