package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

import dec.ny.gov.etrack.dart.db.view.View;
import lombok.Data;

public @Data class DashboardDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonView(View.Public.class)
  private Long projectId;
  @JsonView(View.Public.class)
  private String projectDesc;
  @JsonView(View.Public.class)
  private Facility facility;
  @JsonView(View.Public.class)
  private String applicant;
  private String createDate;
  @JsonView(View.Public.class)
  private String rcvdDate;
  @JsonView(View.Public.class)
  private Long applicantId;
  @JsonView(View.Public.class)
  private Long edbPublicId;
  private String permitType;
  private String permitTypeDesc;
//  private LinkedHashSet<String> permitTypes;
//  private LinkedHashSet<String> permitTypeDescs;
  private String appType;
  private Set<String> appTypes;
  private String trackedId;
  private String trackedIdFormatted;
  private Long batchId;
  private String dartStatus;
  private String responseDate;
  private String outForReview;
  private String dateAssigned;
  private String dueDate;
  private Date dueDateVal;
  private String userAssigned;
  private String analystName;
  private String eaInd;
  private Integer gpInd;
  private Integer region;
  private String programStaff;
//  private String programManager;
  private Set<String> programStaffs;
  private Set<String> programManagers;
  private String sapaInd;
  private String sapaDate;
  private Integer renOrderNum;
  private Integer modOrderNum;
  private String dispositionDate;
  private String effectiveDate;
  private String expiryDate;
  private Long edbDistrictId;
  private String transType;
  private String rejectedProject;
  private String rejectedReason;
  private String rejectedDate;
}
