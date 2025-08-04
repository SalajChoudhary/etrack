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
@Table(name="E_PROJ_SW_FACILITY_TYPE")
public @Data class ProjectSWFacilityType implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJ_SW_FACILITY_TYPE_S")
  @SequenceGenerator(name = "E_PROJ_SW_FACILITY_TYPE_S", sequenceName = "E_PROJ_SW_FACILITY_TYPE_S", allocationSize = 1)
  private Long projSwFacilityTypeId;
  private Long projectId;
  private Integer swFacilityTypeId;
  private Integer swFacilitySubTypeId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
