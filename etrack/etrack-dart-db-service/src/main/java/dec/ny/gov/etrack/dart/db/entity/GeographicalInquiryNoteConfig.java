package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class GeographicalInquiryNoteConfig implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String actionTypeCode;
  private String actionTypeDesc;
}

