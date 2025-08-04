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
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_NOTE_S")
  @SequenceGenerator(name = "E_PROJECT_NOTE_S", sequenceName = "E_PROJECT_NOTE_S", allocationSize = 1)
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

