package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity. Facility;
import dec.ny.gov.etrack.dart.db.entity.PublicSummary;
import lombok.Data;

public @Data class ProjectSummary implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String description;
  private String loggedInUserName;
  private String assignedAnalystId;
  private String assignedAnalystName;
  private String analystAssignedDate;
  private Integer emergencyInd;
  private List<PublicSummary> contactAgents;
  private List<PublicSummary> owners;
  private List<PublicSummary> publics;
  private Facility facility;
  private List<ProjectNoteView> notes;
  private List<Application> application;
  private Map<String, Object> documents;
  private List<Document> reviewDocuments;
  private List<Invoice> invoice;
  private List<Alert> alerts;
  private List<DartPermit> activeAuthorizations;
  private List<DashboardDetail> pendingApplications;
  private Milestone milestone;
//  private boolean readOnly;
  private String invoiceReq;
  private String foilReqInd;
  private List<String> foilRequestNumber;
  private LitigationRequest litigationRequest;
  private List<LitigationRequest> litigationRequestHistory;
  private String dimsrInd;
  private List<Long> inquiries;
//  private String onlineApplnInd;
}
