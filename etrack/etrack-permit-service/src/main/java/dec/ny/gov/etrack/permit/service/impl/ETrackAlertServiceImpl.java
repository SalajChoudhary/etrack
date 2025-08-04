package dec.ny.gov.etrack.permit.service.impl;

import java.util.Date;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.entity.GIInquiryAlert;
import dec.ny.gov.etrack.permit.entity.ProjectAlert;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.repo.GIInquiryAlertRepo;
import dec.ny.gov.etrack.permit.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.permit.service.ETrackAlertService;

@Service
public class ETrackAlertServiceImpl implements ETrackAlertService {

  @Autowired
  private ProjectAlertRepo projectAlertRepo;
  @Autowired
  private GIInquiryAlertRepo inquiryAlertRepo;
  
  private static final Logger logger = LoggerFactory.getLogger(ETrackAlertServiceImpl.class.getName());
  
  
  @Transactional
  @Override
  public void deleteAlertMessage(String userId, String contextId, Long projectId, Long inquiryId, Long alertId) {
    if (projectId != null) {
      ProjectAlert projectAlert = 
          projectAlertRepo.findByProjectAlertIdAndProjectId(alertId, projectId);
      logger.info("Deleting the project alert message. User Id {}, Context Id {}", userId, contextId);
      if (projectAlert == null) {
        throw new BadRequestException("NO_ALERTS_AVAIL",
            "There is no alert associated with this Project and Alert Id", projectAlert);
      }
      projectAlertRepo.delete(projectAlert);      
    } else if (inquiryId != null){
      GIInquiryAlert inquiryAlert = 
          inquiryAlertRepo.findByInquiryAlertIdAndInquiryId(alertId, inquiryId);
      logger.info("Deleting the project alert message. User Id {}, Context Id {}", userId, contextId);
      if (inquiryAlert == null) {
        throw new BadRequestException("NO_INQUIRY_ALERTS_AVAIL",
            "There is no Geographical Inquiry alert associated with this alert id ", inquiryAlert);
      }
      inquiryAlertRepo.delete(inquiryAlert);      
    }
  }

  @Transactional
  @Override
  public void updateAlertMessageAsRead(String userId, String contextId, Long projectId,
      Long alertId) {
    ProjectAlert projectAlert =
        projectAlertRepo.findByProjectAlertIdAndProjectId(alertId, projectId);
    if (projectAlert != null) {
      projectAlert.setModifiedById(userId);
      projectAlert.setModifiedDate(new Date());
      projectAlert.setMsgReadInd(1);
    } else {
      throw new BadRequestException("NO_ALERT_DETAIL_AVAIL",
          "There is no alert for this input Alert and Project Id", alertId + " " + projectId);
    }
  }

}
