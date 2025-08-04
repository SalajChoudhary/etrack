package dec.ny.gov.etrack.permit.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.Application;
import dec.ny.gov.etrack.permit.entity.ApplicationContactAssignment;
import dec.ny.gov.etrack.permit.entity.Project;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;
import dec.ny.gov.etrack.permit.entity.TransactionTypeRule;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.ApplicationPermit;
import dec.ny.gov.etrack.permit.model.ApplicationPermitDetail;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;
import dec.ny.gov.etrack.permit.repo.ApplicationContactAssignmentRepo;
import dec.ny.gov.etrack.permit.repo.ApplicationRepo;
import dec.ny.gov.etrack.permit.repo.PermitTypeCodeRepo;
//import dec.ny.gov.etrack.permit.repo.OnlineUserRepo;
import dec.ny.gov.etrack.permit.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.permit.repo.ProjectRepo;
import dec.ny.gov.etrack.permit.repo.TransactionTypeRuleRepo;
import dec.ny.gov.etrack.permit.service.ETrackPermitService;

@Service
public class ETrackPermitServiceImpl implements ETrackPermitService {

  @Autowired
  private ProjectActivityRepo projectActivityRepo;

  @Autowired
  private ProjectRepo projectRepo;

  @Autowired
  private ETrackPermitDAO eTrackPermitDao;

  @Autowired
  private TransformationService transformationService;

  @Autowired
  private ApplicationRepo applicationRepo;

  @Autowired
  private TransactionTypeRuleRepo transactionTypeRuleRepo;

  @Autowired
  private ApplicationContactAssignmentRepo applicationContactAssignmentRepo;

  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;
  
