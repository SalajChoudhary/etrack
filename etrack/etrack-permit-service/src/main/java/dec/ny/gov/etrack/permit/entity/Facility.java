package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_FACILITY")
public @Data class Facility implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;  
  @Id
  private Long projectId;
  private String facilityName;
  private Long edbDistrictId;
  private String decId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String comments;
  private String chgBoundaryReason;
}