package gov.ny.dec.district.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.management.timer.Timer;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.district.dart.dao.DARTDistrictDAO;
import gov.ny.dec.district.dart.entity.ApplicationNarrativeDetail;
import gov.ny.dec.district.dart.entity.District;
import gov.ny.dec.district.etrack.entity.SubmitDocument;
import gov.ny.dec.district.etrack.entity.SupportDocument;
import gov.ny.dec.district.etrack.repository.ETrackSubmitDocumentRepository;
import gov.ny.dec.district.etrack.repository.SupportDocumentRepo;
import gov.ny.dec.district.exception.DARTDistrictServiceException;
import gov.ny.dec.district.exception.ValidationException;
import gov.ny.dec.district.service.DARTDistrictService;
import gov.ny.dec.district.util.DartDistrictServiceConstants;
import gov.ny.dec.district.util.DistrictResponseHandler;

@Service
@Transactional
public class DARTDistrictServiceImpl implements DARTDistrictService {

  @Autowired
  private ETrackSubmitDocumentRepository eTrackSubmitDocRepository;

  @Autowired
  private DistrictResponseHandler districtResponseHandler;

  @Autowired
  private DARTDistrictDAO dartDistrictDAO;
  
  @Autowired
  private SupportDocumentRepo supportDocumentRepo;

  private static Logger logger = LoggerFactory.getLogger(DARTDistrictServiceImpl.class.getName());

  private static final Integer SEARCH_RESULT_MAX_SIZE = 501;

  @Override
  public ResponseEntity<DistrictDetail> getDistrictDetails(final String userId, final String contextId, Long districtId) {
    
    DistrictDetail districtDetail = null;
    logger.info("Entering getDistrictDetails(). District id: {}, User Id {}, Context Id {}", districtId, userId, contextId);
    try {
      List<SubmitDocument> submittedDocuments = eTrackSubmitDocRepository
          .findByEdbDistrictIdAndDocumentStateCodeOrderByModifiedDateDesc(districtId, "A");

      logger.info("Retrieval of district details for the district Id {} "
          + "successful. User Id {}, Context Id {}", districtId, userId, contextId);
      districtDetail = districtResponseHandler.transformDistrictDetails(userId, contextId, submittedDocuments);
      logger.info(
          "Transformation of response received from database completed for the district Id {, User Id {}, Context Id {}}",
          districtId, userId, contextId);
      Set<Long> projectIds = supportDocumentRepo.findAllProjectsByDistrictId(districtId);
      if (!CollectionUtils.isEmpty(projectIds)) {
        List<SupportDocument> supportDocumentLists =  supportDocumentRepo.findAllByProjectIds(projectIds);
        if (!CollectionUtils.isEmpty(supportDocumentLists)) {
          Set<Long> litigationHoldProjectIds = supportDocumentRepo.findAllLitigationHoldEligibleProjectsByProjectIds(projectIds);
          districtDetail.setLitigationHoldProjects(litigationHoldProjectIds);
          districtResponseHandler.transformSupportDocumentDistrictDetails(
              supportDocumentLists, districtDetail, userId, contextId);
          logger.info("Merged the support document list user Id {}, Context Id {}", userId, contextId);
        }
      }
    } catch (DARTDistrictServiceException | ValidationException e) {
      throw e;
    } catch (Exception e) {
      populateLoggerExeptionMap("getDistrictDetails()",
          "Retrieving documents related to district id: ".concat(districtId.toString()),
          "District id: ".concat(districtId.toString()), e.getMessage());
      throw new DARTDistrictServiceException("Error while receiving the District details {}", e);
    }
    return new ResponseEntity<>(districtDetail, HttpStatus.OK);
  }