  @Autowired
  private PermitTypeCodeRepo permitTypeCodeRepo;
  
  
//  @Autowired
//  private OnlineUserRepo onlineUserRepo;

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackPermitServiceImpl.class.getName());

  private final SimpleDateFormat MM_DD_YYYY_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
  private static final String SW_PERMIT_CODE = "SW1";
  private static final String DAM_PERMIT_CODE = "DA";


  /*
   * private void updateBusinessLegalName(Applicant applicant, Public publicName, String userId) {
   * if (applicant.getOrganization() != null &&
   * !StringUtils.isEmpty(applicant.getOrganization().getVerifiedLegalName()) &&
   * publicName.getBusinessValidatedInd() != null && publicName.getBusinessValidatedInd().equals(1))
   * { if (!applicant.getOrganization().getBusOrgName()
   * .equalsIgnoreCase(applicant.getOrganization().getVerifiedLegalName())) {
   * publicRepo.updatePublicBusinessOrgName(applicant.getOrganization().getVerifiedLegalName(),
   * publicName.getPublicId(), userId, new Date()); } } }
   */


  @Override
  public ProjectDetail getProjectDetails(final String userId, final String contextId,
      final Long projectId) {
    Optional<Project> response = projectRepo.findById(projectId);
    if (response.isPresent()) {
      Project project = response.get();
      return transformationService.transformProjectEntity(userId, contextId, project);
    } else {
      throw new BadRequestException("NO_PROJECT_AVAILABLE",
          "There is no project associated with this input id " + projectId, projectId);
    }
  }


  // @Override
  // public void savePermitType(String userId, String contextId, Long projectId,
  // Map<String, Object> permitTypesMap) {
  // logger.info("Entering into saving/updating the permit types User Id {}, Context Id {}", userId,
  // contextId);
  // try {
  // List<Application> applications = applicationRepo.findByProjectId(projectId);
  // Optional<Project> projectAvailability = projectRepo.findById(projectId);
  //
  // if (projectAvailability.isPresent()) {
  // Project project = projectAvailability.get();
  // if (project.getConstrnType() != null && project.getConstrnType() > 0
  // && permitTypesMap.get("constrnType") == null) {
  // projectRepo.updateProjectInfo(project.getProjectDesc(), project.getProposedUseCode(),
  // projectId, userId, new Date(), null, null, null, project.getWetlandIds(),
  // project.getStrWaterbodyName(), project.getDamType(), project.getSeqrInd());
  // }
  // }
  //
  // if (!CollectionUtils.isEmpty(applications)) {
  // applicationRepo.deleteAll(applications);
  // }
  //
  // applications = new ArrayList<>();
  // List<String> permitTypes = (List<String>) permitTypesMap.get("permitTypes");
  // for (String permitType : permitTypes) {
  // Application application = new Application();
  // application.setProjectId(projectId);
  // application.setPermitTypeCode(permitType);
  // application.setCreatedById(userId);
  // application.setCreateDate(new Date());
  // applications.add(application);
  // }
  // applicationRepo.saveAll(applications);
  // List<ProjectActivity> projectActivityList =
  // projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId,
  // ActivityTaskStatus.PROJECT_INFO.getActivityStatus());
  // ProjectActivity projectActivity = null;
  // if (CollectionUtils.isEmpty(projectActivityList)) {
  // projectActivity = new ProjectActivity();
  // projectActivity.setActivityStatusId(ActivityTaskStatus.PROJECT_INFO.getActivityStatus());
  // projectActivity.setProjectId(projectId);
  // projectActivity.setStartDate(new Date());
  // projectActivity.setCreateDate(new Date());
  // projectActivity.setCreatedById(userId);
  // logger.info("Saving project {} activity status Context Id : {}", projectId, contextId);
  // projectActivityRepo.save(projectActivity);
  // }
  // logger.info("Exiting from saving the permit types User Id {}, Context Id {}", userId,
  // contextId);
  // } catch (Exception e) {
  // throw new ETrackPermitException("APPLICATION_PERSIST_ERROR",
  // "Error while persisting the applications " + e.getMessage(), e);
  // }
  // }

  @Override
  public List<String> getPermitTypes(String userId, String contextId, Long projectId) {
    logger.info("Entering into get application permit types User Id {} , Context Id {}", userId,
        contextId);
    List<Application> applications = applicationRepo.findByProjectId(projectId);
    if (CollectionUtils.isEmpty(applications)) {
      logger.error("No permit type is available for the input project id {} Context Id {}",
          projectId);
      throw new DataNotFoundException("No permit type found for the project",
          String.valueOf(projectId));
    }
    List<String> permitTypes = new ArrayList<>();
    for (Application permitType : applications) {
      permitTypes.add(permitType.getPermitTypeCode());
    }
    return permitTypes;
  }


  @Transactional(rollbackFor = {BadRequestException.class, ETrackPermitException.class})
  @Override
  public void assignContacts(final String userId, final String contextId, final Long projectId,
      final List<ApplicationPermitDetail> assignedContacts) {
    logger.info("Entering into assignContacts. User Id {}, Context id {}", userId, contextId);

    if (CollectionUtils.isEmpty(assignedContacts)) {
      throw new BadRequestException("NO_CONTACTS_ASSIGNED",
          "There is no assigned contacts for this project", projectId);
    }
    try {
      List<Long> applicationIds = applicationRepo.findAllApplicationIdByProjectId(projectId);
      applicationContactAssignmentRepo.deleteAllByApplicationIds(applicationIds);
      List<ApplicationContactAssignment> applicationContactAssignments = new ArrayList<>();
      for (ApplicationPermitDetail assignedContact : assignedContacts) {
        if (assignedContact.getApplicationId() != null && assignedContact.getRoleId() != null
            && assignedContact.getPermitFormId() != null) {
          ApplicationContactAssignment applicationContactAssignment =
              new ApplicationContactAssignment();
          applicationContactAssignment.setApplicationId(assignedContact.getApplicationId());
          applicationContactAssignment.setRoleId(assignedContact.getRoleId());
          applicationContactAssignment.setPermitFormId(assignedContact.getPermitFormId());
          applicationContactAssignment.setCreatedById(userId);
          applicationContactAssignment.setCreateDate(new Date());
          applicationContactAssignments.add(applicationContactAssignment);
        } else {
          throw new BadRequestException("INSUFFICIENT_CONTACT_FORM_DETAIL",
              "One or more Permit Contact Assignment details is missing ", assignedContact);
        }
      }
      if (!CollectionUtils.isEmpty(applicationContactAssignments)) {
        applicationContactAssignmentRepo.saveAll(applicationContactAssignments);
      }
      logger.info("Exiting from assignContacts. User Id {}, Context id {}", userId, contextId);
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      throw new ETrackPermitException("CONTACT_ASSIGNMENT_ERROR",
          "Error while assigning the Contact/Agent Assignment ", e);
    }
  }

  private Application prepareApplicationPermitDataToPersist(final String userId,
      final String contextId, ApplicationPermitDetail submittedPermit) {

    Application application = null;
    if (submittedPermit.getApplicationId() != null && submittedPermit.getApplicationId() > 0) {
      Optional<Application> applicationAvailable =
          applicationRepo.findById(submittedPermit.getApplicationId());
      if (!applicationAvailable.isPresent()) {
        throw new BadRequestException("NO_APPLN_AVAILABLE",
            "There is no application is associated with this application Id "
                + submittedPermit.getApplicationId(),
            submittedPermit);
      }
      application = applicationAvailable.get();
    } else {
      application = new Application();
    }
    application.setPermitTypeCode(submittedPermit.getPermitTypeCode());
    application.setRoleId(submittedPermit.getRoleId());
    application.setEdbApplId(submittedPermit.getEdbApplnId());
    application.setBatchIdEdb(submittedPermit.getBatchId());
//    application.setBatchGroupEtrack(submittedPermit.getCalculatedBatchIdForProcess());
    application.setProgId(submittedPermit.getProgramId());
    if (submittedPermit.getPolEmissionInd() != null) {
      application.setPolEmissionInd(submittedPermit.getPolEmissionInd());
    }
    application.setEdbTransTypeCode(submittedPermit.getEdbTransType());
    application.setEdbAuthTemplateAuthId(submittedPermit.getEdbAuthId());
    Integer extensionReqInd = 0;
//    if (submittedPermit.getTrackingInd() != null && submittedPermit.getTrackingInd().equals(1)) {
//      application.setTrackingInd(1);
//    } else {
//      application.setTrackingInd(0);
//    }

    if (submittedPermit.getEdbTrackingInd() != null
        && submittedPermit.getEdbTrackingInd().equals(1)) {
      application.setEdbTrackingInd(1);
      application.setTrackingInd(1);
    } else {
      application.setEdbTrackingInd(0);
      application.setTrackingInd(0);
    }
    
    try {
      if (StringUtils.hasLength(submittedPermit.getEdbPermitEffectiveDate())) {
        MM_DD_YYYY_FORMAT.setLenient(false);
        application.setEdbAuthEffDate(
            MM_DD_YYYY_FORMAT.parse(submittedPermit.getEdbPermitEffectiveDate()));
      }
      if (StringUtils.hasLength(submittedPermit.getEdbPermitExpiryDate())) {
        MM_DD_YYYY_FORMAT.setLenient(false);
        application
            .setEdbAuthExpDate(MM_DD_YYYY_FORMAT.parse(submittedPermit.getEdbPermitExpiryDate()));
      }
    } catch (ParseException e) {
      throw new BadRequestException("INVALID_EXPIRY_EFF_DATE_PASSED",
          "Invalid format of Effective or Expiry Date is passed. User Id {}, Context Id {} ",
          submittedPermit);
    }

    if (StringUtils.hasLength(submittedPermit.getNewReqInd())
        && "Y".equals(submittedPermit.getNewReqInd())) {
      application.setUserSelNewInd(1);
      application.setUserSelModInd(0);
      application.setUserSelExtInd(0);
      application.setUserSelRenInd(0);
      application.setUserSelTransferInd(0);
      application.setChgOriginalProjectInd(0);
    } else {
      application.setUserSelNewInd(0);
      if (StringUtils.hasLength(submittedPermit.getModReqInd())
          && "Y".equals(submittedPermit.getModReqInd())) {
        application.setUserSelModInd(1);
      } else {
        application.setUserSelModInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getExtnReqInd())
          && "Y".equals(submittedPermit.getExtnReqInd())) {
        application.setUserSelExtInd(1);
      } else {
        application.setUserSelExtInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getRenewReqInd())
          && "Y".equals(submittedPermit.getRenewReqInd())) {
        application.setUserSelRenInd(1);
      } else {
        application.setUserSelRenInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getTransferReqInd())
          && "Y".equals(submittedPermit.getTransferReqInd())) {
        application.setUserSelTransferInd(1);
      } else {
        application.setUserSelTransferInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getModQuestionAnswer())
          && "Y".equals(submittedPermit.getModQuestionAnswer())) {
        application.setChgOriginalProjectInd(1);
      } else {
        application.setChgOriginalProjectInd(0);
      }

      extensionReqInd = application.getUserSelExtInd();
      if (application.getUserSelModInd().equals(1) && application.getUserSelExtInd().equals(1)) {
        logger.info(
            "User Requested for MOD and EXTN and System will take MOD as request. "
                + "User Id {}, Context Id {}, Permit Type {}",
            userId, contextId, submittedPermit.getPermitTypeCode());
        extensionReqInd = 0;
      }
    }
    List<TransactionTypeRule> transactionTypeRuleList =
        transactionTypeRuleRepo.findTranstypeAndAssociateDetails(
            submittedPermit.getPermitTypeCode(), application.getUserSelNewInd(),
            application.getUserSelModInd(), extensionReqInd, application.getUserSelTransferInd(),
            application.getUserSelRenInd(), application.getChgOriginalProjectInd());

    if (CollectionUtils.isEmpty(transactionTypeRuleList)) {
      throw new BadRequestException("INCORRECT_DATA_MAPPING",
          "There is no Trans Type mapping for the permit type code "
              + submittedPermit.getPermitTypeCode(),
          submittedPermit.getPermitTypeCode());
    }
    if (StringUtils.hasLength(submittedPermit.getEdbTransType()) 
        && ( "REI".equals(submittedPermit.getEdbTransType()) 
        || submittedPermit.getEdbTransType().contains(":E"))) {
      boolean constructionExpiredPermit = false;
      for (TransactionTypeRule transactionTypeRule : transactionTypeRuleList) {
        if (transactionTypeRule.getEdbTransTypeCode().equals("REI") ) {
          constructionExpiredPermit = true;
          application.setEdbTransTypeCode(transactionTypeRule.getEdbTransTypeCode().replace(":E", ""));
          application.setTransTypeCode(transactionTypeRule.getTransTypeCode());
          application.setUploadTransTypeCode(transactionTypeRule.getTransTypeCode());
          application.setTransactionTypeRuleId(transactionTypeRule.getTransactionTypeRuleId());
        }
        if (!constructionExpiredPermit) {
          application.setTransTypeCode(transactionTypeRuleList.get(0).getTransTypeCode());
          application.setUploadTransTypeCode(transactionTypeRuleList.get(0).getTransTypeCode());
          application
              .setTransactionTypeRuleId(transactionTypeRuleList.get(0).getTransactionTypeRuleId());          
        }
      }
    } else {
      application.setTransTypeCode(transactionTypeRuleList.get(0).getTransTypeCode());
      application.setUploadTransTypeCode(transactionTypeRuleList.get(0).getTransTypeCode());
      application
          .setTransactionTypeRuleId(transactionTypeRuleList.get(0).getTransactionTypeRuleId());
    }
    return application;
  }

  private Application prepareAmendApplicationPermitDatasToPersist(final String userId,
      final String contextId, ApplicationPermitDetail submittedPermit, Application application) {

    Integer extensionReqInd = 0;

    if (StringUtils.hasLength(submittedPermit.getNewReqInd())
        && "Y".equals(submittedPermit.getNewReqInd())) {
      application.setUserSelNewInd(1);
      application.setUserSelModInd(0);
      application.setUserSelExtInd(0);
      application.setUserSelRenInd(0);
      application.setUserSelTransferInd(0);
      application.setChgOriginalProjectInd(0);
    } else {
      application.setUserSelNewInd(0);
      if (StringUtils.hasLength(submittedPermit.getModReqInd())
          && "Y".equals(submittedPermit.getModReqInd())) {
        application.setUserSelModInd(1);
      } else {
        application.setUserSelModInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getExtnReqInd())
          && "Y".equals(submittedPermit.getExtnReqInd())) {
        application.setUserSelExtInd(1);
      } else {
        application.setUserSelExtInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getRenewReqInd())
          && "Y".equals(submittedPermit.getRenewReqInd())) {
        application.setUserSelRenInd(1);
      } else {
        application.setUserSelRenInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getTransferReqInd())
          && "Y".equals(submittedPermit.getTransferReqInd())) {
        application.setUserSelTransferInd(1);
      } else {
        application.setUserSelTransferInd(0);
      }

      if (StringUtils.hasLength(submittedPermit.getModQuestionAnswer())
          && "Y".equals(submittedPermit.getModQuestionAnswer())) {
        application.setChgOriginalProjectInd(1);
      } else {
        application.setChgOriginalProjectInd(0);
      }

      extensionReqInd = application.getUserSelExtInd();
      if (application.getUserSelModInd().equals(1) && application.getUserSelExtInd().equals(1)) {
        logger.info(
            "User Requested for MOD and EXTN and System will take MOD as request. "
                + "User Id {}, Context Id {}, Permit Type {}",
            userId, contextId, submittedPermit.getPermitTypeCode());
        extensionReqInd = 0;
      }
    }

    List<TransactionTypeRule> transactionTypeRuleList =
        transactionTypeRuleRepo.findTranstypeAndAssociateDetails(
            submittedPermit.getPermitTypeCode(), application.getUserSelNewInd(),
            application.getUserSelModInd(), extensionReqInd, application.getUserSelTransferInd(),
            application.getUserSelRenInd(), application.getChgOriginalProjectInd());

    if (CollectionUtils.isEmpty(transactionTypeRuleList)) {
      throw new BadRequestException("INCORRECT_DATA_MAPPING",
          "There is no Trans Type mapping for the permit type code "
              + submittedPermit.getPermitTypeCode(),
          submittedPermit.getPermitTypeCode());
    }
    if ("REI".equals(submittedPermit.getEdbTransType())) {
      transactionTypeRuleList.forEach(transactionTypeRule -> {
        if (transactionTypeRule.getEdbTransTypeCode().equals("REI")) {
          application.setTransTypeCode(transactionTypeRule.getTransTypeCode());
          application.setUploadTransTypeCode(transactionTypeRule.getTransTypeCode());
          application.setTransactionTypeRuleId(transactionTypeRule.getTransactionTypeRuleId());
        }
      });
    } else {
      application.setTransTypeCode(transactionTypeRuleList.get(0).getTransTypeCode());
      application.setUploadTransTypeCode(transactionTypeRuleList.get(0).getTransTypeCode());
      application
          .setTransactionTypeRuleId(transactionTypeRuleList.get(0).getTransactionTypeRuleId());
    }
    return application;
  }

  private void saveOrUpdatePermitContacts(final String userId, final String contextId,
      final Long projectId, List<ApplicationPermitDetail> eTrackPermits,
      List<ApplicationPermitDetail> dartPermits) {

    List<Application> applicationList = applicationRepo.findByProjectId(projectId);

    Date createDate = null;
    String createdBy = null;
    if (!CollectionUtils.isEmpty(applicationList)) {
      createDate = applicationList.get(0).getCreateDate();
      createdBy = applicationList.get(0).getCreatedById();
    }

    List<Application> submittedApplicationPermitsList = new ArrayList<>();
    if (CollectionUtils.isEmpty(eTrackPermits) && CollectionUtils.isEmpty(dartPermits)) {
      logger.error("There is no permits requested to persist, User Id {}, Context Id {}", userId,
          contextId);
      throw new BadRequestException("NO_PERMITS_PASSED", "There is no permits requested to persist",
          eTrackPermits);
    }

    if (!CollectionUtils.isEmpty(eTrackPermits)) {
      for (ApplicationPermitDetail submittedPermit : eTrackPermits) {
        Application application =
            prepareApplicationPermitDataToPersist(userId, contextId, submittedPermit);
        application.setProjectId(projectId);
        if (submittedPermit.getApplicationId() == null || submittedPermit.getApplicationId() == 0) {
          application.setCreateDate(new Date());
          application.setCreatedById(userId);
        } else {
          application.setCreateDate(createDate);
          application.setCreatedById(createdBy);
          application.setModifiedDate(new Date());
          application.setModifiedById(userId);
        }
        submittedApplicationPermitsList.add(application);
      }
//      if (!CollectionUtils.isEmpty(submittedApplicationPermitsList)) {
//        submittedApplicationPermitsList.get(0).setTrackingInd(1);
//      }
    }
    // applicationRepo.deleteByProjectId(projectId);
    applicationRepo.saveAll(submittedApplicationPermitsList);

    Map<Long, List<ApplicationPermitDetail>> batchedDartPermits = new HashMap<>();
    if (!CollectionUtils.isEmpty(dartPermits)) {
      for (ApplicationPermitDetail submittedDartPermit : dartPermits) {
        if (submittedDartPermit.getBatchId() != null && submittedDartPermit.getBatchId() <= 0) {
          throw new BadRequestException("BATCH_ID_NOT_AVAIL",
              "Batch Id is not passed for the DART permits", dartPermits);
        }
        if (batchedDartPermits.get(submittedDartPermit.getBatchId()) != null) {
          batchedDartPermits.get(submittedDartPermit.getBatchId()).add(submittedDartPermit);
        } else {
          List<ApplicationPermitDetail> dartPermitApplicationDetail = new ArrayList<>();
          dartPermitApplicationDetail.add(submittedDartPermit);
          batchedDartPermits.put(submittedDartPermit.getBatchId(), dartPermitApplicationDetail);
        }
      }

      for (Long batchProcessId : batchedDartPermits.keySet()) {
        // boolean trackingIndAvailable = false;
        List<Application> dartPermitsList = new ArrayList<>();
        for (ApplicationPermitDetail batchedDartPermit : batchedDartPermits.get(batchProcessId)) {
          Application application =
              prepareApplicationPermitDataToPersist(userId, contextId, batchedDartPermit);
          application.setProjectId(projectId);

          // if (application.getTrackingInd() != null && application.getTrackingInd().equals(1)) {
          // trackingIndAvailable = true;
          // }

          if (batchedDartPermit.getApplicationId() == null
              || batchedDartPermit.getApplicationId() == 0) {
            application.setCreateDate(new Date());
            application.setCreatedById(userId);
          } else {
            application.setCreateDate(createDate);
            application.setCreatedById(createdBy);
            application.setModifiedDate(new Date());
            application.setModifiedById(userId);
          }
          dartPermitsList.add(application);
        }
        applicationRepo.saveAll(dartPermitsList);
        // if (!CollectionUtils.isEmpty(dartPermitsList)) {
        // if (!trackingIndAvailable) {
        // dartPermitsList.get(0).setTrackingInd(1);
        //
        // }
        // }
      }
    }
  }

  @Override
  public void removeApplicationPermit(final String userId, final String contextId,
      final Long projectId, final Long applicationId, final String permitTypeCode) {
    logger.info("Entering into removeApplicationPermit method. User Id {}, Context Id {}:", userId,
        contextId);
    try {
      Optional<Application> permitApplicationAvailability =
          applicationRepo.findByApplicationIdAndProjectIdAndPermitTypeCode(applicationId, projectId,
              permitTypeCode);
      if (!permitApplicationAvailability.isPresent()) {
        throw new BadRequestException("NO_PERMIT_AVAILABLE",
            "There is no Permit applications available for the Project id " + projectId,
            applicationId);
      }
      applicationRepo.deleteById(applicationId);
      logger.info("Exiting from removeApplicationPermit method. User Id {}, Context Id {}:", userId,
          contextId);
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("REMOVE_PERMIT_ERROR",
          "Error while removing the Permit for the input permit id " + applicationId, e);
    }
  }

  @Transactional
  @Override
  public void removeApplicationPermits(final String userId, final String contextId,
      final Long projectId, final List<Long> applicationIds) {

    logger.info("Entering into removeApplicationPermits method. User Id {}, Context Id {}:", userId,
        contextId);
    try {
      Iterable<Application> permitApplicationAvailability =
          applicationRepo.findAllById(applicationIds);
      long noOfApplications =
          StreamSupport.stream(permitApplicationAvailability.spliterator(), false).count();
      if (applicationIds.size() != noOfApplications) {
        throw new BadRequestException("INVALID_REQ",
            "One or more records are missing for the input application ids passed", applicationIds);
      }
      applicationRepo.deleteAll(permitApplicationAvailability);
      permitApplicationAvailability.forEach(permitAppln -> {
        if (permitAppln.getPermitTypeCode().equalsIgnoreCase(SW_PERMIT_CODE)) {
         applicationRepo.deleteSwFacilityTypeCode(projectId); 
        } else if (permitAppln.getPermitTypeCode().equalsIgnoreCase(DAM_PERMIT_CODE)) {
          applicationRepo.resetDAMConstructionTypeDetails(userId, projectId);
        }
        final List<String> CONSTN_PERMIT_CODES = permitTypeCodeRepo.findAllConstructionPermits();
        if (CONSTN_PERMIT_CODES.contains(permitAppln.getPermitTypeCode())) {
          List<String> constructionPermits = applicationRepo.findAvailableConstructionPermitsByProjectId(projectId, CONSTN_PERMIT_CODES);
          if (!CollectionUtils.isEmpty(constructionPermits) 
              && constructionPermits.size()==1 
              && constructionPermits.get(0).equals(permitAppln.getPermitTypeCode())) {
            applicationRepo.resetConstructionTypeDetails(userId, projectId);
          }
        }
      });
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("REMOVE_PERMITS_ERROR",
          "Error while removing the Permits for the input permit ids " + applicationIds, e);
    }
    logger.info("Exiting from removeApplicationPermits method. User Id {}, Context Id {}:", userId,
        contextId);
  }

  private void findAnyDuplicatePermitSubmission(String userId, String contextId, Long projectId,
      ApplicationPermit applicationPermit) {

    logger.info("Entering into findAnyDuplicatePermitSubmission method. User Id {}, Context Id {}",
        userId, contextId);
    Map<String, String> regularPermitCodeMap = new HashMap<>();
    Map<String, String> gpPermitCodeMap = new HashMap<>();
    List<Long> dartGPPermitApplicationIds = new ArrayList<>();

    logger.info("Checking whether any existing permit has been applied "
        + "for more than once.User Id {}, Context Id {}", userId, contextId);
    if (!CollectionUtils.isEmpty(applicationPermit.getDartPermits())) {
      applicationPermit.getDartPermits().forEach(dartPermit -> {
        if (dartPermit.getPermitTypeCode().startsWith("GP")) {
          dartGPPermitApplicationIds.add(dartPermit.getEdbApplnId());
        }
        regularPermitCodeMap.put(dartPermit.getPermitTypeCode(), dartPermit.getPermitTypeCode());
        // if (regularPermitCodeMap.get(dartPermit.getPermitTypeCode()) != null
        // && !dartPermit.getPermitTypeCode().startsWith("GP")) {
        // throw new BadRequestException("NO_NEW_PERMIT_IF_EXIST", "You cannot apply for a New
        // permit at the same time "
        // + "you are choosing an option in the Existing Auths/Pending Apps section.",
        // applicationPermit.getDartPermits());
        // } else {
        // if (dartPermit.getPermitTypeCode().startsWith("GP")) {
        // dartGPPermitApplicationIds.add(dartPermit.getEdbApplnId());
        // }
        // regularPermitCodeMap.put(dartPermit.getPermitTypeCode(), dartPermit.getPermitTypeCode());
        // }
      });
    }

    if (!CollectionUtils.isEmpty(dartGPPermitApplicationIds)) {
      logger.info("Retrieve the application permit type associated with the "
          + "GP applied in DART. User Id {}, Context Id {}", userId, contextId);
      dartGPPermitApplicationIds.forEach(dartGPPermitApplicationId -> {
        String dartGPRegularRelatedpermit = eTrackPermitDao
            .retrieveRegularRelatedPermitsByGPApplnId(userId, contextId, dartGPPermitApplicationId);
        if (!StringUtils.hasLength(dartGPRegularRelatedpermit)) {
          throw new BadRequestException("NO_DART_GP_PERMITS",
              "There is no General Permits exists in DART for the requested one "
                  + dartGPPermitApplicationIds,
              dartGPPermitApplicationIds);
        }
        regularPermitCodeMap.put(dartGPRegularRelatedpermit, dartGPRegularRelatedpermit);
      });
    }

    logger.info("Checking whether any new permit has been applied "
        + "for more than once. User Id {}, Context Id {}", userId, contextId);
    if (!CollectionUtils.isEmpty(applicationPermit.getEtrackPermits())) {
      applicationPermit.getEtrackPermits().forEach(eTrackPermit -> {
        if (regularPermitCodeMap.get(eTrackPermit.getPermitTypeCode()) != null) {
          throw new BadRequestException("DUP_PERMIT_TYPE_AVAIL",
              "Permit Types must be unique among those selected within Existing Authorizations, "
                  + "New eTrack Permits and General Permits.",
              applicationPermit.getEtrackPermits());
        } else {
          if (eTrackPermit.getPermitTypeCode().startsWith("GP")) {
            gpPermitCodeMap.put(eTrackPermit.getPermitTypeCode(), eTrackPermit.getPermitTypeCode());
          }
          regularPermitCodeMap.put(eTrackPermit.getPermitTypeCode(),
              eTrackPermit.getPermitTypeCode());
        }
      });
    }

    logger.info(
        "Checking whether General Related Regular Permits is "
            + "part of the new submitted applications. User Id {}, Context Id {}",
        userId, contextId);
    if (!CollectionUtils.isEmpty(gpPermitCodeMap)) {
      List<String> relatedRegularPermits =
          applicationRepo.findAllRelatedRegularPermitsForGP(gpPermitCodeMap.keySet());
      if (!CollectionUtils.isEmpty(relatedRegularPermits)) {
        relatedRegularPermits.forEach(relatedRegularPermit -> {
          if (regularPermitCodeMap.get(relatedRegularPermit) != null) {
            throw new BadRequestException("DUP_PERMIT_TYPE_AVAIL",
                "Permit Types must be unique among those selected within Existing Authorizations, "
                    + "New eTrack Permits and General Permits.",
                applicationPermit.getEtrackPermits());
          } else {
            regularPermitCodeMap.put(relatedRegularPermit, relatedRegularPermit);
          }
        });
      }
    }
    logger.info("Exiting from findAnyDuplicatePermitSubmission method. User Id {}, Context Id {}",
        userId, contextId);
  }

  @Transactional
  @Override
  public void saveApplicationPermits(String userId, String contextId, Long projectId,
      ApplicationPermit applicationPermit) {
    logger.info("Entering into saveApplicationPermits, User Id {}, Context id {}", userId,
        contextId);

    findAnyDuplicatePermitSubmission(userId, contextId, projectId, applicationPermit);
    try {
      Optional<Project> projectAvailability = projectRepo.findById(projectId);
      if (projectAvailability.isPresent()) {
        Project project = projectAvailability.get();
        if (project.getConstrnType() != null && project.getConstrnType() > 0
            && applicationPermit.getConstrnType() == null) {
          logger.info(
              "Application Permit is not Construction type any more. so, reseting the Proposed Start Date and End date");
          projectRepo.updateProjectInfo(project.getProjectDesc(), project.getProposedUseCode(),
              projectId, userId, new Date(), null, null, null, project.getWetlandIds(),
              project.getStrWaterbodyName(), project.getDamType(), project.getSeqrInd());
        }
      }
      Integer emergencyInd = 0;
      if (StringUtils.hasLength(applicationPermit.getEmergencyInd())
          && "E".equals(applicationPermit.getEmergencyInd())) {
        emergencyInd = 1;
      }
      projectRepo.updateEAIndicator(userId, projectId, emergencyInd);
      saveOrUpdatePermitContacts(userId, contextId, projectId, applicationPermit.getEtrackPermits(),
          applicationPermit.getDartPermits());
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      throw new ETrackPermitException("PERSIST_PERMIT_ERROR",
          "Error while persisting the permits. Error Detail " + e.getMessage(), e);
    }
    logger.info("Existing from saveApplicationPermits, User Id {}, Context id {}", userId,
        contextId);
  }

  @Override
  public void storeValidatorForStep(String userId, String contextId, Long projectId,
      String category, Integer activityId, Integer indicator) {

    logger.info("Entering into Store validatro for the step/sub stepper. User Id {}, Context Id {}",
        userId, contextId);
    List<ProjectActivity> projectActivities =
        projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId, activityId);
    ProjectActivity projectActivity = null;
    if (CollectionUtils.isEmpty(projectActivities)) {
      projectActivity = new ProjectActivity();
      projectActivity.setActivityStatusId(activityId);
      projectActivity.setProjectId(projectId);
      projectActivity.setStartDate(new Date());
      projectActivity.setCreateDate(new Date());
    } else {
      projectActivity = projectActivities.get(0);
      projectActivity.setModifiedById(userId);
      projectActivity.setModifiedDate(new Date());
    }
    projectActivity.setCreatedById(userId);
    if (1 == indicator) {
      projectActivity.setCompletionDate(new Date());
    } else {
      projectActivity.setCompletionDate(null);
    }
    projectActivityRepo.save(projectActivity);
    logger.info("Exiting from Store validator for the step/sub stepper. User Id {}, Context Id {}",
        userId, contextId);
  }

  @Transactional
  @Override
  public void updateExistingPermitAmendDetails(String userId, String contextId, Long projectId,
      List<ApplicationPermitDetail> existingPermits) {

    if (CollectionUtils.isEmpty(existingPermits)) {
      logger.error("There is no Existing permit details are passed for the "
          + "amendment request. User Id {}, Context Id {}", userId, contextId);
      throw new BadRequestException("NO_PERMIT_EXIST",
          "There is no Existing permit " + "details are passed for the amendment request",
          existingPermits);
    }
    existingPermits.forEach(permit -> {
      Date completionDate = null;
      if (StringUtils.hasLength(permit.getEstCompletionDate())) {
        try {
          completionDate = MM_DD_YYYY_FORMAT.parse(permit.getEstCompletionDate());
        } catch (ParseException e) {
          logger.error("Estimated Completion details are not in MM/dd/yyyy format "
              + "{}. User Id {} , Context Id {}", permit.getEstCompletionDate());
          throw new BadRequestException("INCORRECT_DETAILS_PASSED", "Incorrect details are passed",
              existingPermits);
        }
      }
      applicationRepo.updatePermitformSubmissionDetails(permit.getModExtReason(), completionDate,
          permit.getApplicationId(), permit.getEdbApplnId(), permit.getPermitTypeCode(),
          permit.getBatchId(), userId);
    });
  }

  @Override
  public List<ApplicationPermitDetail> retrievePermitDetails(String userId, String contextId,
      Long projectId, Long batchId) {
    logger.info("Retrieving the list of applications/permits for the input project Id {} "
        + "and batch Id {}. User Id {}, Context Id {}", projectId, batchId, userId, contextId);
    List<Application> applicationList =
        applicationRepo.findAllByBatchIdEdbAndProjectId(batchId, projectId);
    List<ApplicationPermitDetail> applicationPermits = new ArrayList<>();
    if (CollectionUtils.isEmpty(applicationList)) {
      return applicationPermits;
    }
    applicationList.forEach(application -> {
      ApplicationPermitDetail applicationPermit = new ApplicationPermitDetail();
      applicationPermit.setApplicationId(application.getApplicationId());
      applicationPermit.setBatchId(application.getBatchIdEdb());
      // applicationPermit.setModExtReason(application.getPermitModReason());
      applicationPermit.setPermitTypeCode(application.getPermitTypeCode());
      applicationPermit.setProgramId(application.getProgId());
      applicationPermit.setEstCompletionDate(application.getPermitExtendedDate());
      applicationPermit.setEdbApplnId(application.getEdbApplId());
      applicationPermits.add(applicationPermit);
    });
    return applicationPermits;
  }

  @Transactional(rollbackFor = {BadRequestException.class, ETrackPermitException.class})
  @Override
  public Object storeReviewedPermits(String userId, String contextId,
      Map<String, List<ReviewedPermit>> reviewedPermits) {

    reviewedPermits.keySet().forEach(permitCategory -> {
      if (permitCategory.equals("etrack-permits")) {
        List<ReviewedPermit> eTrackReviewedNewPermits = reviewedPermits.get(permitCategory);

        if (!CollectionUtils.isEmpty(eTrackReviewedNewPermits)) {
          Map<String, List<ReviewedPermit>> existingPermitsByBatchNumber = new HashMap<>();
          eTrackReviewedNewPermits.forEach(eTrackReviewedNewPermit -> {
            if (existingPermitsByBatchNumber.get(eTrackReviewedNewPermit.getBatchGroup()) == null) {
              List<ReviewedPermit> eTrackReviewedPermitsByBatchNumber = new ArrayList<>();
              eTrackReviewedPermitsByBatchNumber.add(eTrackReviewedNewPermit);
              existingPermitsByBatchNumber.put(eTrackReviewedNewPermit.getBatchGroup(),
                  eTrackReviewedPermitsByBatchNumber);
            } else {
              existingPermitsByBatchNumber.get(eTrackReviewedNewPermit.getBatchGroup())
                  .add(eTrackReviewedNewPermit);
            }
          });
          existingPermitsByBatchNumber.keySet().forEach(batchGroup -> {
            List<ReviewedPermit> eTrackReviewedNewPermitsAsBatch =
                existingPermitsByBatchNumber.get(batchGroup);
            Long applicationIdWithTrackingInd = 0L;
            boolean eTrackAlreadyAvailableInd = false;
            for (ReviewedPermit eTrackReviewedNewPermitAsBatch : eTrackReviewedNewPermitsAsBatch) {
              if (eTrackReviewedNewPermitAsBatch.getTrackingInd() == 1
                  && !eTrackAlreadyAvailableInd) {
                applicationIdWithTrackingInd = eTrackReviewedNewPermitAsBatch.getApplicationId();
              } else {
                eTrackReviewedNewPermitAsBatch.setTrackingInd(0);
              }
            }
            if (applicationIdWithTrackingInd.equals(0L)) {
              logger.info(
                  "No Tracking Indicator is preferred by the Staff. Setting the first one as Tracking application "
                      + "User Id {} , Context Id {}",
                  userId, contextId);
              eTrackReviewedNewPermitsAsBatch.get(0).setTrackingInd(1);
            }
            // Identify the batch Id and use the same for the entire batch.
            eTrackReviewedNewPermitsAsBatch.forEach(eTrackPermit -> {
              applicationRepo.updateReviewedETrackPermitsByApplicationId(userId,
                  eTrackPermit.getTransType(), eTrackPermit.getApplicationId(),
                  eTrackPermit.getTrackingInd(), batchGroup);
            });
          });
        }
      } else {
        List<ReviewedPermit> reviewedExistingPermits = reviewedPermits.get(permitCategory);
        if (!CollectionUtils.isEmpty(reviewedExistingPermits)) {

          Map<String, List<ReviewedPermit>> existingPermitsByPermitType = new HashMap<>();
          reviewedExistingPermits.forEach(reviewedExistingPermit -> {
            if (existingPermitsByPermitType
                .get(reviewedExistingPermit.getModifiedTransType()) == null) {
              List<ReviewedPermit> reviewedListByBatch = new ArrayList<>();
              reviewedListByBatch.add(reviewedExistingPermit);
              existingPermitsByPermitType.put(reviewedExistingPermit.getModifiedTransType(),
                  reviewedListByBatch);
            } else {
              existingPermitsByPermitType.get(reviewedExistingPermit.getModifiedTransType())
                  .add(reviewedExistingPermit);
            }
          });
          existingPermitsByPermitType.keySet().forEach(modifiedTransType -> {
            List<ReviewedPermit> reviewedPermitsGroupedByModifiedTransType =
                existingPermitsByPermitType.get(modifiedTransType);
            // create new batch id and update the details with the reviewed TransType.
            if (!modifiedTransType
                .equals(reviewedPermitsGroupedByModifiedTransType.get(0).getModifiedTransType())) {
              Set<Long> applicationIds = new HashSet<>();
              reviewedPermitsGroupedByModifiedTransType
                  .forEach(reviewedPermitGroupedByModifiedTransType -> {
                    applicationIds.add(reviewedPermitGroupedByModifiedTransType.getApplicationId());
                  });
              int updatedRecords = applicationRepo.updateReviewedPermitsByApplicationIds(userId,
                  modifiedTransType, applicationIds);

              if (updatedRecords != applicationIds.size()) {
                throw new BadRequestException("REQUESTED_NOT_MATCHING_WITH_UPDATE",
                    "One or more application record is not available or missing. "
                        + "Hence rolling back the complete transaction",
                    reviewedPermits);
              }
            }
          });
        }
      }
    });
    return reviewedPermits;
  }

  @Transactional
  @Override
  public void updateAmendedApplicationTransTypes(String userId, String contextId, Long projectId,
      ApplicationPermit applicationPermit) {

    logger.info("Entering into updateAmendedApplicationTransTypes, User Id {}, Context id {}",
        userId, contextId);

    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException("NO_PROJECT_FOUND",
          "There is no Project Found associated with the id " + projectId, applicationPermit);
    }
    List<ApplicationPermitDetail> dartPermits = applicationPermit.getDartPermits();
    List<Application> amendedApplicationPermitsList = new ArrayList<>();
    if (CollectionUtils.isEmpty(dartPermits)) {
      logger.error("There is no permits requested to persist, User Id {}, Context Id {}", userId,
          contextId);
      throw new BadRequestException("NO_PERMIT_REQ_TO_UPDATE",
          "There is no permits requested to update the Trans Type", dartPermits);
    }

    Set<Long> applicationIds = new HashSet<>();
    for (ApplicationPermitDetail submittedDartPermit : dartPermits) {
      applicationIds.add(submittedDartPermit.getApplicationId());
    }

    List<Application> applications =
        applicationRepo.findAllByIdAndProjectId(applicationIds, projectId);
    if (CollectionUtils.isEmpty(applications)) {
      throw new BadRequestException("NO_PERMITS_FOUND",
          "There is no Applications Found associated for the project id " + projectId,
          applicationPermit);
    }
    Map<Long, Application> existingApplicationMap = new HashMap<>();
    applications.forEach(application -> {
      existingApplicationMap.put(application.getApplicationId(), application);
    });

    for (ApplicationPermitDetail submittedDartPermit : dartPermits) {
      Application application = prepareAmendApplicationPermitDatasToPersist(userId, contextId,
          submittedDartPermit, existingApplicationMap.get(submittedDartPermit.getApplicationId()));
      application.setModifiedById(userId);
      application.setModifiedDate(new Date());
      amendedApplicationPermitsList.add(application);
    }
    applicationRepo.saveAll(amendedApplicationPermitsList);
    logger.info("Existing from updateAmendedApplicationTransTypes, User Id {}, Context id {}",
        userId, contextId);
  }

  /**
   * This method is used to save the Additional application for the existing project's permit which
   * is already processed by DART system.
   */
  @Override
  public void saveAdditionalApplicationPermit(final String userId, final String contextId,
      final String token, final String guid, final Long projectId,
      ApplicationPermit applicationPermit) {

    logger.info("Entering into saveAdditionalApplicationPermit. User Id {}, Context Id {}", userId,
        contextId);

    Application updatedApplication =
        saveAdditionalPermitApplicationInETrack(userId, contextId, projectId, applicationPermit);
    
    Long applicationId = updatedApplication.getApplicationId();
    logger.info("Additional permit is created successfully. "
        + "New Application Id {}, User Id {}, Context Id {}", applicationId, userId, contextId);

    try {
      logger.info(
          "Upload this additional permit to DART" + " Application Id {}, User Id {}, Context Id {}",
          applicationId, userId, contextId);
      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("contextId", contextId);
      headers.add("projectId", String.valueOf(projectId));
      headers.add("guid", guid);
      headers.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-dart-district/upload-addl-permit/" + applicationId).build()
          .toString();
      logger.info(
          "Making a call to eTrack-dart-district-service to invoke upload additional Permit to Dart procedure. "
              + "User Id {}, Context Id {}, Project Id {}",
          userId, contextId, projectId);
      ResponseEntity<Void> response = eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, Void.class);
      if (response.getStatusCode().equals(HttpStatus.IM_USED)) {
        throw new ETrackPermitException(response.getStatusCode(), "BUILT_PEMIT_ERR",
            "A permit has already been drafted. Please contact system administrator to proceed.");
      }
      logger.info("Uploaded this additional permit to DART Successfully "
          + " Application Id {}, User Id {}, Context Id {}", applicationId, userId, contextId);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      logger.error("Error while uploading the Additional application into DART. User Id {}, Context Id {}", userId, contextId, e);
      applicationRepo.deleteAdditionalPermitByApplicationId(applicationId);
      throw new ETrackPermitException(e.getStatusCode(), "ADDL_PERMIT_ERR", e.getResponseBodyAsString());
    } catch (ETrackPermitException e ) {
      applicationRepo.deleteAdditionalPermitByApplicationId(applicationId);
      throw e;
    } catch (Exception e) {
      logger.error(
          "Error while uploading the additional permit into Dart. "
              + "Application Id {}, User Id {}, Context Id {}",
          applicationId, userId, contextId, e);
      if (applicationId != null) {
        applicationRepo.deleteAdditionalPermitByApplicationId(applicationId);
      }
      throw new ETrackPermitException("ADDITIONAL_PERMIT_PERSIST_IN_DART_ERR",
          "Error while persisting the Additional permit.", e);
    }
    logger.info("Exiting from saveAdditionalApplicationPermit. User Id {}, Context Id {}", userId,
        contextId);
  }

  @Transactional
  public Application saveAdditionalPermitApplicationInETrack(final String userId,
      final String contextId, final Long projectId, final ApplicationPermit applicationPermit) {
    logger.info("Entering into saveAdditionalPermitApplicationInETrack. User Id {}, Context Id {}",
        userId, contextId);
    try {
      Project projectAvailability = projectRepo.findByProjectIdAndUploadToDart(projectId);
      
      if (projectAvailability == null) {
        throw new BadRequestException("PROJECT_NOT_IN_DART",
            "This project is not available in DART " + projectId, applicationPermit);
      }
      if (!CollectionUtils.isEmpty(applicationPermit.getDartPermits())
          || CollectionUtils.isEmpty(applicationPermit.getEtrackPermits())
          || applicationPermit.getEtrackPermits().size() > 1) {
        throw new BadRequestException("INCORRECT_ADDL_PERMIT_ERR",
            "Additional Application permit contains invalid details " + projectId,
            applicationPermit);
      }
      
//      findAnyDuplicatePermitSubmission(userId, contextId, projectId, applicationPermit);
      
      ApplicationPermitDetail additionalApplication = applicationPermit.getEtrackPermits().get(0);
      int count = applicationRepo.findApplicantAlreadySubmitted(additionalApplication.getPermitTypeCode(), projectId);
      if (count > 0) {
        throw new BadRequestException("PERMIT_ALREADY_EXIST",
            "This application is already applied for this project." + projectId,
            applicationPermit);
      }
      
      if (!"NEW".equals(additionalApplication.getTransType().toUpperCase())) {
        logger.info("Check if any active authorization permit is available "
            + "and user wants to apply one of those or not. User Id {}, Context Id {}", userId, contextId);
        
        if (CollectionUtils.isEmpty(applicationPermit.getActiveAuthorizations())) {
          throw new BadRequestException("INVALID_TRANS_TYPE_RQSTD",
              "Only Active Authorization permits are allowed to Modify/Transfer/Extend. "
              + "Other applications should be treated as NEW." + projectId,
              applicationPermit);
        }
        
        Map<String, String> permitTypeAndProgramId = new HashMap<>();
        applicationPermit.getActiveAuthorizations().forEach(activeAuthPermit -> {
          permitTypeAndProgramId.put(
              activeAuthPermit.getPermitTypeCode(), activeAuthPermit.getProgramId());
        });
        if (permitTypeAndProgramId.get(additionalApplication.getPermitTypeCode()) == null) {
          throw new BadRequestException("INVALID_TRANS_TYPE_RQSTD",
              "Please choose the permit from Active Authorization which are not applied for this project." + projectId,
              applicationPermit);
        }
        additionalApplication.setProgramId(
            permitTypeAndProgramId.get(additionalApplication.getPermitTypeCode()));
      }
      logger.info(
          "Check whether requested Trans Type {} is valid "
              + "for the permit type {}, User Id {}, Context Id {}",
          additionalApplication.getTransType(), additionalApplication.getPermitTypeCode(), userId,
          contextId);
      eTrackPermitDao.isValidPermitAndTransTypeMapping(userId, contextId,
          additionalApplication.getPermitTypeCode(), additionalApplication.getTransType());

      Application application = applicationRepo.findExistingApplicationByProjectIdAndBatchId(
          projectId, additionalApplication.getBatchId());
      if (application == null) {
        throw new BadRequestException("NO_APPLICATION_AVAIL_FOR_BATCH_ERR",
            "There is no tracked application available for this batch " + additionalApplication.getBatchId() 
            + " for this project " + projectId, applicationPermit);
      }
      Application newApplicationPermit = new Application();
      newApplicationPermit.setProjectId(projectId);
      newApplicationPermit.setPermitTypeCode(additionalApplication.getPermitTypeCode());
      newApplicationPermit.setTransTypeCode(additionalApplication.getTransType());
      newApplicationPermit.setUploadTransTypeCode(additionalApplication.getTransType());
      newApplicationPermit.setBatchIdEdb(application.getBatchIdEdb());
      newApplicationPermit.setCreatedById(userId);
      newApplicationPermit.setCreateDate(new Date());
      newApplicationPermit.setUserSelNewInd(application.getUserSelNewInd());
      newApplicationPermit.setUserSelExtInd(application.getUserSelExtInd());
      newApplicationPermit.setUserSelModInd(application.getUserSelModInd());
      newApplicationPermit.setUserSelRenInd(application.getUserSelRenInd());
      newApplicationPermit.setUserSelTransferInd(application.getUserSelTransferInd());
      newApplicationPermit.setPendingInd(application.getPendingInd());
      newApplicationPermit.setChgOriginalProjectInd(application.getChgOriginalProjectInd());
      newApplicationPermit.setProgId(additionalApplication.getProgramId());
      // newApplicationPermit.setProgId(application.getProgId());

      List<TransactionTypeRule> transactionTypeRuleList =
          transactionTypeRuleRepo.findTranstypeAndAssociateDetails(
              newApplicationPermit.getPermitTypeCode(), newApplicationPermit.getUserSelNewInd(),
              newApplicationPermit.getUserSelModInd(), newApplicationPermit.getUserSelExtInd(),
              newApplicationPermit.getUserSelTransferInd(), newApplicationPermit.getUserSelRenInd(),
              newApplicationPermit.getChgOriginalProjectInd());

      if (!CollectionUtils.isEmpty(transactionTypeRuleList)) {
        newApplicationPermit
            .setTransactionTypeRuleId(transactionTypeRuleList.get(0).getTransactionTypeRuleId());
      }
      return applicationRepo.save(newApplicationPermit);
    } catch (BadRequestException | ETrackPermitException bre) {
      throw bre;
    } catch (Exception e) {
      throw new ETrackPermitException("ADDITIONAL_PERMIT_PERSIST_ERROR",
          "Error while persisting the Additional permit. Error Detail " + e.getMessage(), e);
    }
  }
}
