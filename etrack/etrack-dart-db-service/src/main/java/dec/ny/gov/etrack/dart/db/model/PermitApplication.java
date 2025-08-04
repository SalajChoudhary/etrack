package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class PermitApplication implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long applicationId;
  private Long edbApplicationId;
  private Long edbDistrictId;
  private Long batchId;
  private String permitTypeCode;
  private String permitTypeDesc;
  private String projectDesc;
  private String transType;
  private String edbTransType;
  private String dartPendingApp;
  private List<PermitContact> contacts;
  private String receivedDate;
  private String programId;
  private String programIdFormatted;
  private String effectiveStartDate;
  private String effectiveEndDate;
  private String formSubmittedInd;
  private String supplementalFromLabel;
  private String newReqInd;
  private String modReqInd;
  private String extnReqInd;
  private String renewReqInd;
  private String transferReqInd;
  private String pendingAppTransferReqInd;
  private String modQuestionAnswer;
  private String permitRenewedInd;
  private List<AvailTransType> availableTransTypes;
  private Integer edbTrackingInd;
  private Integer trackingInd;
  private String calculatedBatchIdForProcess;
  private String batchGroup;
  private String applnPermitDesc;
  private String extendedDate;
  private Long edbAuthId;
}
