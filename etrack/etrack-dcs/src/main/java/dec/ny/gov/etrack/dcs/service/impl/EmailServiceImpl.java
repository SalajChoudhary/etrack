package dec.ny.gov.etrack.dcs.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.EmailContent;
import dec.ny.gov.etrack.dcs.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

  @Autowired
  private JavaMailSender mailSender;

  private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class.getName());
  private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^(.+)@(\\S+)$");

  @Value("${etrack.email.correspondence.from.address}")
  private String fromEmailAddress;



  /**
   * This method is used to send an email to the requested user 
   * 
   * @param userId - User who initiates this request
   * @param contextId - Unique Id
   * @param emailContent - Email content
   */
  @Override
  public void sendEmail(final String userId, final String contextId, 
      final Long projectId, EmailContent emailContent) {

    logger.info("Requesting to send an email. User Id {}, Context Id {}", userId, contextId);
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      logger.info(
          "Check whether we have any existing email correspondence needs to be "
              + "replied or pending email getting initiated. User Id {} , Context Id {}",
          userId, contextId);

      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

      InternetAddress[] toAddresses = null;
      if (!CollectionUtils.isEmpty(emailContent.getToEmailId())) {
        toAddresses = new InternetAddress[emailContent.getToEmailId().size()];
        for (int index = 0; index < emailContent.getToEmailId().size(); index++) {
          toAddresses[index] = new InternetAddress(emailContent.getToEmailId().get(index));
        }
        mimeMessageHelper.setTo(toAddresses);
      } else {
        throw new ValidationException("TO_ADR_EMPTY", "To address cannot be empty");
      }
      InternetAddress[] ccAddresses = null;
      if (!CollectionUtils.isEmpty(emailContent.getCcEmailId())) {
        ccAddresses = new InternetAddress[emailContent.getCcEmailId().size()];
        for (int index = 0; index < emailContent.getCcEmailId().size(); index++) {
          ccAddresses[index] = new InternetAddress(emailContent.getCcEmailId().get(index));
        }
        mimeMessageHelper.setCc(ccAddresses);
      }
      mimeMessageHelper.setSubject(emailContent.getSubject());
      mimeMessageHelper.setFrom(new InternetAddress(fromEmailAddress));
      StringBuilder amendEmailContent = new StringBuilder();
      mimeMessageHelper.setText(emailContent.getEmailBody());
      mimeMessageHelper.setText(amendEmailContent.toString());
      StringBuilder subjectShortDesc = new StringBuilder();
      subjectShortDesc.append(emailContent.getSubject());
      
      if (!StringUtils.hasLength(emailContent.getFromEmailId())) {
        throw new ValidationException("FROM_EMAIL_ADDR_BLANK", "From Email address is blank");
      }
      for (String toEmail : emailContent.getToEmailId()) {
        logger.info("Create To email correspondences. User id {}, Context Id {}, toEmail {}",
            userId, contextId, toEmail);
        Matcher matcher = EMAIL_REGEX_PATTERN.matcher(toEmail);
        if (!matcher.matches()) {
          logger.error("Invalid TO email address is passed in the input. User Id {} , Context {}",
              userId, contextId);
          throw new ValidationException("INVALID_EMAIL_ADR", "Invalid Email address is passed "+ toEmail);
        }
      }
      mailSender.send(mimeMessage);
    } catch (ValidationException ve) {
      throw ve;
    } catch (Exception e) {
      logger.error("Error while sending email and persisting the details "
          + "into database. User Id , Context Id {}", userId, contextId, e);
      throw new DcsException("Error while processing and sending email", e);
    }
    logger.info("Exiting from sending an email . User Id {}, Context Id {}", userId, contextId);
  }
}
