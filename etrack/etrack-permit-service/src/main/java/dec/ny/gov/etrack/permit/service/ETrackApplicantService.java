package dec.ny.gov.etrack.permit.service;

import java.util.List;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.model.Applicant;

@Service
public interface ETrackApplicantService {

  /**
   * Prepare the Public Entity details to Persist into eTrack database.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicant - new Public detail.
   * @param category - public category. P-Public, O-Owner and C- Contact/Agent.
   * 
   * @return - Transformed Public details.
   */
  Applicant addApplicant(final String userId, final String contextId, final Long projectId,
      Applicant applicant, String category);

  /**
   * Save the public/applicant details into ETrack Database.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id. - Project Id
   * @param applicant - Public details.
   * 
   * @return - Persisted Public details with newly created Public Id.
   */
  Applicant saveApplicant(final String userId, final String contextId, final Long projectId,
      final Applicant applicant);

  /**
   * Update the existing public with the requested update detail.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicant - Updated public details needs to be persisted.
   * @param category - Public Category. P - Public, O- Owner, C- Contact/Agent.
   * 
   * @return - Updated the Applicant details.
   */
  Applicant updateApplicant(final String userId, final String contextId,
      final Long projectId, Applicant applicant, final String category);

  /**
   * Returns the legal names sounded like the input legal name.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param legalName - Legal Name.
   * 
   * @return - Returns the list of all the matched legal name(s)
   */
  Object getBusinessVerified(String userId, String contextId, String legalName);

  /**
   * Delete the requested Public. Delete if its newly created or non-associate the Public from project if its DART Public.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param edbPublicId - Enterprise Public Id.
   * @param applicantId - eTrack Public Id
   * @param category - Public Category. P - Public, O- Owner, C- Contact/Agent.
   */
  void deleteApplicant(final String userId, final String contextId, final Long projectId,
      final Long edbPublicId, final Long applicantId, final String category);

  /**
   * Delete the Contact/Agents for the list of input contact Ids and project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param contactIds - Contact/Agent Ids to be deleted.
   */
  void deleteContacts(final String userId, final String contextId, final Long projectId,
      final List<Long> contactIds);
  
  /**
   * Mark the list of input applicants as signed in the Step 5. 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicants - List of applicant ids.
   */
  void addAcknowledgedApplicants(final String userId, final String contextId,
      final Long projectId, final List<Long> applicants);


  /**
   * Update/Mark the input public as Online Submitter for the requested Project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param publicId - Public id.
   * @param publicIdTobeDeleted  - Public Id to be deleted or dis-associate from the project if new or existing public respectively.
   */
  void updateOnlineSubmitter(String userId, String contextId, Long projectId, Long publicId, Long publicIdTobeDeleted);
}
