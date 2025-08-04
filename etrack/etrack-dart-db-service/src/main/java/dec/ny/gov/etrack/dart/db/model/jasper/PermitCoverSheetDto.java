package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class PermitCoverSheetDto implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String decId;
  private String facilityName;
  private String projectId;
  private String projectDescription;
  private String proposedStartDate;
  private String proposedEndDate;
  private String constructionInd;
  private String proposedUse;
  private List<String> newPermits;
  private List<String> modificationPermits;
  private List<String> renewalPermits;
  private List<String> contactPermits;
  private List<FacilityReport> facilityReport;
  private List<ApplicantReport> applicantReport;
  private List<ApplicationContactReport> contactReport;
}
