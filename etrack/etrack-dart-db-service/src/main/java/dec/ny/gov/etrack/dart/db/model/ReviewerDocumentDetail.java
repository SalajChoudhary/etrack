package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ReviewerDocumentDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long correspondenceId;
  private String displayName;
  private Long projectId;
  private String facilityName;
  private String decId;
  private String county;
  private String municipality;
  private String dateAssigned;
  private String dueDate;
  private String permitTypeCode;
  private String permitTypeDesc;
  private String programStaff;
  private String programManager;
  private Long edbDistrictId;
  private Long edbPublicId;
  private String eaInd;
  private Integer gpInd;
  private String appStatus;
}
