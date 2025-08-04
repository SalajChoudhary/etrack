package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="E_POLYGON_FOR_UPLOAD")
public @Data class UploadPolygonEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long polygonForUploadId;
  private Long edbDistrictId;
  private Long projectId;
//  private Long approvedPolygonId;
  private Integer retryCounter;
  private Date createDate;
}
