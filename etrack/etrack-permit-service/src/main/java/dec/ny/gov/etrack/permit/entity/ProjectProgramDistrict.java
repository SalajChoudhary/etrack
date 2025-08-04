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
@Table(name="E_PROGRAM_DISTRICT_IDENTIFIER")
public @Data class ProjectProgramDistrict implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROGRAM_DISTRICT_IDENTIFIER_S")
  @SequenceGenerator(name = "E_PROGRAM_DISTRICT_IDENTIFIER_S", sequenceName = "E_PROGRAM_DISTRICT_IDENTIFIER_S", allocationSize = 1)
  private Long programDistrictId;
  private String programDistrictCode;
  private String programDistrictIdentifier;
  private String edbProgramDistrictIdentifier;
  private Long projectId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private String modifiedDate;
}
