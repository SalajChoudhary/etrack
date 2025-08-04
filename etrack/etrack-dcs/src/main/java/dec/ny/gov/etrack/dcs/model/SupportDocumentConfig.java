package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class SupportDocumentConfig implements  Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Integer documentSubTypeTitleId;
  private String documentClassNm;
  private Integer documentTypeId;
  private Integer documentSubTypeId;
}
