package dec.ny.gov.etrack.permit.service;

import java.util.List;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.DIMSRRequest;
import dec.ny.gov.etrack.permit.model.DartUploadDetail;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.EmailContent;
import dec.ny.gov.etrack.permit.model.PermitTaskStatus;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.ProjectInfo;
import dec.ny.gov.etrack.permit.model.ReviewCompletionDetail;

@Service
public interface ProjectService {
  
  /**
   * Persist the Project at initial stage to begin the user to submit permit.
   * This is the initial entry point to collect to mode of application, SEQR type, Public who is applying for and Facility they are planning to apply.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectDetail - Project details. 
   * 
   * @return - Updated Project details with newly added project Id.
   */
  ProjectDetail saveProject(final String userId, final String contextId,
      ProjectDetail projectDetail);

  /**
   * Update the Facility details received from the Step 1 in the Application permit process.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param projectDetail -Updated Facility details.
   * 
   * @return -Returns the updated Project details.
   */
  ProjectDetail updateProject(final String userId, final String contextId, final String token, final ProjectDetail projectDetail);

  /**
   * Retrieve the Facility details for the input project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @return - Project Initial details.
   */
  ProjectDetail retrieveProjectDetail(final String userId, final String contextId,
      final Long projectId);

  /**
   * Returns the list of statuses completed for this project for the input mode.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param mode - 0-Data Entry Mode, 1- Validate Mode.
   * 
   * @return - Return the list of completed status.
   */
  List<PermitTaskStatus> getProjectPermitStatus(final String userId, final String contextId,
      final Long projectId, Integer mode);
  

  /**
   * Store the Project related information collected in Step 3 sub step 2.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param projectInfo - Project information details.
   * 
   * @return - Updated the Project informations.
   */
  ProjectInfo storeProjectInfo(final String userId, final String contextId,
      final Long projectId, final ProjectInfo projectInfo);
  

  /**
   * Submit the project once all the details are added/updated and user wants to submit the project.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   */
  void submitProject(final String userId, final String contextId, final Long projectId);
    
  /**
   * Delete the requested un-submitted project by the user.
   *  
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param projectId - Project Id.
   */
  void deleteProject(String userId, String contextId, String token, Long projectId);
  
  /**
   * Add the list of support documents added and missed details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   */
  void addSupportDocument(final String userId, final String contextId, final Long projectId);
  
  /**
   * Update the signature received status for the input public id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param publicId - Public id.
   */
  void updateSignatureReceived(String userId, String contextId, Long projectId,
      List<Long> publicId);
  
  /**
   * Update the Project assignment details if the DEC staff wants to update for the input project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param assignmentNote - Assignment details.
   */
  void updateProjectAssignment(final String userId, final String contextId, final Long projectId, 
      final AssignmentNote assignmentNote);

  /**
   * Update the document reviewer details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param documentReview - Document review details.
   * 
   * @return - Email content.
   */
  EmailContent updateDocumentReviewerDetails(String userId, String contextId, Long projectId,
      DocumentReview documentReview);
  
  /**
   * Update the document review completion details. This will mark the document review request as completed and 
   * upload the correspondence into DMS if any.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param projectId - Project Id.
   * @param reviewCompletionDetail - Review Completion details.
   */
  void updateDocumentReviewerCompletionDetails(String userId, String contextId, final String token,
      Long projectId, ReviewCompletionDetail reviewCompletionDetail);

  /**
   * Save the DIMSR request in both eTrack and DART database.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param ppid - an unique GUID assigned for the logged in user by AD/ASMS system.
   * @param dimsrRequest - DIMSR request.
   * 
   * @return - Updated DIMSR request with newly created project id.
   */
  DIMSRRequest saveDIMSRDetails(
      final String userId, final String contextId, final String token, final String ppid, final DIMSRRequest dimsrRequest);
  
  /**
   * Upload the validated projects into DART.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param ppid - an unique GUID assigned for the logged in user by AD/ASMS system.
   * @param projectId - Project Id.
   * @param dartUploadDetail - Detail to be uploaded into DART.
   */
  void uploadProjectDetailsToEnterprise(
      String userId, String contextId, String token, String ppid, Long projectId, DartUploadDetail dartUploadDetail);


  /**
   * Reject the Project which has requested for the validation.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project id.
   * @param rejectedReason - Project Rejected reason.
   */
  void rejectProjectValidation(final String userId, final String contextId, 
      final Long projectId, final String rejectedReason);

  /**
   * Associate the Geographical Inquiry to the project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project id.
   * @param inquiryId - Inquiry id.
   */
  void associateGeographicalInquiryToProject(String userId, String contextId,
      Long projectId, Long inquiryId);
}
