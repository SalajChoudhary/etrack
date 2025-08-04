package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class Permit implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String decId;
  private String facilityName;
  private String projectId;
  private String newPermits;
  private String modification;
  private String renewal;
  private String proposedUseCode;
  private String proposedStartDate;
  private String proposedEndDate;
  private Facility facility;
  private List<Applicant> applicants;
  private List<Contact> contacts;
  
}
