package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.entity.EmailCorrespondence;
import dec.ny.gov.etrack.dart.db.model.DashboardEmailEnvelop;
import dec.ny.gov.etrack.dart.db.model.EmailContent;
import dec.ny.gov.etrack.dart.db.model.VirtualDesktopEmailShortDesc;

@Service
public interface EmailService {

  /**
   * Retrieve all the email correspondence by the user and project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of Email Correspondence(s).
   */
  Object retrieveEmailCorrespondence(final String userId, final String contextId, final Long projectId);
  
  /**
   * Retrieve the Email details for the input correspondence id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param correspondenceId - Correspondence id.
   * @param envelopRequestedInd - User requested the correspondence by clicking envelop from VW.
   * 
   * @return - Email Content for the correspondence Id.
   */
  EmailContent retrieveEmailCorrespondenceByCorrespondenceId(final String userId, final String contextId, 
      final Long projectId, final Long correspondenceId, final boolean envelopRequestedInd);
  
  /**
   * Returns the list of email notifications available for the logged in user in Analyst Dash board.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Email notification details.
   */
  List<DashboardEmailEnvelop> retrieveEmailNotificationDetails(final String userId, final String contextId);
  
  /**
   * List of Email(s) sent and receive for the input Project Id for the logged in User.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Sent and Received Email notifications details.
   */
  Map<String, List<VirtualDesktopEmailShortDesc>> retrieveEmailNotificationsInVirtualDesktop(final String userId, final String contextId,
      final Long projectId);
  
  /**
   * Returns the list of email correspondence(s) between the sender and receive id passed as an input.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param emailSendorId - Email Sender id.
   * @param emailReceiverId - Email Received user id.
   * @param correspondenceType - Correspondence type - S - Send, R - Received.
   * 
   * @return - List of Email Correspondence(s)
   */
  List<EmailCorrespondence> retrieveCorrespondencesForTheRequestor(final String userId, final String contextId, final Long projectId,
      final String emailSendorId, final String emailReceiverId, final String correspondenceType);

  /**
   * Retrieve all the correspondences sent/received by the reviewer while reviewing the input document Id.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param reviewerId - Program Area Reviewer Id.
   * @param projectId - Project Id.
   * @param documentId - Document Id.
   * 
   * @return - List of Email Correspondences.
   */
  List<List<String>> retrieveEmailCorrespondenceByDocumentId(final String userId, final String contextId,
      final String reviewerId, final Long projectId, final Long documentId);

  /**
   * List of correspondences sent/received by the reviewer for the Document Ids.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param reviewerId - Program Area Reviewer Id.
   * @param projectId - Project Id.
   * @param documentIds - Document Ids.
   * 
   * @return - List of Correspondences.
   */
  List<EmailCorrespondence> retrieveCorrespondenceByReviewerAndDocumentIds(String userId, String contextId,
      String reviewerId, Long projectId, List<Long> documentIds);

}

