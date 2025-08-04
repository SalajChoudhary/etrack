package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Entity
@Table(name = "E_FACILITY")
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Facility implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;  
  @Id
  private Long projectId;
  private String decId;
  private String facilityName;
  private Long edbDistrictId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String comments;
}