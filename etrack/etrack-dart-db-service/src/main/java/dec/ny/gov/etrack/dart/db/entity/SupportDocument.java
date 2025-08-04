package dec.ny.gov.etrack.dart.db.entity;

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
  private long documentTitleId;
  private String documentTypeDesc;
  private String documentSubTypeDesc;
  private String documentTitle;
  private String reqType;
  private Long documentTypeId;
  private Long documentSubTypeId;
  private Long documentSubTypeTitleId;
}
