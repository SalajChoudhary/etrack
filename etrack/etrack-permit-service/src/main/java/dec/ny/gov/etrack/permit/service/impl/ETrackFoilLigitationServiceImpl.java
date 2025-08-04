package dec.ny.gov.etrack.permit.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.permit.entity.LitigationHold;
import dec.ny.gov.etrack.permit.entity.LitigationHoldHistory;
import dec.ny.gov.etrack.permit.entity.ProjectFoilStatusDetail;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.FoilRequest;
import dec.ny.gov.etrack.permit.model.LitigationRequest;
import dec.ny.gov.etrack.permit.repo.FoilRequestRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestHistoryRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestRepo;
import dec.ny.gov.etrack.permit.repo.ProjectRepo;
import dec.ny.gov.etrack.permit.service.ETrackFoilLigitationService;

@Service
public class ETrackFoilLigitationServiceImpl implements ETrackFoilLigitationService {

  @Autowired
  private FoilRequestRepo foilRequestRepo;
  @Autowired
  private LitigationHoldRequestRepo litigationHoldRequestRepo;
  @Autowired
  private LitigationHoldRequestHistoryRepo LitigationHoldRequestHistoryRepo;
  @Autowired
  private ProjectRepo projectRepo;

  private final SimpleDateFormat MM_DD_YYYY_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
  private static final Logger logger = LoggerFactory.getLogger(ETrackApplicantServiceImpl.class.getName());
  
  
  @Override
  @Transactional
  public List<String> saveOrUpdateFoilRequest(String userId, String contextId, Long projectId,
      FoilRequest foilRequest) {

    logger.info("Entering into saveOrUpdateFoilRequest. User Id {}, Context Id {}", userId,
        contextId);
    List<ProjectFoilStatusDetail> foilRequestsList = foilRequestRepo.findByProjectId(projectId);

    Integer foilRequestInd = null;
    if (foilRequest != null && !CollectionUtils.isEmpty(foilRequest.getFoilRequestNumber())) {
      foilRequestInd = 1;
    } else {
      foilRequestInd = 0;
    }
    projectRepo.updateProjectFoilRequestIndicator(userId, projectId, foilRequestInd);

    List<ProjectFoilStatusDetail> projectFoilStatusDetails = new ArrayList<>();
    Date createDate = null;
    String createdById = null;
    if (!CollectionUtils.isEmpty(foilRequestsList)) {
      logger.info(
          "Deleting the existing the details for the project id {}. User Id {}, Context Id {}",
          projectId, userId, contextId);
      foilRequestRepo.deleteAll(foilRequestsList);
      createDate = foilRequestsList.get(0).getCreateDate();
      createdById = foilRequestsList.get(0).getCreatedById();
    }
    for (String foilRequestNumber : foilRequest.getFoilRequestNumber()) {
      if (StringUtils.hasLength(foilRequestNumber)) {
        ProjectFoilStatusDetail projectFoilStatusDetail = new ProjectFoilStatusDetail();
        projectFoilStatusDetail.setFoilReqNum(foilRequestNumber);
        projectFoilStatusDetail.setProjectId(projectId);
        if (StringUtils.hasLength(createdById)) {
          projectFoilStatusDetail.setCreateDate(createDate);
          projectFoilStatusDetail.setCreatedById(createdById);
          projectFoilStatusDetail.setModifiedById(userId);
          projectFoilStatusDetail.setModifiedDate(new Date());
        } else {
          projectFoilStatusDetail.setCreateDate(new Date());
          projectFoilStatusDetail.setCreatedById(userId);
        }
        projectFoilStatusDetails.add(projectFoilStatusDetail);
      }
    }
    if (!CollectionUtils.isEmpty(projectFoilStatusDetails)) {
      logger.info("Store the new/updated foil status details, User Id {}, Context Id {}", userId,
          contextId);
      foilRequestRepo.saveAll(projectFoilStatusDetails);
    }
    logger.info("Exiting from saveOrUpdateFoilRequest. User Id {}, Context Id {}", userId,
        contextId);
    if (foilRequest != null && !CollectionUtils.isEmpty(foilRequest.getFoilRequestNumber())) {
      return foilRequest.getFoilRequestNumber();
    }
    return new ArrayList<>();
  }

