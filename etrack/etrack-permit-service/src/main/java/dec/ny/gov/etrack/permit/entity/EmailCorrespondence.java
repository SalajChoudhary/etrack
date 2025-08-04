package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "E_EMAIL_CORRESPONDENCE")
@AllArgsConstructor
@NoArgsConstructor
public @Data class EmailCorrespondence implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_EMAIL_CORRESPONDENCE_S")
  @SequenceGenerator(name = "E_EMAIL_CORRESPONDENCE_S", sequenceName = "E_EMAIL_CORRESPONDENCE_S", allocationSize = 1)
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
  private String emailRqstdUserId;
  private String emailRcvdUserId;
  private String emailRcvdUserName;
  private String emailSendUserName;
  private Long refCorrespondenceId;
}
