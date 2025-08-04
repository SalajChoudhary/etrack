package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ReviewApplication implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long applId;
  private Long batchId;
  private Date requestDate;
  private Date dueDate;
  private String programStaff;
  private Long districtId;
  private String decId;
  private String facilityName;
  private String status;
  private String county;
  private String permitType;
  private String municipality;
}
