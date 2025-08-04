package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT_ALERT")
public @Data class ProjectAlert implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_ALERT_S")
  @SequenceGenerator(name = "E_PROJECT_ALERT_S", sequenceName = "E_PROJECT_ALERT_S", allocationSize = 1)
  private Long projectAlertId;
  private Long projectId;
  private Date alertDate;
  private String alertNote;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Integer msgReadInd;
  private String alertRcvdUserId;
  private Long projectNoteId;
}
