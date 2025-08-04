package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class ApplicationPermitDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long applicationId;
  private String permitTypeCode;
  private String edbTransType;
  private Long roleId;
  private Long edbApplnId;
  private String newReqInd;
  private String modReqInd;
  private String extnReqInd;
  private String renewReqInd;
  private String transferReqInd;
  private String pendingAppTransferReqInd;
  private String modQuestionAnswer;
  private Long batchId;
  private String programId;
  private Integer polEmissionInd;
  private String modExtReason;
  private String estCompletionDate;
  private Integer trackingInd;
  private Integer edbTrackingInd;
  private String edbPermitEffectiveDate;
  private String edbPermitExpiryDate;
  private String calculatedBatchIdForProcess;
  private String transType;
  private Long edbAuthId;
  private Long permitFormId;
}
