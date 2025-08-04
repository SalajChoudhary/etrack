package dec.ny.gov.etrack.permit.service;

import org.springframework.stereotype.Service;

@Service
public interface ETrackAlertService {

  /**
   * Delete the requested alert message.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param inquiryId = Geographical Inquiry id.
   * @param alertId - Alert id.
   */
  void deleteAlertMessage(final String userId, final String contextId, final Long projectId, 
      final Long inquiryId,  final Long alertId);
  
  /**
   * Mark the requested alert message as read by the user. so, this message doesn't need to be shown to the user next time.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param alertId - Alert id. 
   */
  void updateAlertMessageAsRead(final String userId, final String contextId, final Long projectId,
      final Long alertId);

}
