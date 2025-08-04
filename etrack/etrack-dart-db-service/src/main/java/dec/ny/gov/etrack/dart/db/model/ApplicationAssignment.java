package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import lombok.Data;

public @Data class ApplicationAssignment implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String formName;
  private Integer permitFormId;
  private Set<Long> applicationIds;
  private List<PermitContact> contacts;
  private Set<String> jafForms;
  
}
