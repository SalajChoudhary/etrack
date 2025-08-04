package dec.ny.gov.etrack.dart.db.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.dao.DashboardDetailDAO;
import dec.ny.gov.etrack.dart.db.dao.SupportDocumentDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.OutForReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.repo.DartMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.PendingAppRepo;
import dec.ny.gov.etrack.dart.db.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.DashboardService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@Service
public class DashboardServiceImpl implements DashboardService {
  
  private static Logger logger = LoggerFactory.getLogger(DartDbServiceImpl.class.getName());
  
  @Autowired
  private PendingAppRepo pendingAppRepo;
  @Autowired
  private DartDBServiceUtility dartDBServiceUtility;
  @Autowired
  private TransformationService transformationService;
  @Autowired
  private DartMilestoneRepo dartMilestoneRepo;
  @Autowired
  private DashboardDetailDAO dashboardDetailDAO;
  @Autowired
  private SupportDocumentRepo supportDocumentRepo;
  @Autowired
  private SupportDocumentDAO supportDocumentDao;
  @Autowired
  private DartDbService dartDBService;
  @Autowired
  private DartDbDAO dartDBDAO;
  
  @Override
  public List<DashboardDetail> getResumeEntryPrjects(final String userId, final String contextId) {
    logger.info("Collecting Unsubmitted projects. User Id {}, Context Id {}", userId, contextId);
    List<DashboardDetail> unsubmittedProjects = dartDBService.getUnsubmittedApps(userId, contextId).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    return unsubmittedProjects;
  }

  
  @Override
  public List<DashboardDetail> getValidateEligibleProjects(final String userId, final String contextId) {
    logger.info("Collecting Validated projects. User Id {}, Context Id {}", userId, contextId);
    List<PendingApplication> validateApplications =
        pendingAppRepo.findAllValidationEligibleApplicationsByUserId(userId);
    List<DashboardDetail> validationEligibleApplications =
        transformationService.prepareApplicationInfo(userId, contextId, validateApplications, null);
    List<DashboardDetail> validationEligibleProjects = dartDBServiceUtility.amendMunicipalityDetails(validationEligibleApplications).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    return validationEligibleProjects;
  }

  @Override
  public List<DashboardDetail> getAllActiveProjects(final String userId, final String contextId) {
    Iterable<DartMilestone> dartMilestonesList = dartMilestoneRepo.findAll();
    Map<Long, DartMilestone> dartMilestoneMapByBatchId = new HashMap<>();
    dartMilestonesList.forEach(dartMilestone -> {
      dartMilestoneMapByBatchId.put(dartMilestone.getBatchId(), dartMilestone);
    });
    List<DashboardDetail> allActive = retrieveAllThePendingApplications(userId, contextId, dartMilestoneMapByBatchId).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    return allActive;
  }

  @Override
  public List<DashboardDetail> retrieveAllThePendingApplications(final String userId,
      final String contextId, Map<Long, DartMilestone> dartMilestoneMapWithBatchId) {
    logger.info(
        "Entering to retrieveAllTheUnIssuedApplications for the input user. User Id {}, Context Id {}",
        userId, contextId);
    List<DartApplication> allactiveApplicationsList =
        dashboardDetailDAO.retrieveDARTPendingApplications(userId, contextId, null, null);
    logger.debug("All active applications {}", allactiveApplicationsList);
    return transformationService.transformDataIntoDashboardData(allactiveApplicationsList, null,
        dartMilestoneMapWithBatchId);
  }
  
  private Map<Long, Long> outForReviewProjects(final String userId) {
    List<Long> outFOrReviewProjects = supportDocumentRepo.findAllOutForReviewProjects(userId);

    Map<Long, Long> outForReviewProjectMapping = new HashMap<>();
    if (!CollectionUtils.isEmpty(outFOrReviewProjects)) {
      outFOrReviewProjects.forEach(projectId -> {
        outForReviewProjectMapping.put(projectId, projectId);
      });
    }
    return outForReviewProjectMapping;
  }
  
