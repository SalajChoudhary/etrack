package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ProjectAlert implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long projectAlertId;
  private String facilityName;
  private Long projectId;
  private Date alertDate;
  private String alertNote;
  private String createdById;
  private Date createDate;
  private Integer readInd;
  private String comments;
}
