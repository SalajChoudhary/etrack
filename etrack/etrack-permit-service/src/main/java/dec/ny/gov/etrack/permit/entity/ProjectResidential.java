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
@Table(name="E_PROJECT_RES_DEV")
public @Data class ProjectResidential {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_RES_DEV_S")
  @SequenceGenerator(name = "E_PROJECT_RES_DEV_S", sequenceName = "E_PROJECT_RES_DEV_S", allocationSize = 1)
  private Long projectResDevId;
  private Integer resDevTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Long projectId;
}
