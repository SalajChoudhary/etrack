package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class GIInquiryAlert implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long inquiryAlertId;
  private Long inquiryId;
  private Date alertDate;
  private String alertNote;
  private String createdById;
  private Date createDate;
  private Integer readInd;
  private String comments;
}
