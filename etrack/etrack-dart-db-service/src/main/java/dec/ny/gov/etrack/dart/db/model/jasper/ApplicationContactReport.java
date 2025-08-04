package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class ApplicationContactReport implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private List<String> permitList;
  private String contactName;
  private String contactAddress;
  private String contactCityStateZip;
  private String contactPhone;
  private String contactEmail;
}
