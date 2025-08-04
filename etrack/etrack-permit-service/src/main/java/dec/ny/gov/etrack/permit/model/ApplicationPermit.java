package dec.ny.gov.etrack.permit.model;

import java.util.List;
import lombok.Data;

public @Data class ApplicationPermit {
  private String permitType;
  private Long edbApplId;
  private Integer constrnType;
  private List<ApplicationPermitDetail> etrackPermits;
  private List<ApplicationPermitDetail> dartPermits;
  private String emergencyInd;
  private List<ActiveAdditionalPermit> activeAuthorizations;
}
