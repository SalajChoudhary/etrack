package dec.ny.gov.etrack.dcs.service;

import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dcs.model.EmailContent;

@Service
public interface EmailService {

  /**
   * Send an email to the requested user in the email content.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param emailContent - Email content includes email addresses, content, subject etc..,
   */
  void sendEmail(final String userId, final String contextId, 
      final Long projectId, EmailContent emailContent);
}
