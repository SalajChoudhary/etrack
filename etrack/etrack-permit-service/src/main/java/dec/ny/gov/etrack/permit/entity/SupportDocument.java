package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class SupportDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long documentId;
  private String ecmaasGuid;
  private String documentClassNm;
  private String documentNm;
}