  private Map<Long, DartMilestone> retrieveMilestoneMapByProjectId() {
    Iterable<DartMilestone> dartMilestonesList = dartMilestoneRepo.findAll();
    Map<Long, DartMilestone> dartMilestoneMapByBatchId = new HashMap<>();
    dartMilestonesList.forEach(dartMilestone -> {
      dartMilestoneMapByBatchId.put(dartMilestone.getBatchId(), dartMilestone);
    });
    return dartMilestoneMapByBatchId;
  }
  
  @Override
  public List<DashboardDetail> getTasksDueApplications(final String userId, final String contextId) {
    logger.info("Collecting Tasks Due projects. User Id {}, Context Id {}", userId,
        contextId);
    
    List<DartApplication> tasksDueFromDart =
        dashboardDetailDAO.retrieveDARTDueApps(userId, contextId);
    List<DashboardDetail> transformedTasksDue = transformationService.transformDataIntoDashboardData(tasksDueFromDart,
        outForReviewProjects(userId), retrieveMilestoneMapByProjectId()).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());;
    logger.info("Collected Tasks Due projects. User Id {}, Context Id {}", userId,
        contextId);
    return transformedTasksDue;
  }

  @Override
  public List<DashboardDetail> getApplicantResponseDueApplications(String userId, final String contextId) {
    List<DartApplication> applicantResponseDueFromDart =
        dashboardDetailDAO.retrieveDARTAplctResponseDueApps(userId, contextId);
    List<DashboardDetail> transformedapplicantResponsesDue = transformationService.transformDataIntoDashboardData(
        applicantResponseDueFromDart, outForReviewProjects(userId), retrieveMilestoneMapByProjectId()).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());;
    return transformedapplicantResponsesDue;
  }

  @Override
  public List<DashboardDetail> getSuspendedApplications(String userId, final String contextId) {
    logger.info("Collecting Suspended projects. User Id {}, Context Id {}", userId, contextId);
    List<DartApplication> suspendedApps =
        dashboardDetailDAO.retrieveDARTSuspendedApps(userId, contextId);
    return  transformationService.transformDataIntoDashboardData(suspendedApps, null, retrieveMilestoneMapByProjectId());
  }

  @Override
  public List<DashboardDetail> getOutForReviewApplications(String userId, final String contextId) {
    logger.info("Collecting Out for Review projects. User Id {}, Context Id {}", userId, contextId);
    List<OutForReviewEntity> outForReviewEntityApps =
        supportDocumentDao.retrieveOutForReviewApps(userId, contextId);
    List<DashboardDetail> outforReviewApps = null;
    if (!CollectionUtils.isEmpty(outForReviewEntityApps)) {
      outforReviewApps = transformationService.transformOutForReviewAppsToDashboard(outForReviewEntityApps).stream()
          .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());;
    }
    return outforReviewApps;
  }

  @Override
  public List<DashboardDetail> getEmergencyAuthorizationApplications(String userId, final String contextId) {
    List<DartApplication> emergencyAuthorizationAppsList =
        dashboardDetailDAO.retrieveEmergencyAuthorizationApps(userId, contextId);
    List<DashboardDetail> emergencyAuthorizationApps = transformationService.transformDataIntoDashboardData(
        emergencyAuthorizationAppsList, outForReviewProjects(userId), retrieveMilestoneMapByProjectId());

    Map<Long, String> projectIdAndReviewerName = new HashMap<>();
    Set<Long> projectIds = new HashSet<>();
    emergencyAuthorizationApps.forEach(emergencyApp -> {
      projectIds.add(emergencyApp.getProjectId());
    });
    if (!CollectionUtils.isEmpty(projectIds)) {
      List<String> projectIdAndReviewerNameList =
          supportDocumentRepo.findReviewerDetailsByProjectIds(projectIds);
      if (!CollectionUtils.isEmpty(projectIdAndReviewerNameList)) {
        projectIdAndReviewerNameList.forEach(projectIdAndReviewer -> {
          String[] projectIdAndRevName = projectIdAndReviewer.split(",");
          projectIdAndReviewerName.put(Long.valueOf(projectIdAndRevName[0]),
              projectIdAndRevName[1]);
        });
      }
    }
    emergencyAuthorizationApps.forEach(emergencyApp -> {
      emergencyApp.setProgramStaff(projectIdAndReviewerName.get(emergencyApp.getProjectId()));
    });
    List<DashboardDetail>  sortedEAApplications = emergencyAuthorizationApps.stream()
    .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    return sortedEAApplications;
  }


  @Override
  public List<DashboardDetail> getRegionalAllActiveApplications(String userId, String contextId,
      Integer facilityRegionId) {
    logger.info("Entering into retrieve all the Pending applications "
        + "for the region dasboard User Id {}, Context Id {}", userId, contextId);    

    List<DartApplication> allactiveApplicationsList = null;
    if (facilityRegionId == null) {
      Long userRegionId = dartDBDAO.getUserRegionId(userId, contextId);
      if (userRegionId != null) {
        facilityRegionId = userRegionId.intValue();
      }
    } else if (facilityRegionId.equals(-1)) {
      facilityRegionId = null;
    }
    allactiveApplicationsList = dashboardDetailDAO.retrieveDARTPendingApplications(
        null, contextId, null, facilityRegionId);
        
    logger.info("Retrieved all the Pending applications "
        + "for the region dasboard User Id {}, Context Id {}", userId, contextId);    
    List<DashboardDetail> allActive = transformationService.transformDataIntoDashboardData(allactiveApplicationsList,
        null, retrieveMilestoneMapByProjectId()).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    logger.info("Transformed all the regional pending applications. User Id {}, Context Id {}", userId, contextId);
    return allActive;
  }

  @Override
  public List<DashboardDetail> getRegionalUnvalidatedApplications(String userId, String contextId,
      Integer facilityRegionId) {
    List<PendingApplication> regionalProjects = null;
    if (facilityRegionId == null) {
      regionalProjects = pendingAppRepo.findAllUnValidatedApplications();
    } else {
      regionalProjects = pendingAppRepo.findAllUnValidatedApplicationsByRegionId(facilityRegionId);
    }
    Map<Long, ApplicantDto> lrpsMap = dartDBServiceUtility.getLegalResponsePartyDetails(null, contextId, true);
    logger.info("Received LRP details. User Id {}, Context Id {}", userId, contextId);

    List<DashboardDetail> regionalProjectsList = 
        transformationService.prepareApplicationInfo(userId, contextId, regionalProjects, lrpsMap);
    List<DashboardDetail> dashboardDetailsAfterAddingMunicipalities =
        dartDBServiceUtility.amendMunicipalityDetails(regionalProjectsList);
    
    List<DashboardDetail> sortedUnValidateApplications = dartDBServiceUtility.amendCountyDetails(dashboardDetailsAfterAddingMunicipalities).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    return sortedUnValidateApplications;
  }


  @SuppressWarnings("unchecked")
  @Override
  public List<DashboardDetail> getRegionalProgramReviewApplications(String userId, String contextId,
      Integer facilityRegionId) {
    return (List<DashboardDetail>)dartDBService.getProgramReviewerDashboardDetails(null, contextId, facilityRegionId);
  }


  @Override
  public List<DashboardDetail> getRegionalDisposedApplications(String userId, String contextId,
      Integer facilityRegionId) {
    logger.info("Collecting Disposed applications/projects. User Id {}, Context Id {}", userId,
        contextId);
    List<DartApplication> disposedApps =
        dashboardDetailDAO.retrieveDARTDisposedApps(userId, contextId, facilityRegionId);
    List<DashboardDetail> transformedDisposedApps =
        transformationService.transformDataIntoDashboardData(disposedApps, null, retrieveMilestoneMapByProjectId()).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed()).collect(Collectors.toList());
    return transformedDisposedApps;
  }
  
}