  /**
   * 
   */
  @Override
  @Transactional
  public ResponseEntity<List<District>> getDistrictDetailsByDecId(final String userId, String contextId, final String decId) {
    logger.info("Entering getDistrictDetailsByDecId(). Dec id: {}", decId);
    try {
      logger.info("Retrieving the district details for the DEC ID {} has started", decId);
      List<District> districts = dartDistrictDAO.searchDistrictDetailByDecId(userId, contextId, decId);
      logger.info("Retrieval of district details for the DEC ID {} is completed.", decId);
      if (!CollectionUtils.isEmpty(districts)) {
        logger.info("Retrieval successful. Exiting getDistrictDetailsByDecId().");
        return new ResponseEntity<>(districts, HttpStatus.OK);
      }
    } catch (DataAccessException e) {
      throw new DARTDistrictServiceException(
          "Error while retrieving the data for the decID " + decId + " " + e.getMessage(), e);
    } catch (Exception e) {
      populateLoggerExeptionMap("getDistrictDetailsByDecId()", "Retrieving district details",
          "Dec id: ".concat(decId), e.getMessage());
      throw new DARTDistrictServiceException(
          "General Error while retrieving the data for the decID " + decId + " " + e.getMessage(), e);
    }
    logger.info(
        "There were no district details related to dec id: {} . Exiting getDistrictDetailsByDecId().",
        decId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Transactional
  @Override
  public ResponseEntity<List<District>> getDistrictDetailsByFacilityName(final String userId, final String contextId, 
      final String facilityName, final String searchType) {
    
    logger.info("Entering into getDistrictDetailsByFacilityName(). User Id {}, Context Id {}, "
        + "Facility name: {}, Search Type {}", userId, contextId, facilityName, searchType);
    try {
      List<District> districts =
          dartDistrictDAO.searchDistrictDetailByFacilityName(userId, contextId, facilityName, searchType);
      
      logger.info(
          "Retrieving the district details for the Facility name {} and the Search Type {} is completed. User Id {}, Context Id {}",
          facilityName, searchType, userId, contextId);

      if (!CollectionUtils.isEmpty(districts)) {
        if (districts.size() >= SEARCH_RESULT_MAX_SIZE) {
          throw new ValidationException(DartDistrictServiceConstants.TOO_MANY_FACILITIES,
              DartDistrictServiceConstants.TOO_MANY_FACILITIES_MSG);
        }
        logger.info("Exiting from getDistrictDetailsByFacilityName(). User Id {}, Context Id {}, "
            + "Facility name: {}, Search Type {}", userId, contextId, facilityName, searchType);
        return new ResponseEntity<>(districts, HttpStatus.OK);
      }
    } catch (ValidationException e) {
      throw e;
    } catch (DataAccessException e) {
      throw new DARTDistrictServiceException(
          "Error while retrieving the data for the facility name " + facilityName
              + " with search Type " + searchType, e);
    } catch (Exception e) {
      throw new DARTDistrictServiceException(
          "General Error while retrieving the data for the facility name " + facilityName
              + " with search Type " + searchType,e);
    }
    logger.info(
        "There were no district details related to facility name: {} . Exiting getDistrictDetailsByFacilityName().",
        facilityName);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


  private void populateLoggerExeptionMap(String methodName, String eventName, String applicableId,
      String errorMessage) {
    logger.debug("Entering populateLoggerExceptionMap().");
    Map<String, String> loggingMap = new HashMap<>();
    loggingMap.put("Application name", "eTrack");
    loggingMap.put("Method name", methodName);
    loggingMap.put("Event name", eventName);
    loggingMap.put("Applicable ids", applicableId);
    loggingMap.put("Error message", errorMessage);
    logger.error(loggingMap.toString());
    loggingMap = null;
  }

  @Override
  public List<ApplicationNarrativeDetail> getApplicationNarrativeDescription(final String userId,
      final String contextId, final Long districtId) {
    return dartDistrictDAO.retrieveApplicationPermitDescNarrative(userId, contextId, districtId);
  }

  @Override
  public void uploadDIMSRApplication(String userId, String contextId, Long projectId, String guid) {
    logger.info("Uploading the DIMSR application {} into DART. User Id {}, Context Id {}", projectId, userId, contextId);
    dartDistrictDAO.uploadDIMSRApplicationDetailsToDart(userId, contextId, projectId, guid);
//    logger.info("Refreshing the Status and Milestone details for this DIMSR application"
//        + " {} into DART. User Id {}, Context Id {}", projectId, userId, contextId);
//    dartDistrictDAO.refreshMilestone(userId, contextId, projectId);
  }

  @Override
  public void uploadETrackApplicationDetailsToDart(String userId, String contextId, Long projectId, String guid) {
    logger.info("Uploading the eTrack Permit application {} into DART. User Id {}, Context Id {}", projectId, userId, contextId);
    dartDistrictDAO.uploadETrackDataToDart(userId, contextId, projectId, guid);
//    logger.info("Refreshing the Status and Milestone details for this eTrack Pemit application"
//        + " {} into DART. User Id {}, Context Id {}", projectId, userId, contextId);
//    dartDistrictDAO.refreshMilestone(userId, contextId, projectId);
  }

//  @Scheduled(fixedDelay = Timer.ONE_HOUR*8)
  @Override
  public void refreshMilestoneStatus() {
    String contextId = UUID.randomUUID().toString();
    logger.info("Schedule the refresh Milestone status from eTrack to DART  Context Id {}", contextId);
    try {
      dartDistrictDAO.refreshMilestone(null, contextId, null);
    } catch (DARTDistrictServiceException e) {
      logger.error("Error while refreshing the Milestone details ", e);
    }
  }

  @Override
  public void refreshMilestoneStatusByProjectId(final String userId, final String contextId, final Long projectId) {
    logger.info("Refresh Milestone status from eTrack to DART for the "
        + "Project Id {}. User Id {}, Context Id {}", projectId, userId, contextId);
    dartDistrictDAO.refreshMilestone(userId, contextId, projectId);
  }
  
  @Override
  public void addAdditionalPermitToDart(final String userId, final String contextId, final Long projectId,
      final Long applId, final String guid) {
    dartDistrictDAO.addAdditionalPermitToDart(userId, contextId, projectId, applId, guid);
  }
}
