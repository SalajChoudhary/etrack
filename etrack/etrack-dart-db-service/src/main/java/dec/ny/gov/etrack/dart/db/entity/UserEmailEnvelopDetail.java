package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class UserEmailEnvelopDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long correspondenceId;
  private String emailSubject;
  private String ccEmailAdr;
  private String toEmailAdr;
  private Long refCorrespId;
  private String emailRqstdUserId;
  private String emailRcvdUserId;
  private String emailRcvdUserName;
}
