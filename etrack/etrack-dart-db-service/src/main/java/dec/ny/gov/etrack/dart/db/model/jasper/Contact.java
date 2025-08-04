package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import lombok.Data;

public @Data class Contact implements Serializable{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String name;
  private String address;
  private String citystatezip;
  private String taxpayerid;
  private String telephone;
  private String email;
  private String relationship;
}
