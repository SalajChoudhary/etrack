package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_GEO_INQUIRY_NOTE")
public @Data class GeographicalInquiryNote implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long inquiryNoteId;
  private Long inquiryId;
  private Integer actionTypeCode;
  private String actionTypeDesc;
  private String actionDate;
  private String actionNote;
  private String comments;
  private String createDate;
  private String createdById;
  private String modifiedById;
  private Date modifiedDate;
}
