package dec.ny.gov.etrack.dart.db.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="E_PROJECT_DEVELOPMENT")
@AllArgsConstructor
@NoArgsConstructor
public @Data class ProjectDevelopment {
  @Id
  private Long projectDevelopmentId;
  private Integer developmentTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
//  @JsonBackReference
  @ManyToOne
  @JoinColumn(name="PROJECT_ID")
  private Project project;
}
