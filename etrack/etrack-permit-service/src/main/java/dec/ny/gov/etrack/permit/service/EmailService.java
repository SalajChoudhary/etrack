package dec.ny.gov.etrack.permit.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.EmailCorrespondence;
import dec.ny.gov.etrack.permit.entity.RegionUserEntity;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.EmailContent;
import dec.ny.gov.etrack.permit.repo.EmailCorrespondenceRepo;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class.getName());
  private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^(.+)@(\\S+)$");
  private static final Integer ANALYST = 50;

  @Autowired
  private EmailCorrespondenceRepo emailCorrespondenceRepo;

  @Value("${etrack.email.correspondence.from.address}")
  private String fromEmailAddress;

  @Autowired
  private ETrackPermitDAO eTrackPermitDao;
  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;



  /**
   * Send an email to the requested user(s) and persist the correspondence into DB.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId = Project id.
   * @param emailContent - Email content
   * @param attachments - attached files.
   */
  public void sendEmail(final String userId, final String contextId, final Long projectId,
      EmailContent emailContent, MultipartFile[] attachments) {

    logger.info("Requesting to send an email. User Id {}, Context Id {}", userId, contextId);

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      logger.info(
          "Check whether we have any existing email correspondence needs to be "
              + "replied or pending email getting initiated. User Id {} , Context Id {}",
          userId, contextId);
      
      if (StringUtils.hasLength(emailContent.getEmailBody()) 
          && emailContent.getEmailBody().length() > 3500) {
        throw new BadRequestException("TOO_MANY_CHARS", 
            "Email body content is more than 3500 characters. Please add details in a document and attach as a file", projectId);
      }
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

      mimeMessageHelper.setSubject(emailContent.getSubject());
      mimeMessageHelper.setFrom(new InternetAddress(fromEmailAddress));
      StringBuilder amendEmailContent = new StringBuilder();
      StringBuilder emailCorrespondenceEmailContent = new StringBuilder();

      mimeMessageHelper.setText(emailContent.getEmailBody());

      EmailCorrespondence emailCorrespondence = emailCorrespondenceRepo
          .findByCorrespondenceIdAndProjectId(emailContent.getEmailCorrespondenceId(), projectId);

      if (emailCorrespondence != null && "P".equals(emailCorrespondence.getEmailStatus())) {
        logger.info(
            "Updating the existing Pending correspondence for the id {} , User Id {}, Context Id {}",
            emailContent.getEmailCorrespondenceId(), userId, contextId);

        // emailCorrespondence.setRefCorrespId(emailContent.getEmailCorrespondenceId());
        // amendEmailContent.append(emailCorrespondence.getEmailContent().replaceAll("<br>", "\n"));
        // emailCorrespondenceEmailContent.append(emailCorrespondence.getEmailContent());

        if (StringUtils.hasLength(emailContent.getEmailBody())) {
          emailCorrespondenceEmailContent.append("<br>").append(emailContent.getEmailBody())
              .append("<br>");
          amendEmailContent.append("\n").append(emailContent.getEmailBody()).append("\n");
        }

        emailCorrespondence.setEmailContent(amendEmailContent.toString());
        emailCorrespondence.setEmailSubject(emailContent.getSubject());
      } else {
        logger.info("Email correspondence could be reply correspondence "
            + "or new email request. User Id {}, Context Id {} ", userId, contextId);
        emailCorrespondence = new EmailCorrespondence();
        emailCorrespondence.setFromEmailAdr(emailContent.getFromEmailId());
        emailCorrespondence.setProjectId(projectId);
        emailCorrespondence.setCreateDate(new Date());
        emailCorrespondence.setCreatedById(userId);
        emailCorrespondence.setEmailRqstdUserId(userId);
        emailCorrespondence.setTopicId(emailContent.getTopicId());
        emailCorrespondence.setRefCorrespondenceId(emailContent.getTopicId());
        emailCorrespondence.setEmailSubject(emailContent.getSubject());
        emailCorrespondenceEmailContent.append(emailContent.getEmailBody()).append("<br>");
        amendEmailContent.append(emailContent.getEmailBody()).append("\n");
      }

      if (attachments != null && attachments.length > 0) {
        amendEmailContent.append("Attached File Name :").append("\n");
        emailCorrespondenceEmailContent.append("Attached File Name :").append("<br>");
        for (MultipartFile file : attachments) {
          mimeMessageHelper.addAttachment(file.getOriginalFilename(), file);
          emailCorrespondenceEmailContent.append(file.getOriginalFilename()).append("<br>");
          amendEmailContent.append(file.getOriginalFilename()).append("\n");
        }
      }
      mimeMessageHelper.setText(amendEmailContent.toString());
      StringBuilder subjectShortDesc = new StringBuilder();
      subjectShortDesc.append(emailContent.getSubject());
      if (attachments != null && attachments.length != 0) {
        subjectShortDesc.append("with attached files ");
        for (MultipartFile attachment : attachments) {
          subjectShortDesc.append(attachment.getOriginalFilename()).append("<br>");
        }
      }
      emailCorrespondence.setEmailContent(emailCorrespondenceEmailContent.toString());
      emailCorrespondence.setSubShortDesc(subjectShortDesc.toString());
      emailCorrespondence.setToEmailAdr(String.join(";", emailContent.getToEmailId()));
      if (!CollectionUtils.isEmpty(emailContent.getCcEmailId())) {
        emailCorrespondence.setCcEmailAdr(String.join(";", emailContent.getCcEmailId()));
      }

      if (!StringUtils.hasLength(emailContent.getFromEmailId())) {
        throw new BadRequestException("FROM_EMAIL_ADDR_BLANK", "From Email address is blank",
            emailContent);
      }

      List<RegionUserEntity> sendUserEntities = eTrackPermitDao
          .findTheUserDetailsByEmailAddress(userId, contextId, emailContent.getFromEmailId());

      emailCorrespondence.setEmailStatus("S");
      emailCorrespondence.setEmailSendUserName(sendUserEntities.get(0).getDisplayName());
      List<EmailCorrespondence> emailCorrespondences = new ArrayList<>();
      List<String> failureEmailAddresses = new ArrayList<>();

      if (CollectionUtils.isEmpty(emailContent.getToEmailId())) {
        throw new BadRequestException("TO_ADR_EMPTY", "To address cannot be empty", emailContent);
      }

      Set<String> toEmailAddress = new HashSet<>();
      for (String toEmail : emailContent.getToEmailId()) {
        logger.info("Create To email correspondences. User id {}, Context Id {}, toEmail {}",
            userId, contextId, toEmail);
        try {
          Matcher matcher = EMAIL_REGEX_PATTERN.matcher(toEmail);
          if (!matcher.matches()) {
            logger.error("Invalid TO email address is passed in the input. User Id {} , Context {}",
                userId, contextId);
            throw new DataNotFoundException();
          }
          List<RegionUserEntity> userEntities =
              eTrackPermitDao.findTheUserDetailsByEmailAddress(userId, contextId, toEmail);

          if (CollectionUtils.isEmpty(userEntities)) {
            failureEmailAddresses.add(toEmail);
          } else {
            boolean isAnalyst = false;
            for (RegionUserEntity userEntity : userEntities) {
              if (userEntity.getRoleTypeId() != null) {
                if (userEntity.getRoleTypeId().equals(ANALYST)) {
                  isAnalyst = true;
                }
              }
            }
            if ((attachments != null && attachments.length > 0) || !isAnalyst) {
              toEmailAddress.add(toEmail);
            }
            EmailCorrespondence updatedEmailCorrespondence =
                prepareNewEmailCorresponse(emailCorrespondence);
            updatedEmailCorrespondence.setEmailRcvdUserId(userEntities.get(0).getUserId());
            updatedEmailCorrespondence.setEmailRcvdUserName(userEntities.get(0).getDisplayName());
            emailCorrespondences.add(updatedEmailCorrespondence);
          }
        } catch (DataNotFoundException dfe) {
          failureEmailAddresses.add(toEmail);
        }
      }
      if (!CollectionUtils.isEmpty(toEmailAddress)) {
        List<String> toEmailAddressList = new ArrayList<>(toEmailAddress);
        InternetAddress[] toAddresses = new InternetAddress[toEmailAddressList.size()];
        for (int index = 0; index < toEmailAddress.size(); index++) {
          toAddresses[index] = new InternetAddress(toEmailAddressList.get(index));
        }
        mimeMessageHelper.setTo(toAddresses);
      }

      List<String> ccEmailAddress = new ArrayList<>();
      if (!CollectionUtils.isEmpty(emailContent.getCcEmailId())) {
        for (String ccEmail : emailContent.getCcEmailId()) {
          logger.info("Create CC email correspondences. User id {}, Context Id {}, toEmail {}",
              userId, contextId, ccEmail);
          try {
            Matcher matcher = EMAIL_REGEX_PATTERN.matcher(ccEmail);
            if (!matcher.matches()) {
              logger.error(
                  "Invalid CC email address is passed in the input. User Id {} , Context {}",
                  userId, contextId);
              throw new DataNotFoundException();
            }
            List<RegionUserEntity> userEntities =
                eTrackPermitDao.findTheUserDetailsByEmailAddress(userId, contextId, ccEmail);

            if (CollectionUtils.isEmpty(userEntities)) {
              failureEmailAddresses.add(ccEmail);
            } else {
              for (RegionUserEntity userEntity : userEntities) {
                if (userEntity.getRoleTypeId() != null) {
                  ccEmailAddress.add(ccEmail);
                  break;
                }
              }
              EmailCorrespondence updatedEmailCorrespondence =
                  prepareNewEmailCorresponse(emailCorrespondence);
              updatedEmailCorrespondence.setEmailRcvdUserId(userEntities.get(0).getUserId());
              updatedEmailCorrespondence.setEmailRcvdUserName(userEntities.get(0).getDisplayName());
              emailCorrespondences.add(updatedEmailCorrespondence);
            }
          } catch (DataNotFoundException dfe) {
            failureEmailAddresses.add(ccEmail);
          }
        }
        if (!CollectionUtils.isEmpty(ccEmailAddress)) {
          InternetAddress[] ccAddresses = new InternetAddress[ccEmailAddress.size()];;
          for (int index = 0; index < ccEmailAddress.size(); index++) {
            ccAddresses[index] = new InternetAddress(ccEmailAddress.get(index));
          }
          mimeMessageHelper.setCc(ccAddresses);
        }
      }

      if (!CollectionUtils.isEmpty(failureEmailAddresses)) {
        throw new BadRequestException("INCORRECT_EMAIL_ADDRESS",
            "One or more incorrect Email Address is passed in the TO or CC list",
            failureEmailAddresses);
      }

      if (emailCorrespondences.get(0).getTopicId() != null) {
        logger.info("Updating the pending email with status and add new email correspondence "
            + "for the newly added user. User Id {}, Context Id {}", userId, contextId);
        emailCorrespondenceRepo.save(emailCorrespondences.get(0));
        for (int correspondenceIndex = 1; correspondenceIndex < emailCorrespondences
            .size(); correspondenceIndex++) {
          EmailCorrespondence persistNewEmailCorrespondence =
              emailCorrespondences.get(correspondenceIndex);
          persistNewEmailCorrespondence.setCorrespondenceId(null);
          persistNewEmailCorrespondence.setRefCorrespondenceId(null);
          emailCorrespondenceRepo.save(persistNewEmailCorrespondence);
        }
      } else {
        logger.info("Adding new email Correspondence "
            + "for the requested user(s). User Id {}, Context Id {}", userId, contextId);
        EmailCorrespondence newEmailCorrespondence =
            emailCorrespondenceRepo.save(emailCorrespondences.get(0));
        newEmailCorrespondence.setTopicId(newEmailCorrespondence.getCorrespondenceId());
        newEmailCorrespondence.setRefCorrespondenceId(newEmailCorrespondence.getCorrespondenceId());
        emailCorrespondenceRepo.save(newEmailCorrespondence);
        for (int correspondenceIndex = 1; correspondenceIndex < emailCorrespondences
            .size(); correspondenceIndex++) {
          EmailCorrespondence persistNewEmailCorrespondence =
              emailCorrespondences.get(correspondenceIndex);
          persistNewEmailCorrespondence.setCorrespondenceId(null);
          persistNewEmailCorrespondence.setRefCorrespondenceId(null);
          persistNewEmailCorrespondence.setTopicId(newEmailCorrespondence.getTopicId());
          emailCorrespondenceRepo.save(persistNewEmailCorrespondence);
        }
      }
      if (CollectionUtils.isEmpty(toEmailAddress) && CollectionUtils.isEmpty(ccEmailAddress)) {
        logger.warn("There is no non analyst email addresses passed in the To or cc address. "
            + "Email correspondence will be generated by no email will be triggered to outlook/inbox");
      } else {
        logger.info(
            "Initiate the email to all the non-analyst recipients. User Id {}, Context Id {}",
            userId, contextId);
        mailSender.send(mimeMessage);
      }
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      logger.error("Error while sending email and persisting the details "
          + "into database. User Id {}, Context Id {}", userId, contextId, e);
      throw new ETrackPermitException("EMAIL_CORRESPONDENCE_ERROR",
          "Error while processing and sending email to the user", e);
    }
    logger.info("Exiting from sending an email . User Id {}, Context Id {}", userId, contextId);
  }

  // private boolean isUserEligibleToReceiveEmail(final String userId, final String contextId, final
  // String token) {
  // logger.info("Requesting ASMS service to retrive the Roles and check whether the user is
  // eligible to receive email or not"
  // + "User Id: {}, Context Id: {}", userId, contextId);
  //
  // String uri = UriComponentsBuilder.newInstance()
  // .pathSegment("/etrack-asms/user/authInfo").build().toString();
  // HttpHeaders headers = new HttpHeaders();
  // headers.add("userId", userId);
  // headers.add(HttpHeaders.AUTHORIZATION, token);
  // HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
  // try {
  // List<String> permits = eTrackOtherServiceRestTemplate
  // .exchange(uri, HttpMethod.GET, requestEntity, typeRef).getBody();
  // throw new DataExistException(e.getErrorCode(),
  // new ObjectMapper().writeValueAsString(permits));
  // } catch (DataExistException de) {
  // throw de;
  // } catch (HttpServerErrorException hse) {
  // throw new ETrackPermitException(hse.getStatusCode(), "RECEIVED_ACTIVE_PERMIT_ERR",
  // "Received error from Dart DB service " + "while requesting Active Authorization "
  // + hse.getResponseBodyAsString());
  // } catch (Exception ex) {
  // throw new ETrackPermitException("RECEIVED_ACTIVE_PERMIT_GENERAL_ERR",
  // "Received error from Dart DB service " + "while requesting Active Authorization", ex);
  // }
  // return true;
  // }

  private EmailCorrespondence prepareNewEmailCorresponse(
      final EmailCorrespondence emailCorrespondence) {
    return new EmailCorrespondence(emailCorrespondence.getCorrespondenceId(),
        emailCorrespondence.getProjectId(), emailCorrespondence.getSubShortDesc(),
        emailCorrespondence.getEmailSubject(), emailCorrespondence.getFromEmailAdr(),
        emailCorrespondence.getToEmailAdr(), emailCorrespondence.getCcEmailAdr(),
        emailCorrespondence.getEmailContent(), emailCorrespondence.getEmailStatus(),
        emailCorrespondence.getCreatedById(), emailCorrespondence.getCreateDate(),
        emailCorrespondence.getModifiedById(), emailCorrespondence.getModifiedDate(),
        emailCorrespondence.getTopicId(), emailCorrespondence.getEmailRqstdUserId(),
        emailCorrespondence.getEmailRcvdUserId(), emailCorrespondence.getEmailRcvdUserName(),
        emailCorrespondence.getEmailSendUserName(), emailCorrespondence.getRefCorrespondenceId());
  }

  /**
   * Mark the email message as read once the user read the content. This helps the system to avoid
   * showing old message also as new.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param correspondenceId - Email Correspondence Id.
   */
  @Transactional
  public void updateEmailReadStatus(final String userId, final String contextId,
      final Long projectId, final Long correspondenceId) {
    emailCorrespondenceRepo.updateEmailReadStatus(userId, projectId, correspondenceId);
  }


  /**
   * Delete the email thread for the input Correspondence id if any.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param correspondenceId - Correspondence Id.
   */
  @Transactional
  public void deleteEmailCorrespondence(final String userId, final String contextId,
      final Long projectId, final Long correspondenceId) {
    emailCorrespondenceRepo.deleteEmailCorrespondenceId(userId, projectId, correspondenceId);
  }
}
