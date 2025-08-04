package dec.ny.gov.etrack.permit.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_PROJECT_DEVELOPMENT")
public @Data class ProjectDevelopment {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_DEVELOPMENT_S")
  @SequenceGenerator(name = "E_PROJECT_DEVELOPMENT_S", sequenceName = "E_PROJECT_DEVELOPMENT_S", allocationSize = 1)
  private Long projectDevelopmentId;
  private Integer developmentTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Long projectId;
//  @ManyToOne
//  @JoinColumn(name="PROJECT_ID")
//  private Project project;
}
