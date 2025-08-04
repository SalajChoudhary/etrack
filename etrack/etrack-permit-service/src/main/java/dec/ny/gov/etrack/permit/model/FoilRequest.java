package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class FoilRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long foilReqId;
  private String foilReqInd;
  private List<String> foilRequestNumber;
  private String modified;
}
