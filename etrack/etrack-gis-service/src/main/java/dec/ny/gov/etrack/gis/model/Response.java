package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class Response implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String resultCode;
  private String resultResponse;
}
