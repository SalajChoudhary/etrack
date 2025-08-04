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
@Table(name = "E_GEO_INQUIRY_ALERT")
public @Data class GIInquiryAlert implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_GEO_INQUIRY_ALERT_S")
  @SequenceGenerator(name = "E_GEO_INQUIRY_ALERT_S", sequenceName = "E_GEO_INQUIRY_ALERT_S", allocationSize = 1)
  private Long inquiryAlertId;
  private Long inquiryId;
  private Date alertDate;
  private String alertNote;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Integer msgReadInd;
  private String alertRcvdUserId;
  private Long inquiryNoteId;
}
