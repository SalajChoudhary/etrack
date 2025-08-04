package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SpatialInquirySupportDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Integer reqdDocForSpatialInqId;
  private String documentTitle;
  private Integer documentSubTypeTitleId;
  private String spatialInqCategoryCode;
  private String spatialInqCategoryDesc;
  private Integer reqdDocumentInd;
}
