package dec.ny.gov.etrack.permit.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.model.ApplicationPermit;
import dec.ny.gov.etrack.permit.model.ApplicationPermitDetail;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;

@Service
public interface ETrackPermitService {
  
  
  /**
   * Retrieve the Facility/Project details to display in the Step 1 process for the user to amend it if required.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Project details.
   */
  ProjectDetail getProjectDetails(final String userId, final String contextId,
      final Long projectId);

  /**
   * Returns the list of Permit types for the input Project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return  List of Permits.
   */
  List<String> getPermitTypes(final String userId, final String contextId,
      final Long projectId);

  /**
   * Remove the applied permit for the requested application id and permit type.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicationId - Requested Application id to be deleted.
   * @param permitType - Permit Type.
   */
  void removeApplicationPermit(final String userId, final String contextId,
      final Long projectId, final Long applicationId, final String permitType);

  /**
   * Save the requested application permit for the input project id.
   *  
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicationPermit - new Application/Permit
   */
  void saveApplicationPermits(final String userId, final String contextId,
      final Long projectId, ApplicationPermit applicationPermit);
  
  /**
   * Store the validated indicator for the requested activity step.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param category - Category- Short description of the Category.
   * @param activityId - Project Activity Status Id.
   * @param indicator - Indicator 0-Not validated, 1-Validated
   */
  void storeValidatorForStep(final String userId, final String contextId, final Long projectId, final String category,
      final Integer activityId, final Integer indicator);

  /**
   * Remove the already applied permits for the requested Application ids if any exist.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicationIds - List of Application Ids to be deleted.
   */
  void removeApplicationPermits(final String userId, final String contextId, final Long projectId,
      List<Long> applicationIds);

  /**
   * Update the existing permits.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param existingPermits - Updated details of the existing permits.
   */
  void updateExistingPermitAmendDetails(final String userId, final String contextId, final Long projectId,
      List<ApplicationPermitDetail> existingPermits);

  /**
   * Retrieve the Permit details associated with the Batch and Project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param batchId - Batch Id.
   * 
   * @return - List of permits associated with this batch id.
   */
  List<ApplicationPermitDetail> retrievePermitDetails(final String userId, final String contextId, final Long projectId,
      Long batchId);

  /**
   * Store the reviewed permits by the DEC Staff.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param reviewedPermits - List of Reviewed Permits.
   * 
   * @return - Updated the reviewed permits.
   */
  Object storeReviewedPermits(final String userId, final String contextId,
      Map<String, List<ReviewedPermit>> reviewedPermits);

  /**
   * Assign the permit contact to the permit form.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param assignContacts - Contact/Agent assignment details.
   */
  void assignContacts(final String userId, final String contextId, final Long projectId,
      List<ApplicationPermitDetail> assignContacts);
  
  /**
   * Update the Transaction types.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicationPermit - Permit and updated Transaction type.
   */
  void updateAmendedApplicationTransTypes(final String userId, final String contextId, final Long projectId,
      ApplicationPermit applicationPermit);
  
  /**
   * Persist the Additional Application permit requested by the Staff.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param guid - ppid assigned for this user in Ad/ASMS.
   * @param projectId - Project Id.
   * @param applicationPermit - Additional application permit.
   */
  void saveAdditionalApplicationPermit(final String userId, final String contextId, 
      final String token, final String guid, final Long projectId, ApplicationPermit applicationPermit);
}
