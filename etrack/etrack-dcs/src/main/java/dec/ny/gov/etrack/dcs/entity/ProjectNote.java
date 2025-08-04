package dec.ny.gov.etrack.dcs.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="E_PROJECT_NOTE")
public @Data class ProjectNote implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectNoteId;
  private Long projectId;
  private Integer actionTypeCode;
  private Date actionDate;
  private String actionNote;
  private String comments;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
