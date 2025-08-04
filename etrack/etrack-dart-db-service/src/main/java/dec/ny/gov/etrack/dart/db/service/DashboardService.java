package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;

@Service
public interface DashboardService {
  
  /**
   * Retrieve all the eTrack projects which are yet to be submitted.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request. 
   * 
   * @return - List of Resume entry projects.
   */
  List<DashboardDetail> getResumeEntryPrjects(String userId, String contextId);
  
  /**
   * Retrieve all the eTrack projects which are eligible to review or under review process.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request. 
   * 
   * @return - List of review in progress projects.
   */
  List<DashboardDetail> getValidateEligibleProjects(final String userId, final String contextId);
  
 
  /**
   * Retrieve all the Active projects from enterprise.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Active projects.
   */
  List<DashboardDetail> getAllActiveProjects(final String userId, final String contextId);
  
  /**
   * Retrieve the list of all the tasks due status applications from the enterprise.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request. 
   * 
   * @return - List of Tasks Due applications.
   */
  List<DashboardDetail> getTasksDueApplications(final String userId, final String contextId);
  
  /**
   * Retrieve the list of all the Applicant Response due status applications from the enterprise.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Applicant Response Due applications.
   */
  List<DashboardDetail> getApplicantResponseDueApplications(final String userId, final String contextId);
  
  /**
   * Retrieve the list of all the Suspended applications from the enterprise.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Suspended applications.
   */
  List<DashboardDetail> getSuspendedApplications(final String userId, final String contextId);
  
  /**
   * Retrieve the list of all the Out for review applications from the enterprise.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Out for review applications.
   */
  List<DashboardDetail> getOutForReviewApplications(final String userId, final String contextId);
  
  /**
   * Retrieve the list of all the Emergency Authorization applications from the enterprise.
   * 
   * @param userId - User who initiates request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Out for Emergency Authorization applications.
   */
  List<DashboardDetail> getEmergencyAuthorizationApplications(final String userId, final String contextId);
  
  /**
   * Retrieve all the Unissued Applications from enterprise.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param dartMilestoneMapWithBatchId - DART milestone status for each batch id.
   * 
   * @return - List of Dashbaord details.
   */
  List<DashboardDetail> retrieveAllThePendingApplications(final String userId,
      final String contextId, Map<Long, DartMilestone> dartMilestoneMapWithBatchId);

  /**
   * Retrieve all the regional related all active applications. Retrieve region specific if any region id passed else everything.
   *  
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityRegionId - Facility region id to pull the list of applications.
   * 
   * @return - List of All Active Applications.
   */
  List<DashboardDetail> getRegionalAllActiveApplications(String userId, String contextId,
      Integer facilityRegionId);

  /**
   * Retrieve all the regional related all active applications. Retrieve region specific if any region id passed else everything.
   *  
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityRegionId - Facility region id to pull the list of applications.
   * 
   * @return - List of All Active Applications.
   */
  List<DashboardDetail> getRegionalUnvalidatedApplications(String userId, String contextId,
      Integer facilityRegionId);

  /**
   * Retrieve all the regional related all active applications. Retrieve region specific if any region id passed else everything.
   *  
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityRegionId - Facility region id to pull the list of applications.
   * 
   * @return - List of All Active Applications.
   */
  List<DashboardDetail> getRegionalProgramReviewApplications(String userId, String contextId,
      Integer facilityRegionId);

  /**
   * Retrieve all the regional related all active applications. Retrieve region specific if any region id passed else everything.
   *  
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityRegionId - Facility region id to pull the list of applications.
   * 
   * @return - List of All Active Applications.
   */
  List<DashboardDetail> getRegionalDisposedApplications(String userId, String contextId, Integer facilityRegionId);

}
