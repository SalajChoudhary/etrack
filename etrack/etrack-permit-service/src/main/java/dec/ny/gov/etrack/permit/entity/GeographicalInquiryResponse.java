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
@Table(name="E_SPATIAL_INQ_RESPONSE")
public  @Data class GeographicalInquiryResponse implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
 
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_SPATIAL_INQ_RESPONSE_S")
  @SequenceGenerator(name = "E_SPATIAL_INQ_RESPONSE_S", sequenceName = "E_SPATIAL_INQ_RESPONSE_S", allocationSize = 1)
  private Long inqResponseId;
  private Long inquiryId; 
  private Integer responseSentInd;
  private Date responseSentDate;
  private Integer inquiryCompletedInd;
  private String responseText;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
