package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

public @Data class ProjectSubmittalReport implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private BigDecimal emailedProjects;
  private BigDecimal paperProjects;
  private BigDecimal percentageOfEmailedProjects;
  private BigDecimal percentageOfPaperProjects;
  private BigDecimal totalProjects;
}
