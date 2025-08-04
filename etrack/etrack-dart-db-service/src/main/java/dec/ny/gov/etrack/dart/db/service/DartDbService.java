package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.ReviewDocument;
import dec.ny.gov.etrack.dart.db.model.Alert;
import dec.ny.gov.etrack.dart.db.model.BridgeIdNumber;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.model.ProjectRejectDetail;
import dec.ny.gov.etrack.dart.db.model.RegionUserEntity;

@Service
public interface DartDbService {
  
  /**
   * Retrieve the Project Information for the input project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.

   * @return - Project Information.
   */
  ProjectInfo getProjectInformation(final String userId, final String contextId,
      final Long projectId);

  /**
   * Retrieve the associated Facility BINs for the input Project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of BridgeIdNumber.
   */
  List<BridgeIdNumber> getFacilityBins(final String userId, final String contextId,
      final Long projectId);

  /**
   * Retrieve all the in progress applications for the input user id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of in progress applications by this user.
   */
  List<DashboardDetail> getUnsubmittedApps(final String userId, final String contextId);
  
  /**
   * Retrieve the summary for the support documents uploaded for this project.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of Support Document.
   */
  Object retrieveSupportDocumentSummary(String userId, String contextId, Long projectId);

  /**
   * Retrieve the all the details required for the logged in DEC Staff.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Projects with various status, created/managed by this user. 
   */
  Object getUserDashboardDetails(String userId, String contextId);

  /**
   * Retrieve all the required applications to be signed-in in Step 5.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Retrieve all the Required Applicants to be signed-in
   */
  Object retrieveRequiredApplicantsToSign(final String userId, final String contextId, Long projectId);
  
  /**
   * Retrieve the project summary for the input project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param projectId - Project Id.
   * 
   * @return - Project Summary/Information.
   */
  Object retrieveProjectSummary(final String userId, final String contextId, final String jwtToken, final Long projectId);

  /**
   * Retrieve Project details for the input reviewer to review.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityRegionId - Facility region id  to pull the list of projects associated with this region.
   * 
   * @return - List of Project details.
   */
  Object getProgramReviewerDashboardDetails(final String userId, final String contextId, final Integer facilityRegionId);
  
  /**
   * Retrieve entire list of project details to display in the Regional dashboard or return only the region specific projects.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityRegionId - Facility Region Id.
   * 
   * @return - Region dashboard details.
   *
  Object getUserRegionalDashboard(final String userId, final String contextId, final Integer facilityRegionId);
  */
  
  /**
   * Retrieve the list of users belongs to the region and role type Id.
   *    Role Type Id :
   *        50 - DEC Analyst
   *        55 - Program Area reviewer.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param regionId - Region Id.
   * @param roleTypeId - Role Type Id.
   * 
   * @return - List of Users.
   */
  Object getUsersByRegionAndRoleTypeId(final String userId, final String contextId, final Integer regionId, final Integer roleTypeId);
  
  /**
   * Retrieve the Assignment details for the input Project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @return - User Assignment details.
   */
  Object retrieveAssignmentDetails(String userId, String contextId, Long projectId);
  
  /**
   * Retrieve the list of alerts to display in the Analyst Dashboard.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Alerts.
   */
  List<Alert> retrieveAnalystsAlerts(String userId, String contextId);
  
  /**
   * Retrieve all the reviewer eligible documents.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of review documents.
   */
  List<ReviewDocument> retrieveEligibleReviewDocuments(String userId, String contextId, Long projectId);
  
  /**
   * Returns the region id associated with this DEC Staff.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Long value of Region Id.
   */
  Long findRegionIdByUserId(String userId, String contextId);
  
  /**
   * Returns the list of users from enterprise system who has valid email address.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of valid users.
   */
  List<RegionUserEntity> getUsersWithValidEmailAddress(final String userId, final String contextId);
  
  /**
   * Retrieve all the DIMSR related details like Pending Applications, 
   * Existing Applications, Facility and LRP details for the input DEC ID.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param decId - DEC ID.
   * 
   * @return - DIMSR related information.
   */
  Object retrieveSupportDetailsForDIMSR(String userId, String contextId, String decId);
  
  /**
   * Retrieve the Project details for the input Online user.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param dartMilestoneMapByBatchId - Milestone details per batch.
   * 
   * @return - List of DashboardDetail.
   */
  List<DashboardDetail> retrieveAllOnlineUserDartApplications(final String userId, final String contextId, 
      Map<Long, DartMilestone> dartMilestoneMapByBatchId);

  /**
   * Retrieve all the active authorizations Permits for the input facility (enterprise district Id).
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param edbDistrictId - Enterprise District Id.
   * 
   * @return - List of Active Authorization Permits.
   */
  List<String> retrieveActiveAuthorizationPermits(final String userId, final String contextId, final Long projectId,
      final Long edbDistrictId);
  
  /**
   * List of alerts associated with the logged in user.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Alerts.
   */
  List<Alert> viewAnalystDashboardAlerts(final String userId, final String contextId);
  
  /**
   * Retrieve the Project rejection details if the input project rejected. else return empty.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Project Rejection details.
   */
  ProjectRejectDetail retrieveProjectRejectionDetails(String userId, String contextId, Long projectId);
}
