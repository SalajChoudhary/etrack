package dec.ny.gov.etrack.dart.db.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.EmailCorrespondence;
import dec.ny.gov.etrack.dart.db.entity.Facility;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.DashboardEmailCorrespondence;
import dec.ny.gov.etrack.dart.db.model.DashboardEmailEnvelop;
import dec.ny.gov.etrack.dart.db.model.EmailContent;
import dec.ny.gov.etrack.dart.db.model.RegionUserEntity;
import dec.ny.gov.etrack.dart.db.model.VirtualDesktopEmailShortDesc;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.EmailCorrespondenceRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.service.EmailService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Service
public class EmailServiceImpl implements EmailService {

  @Autowired
  private EmailCorrespondenceRepo emailCorrespondenceRepo;
  
  @Autowired
  private FacilityRepo facilityRepo;
  @Autowired
  private ApplicantRepo applicantRepo;
  @Autowired
  private DartDbDAO dartDbDAO;
  
  
  private final SimpleDateFormat mmDDYYYFormatAMPM = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class.getName());
  
  @Override
  public Object retrieveEmailCorrespondence(final String userId, final String contextId, final Long projectId) {
    List<EmailCorrespondence> emailCorrespondences = null;

    if (projectId != null && projectId > 0) {
      emailCorrespondences =
          emailCorrespondenceRepo.findByCorrepondenceByUserIdAndProjectId(userId, projectId);
      if (!CollectionUtils.isEmpty(emailCorrespondences)) {
        List<EmailCorrespondence> emailCorrespondences2 = new ArrayList<>();
        emailCorrespondences.forEach(emailCorrespondence -> {
          Date emailRequestedDate = emailCorrespondence.getCreateDate();
          if (emailRequestedDate != null) {
            String requestedDate = mmDDYYYFormatAMPM.format(emailRequestedDate);
            StringBuilder emailSubject = new StringBuilder();
            emailSubject.append(emailCorrespondence.getEmailSubject()).append(" - ")
                .append(requestedDate);
            emailCorrespondence.setEmailSubject(emailSubject.toString());
            emailCorrespondences2.add(emailCorrespondence);
          }
        });
        return emailCorrespondences2;
      }
    } else {
      emailCorrespondences = emailCorrespondenceRepo.findByCorrepondenceByUserId(userId);
      List<ApplicantDto> lrpApplicants = getLegalResponsePartyDetailsByProjectId(userId, contextId, projectId);
      
      if (!CollectionUtils.isEmpty(emailCorrespondences)) {
        List<DashboardEmailCorrespondence> dashboardEmailCorrespondences = new ArrayList<>();
        emailCorrespondences.forEach(emailCorrespondence -> {
          DashboardEmailCorrespondence dashboardEmailCorrespondence =
              new DashboardEmailCorrespondence();
          dashboardEmailCorrespondence
              .setCorrespondenceId(emailCorrespondence.getCorrespondenceId());
          dashboardEmailCorrespondence.setProjectId(emailCorrespondence.getProjectId());
          dashboardEmailCorrespondence.setSubShortDesc(emailCorrespondence.getSubShortDesc());
          dashboardEmailCorrespondence.setEmailSubject(emailCorrespondence.getEmailSubject());
          dashboardEmailCorrespondence.setFromEmailAdr(emailCorrespondence.getFromEmailAdr());
          dashboardEmailCorrespondence.setToEmailAdr(emailCorrespondence.getToEmailAdr());
          dashboardEmailCorrespondence.setCcEmailAdr(emailCorrespondence.getCcEmailAdr());
          dashboardEmailCorrespondence.setEmailContent(emailCorrespondence.getEmailContent());
          dashboardEmailCorrespondence.setRefCorrespId(emailCorrespondence.getTopicId());
          dashboardEmailCorrespondence.setEmailRead(emailCorrespondence.getEmailRead());

          if (!CollectionUtils.isEmpty(lrpApplicants)) {
            dashboardEmailCorrespondence.setApplicantName(lrpApplicants.get(0).getDisplayName());
          }
          
          dec.ny.gov.etrack.dart.db.entity.Facility facility =
              facilityRepo.findByProjectId(emailCorrespondence.getProjectId());
          if (facility != null) {
            dashboardEmailCorrespondence.setFacilityName(facility.getFacilityName());
            dashboardEmailCorrespondence.setDecIdFormatted(formatDECId(facility.getDecId()));
          }
          Date emailRequestedDate = emailCorrespondence.getCreateDate();
          if (emailRequestedDate != null) {
            String requestedDate = mmDDYYYFormatAMPM.format(emailRequestedDate);
            StringBuilder emailSubject = new StringBuilder();
            emailSubject.append(emailCorrespondence.getEmailSubject()).append(" - ")
                .append(requestedDate);
            dashboardEmailCorrespondence.setEmailSubject(emailSubject.toString());
            dashboardEmailCorrespondences.add(dashboardEmailCorrespondence);
          }
        });
        return dashboardEmailCorrespondences;
      }
    }
    return emailCorrespondences;
  }

  private List<ApplicantDto> getLegalResponsePartyDetailsByProjectId(final String userId, 
      final String contextId, final Long projectId) {
    logger.info("Entering into getLegalResponsePartyDetailsByProjectId. User Id {}, Context Id {}", userId, contextId);
    List<ApplicantDto> lrpsList = applicantRepo.findLRPDetailsByProjectId(projectId);
    if (!CollectionUtils.isEmpty(lrpsList)) {
      for (ApplicantDto applicant : lrpsList) {
        ApplicantDto applicantDetail = new ApplicantDto();
        applicantDetail.setDisplayName(applicant.getDisplayName());
        applicantDetail.setPublicId(applicant.getPublicId());
        lrpsList.add(applicantDetail);
      }
    }
    logger.info("Exiting from getLegalResponsePartyDetailsByProjectId. User Id {}, Context Id {}", userId, contextId);
    return lrpsList;
  }
  
  
  @Override
  public EmailContent retrieveEmailCorrespondenceByCorrespondenceId(String userId, String contextId,
      Long projectId, Long correspondenceId, final boolean envelopRequestedInd) {
    
    logger.info("Entering into Email correspondences by the Correspondence id. User Id {}, Context Id {}", userId, contextId);
    
    EmailContent emailContent = null;

    
    List<EmailCorrespondence> emailCorrespondences =
        emailCorrespondenceRepo.findByCorrespondenceIdAndProjectId(correspondenceId, projectId);
    
    if (!CollectionUtils.isEmpty(emailCorrespondences)) {
      emailContent = new EmailContent();
      List<RegionUserEntity> regionUserEntity = dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId);
      if (CollectionUtils.isEmpty(regionUserEntity)) {
        logger.error("There is no valid user for the input User id {} , Context Id ", userId, contextId);
        throw new BadRequestException("INVALID_USER", "Invalid User id is passed as an from Address", userId);
      }
      emailContent.setReplyFromAdr(regionUserEntity.get(0).getEmailAddress());
      List<String> emailContents = new ArrayList<>();
      int emailCorrespondenceIndex = 0;
      if (envelopRequestedInd) {
        EmailCorrespondence emailCorrespondence = emailCorrespondences.get(0);
        emailContents.add(emailCorrespondence.getEmailContent());
        emailContent.setTopicId(emailCorrespondence.getTopicId());
        emailContent.setProjectId(projectId);
        emailContent.setSubShortDesc(emailCorrespondence.getSubShortDesc());
        emailContent.setEmailSubject(emailCorrespondence.getEmailSubject());
        emailContent.setFromEmailAdr(emailCorrespondence.getFromEmailAdr());
        emailContent.setToEmailAdr(emailCorrespondence.getToEmailAdr());
        emailContent.setCcEmailAdr(emailCorrespondence.getCcEmailAdr());
        emailContent.setEmailRead(emailCorrespondence.getEmailRead());
        emailContent.setDeletedInd(emailCorrespondence.getDeletedInd());
        emailCorrespondenceIndex = 1;
      }

      for (int index = emailCorrespondenceIndex; index < emailCorrespondences.size(); index++) {
        StringBuilder sb = new StringBuilder();
        if (index == 0) {
          Facility facility = facilityRepo.findByProjectId(projectId);
          sb.append("<b> Project Review Message : ").append(facility.getFacilityName()).append(" PID ").append(projectId).append("</b><br><br>");
        }
        EmailCorrespondence existingEmailCorrespondence = emailCorrespondences.get(index);
        sb.append("From: ").append(existingEmailCorrespondence.getFromEmailAdr()).append("<br>");
        sb.append("To: ").append(existingEmailCorrespondence.getToEmailAdr()).append("<br>");
        if (StringUtils.hasLength(existingEmailCorrespondence.getCcEmailAdr())) {
          sb.append("Cc: ").append(existingEmailCorrespondence.getCcEmailAdr()).append("<br>");
        }
        if (existingEmailCorrespondence.getCorrespondenceId()
            .equals(existingEmailCorrespondence.getTopicId())
            && existingEmailCorrespondence.getModifiedDate() != null) {
          sb.append("Sent at: ")
              .append(mmDDYYYFormatAMPM.format(existingEmailCorrespondence.getModifiedDate()))
              .append("<br>");
        } else {
          sb.append("Sent at: ")
              .append(mmDDYYYFormatAMPM.format(existingEmailCorrespondence.getCreateDate()))
              .append("<br>");
        }
        if (StringUtils.hasLength(existingEmailCorrespondence.getEmailContent())) {
          sb.append("<br>")
              .append(existingEmailCorrespondence.getEmailContent().replaceAll("\n", "<br>"))
              .append("<br>");
        }
        emailContents.add(sb.toString());
      }
      emailContent.setEmailContent(emailContents);
    }
    logger.info("Exiting from Email correspondences by the Correspondence id. User Id {}, Context Id {}", userId, contextId);
    return emailContent;
  }

  
  @Override
  public List<DashboardEmailEnvelop> retrieveEmailNotificationDetails(final String userId, final String contextId) {
    
    logger.info("Entering into retrieve the Email notifications to display in the Main dashboard. "
        + "User Id {}, Context Id {}", userId, contextId);
    List<DashboardEmailEnvelop> dashboardEmailEnvelops = new ArrayList<>();
    List<EmailCorrespondence> projectIdAndCountList =
        emailCorrespondenceRepo.retrieveEmailUnreadMessageCountByUserId(userId);
    
    if (!CollectionUtils.isEmpty(projectIdAndCountList)) {
      Map<Long, Integer> projectIdAndCountMap = new LinkedHashMap<>();
      Map<Long, String> projectIdAndFacilityMap = new HashMap<>();
      
      projectIdAndCountList.forEach(email -> {
        if (projectIdAndCountMap.get(email.getProjectId()) == null) {
          projectIdAndCountMap.put(email.getProjectId(), 1);
        } else {
          projectIdAndCountMap.put(email.getProjectId(), projectIdAndCountMap.get(email.getProjectId()) + 1);
        }
      });
      
      List<String> projectIdsAndFacilityNameList =
          emailCorrespondenceRepo.retrieveFacilityNameByProjectIds(projectIdAndCountMap.keySet());
      
      projectIdsAndFacilityNameList.forEach(projectIdsAndFacilityName -> {
        String[] projectIdAndFacility = projectIdsAndFacilityName.split(",");
        projectIdAndFacilityMap.put(Long.valueOf(projectIdAndFacility[0]), projectIdAndFacility[1]);
      });
      
      projectIdAndCountMap.keySet().forEach(projectId -> {
        DashboardEmailEnvelop dashboardEmailEnvelop = new DashboardEmailEnvelop();
        dashboardEmailEnvelop.setProjectId(projectId);
        dashboardEmailEnvelop.setFacilityName(projectIdAndFacilityMap.get(projectId));
        dashboardEmailEnvelop.setUnreadCount(projectIdAndCountMap.get(projectId));
        dashboardEmailEnvelops.add(dashboardEmailEnvelop);
      });
    }
    return dashboardEmailEnvelops;
  }

  
  @Override
  public Map<String, List<VirtualDesktopEmailShortDesc>> retrieveEmailNotificationsInVirtualDesktop(String userId, String contextId,
      Long projectId) {
    
    logger.info("Entering into retrieveEmailNotificationsInVirtualDesktop to display "
        + "User Id {}, Context Id {}", userId, contextId);
    
    List<EmailCorrespondence> emailCorrespondencesList 
      = emailCorrespondenceRepo.findCorrespondenceByUserIdAndProjectId(userId, projectId);
    
    Map<String, List<EmailCorrespondence>> receivedEmailsMap = new HashMap<>();
    Map<String, List<EmailCorrespondence>> sentEmailsMap = new HashMap<>();
    Map<String, String> emailUserMapResponse = new HashMap<>();
    
    Map<String, List<VirtualDesktopEmailShortDesc>> emailCorrespondenceForTheUser = new HashMap<>();
    
    if (!CollectionUtils.isEmpty(emailCorrespondencesList)) {
      emailCorrespondencesList.forEach(emailCorrespondence -> {
        
        if (StringUtils.hasLength(emailCorrespondence.getEmailRcvdUserId()) 
            && emailCorrespondence.getEmailRcvdUserId().equals(userId)) {
          
          String emailSenderUserId = emailCorrespondence.getEmailRqstdUserId();
          if (receivedEmailsMap.get(emailSenderUserId) != null) {
            receivedEmailsMap.get(emailSenderUserId).add(emailCorrespondence);
          } else {
            List<EmailCorrespondence> receivedEmailCorrespondences = new ArrayList<>();
            receivedEmailCorrespondences.add(emailCorrespondence);
            receivedEmailsMap.put(emailSenderUserId, receivedEmailCorrespondences);
            emailUserMapResponse.put(emailCorrespondence.getEmailRqstdUserId(), 
                emailCorrespondence.getEmailSendUserName());
         }
        } else if (StringUtils.hasLength(emailCorrespondence.getEmailRqstdUserId()) 
            && emailCorrespondence.getEmailRqstdUserId().equals(userId)) {
          
          String emailReceivedUserId = emailCorrespondence.getEmailRcvdUserId();
          if (sentEmailsMap.get(emailReceivedUserId) != null) {
            sentEmailsMap.get(emailReceivedUserId).add(emailCorrespondence);
          } else {
            List<EmailCorrespondence> sentEmailCorrespondences = new ArrayList<>();
            sentEmailCorrespondences.add(emailCorrespondence);
            sentEmailsMap.put(emailReceivedUserId, sentEmailCorrespondences);
            emailUserMapResponse.put(emailReceivedUserId, 
                emailCorrespondence.getEmailRcvdUserName());
          }
        }
      });
    }
    
    List<VirtualDesktopEmailShortDesc> receivedEmailShortDescList = new ArrayList<>();
    receivedEmailsMap.keySet().forEach(correspondenceUserId -> {
      VirtualDesktopEmailShortDesc emailShortDesc = new VirtualDesktopEmailShortDesc();
      emailShortDesc.setEmailUserId(correspondenceUserId);
      emailShortDesc.setEmailUserName(emailUserMapResponse.get(correspondenceUserId));
      Integer unreadCount = 0;
      List<EmailCorrespondence> sortedEmailCorrespondence = receivedEmailsMap.get(correspondenceUserId).stream().sorted(Comparator.comparing(
          EmailCorrespondence::getCreateDate).reversed()).collect(Collectors.toList());
      emailShortDesc.setMostRecentCorrespondenceDate(sortedEmailCorrespondence.get(0).getCreateDate());
      for (EmailCorrespondence correspondence : receivedEmailsMap.get(correspondenceUserId)) {
        if (StringUtils.isEmpty(correspondence.getEmailRead())  
            || correspondence.getEmailRead().equals("0")) {
          unreadCount += 1;
        }
      }
      emailShortDesc.setUnreadCount(unreadCount);
      receivedEmailShortDescList.add(emailShortDesc);
    });
    emailCorrespondenceForTheUser.put(DartDBConstants.EMAIL_RECIEVED, 
        receivedEmailShortDescList.stream().sorted(Comparator.comparing(
        VirtualDesktopEmailShortDesc::getMostRecentCorrespondenceDate).reversed()).collect(Collectors.toList()));
    
    List<VirtualDesktopEmailShortDesc> sendEmailShortDescList = new ArrayList<>();
    sentEmailsMap.keySet().forEach(correspondenceUserId -> {
      VirtualDesktopEmailShortDesc emailShortDesc = new VirtualDesktopEmailShortDesc();
      emailShortDesc.setEmailUserId(correspondenceUserId);
      emailShortDesc.setEmailUserName(emailUserMapResponse.get(correspondenceUserId));
      List<EmailCorrespondence> sortedEmailCorrespondence = sentEmailsMap.get(
          correspondenceUserId).stream().sorted(Comparator.comparing(
          EmailCorrespondence::getCreateDate).reversed()).collect(Collectors.toList());
      
      emailShortDesc.setMostRecentCorrespondenceDate(sortedEmailCorrespondence.get(0).getCreateDate());
      Integer unreadCount = 0;
      for (EmailCorrespondence correspondence : sentEmailsMap.get(correspondenceUserId)) {
        if (StringUtils.isEmpty(correspondence.getEmailRead()) 
            || correspondence.getEmailRead().equals("0")) {
          unreadCount += 1;
        }
      }
      emailShortDesc.setUnreadCount(unreadCount);
      sendEmailShortDescList.add(emailShortDesc);
    });
    
    emailCorrespondenceForTheUser.put(DartDBConstants.EMAIL_SENT, sendEmailShortDescList.stream().sorted(Comparator.comparing(
        VirtualDesktopEmailShortDesc::getMostRecentCorrespondenceDate).reversed()).collect(Collectors.toList()));
    logger.info("Exiting from retrieveEmailNotificationsInVirtualDesktop to display "
        + "User Id {}, Context Id {}", userId, contextId);
    return emailCorrespondenceForTheUser;
  }

  @Override
  public List<EmailCorrespondence> retrieveCorrespondencesForTheRequestor(final String userId, final String contextId, final Long projectId,
      final String emailSendorId, final String emailReceiverId, final String correspondenceType) {

    logger.info("Entering into retrieve the email correspondences for the Correspondence "
        + "request type User Id {}, Context Id {}", userId, contextId);
    List<EmailCorrespondence> emailCorrespondenceList 
          = emailCorrespondenceRepo.retrieveEmailCorrespondencesBetweenUser(
              emailReceiverId, emailSendorId, projectId);
    
    Map<Long, EmailCorrespondence> emailCorrespondencesByUser = new LinkedHashMap<>();
    if (!CollectionUtils.isEmpty(emailCorrespondenceList)) {
      emailCorrespondenceList.forEach(emailCorrespondence -> {
        if (emailCorrespondencesByUser.get(emailCorrespondence.getTopicId()) == null) {
          emailCorrespondencesByUser.put(emailCorrespondence.getTopicId(), emailCorrespondence);
        }
      });
      List<EmailCorrespondence> emailCorrespondences = new ArrayList<>();
      emailCorrespondencesByUser.keySet().forEach(topicId -> {
        emailCorrespondences.add(emailCorrespondencesByUser.get(topicId));
      });
      logger.info("Exiting from retrieve the email correspondences for the Correspondence "
          + "request type User Id {}, Context Id {}", userId, contextId);
      return emailCorrespondences;
    }
    return null;
  }

  private String formatDECId(String decId) {
    if (StringUtils.hasText(decId)) {
      StringBuilder sb = new StringBuilder();
      sb.append(decId.substring(0, 1)).append("-").append(decId.substring(1, 5)).append("-")
          .append(decId.substring(5));
      return sb.toString();
    }
    return decId;
  }


  @Override
  public List<List<String>> retrieveEmailCorrespondenceByDocumentId(
      final String userId, final String contextId, final String reviewerId,
      final Long projectId, final Long documentId) {

    List<List<String>> emailCorrespondences = new ArrayList<>();
    List<Long> correspondenceIds = null;
    if (documentId != null && documentId > 0) {
      correspondenceIds =
          emailCorrespondenceRepo.findCorrespondenceIdByUserIdAndDocumentId(reviewerId, documentId);
    } else {
      correspondenceIds =
          emailCorrespondenceRepo.findCorrespondenceIdByReviewerId(reviewerId, projectId);
    }
    if (CollectionUtils.isEmpty(correspondenceIds)) {
      throw new NoDataFoundException("NO_DATA_FOUND", "There is no email correspondence");
    }
    correspondenceIds.forEach(correspondenceId -> {
      EmailContent emailContent = retrieveEmailCorrespondenceByCorrespondenceId(userId, contextId,
          projectId, correspondenceId, false);
      if (emailContent != null) {
        emailCorrespondences.add(emailContent.getEmailContent());
      }
    });
    return emailCorrespondences;
  }


  @Override
  public List<EmailCorrespondence> retrieveCorrespondenceByReviewerAndDocumentIds(String userId, String contextId, 
      final String reviewerId, Long projectId, List<Long> documentIds) {
    logger.info("Entering into retrieveCorrespondenceByReviewerAndDocumentIds for the user Id {}, "
        + "Context Id {}, Reviewer Id {}", userId, contextId, reviewerId);
    
    List<EmailCorrespondence> emailCorrespondenceList 
          = emailCorrespondenceRepo.retrieveEmailCorrespondencesByReviewerIdAndDocumentIds(reviewerId, projectId, documentIds);
    if (!CollectionUtils.isEmpty(emailCorrespondenceList)) {
      Map<Long, EmailCorrespondence> emailCorrespondencesByUser = new LinkedHashMap<>();
      emailCorrespondenceList.forEach(emailCorrespondence -> {
        if (emailCorrespondencesByUser.get(emailCorrespondence.getTopicId()) == null) {
          emailCorrespondencesByUser.put(emailCorrespondence.getTopicId(), emailCorrespondence);
        }
      });
      List<EmailCorrespondence> emailCorrespondences = new ArrayList<>();
      emailCorrespondencesByUser.keySet().forEach(topictId -> {
        emailCorrespondences.add(emailCorrespondencesByUser.get(topictId));
      });
      logger.info("Exiting from retrieveCorrespondenceByReviewerAndDocumentIds for the reviewere id {}, "
          + "Context Id {}", userId, contextId);
      return emailCorrespondences;
    }
    return null;
  }
}
