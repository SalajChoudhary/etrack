package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class MissingDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String reason;
  private List<String> documentTitleIds;
}
