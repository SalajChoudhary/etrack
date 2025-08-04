package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import lombok.Data;

public @Data class ApplicantReport implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String applicantName;
  private String address;
  private String cityStateZip;
  private String propertyRelationship;
  private String taxpayerId;
  private String telephone;
  private String emailAddress;
  private String ownerInd;
  private String operatorInd;
  private String lesseeInd;
}
