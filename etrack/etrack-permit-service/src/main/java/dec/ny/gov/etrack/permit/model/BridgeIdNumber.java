package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class BridgeIdNumber implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String bin;
  private String edbBin;
}
