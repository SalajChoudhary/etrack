package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class SupportDocumentConfig implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long supportDocId;
  private Long documentTypeId;
  private Long documentSubTypeId;
  private String documentName;
  private String permitTypeCode;
  private Long documentSubTypeTitleId; 
  
}
