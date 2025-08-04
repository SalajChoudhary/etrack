package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="E_GEO_INQUIRY_NOTE")
public @Data class GeographicalInquiryNote implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_GEO_INQUIRY_NOTE_S")
  @SequenceGenerator(name = "E_GEO_INQUIRY_NOTE_S", sequenceName = "E_GEO_INQUIRY_NOTE_S", allocationSize = 1)
  private Long inquiryNoteId;
  private Long inquiryId;
  private Integer actionTypeCode;
  private Date actionDate;
  private String actionNote;
  private String comments;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}

