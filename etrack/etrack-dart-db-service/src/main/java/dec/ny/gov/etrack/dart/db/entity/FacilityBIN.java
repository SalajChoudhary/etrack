package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_FACILITY_BIN")
public @Data class FacilityBIN implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long facilityBinId;
  private String bin;
  private Long projectId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String edbBin;
  private String deletedInd;
}