  @Override
  @Transactional
  public Map<String, Object> saveOrUpdateLitigationRequest(String userId, String contextId,
      Long projectId, LitigationRequest litigationRequest) {
    MM_DD_YYYY_FORMAT.setLenient(false);

    logger.info("Entering into saveOrUpdateLitigationRequest. User Id {}, Context Id {}", userId,
        contextId);

    try {
      logger.info(
          "Checking whether the litigation is available "
              + "or not for the project id {} . User Id {}, Context Id {}",
          projectId, userId, contextId);
      
      LitigationHold litigationHold = litigationHoldRequestRepo.findByProjectId(projectId);
      
      litigationRequest.setHoldInd("Y");
      if (litigationHold == null) {
        if (!(litigationRequest != null
            && StringUtils.hasLength(litigationRequest.getLitigationStartDate()))) {
          throw new BadRequestException("LITIGATION_START_DATE_NOT_AVAIL",
              "Litigation Start date is not available for this new Litigation Hold request.",
              litigationRequest);
        }
        
        litigationHold = new LitigationHold();
        litigationHold.setCreateDate(new Date());
        litigationHold.setCreatedById(userId);
      } else {
        if (!StringUtils.hasLength(litigationRequest.getLitigationStartDate())) {
          litigationRequest.setHoldInd("N");
        }
        litigationHold.setModifiedDate(new Date());
        litigationHold.setModifiedById(userId);
      }

      if ("Y".equals(litigationRequest.getHoldInd())) {
        litigationHold.setLitigationHoldInd(1);
      } else if ("N".equals(litigationRequest.getHoldInd())) {
        litigationHold.setLitigationHoldInd(0);
      } else {
        throw new BadRequestException("LITIGATION_HOLD_IND_INVALID",
            "Litigation Hold indicator is not an valid.", litigationRequest);
      }
      litigationHold.setProjectId(projectId);
      if (StringUtils.hasLength(litigationRequest.getLitigationStartDate())) {
        litigationHold.setLitigationHoldStartDate(
            MM_DD_YYYY_FORMAT.parse(litigationRequest.getLitigationStartDate()));
      } else {
        litigationHold.setLitigationHoldStartDate(null);
      }

      if (StringUtils.hasLength(litigationRequest.getLitigationEndDate())) {
        litigationHold.setLitigationHoldEndDate(
            MM_DD_YYYY_FORMAT.parse(litigationRequest.getLitigationEndDate()));
      } else {
        litigationHold.setLitigationHoldEndDate(null);
      }
      logger.info("Save/Update the Litigation details. User Id {}, Context Id {}", userId,
          contextId);
      litigationHoldRequestRepo.save(litigationHold);

      Map<String, Object> litigationDetails = new HashMap<>();
      litigationDetails.put("litigationRequest", getLitigationRequest(projectId));
      litigationDetails.put("litigationRequestHistory", getLitigationRequestHistory(projectId));
      return litigationDetails;
    } catch (ParseException e) {
      throw new BadRequestException("LITIGATION_INCORRECT_DATE", "Incorrect format date is passed",
          litigationRequest);
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      throw new ETrackPermitException("FOIL_LITIGATION_PERSIST_ERROR",
          "Error while saving or updating the Litigation Foil Request " + e.getMessage(), e);
    }
  }

  private List<LitigationRequest> getLitigationRequestHistory(final Long projectId) {
    List<LitigationHoldHistory> litigationHoldHistoryList =
        LitigationHoldRequestHistoryRepo.findByProjectIdOrderByLitigationHoldHIdDesc(projectId);
    if (!CollectionUtils.isEmpty(litigationHoldHistoryList)) {
      List<LitigationRequest> litigationRequestHistoryList = new ArrayList<>();
      litigationHoldHistoryList.forEach(litigationHoldHistory -> {
        LitigationRequest litigationRequestHistory = new LitigationRequest();
        litigationRequestHistory.setLitigationHoldId(litigationHoldHistory.getLitigationHoldId());

        if (litigationHoldHistory.getLitigationHoldStartDate() != null) {
          litigationRequestHistory.setLitigationStartDate(
              MM_DD_YYYY_FORMAT.format(litigationHoldHistory.getLitigationHoldStartDate()));
        }
        if (litigationHoldHistory.getLitigationHoldEndDate() != null) {
          litigationRequestHistory.setLitigationEndDate(
              MM_DD_YYYY_FORMAT.format(litigationHoldHistory.getLitigationHoldEndDate()));
        }
        litigationRequestHistoryList.add(litigationRequestHistory);
      });
      return litigationRequestHistoryList;
    }
    return null;
  }

  private LitigationRequest getLitigationRequest(final Long projectId) {
    LitigationHold litigationHold = litigationHoldRequestRepo.findByProjectId(projectId);
    if (litigationHold != null) {
      LitigationRequest litigationRequest = new LitigationRequest();
      litigationRequest.setLitigationHoldId(litigationHold.getLitigationHoldId());
      if (litigationHold.getLitigationHoldStartDate() != null) {
        litigationRequest.setLitigationStartDate(
            MM_DD_YYYY_FORMAT.format(litigationHold.getLitigationHoldStartDate()));
      }
      if (litigationHold.getLitigationHoldEndDate() != null) {
        litigationRequest.setLitigationEndDate(
            MM_DD_YYYY_FORMAT.format(litigationHold.getLitigationHoldEndDate()));
      }
      if (litigationHold.getLitigationHoldStartDate() != null
          && litigationHold.getLitigationHoldStartDate().before(new Date())
          && (litigationHold.getLitigationHoldEndDate() == null
              || litigationHold.getLitigationHoldEndDate().after(new Date()))) {
        litigationRequest.setHoldInd("Y");
      } else {
        litigationRequest.setHoldInd("N");
      }
      return litigationRequest;
    }
    return null;
  }

}
