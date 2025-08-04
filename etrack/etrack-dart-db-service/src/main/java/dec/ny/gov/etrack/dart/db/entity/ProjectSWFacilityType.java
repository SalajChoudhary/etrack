package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
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
@Table(name="E_PROJ_SW_FACILITY_TYPE")
@AllArgsConstructor
@NoArgsConstructor
public @Data class ProjectSWFacilityType implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long projSwFacilityTypeId;
  private Integer swFacilityTypeId;
  private Integer swFacilitySubTypeId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  @ManyToOne
  @JoinColumn(name="PROJECT_ID") 
  private Project project;
}
