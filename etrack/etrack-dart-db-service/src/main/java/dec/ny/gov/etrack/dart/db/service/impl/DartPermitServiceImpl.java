package dec.ny.gov.etrack.dart.db.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.dao.DashboardDetailDAO;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.ApplicationNarrativeDetail;
import dec.ny.gov.etrack.dart.db.entity.ApplicationPermitForm;
import dec.ny.gov.etrack.dart.db.entity.ContactAgent;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;
import dec.ny.gov.etrack.dart.db.entity.PermitType;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.ActiveAdditionalPermit;
import dec.ny.gov.etrack.dart.db.model.AdditionalPermitDetail;
import dec.ny.gov.etrack.dart.db.model.ApplicationAssignment;
import dec.ny.gov.etrack.dart.db.model.AvailTransType;
import dec.ny.gov.etrack.dart.db.model.BatchPermitDetail;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;
import dec.ny.gov.etrack.dart.db.model.PermitContact;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.repo.ContactRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitTypeRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.service.DartPermitService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Service
public class DartPermitServiceImpl implements DartPermitService {

  private static final Logger logger = LoggerFactory.getLogger(DartDbServiceImpl.class.getName());
  @Autowired
  private DartDbDAO dartDBDAO;
  @Autowired
  private ProjectRepo projectRepo;
  @Autowired
  private PermitTypeRepo permitTypeRepo;
  @Autowired
  private ContactRepo contactRepo;
  @Autowired
  private ProjectActivityRepo projectActivityRepo;
  @Autowired
  private PermitRepo permitRepo;
  @Autowired
  private FacilityRepo facilityRepo;
  @Autowired
  private ApplicationRepo applicationRepo;
  @Autowired
  private DashboardDetailDAO dashboardDetailDAO;
  @Autowired
  private TransformationService transformationService;
  @Autowired
  private RestTemplate eTrackOtherServiceRestTemplate;
  
  private static final String VALIDATED_IND = "validateInd";
  private static final String EMERGENCY_AUTH_IND = "emergencyInd";
  private static final String CONSTRUCTION_TYPE = "constrnType";
  private static final String EXTEND_TRANS_TYPE = "Extend";
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

  @Override
  public Map<String, Object> retrievePermitsAssignment(String userId, String contextId,
      Long projectId) {
    logger.info("Entering into retrieve the permit assignement "
        + "details for Project id {}. User Id {}, Context Id {}", projectId, userId, contextId);

    Map<String, Object> permitApplicationResult = new HashMap<>();
    Optional<Project> projectAvailablity = projectRepo.findById(projectId);
    if (!projectAvailablity.isPresent()) {
      throw new BadRequestException("INVALID_REQ", "Project is not available for the project Id ",
          projectId);
    }

    if (projectAvailablity.get().getEaInd() != null && projectAvailablity.get().getEaInd() == 1) {
      permitApplicationResult.put(EMERGENCY_AUTH_IND, "E");
    } else {
      permitApplicationResult.put(EMERGENCY_AUTH_IND, "N");
    }

    List<ApplicationPermitForm> applicationPermitFormList =
        dartDBDAO.retrievePermitApplicationForm(userId, contextId, projectId);

    Map<String, ApplicationAssignment> applicationAssignmentMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(applicationPermitFormList)) {
      List<ContactAgent> contactAgents = contactRepo.findAllContactsByAssociatedInd(projectId, 1);
      applicationPermitFormList.forEach(applicationPermitForm -> {
        if (applicationPermitForm.getPermitFormName().startsWith("JAF")) {
          if (applicationAssignmentMap.get("JAF") == null) {
            ApplicationAssignment applicationAssignment = new ApplicationAssignment();
            Set<String> jafForms = new HashSet<>();
            if ((applicationPermitForm.getPermitFormDesc() != null
                && !applicationPermitForm.getPermitFormDesc().contains("Joint Application Form"))) {
              jafForms.add(applicationPermitForm.getPermitFormDesc());
            }
            applicationAssignment.setJafForms(jafForms);
            applicationAssignment.setPermitFormId(applicationPermitForm.getPermitFormId());
            Set<Long> applicationIds = new HashSet<>();
            applicationIds.add(applicationPermitForm.getApplicationId());
            applicationAssignment.setApplicationIds(applicationIds);
            List<PermitContact> contacts = new ArrayList<>();
            for (ContactAgent contact : contactAgents) {
              PermitContact assignedContact = new PermitContact();
              assignedContact.setPublicId(contact.getPublicId());
              assignedContact.setRoleId(contact.getRoleId());
              assignedContact.setDisplayName(contact.getDisplayName());
              assignedContact.setEdbPublicId(contact.getEdbPublicId());
              if (applicationPermitForm.getContactAssignedId() != null
                  && applicationPermitForm.getContactAssignedId().equals(contact.getRoleId())) {
                assignedContact.setPermitAssignedInd("Y");
              } else {
                assignedContact.setPermitAssignedInd("N");
              }
              contacts.add(assignedContact);
            }
            applicationAssignment.setContacts(contacts);
            applicationAssignmentMap.put("JAF", applicationAssignment);
          } else {
            if ((applicationPermitForm.getPermitFormDesc() != null
                && !applicationPermitForm.getPermitFormDesc().contains("Joint Application Form"))) {
              applicationAssignmentMap.get("JAF").getJafForms()
                  .add(applicationPermitForm.getPermitFormDesc());
            }
            applicationAssignmentMap.get("JAF").getApplicationIds()
                .add(applicationPermitForm.getApplicationId());
          }
        } else {
          if (applicationAssignmentMap.get(applicationPermitForm.getPermitFormName()) == null) {
            ApplicationAssignment applicationAssignment = new ApplicationAssignment();
            applicationAssignment.setFormName(applicationPermitForm.getPermitFormDesc());
            applicationAssignment.setPermitFormId(applicationPermitForm.getPermitFormId());
            Set<Long> applicationIds = new HashSet<>();
            applicationIds.add(applicationPermitForm.getApplicationId());
            applicationAssignment.setApplicationIds(applicationIds);
            List<PermitContact> contacts = new ArrayList<>();
            for (ContactAgent contact : contactAgents) {
              PermitContact assignedContact = new PermitContact();
              assignedContact.setPublicId(contact.getPublicId());
              assignedContact.setRoleId(contact.getRoleId());
              assignedContact.setDisplayName(contact.getDisplayName());
              assignedContact.setEdbPublicId(contact.getEdbPublicId());
              if (applicationPermitForm.getContactAssignedId() != null
                  && applicationPermitForm.getContactAssignedId().equals(contact.getRoleId())) {
                assignedContact.setPermitAssignedInd("Y");
              } else {
                assignedContact.setPermitAssignedInd("N");
              }
              contacts.add(assignedContact);
            }
            applicationAssignment.setContacts(contacts);
            applicationAssignmentMap.put(applicationPermitForm.getPermitFormName(),
                applicationAssignment);

          } else {
            applicationAssignmentMap.get(applicationPermitForm.getPermitFormName())
                .getApplicationIds().add(applicationPermitForm.getApplicationId());
          }
        }
      });
    }
    logger.info(
        "Existing from retrieve the permit assignement details for Project id {}. User Id {}, Context Id {}",
        projectId, userId, contextId);
    List<Integer> activityList = projectActivityRepo.findProjectActivityStatusId(projectId,
        DartDBConstants.ASSIGN_CONTACT_VAL);
    if (CollectionUtils.isEmpty(activityList)) {
      permitApplicationResult.put(VALIDATED_IND, "N");
    } else {
      permitApplicationResult.put(VALIDATED_IND, "Y");
    }
    if (applicationAssignmentMap.get("JAF") != null) {
      Set<String> sortedSet = new LinkedHashSet<>();
      if (!CollectionUtils.isEmpty(applicationAssignmentMap.get("JAF").getJafForms())) {
        List<String> jafForms = new ArrayList<>(applicationAssignmentMap.get("JAF").getJafForms());
        Collections.sort(jafForms);
        jafForms.forEach(jafForm -> {
          sortedSet.add(jafForm);
        });
      } else {
        sortedSet.add("Joint Application Form (JAF)");
        applicationAssignmentMap.get("JAF").setJafForms(sortedSet);
      }
    }
    permitApplicationResult.put("permit-assign", applicationAssignmentMap);
    return permitApplicationResult;
  }

  @Override
  public Map<String, Object> retrieveAllPermitApplications(final String userId,
      final String contextId, final Long projectId, final boolean permitSummaryInd) {

    logger.info("Entering into retrieveAllPermitApplications. User Id {}, Context Id {}", userId,
        contextId);

    Map<String, Object> permitApplicationResult = new HashMap<>();
    List<ETrackPermit> eTrackPermits = permitRepo.findETrackPermits(projectId);

    Optional<Project> projectAvailablity = projectRepo.findById(projectId);

    if (!projectAvailablity.isPresent()) {
      throw new BadRequestException("PROJECT_NOT_AVAIL",
          "Project is not available for the project Id ", projectId);
    }

    permitApplicationResult.put(EMERGENCY_AUTH_IND, projectAvailablity.get().getEaInd());
    permitApplicationResult.put(CONSTRUCTION_TYPE, projectAvailablity.get().getConstrnType());

    Long edbDistrictId = null;
    Map<Integer, List<PermitApplication>> eTrackPermitApplications = new HashMap<>();
    List<ContactAgent> contactAgents = contactRepo.findAllContactsByAssociatedInd(projectId, 1);

    Map<String, Long> appliedDartPermits = new HashMap<>();

    if (!CollectionUtils.isEmpty(eTrackPermits)) {
      eTrackPermits.forEach(eTrackPermit -> {
        if (eTrackPermit.getEdbApplId() != null && eTrackPermit.getEdbApplId() > 0) {
          appliedDartPermits.put(eTrackPermit.getProgId(), eTrackPermit.getEdbApplId());
        }
      });
      edbDistrictId = eTrackPermits.get(0).getEdbDistrictId();
      // Process Etrack permits
      eTrackPermitApplications = transformationService.transformPermitApplicationData(userId,
          contextId, edbDistrictId, eTrackPermits, contactAgents, permitSummaryInd);
    }
    permitApplicationResult.put("etrack-permits", eTrackPermitApplications);

    if (edbDistrictId == null || edbDistrictId == 0) {
      dec.ny.gov.etrack.dart.db.entity.Facility facility = facilityRepo.findByProjectId(projectId);
      if (facility != null) {
        edbDistrictId = facility.getEdbDistrictId();
      }
    }

    if (edbDistrictId != null && edbDistrictId > 0) {
      logger.info(
          "Retrieving the Pending permits from Enterprise for the district id {}, User Id {}, Context Id {}",
          edbDistrictId, userId, contextId);

      Map<String, Object> existingAuthorizationsData =
          dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId);

      @SuppressWarnings("unchecked")
      List<DartPermit> dartModifyExtendAvailablePermits =
          (List<DartPermit>) existingAuthorizationsData.get(DartDBConstants.EXISTING_APPS_CURSOR);

      logger.info("Existing applications for the input district id {} is {}", edbDistrictId, dartModifyExtendAvailablePermits.size());      
      
      Map<String, Object> expiredPermitApplications = dartDBDAO
          .retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId);

      @SuppressWarnings("unchecked")
      List<DartPermit> dartExpiredPermits =
          (List<DartPermit>) expiredPermitApplications.get(DartDBConstants.EXPIRED_APPS_CURSOR);
      logger.info("Expired applications for the input district id {} is {}", edbDistrictId, dartExpiredPermits.size());
      
      if (CollectionUtils.isEmpty(dartModifyExtendAvailablePermits)) {
        dartModifyExtendAvailablePermits = new ArrayList<>();
      }
      
      Set<String> modifyExtensionEligiblePermitAppIds = new HashSet<>(); 
      if (!CollectionUtils.isEmpty(dartExpiredPermits)) {
        if (CollectionUtils.isEmpty(dartModifyExtendAvailablePermits)) {
          dartModifyExtendAvailablePermits = new ArrayList<>();
        } else {
          dartModifyExtendAvailablePermits.forEach(dartModifyExtendAvailablePermit -> {
            modifyExtensionEligiblePermitAppIds.add(dartModifyExtendAvailablePermit.getTrackedId());
          }); 
        }
        
        dartExpiredPermits.forEach(dartPermit -> {
          dartPermit.setTransType(dartPermit.getTransType()+ ":E");
        });
        
        for (DartPermit dartExpiredPermit : dartExpiredPermits) {
          if (!modifyExtensionEligiblePermitAppIds.contains(dartExpiredPermit.getTrackedId())) {
            dartModifyExtendAvailablePermits.add(dartExpiredPermit);
          }          
        }
      }
      
      List<DartPermit> dartModifyExtendEligiblePermits = new ArrayList<>();
      dartModifyExtendAvailablePermits.forEach(dartModifyExtendAvailablePermit -> {
        if (appliedDartPermits.get(dartModifyExtendAvailablePermit.getTrackedId()) == null) {
          dartModifyExtendEligiblePermits.add(dartModifyExtendAvailablePermit);
        }
      });
      // @SuppressWarnings("unchecked")
      // List<DartPermit> dartModifyExtendExtendedDate = (List<DartPermit>) existingAuthorizationsData
      // .get(DartDBConstants.EXISTING_APPS_EXTEND_DATE_CURSOR);
      //
      Map<Long, String> extendedDateMap = new HashMap<>();
      // dartModifyExtendExtendedDate.forEach(dartModifyExtendDateDetail -> {
      // extendedDateMap.put(dartModifyExtendDateDetail.getApplId(),
      // dartModifyExtendDateDetail.getExtendedDate());
      // });

      List<DartApplication> dartPendingTransferEligbilePermits =
          dashboardDetailDAO.retrieveDARTPendingApplications(null, contextId, edbDistrictId, null);

      Map<String, Object> eTrackDartPermitApplications = getDartPermitDetails(userId, contextId,
          dartModifyExtendEligiblePermits, dartPendingTransferEligbilePermits, projectId,
          edbDistrictId, contactAgents, permitSummaryInd, extendedDateMap);

      eTrackDartPermitApplications.forEach((key, value) -> {
        permitApplicationResult.put(key, value);
      });
//      eTrackDartPermitApplications.keySet().forEach(key -> {
//        permitApplicationResult.put(key, eTrackDartPermitApplications.get(key));
//      });
    } else {
      logger.info(
          "This is new facility created in eTrack. So, no pending permit application will be available in DART ");
      permitApplicationResult.put("dart-mod-extn", new ArrayList<>());
      permitApplicationResult.put("dart-pending-txr", new ArrayList<>());
    }

    if (permitSummaryInd) {
      List<Integer> activityList = projectActivityRepo.findProjectActivityStatusId(projectId,
          DartDBConstants.PERMIT_SUMMARY_VAL);
      if (CollectionUtils.isEmpty(activityList)) {
        permitApplicationResult.put(VALIDATED_IND, "N");
      } else {
        permitApplicationResult.put(VALIDATED_IND, "Y");
      }
    } else {
      List<Integer> activityList = projectActivityRepo.findProjectActivityStatusId(projectId,
          DartDBConstants.ASSIGN_CONTACT_VAL);
      if (CollectionUtils.isEmpty(activityList)) {
        permitApplicationResult.put(VALIDATED_IND, "N");
      } else {
        permitApplicationResult.put(VALIDATED_IND, "Y");
      }
    }
    logger.info("Exiting from retrieveAllPermitApplications . User Id {}, Context Id {}", userId,
        contextId);
    return permitApplicationResult;
  }

  @Override
  public Object retrieveAllPermitSummary(final String userId, final String contextId,
      final String token, final Long projectId) {

    logger.info("Entering into retrieveAllPermitSummary. User Id {}, Context Id {}", userId,
        contextId);

    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException("PROJECT_NOT_AVAILABLE",
          "There is no project associated with this project id", projectId);
    }

    Map<String, Object> permitApplicationSummaryResult = new HashMap<>();
    Map<String, List<PermitApplication>> permitApplicationResult = new HashMap<>();
    List<ETrackPermit> eTrackPermits = permitRepo.findETrackPermits(projectId);

    if (!CollectionUtils.isEmpty(eTrackPermits)) {
      Long edbDistrictId = eTrackPermits.get(0).getEdbDistrictId();

      Map<Long, List<AvailTransType>> availableTransTypes = null;
      Map<Long, String> narrativeDescMapping = new HashMap<>();
      if (edbDistrictId != null && edbDistrictId > 0) {
        boolean isAnyExistingPermitApplied = false;
        for (ETrackPermit eTrackPermit : eTrackPermits) {
          if (eTrackPermit.getEdbApplId() != null && eTrackPermit.getEdbApplId() > 0) {
            isAnyExistingPermitApplied = true;
            break;
          }
        }
        if (isAnyExistingPermitApplied) {
          availableTransTypes = processingDartExistingPermits(userId, contextId, edbDistrictId);
        }
        logger.info(
            "Collecting the  Application Permit description Narrative text . User Id {}, Context Id",
            userId, contextId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("userId", userId);
        headers.add("contextId", contextId);
        headers.add(HttpHeaders.AUTHORIZATION, token);
        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
        String uri = UriComponentsBuilder.newInstance()
            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
            .toString();
        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
        ResponseEntity<List<ApplicationNarrativeDetail>> response = eTrackOtherServiceRestTemplate
            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef);
        List<ApplicationNarrativeDetail> narrativeTextMappingDetails = response.getBody();
        if (!CollectionUtils.isEmpty(narrativeTextMappingDetails)) {
          narrativeTextMappingDetails.forEach(narrativeText -> {
            narrativeDescMapping.put(narrativeText.getApplId(), narrativeText.getApplNarrHtml());
          });
        }
      }
      List<ContactAgent> contactAgents = contactRepo.findAllContactsByAssociatedInd(projectId, 1);

      // Process eTrack permits
      transformationService.transformPermitSummaryDetails(userId, contextId, edbDistrictId,
          eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping,
          availableTransTypes);
      
      permitApplicationResult.forEach((key,value) -> {
        permitApplicationSummaryResult.put(key, value);
      });     
//      permitApplicationResult.keySet().forEach(key -> {
//        permitApplicationSummaryResult.put(key, permitApplicationResult.get(key));
//      });
    }
    List<Integer> activityList = projectActivityRepo.findProjectActivityStatusId(projectId,
        DartDBConstants.PERMIT_SUMMARY_VAL);

    permitApplicationSummaryResult.put(EMERGENCY_AUTH_IND, projectAvailability.get().getEaInd());
    permitApplicationSummaryResult.put(CONSTRUCTION_TYPE, projectAvailability.get().getConstrnType());
    if (projectAvailability.get().getReceivedDate() != null) {
      permitApplicationSummaryResult.put("receivedDate",
          dateFormat.format(projectAvailability.get().getReceivedDate()));
    }
    if (CollectionUtils.isEmpty(activityList)) {
      permitApplicationSummaryResult.put(VALIDATED_IND, "N");
    } else {
      permitApplicationSummaryResult.put(VALIDATED_IND, "Y");
    }
    return permitApplicationSummaryResult;
  }

  @Override
  public List<PermitApplication> retrievePermitModificationSummary(String userId, String contextId,
      Long projectId) {
    logger.info("Entering into retrievePermitModificationSummary. User Id {}, Context Id {}",
        userId, contextId);
    List<PermitApplication> permitApplicationResult = new ArrayList<>();
    List<ETrackPermit> eTrackPermits = permitRepo.findETrackModifiedPermits(projectId);
    if (!CollectionUtils.isEmpty(eTrackPermits)) {
      eTrackPermits.forEach(eTrackpermit -> {
        PermitApplication permitApplication = new PermitApplication();
        permitApplication.setPermitTypeCode(eTrackpermit.getPermitTypeCode());
        permitApplication.setPermitTypeDesc(eTrackpermit.getPermitTypeDesc());
        permitApplication.setTransType(eTrackpermit.getTransTypeCode());
        permitApplication.setEdbTransType(eTrackpermit.getEdbTransTypeCode());
        // permitApplication.setEdbDistrictId(edbDistrictId);
        permitApplication.setApplicationId(eTrackpermit.getApplicationId());
        permitApplication.setEdbApplicationId(eTrackpermit.getEdbApplId());
        permitApplication.setBatchId(eTrackpermit.getBatchIdEdb());
        permitApplication.setProgramId(eTrackpermit.getProgId());
        permitApplication.setProgramIdFormatted(eTrackpermit.getProgIdFormatted());
        if (eTrackpermit.getChgOriginalProjectInd() != null
            && eTrackpermit.getChgOriginalProjectInd().equals(1)) {
          permitApplication.setModQuestionAnswer("Y");
        } else {
          permitApplication.setModQuestionAnswer("N");
        }
        permitApplicationResult.add(permitApplication);
      });
    }
    return permitApplicationResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public AdditionalPermitDetail retrieveAvailablePermitsAddAsAdditional(final String userId,
      final String contextId, final Long projectId) {
    logger.info("Entering into retrieveAvailablePermitsAddAsAdditional. User Id {}, Context Id {}",
        userId, contextId);
    
    
    Map<String, Object> detailsFromEnterprise = dartDBDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId);
    List<Application> applicationList = (List<Application>) detailsFromEnterprise.get(DartDBConstants.APPLICATION_CURSOR);
    
    if (CollectionUtils.isEmpty(applicationList)) {
      throw new BadRequestException("APPLN_NOT_AVAIL",
          "There is no application available in enterprise for the input project Id " + projectId, projectId);
    }
    Set<String> appliedPermits = new HashSet<>();
    applicationList.forEach(application -> {
//      BatchPermitDetail batchDetail = new BatchPermitDetail();
//      batchDetail.setBatchId(String.valueOf(application.getBatchIdEdb()));
//      batchDetail.setTransType(application.getTransTypeCode());
//      batches.add(batchDetail);)
      if (StringUtils.hasLength(application.getRelatedRegPermit()) 
          && !application.getRelatedRegPermit().startsWith("00-")) {
        appliedPermits.add(application.getRelatedRegPermit());
      } else {
        appliedPermits.add(application.getPermitTypeCode());
      }
    });
    
    List<PermitType> availablePermitTypesToApply =
        permitTypeRepo.findEligiblePermitTypesToAddAdditional(appliedPermits);
    AdditionalPermitDetail additionalPermitDetail = new AdditionalPermitDetail();
    List<dec.ny.gov.etrack.dart.db.model.PermitType> permitTypes = new ArrayList<>();
    if (!CollectionUtils.isEmpty(availablePermitTypesToApply)) {
      availablePermitTypesToApply.forEach(availablePermitType -> {
        dec.ny.gov.etrack.dart.db.model.PermitType additionalPermitType =
            new dec.ny.gov.etrack.dart.db.model.PermitType();
        additionalPermitType.setPermitType(availablePermitType.getPermitTypeCode());
        additionalPermitType.setPermitTypeDesc(availablePermitType.getPermitTypeDesc());
        permitTypes.add(additionalPermitType);
      });
    }
    additionalPermitDetail.setPermitTypes(permitTypes);
    List<String> batchIdAndTransTypeList = applicationRepo.findBatchIdAndTransType(projectId);
    List<BatchPermitDetail> batches = new ArrayList<>();
    if (!CollectionUtils.isEmpty(batchIdAndTransTypeList)) {
      batchIdAndTransTypeList.forEach(batchIdAndTransType -> {
        BatchPermitDetail batchIdTransTypeMap = new BatchPermitDetail();
        String[] batchIdTransTYpeStr = batchIdAndTransType.split(",");
        batchIdTransTypeMap.setBatchId(batchIdTransTYpeStr[0]);
        batchIdTransTypeMap.setTransType(batchIdTransTYpeStr[1]);
        batches.add(batchIdTransTypeMap);
      });
    }
    additionalPermitDetail.setBatches(batches);
    dec.ny.gov.etrack.dart.db.entity.Facility facility = facilityRepo.findByProjectId(projectId);

    Map<String, Object> existingApplicationsData =
        dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId,
            facility.getEdbDistrictId());
    List<DartPermit> activeAuthorizations =
        (List<DartPermit>) existingApplicationsData.get(DartDBConstants.EXISTING_APPS_CURSOR);

    List<Long> edbApplicationIds =
        applicationRepo.findAllEnterpriseApplicationsAppliedInETrack(projectId);
    if (!CollectionUtils.isEmpty(activeAuthorizations)) {
      List<ActiveAdditionalPermit> eligibleActivePermitsToAddAdditional = new ArrayList<>();
      activeAuthorizations.forEach(activePermit -> {
        if (!edbApplicationIds.contains(activePermit.getApplId())) {
          ActiveAdditionalPermit activeAdditionalPermit = new ActiveAdditionalPermit();
          activeAdditionalPermit.setEdbApplicationId(activePermit.getApplId());
          activeAdditionalPermit.setBatchId(activePermit.getBatchId());
          activeAdditionalPermit.setPermitTypeCode(activePermit.getPermitType());
          activeAdditionalPermit.setProgramId(activePermit.getTrackedId());
          eligibleActivePermitsToAddAdditional.add(activeAdditionalPermit);
        }
      });
      additionalPermitDetail.setActiveAuthorizations(eligibleActivePermitsToAddAdditional);
    } else {
      additionalPermitDetail.setActiveAuthorizations(new ArrayList<>());
    }
    return additionalPermitDetail;
  }

  private Map<String, Object> getDartPermitDetails(final String userId, final String contextId,
      List<DartPermit> dartModifyExtendEligiblePermits,
      List<DartApplication> dartPendingTransferEligbilePermits, final Long projectId,
      final Long edbDistrictId, List<ContactAgent> contactAgents, final boolean permitSummaryInd,
      final Map<Long, String> extendedDateDetailMap) {

    Map<String, Object> eTrackDartPermitApplications = new HashMap<>();

    logger.info("Retrieve the DART pending permit types details {}. User Id {}, Context Id {}",
        dartModifyExtendEligiblePermits, userId, contextId);

    Set<String> permitTypes = new HashSet<>();
    Map<Long, DartPermit> dartModificationExtenstionPermitApps = new HashMap<>();
    Map<Long, DartApplication> dartPendingTransferEligiblePermitApps = new HashMap<>();

    dartModifyExtendEligiblePermits.forEach(dartModExtendPermit -> {
      permitTypes.add(dartModExtendPermit.getPermitType());
      dartModificationExtenstionPermitApps.put(dartModExtendPermit.getApplId(), dartModExtendPermit);
    });
    
    dartPendingTransferEligbilePermits.forEach(dartPendingPermit -> {
      permitTypes.add(dartPendingPermit.getPermitType());
      dartPendingTransferEligiblePermitApps.put(dartPendingPermit.getApplId(), dartPendingPermit);
    });
    
    List<ETrackPermit> dartModifyExtendAssignedPermitsInETrack = permitRepo.findDartAssignedPermits(
        projectId, permitTypes, dartModificationExtenstionPermitApps.keySet());
    Map<Long, ETrackPermit> dartModExtAssignedPermitsMap = new HashMap<>();
    dartModifyExtendAssignedPermitsInETrack.forEach(modExtendAssignedInETrack -> {
      dartModExtAssignedPermitsMap.put(modExtendAssignedInETrack.getEdbApplId(),
          modExtendAssignedInETrack);
    });
    Map<Long, ETrackPermit> dartTransferAssignedPermitsMap = new HashMap<>();
    logger.info(
        "Extended date details for each Dart Permit Application. " + "{} User Id {}, Context Id {}",
        extendedDateDetailMap, userId, contextId);

    eTrackDartPermitApplications = transformationService.transformDartPermitApplicationData(userId,
        contextId, edbDistrictId, dartModificationExtenstionPermitApps,
        dartModExtAssignedPermitsMap, dartPendingTransferEligiblePermitApps,
        dartTransferAssignedPermitsMap, contactAgents, permitSummaryInd, extendedDateDetailMap);

    return eTrackDartPermitApplications;

  }


  /**
   * Process the Existing Permits and Expired Permits.
   * 
   * @param userId
   * @param contextId
   * @param projectId
   */
  private Map<Long, List<AvailTransType>> processingDartExistingPermits(final String userId,
      final String contextId, final Long edbDistrictId) {

    Map<String, Object> existingAuthorizationsData =
        dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId);

    @SuppressWarnings("unchecked")
    List<DartPermit> dartModifyExtendEligiblePermits =
        (List<DartPermit>) existingAuthorizationsData.get(DartDBConstants.EXISTING_APPS_CURSOR);


    Map<String, Object> expiredPermitsData = dartDBDAO
        .retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId);

    @SuppressWarnings("unchecked")
    List<DartPermit> dartExpiredPermits =
        (List<DartPermit>) expiredPermitsData.get(DartDBConstants.EXISTING_APPS_CURSOR);

    if (!CollectionUtils.isEmpty(dartModifyExtendEligiblePermits)) {
      if (!CollectionUtils.isEmpty(dartExpiredPermits)) {
        dartModifyExtendEligiblePermits.addAll(dartExpiredPermits);
      }
    }
    Map<Long, DartPermit> dartModificationExtenstionPermitApps = new HashMap<>();

    dartModifyExtendEligiblePermits.forEach(dartModExtendPermit -> {
      dartModificationExtenstionPermitApps.put(dartModExtendPermit.getApplId(), dartModExtendPermit);
    });

    List<DartPermit> existingExpiredPermits = new ArrayList<>();
    List<DartPermit> existingRenewableGeneralPermits = new ArrayList<>();
    List<DartPermit> existingNonRenewableGeneralPermits = new ArrayList<>();
    List<DartPermit> existingRenewableRegularPermits = new ArrayList<>();
    List<DartPermit> existingNonRenewableRegularPermits = new ArrayList<>();

    for (DartPermit dartModExtendPermit : dartModificationExtenstionPermitApps.values()) {
      if (StringUtils.hasLength(dartModExtendPermit.getRenewedInd())
          && "1".equals(dartModExtendPermit.getRenewedInd())) {
        if ("REI".equals(dartModExtendPermit.getTransType())) {
          existingExpiredPermits.add(dartModExtendPermit);
        } else if (dartModExtendPermit.getGpAuthId() != null && dartModExtendPermit.getGpAuthId() > 0) {
          existingRenewableGeneralPermits.add(dartModExtendPermit);
        } else {
          existingRenewableRegularPermits.add(dartModExtendPermit);
        }
      } else {
        if (dartModExtendPermit.getGpAuthId() != null && dartModExtendPermit.getGpAuthId() > 0) {
          existingNonRenewableGeneralPermits.add(dartModExtendPermit);
        } else if ("REI".equals(dartModExtendPermit.getTransType())) {
          existingExpiredPermits.add(dartModExtendPermit);
        } else {
          existingNonRenewableRegularPermits.add(dartModExtendPermit);
        }
      }
    }
    
    Map<String, List<PermitApplication>> permitModExtendApplications = new HashMap<>();
    AvailTransType modifyRequestTransType = new AvailTransType("Modify", "MOD");
    AvailTransType transRequestTransType = new AvailTransType("Transfer", "XFER");
    AvailTransType renewalRequestTransType = new AvailTransType("Renew", "REN");
    SimpleDateFormat mmDDYYYFormat = new SimpleDateFormat("MM/dd/yyyy");

    existingExpiredPermits.forEach(existingExpiredPermit -> {
      PermitApplication permitApplication = new PermitApplication();
      permitApplication.setEdbApplicationId(existingExpiredPermit.getApplId());
      permitApplication.setPermitTypeCode(existingExpiredPermit.getPermitType());
      permitApplication.setPermitTypeDesc(existingExpiredPermit.getPermitDesc());
      if (existingExpiredPermit.getNonRenewableEffDate() != null
          && existingExpiredPermit.getMaxPermitTerm() != null) {
        permitApplication.setExtendedDate(
            mmDDYYYFormat.format(DateUtils.addYears(existingExpiredPermit.getNonRenewableEffDate(),
                existingExpiredPermit.getMaxPermitTerm())));
      }
      String expiredBatchId = String.valueOf(existingExpiredPermit.getBatchId()).concat("000E");
      // String expiredBatchId = "X";
      if (permitModExtendApplications.get(expiredBatchId) == null) {
        List<PermitApplication> modExtendEligiblePermitApplicationsList = new ArrayList<>();
        List<AvailTransType> applicableTransTypes = new ArrayList<>();
        AvailTransType extensionTransType = null;
        if (StringUtils.hasLength(permitApplication.getExtendedDate())) {
          extensionTransType =
              new AvailTransType("Extend (" + permitApplication.getExtendedDate() + ")", "EXT");
        } else {
          extensionTransType = new AvailTransType(EXTEND_TRANS_TYPE, "EXT");
        }
        applicableTransTypes.add(extensionTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        permitApplication.setCalculatedBatchIdForProcess(expiredBatchId);
        modExtendEligiblePermitApplicationsList.add(permitApplication);
        permitModExtendApplications.put(expiredBatchId, modExtendEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(expiredBatchId);
        permitModExtendApplications.get(expiredBatchId).add(permitApplication);
      }
    });

    existingRenewableGeneralPermits.forEach(existingRenewableGeneralPermit -> {
      String existingGeneralPermitBatchId = null;
      if ("GP015001".equals(existingRenewableGeneralPermit.getGpPermitType())) {
        logger.info("Categorize this as Openrating Permit as this cannot be renewed. "
            + "Have to be grpuped Separately. User Id {}, Context Id {}", userId, contextId);
        existingGeneralPermitBatchId =
            String.valueOf(existingRenewableGeneralPermit.getBatchId()).concat("000RGOP");
      } else {
        existingGeneralPermitBatchId =
            String.valueOf(existingRenewableGeneralPermit.getBatchId()).concat("000RG");
      }
      PermitApplication permitApplication = new PermitApplication();
      permitApplication.setEdbApplicationId(existingRenewableGeneralPermit.getApplId());
      permitApplication
          .setPermitTypeCode(existingRenewableGeneralPermit.getGpPermitTypeFormatted());
      permitApplication.setPermitTypeDesc(existingRenewableGeneralPermit.getGpPermitDesc());
      permitApplication.setExtendedDate(existingRenewableGeneralPermit.getGpExtendedDate());
      List<AvailTransType> applicableTransTypes = null;
      if (permitModExtendApplications.get(existingGeneralPermitBatchId) == null) {
        List<PermitApplication> modExtendEligiblePermitApplicationsList = new ArrayList<>();
        applicableTransTypes = new ArrayList<>();
        if (existingGeneralPermitBatchId.endsWith("RGOP")) {
          applicableTransTypes.add(modifyRequestTransType);
          applicableTransTypes.add(transRequestTransType);
        } else {
          applicableTransTypes.add(modifyRequestTransType);
          applicableTransTypes.add(renewalRequestTransType);
          applicableTransTypes.add(transRequestTransType);
        }
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtendEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingGeneralPermitBatchId);
        permitModExtendApplications.put(existingGeneralPermitBatchId,
            modExtendEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingGeneralPermitBatchId);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        permitModExtendApplications.get(existingGeneralPermitBatchId).add(permitApplication);
      }
    });

    existingNonRenewableGeneralPermits.forEach(existingNonRenewableGeneralPermit -> {
      // String existingNonRenewalGeneralPermitBatchId = "R";
      String existingNonRenewalGeneralPermitBatchId =
          String.valueOf(existingNonRenewableGeneralPermit.getBatchId()).concat("000NRG");
      PermitApplication permitApplication = new PermitApplication();
      permitApplication.setEdbApplicationId(existingNonRenewableGeneralPermit.getApplId());
      permitApplication
          .setPermitTypeCode(existingNonRenewableGeneralPermit.getGpPermitTypeFormatted());
      permitApplication.setPermitTypeDesc(existingNonRenewableGeneralPermit.getGpPermitDesc());
      permitApplication.setExtendedDate(existingNonRenewableGeneralPermit.getGpExtendedDate());
      List<AvailTransType> applicableTransTypes = null;
      if (permitModExtendApplications.get(existingNonRenewalGeneralPermitBatchId) == null) {
        List<PermitApplication> modExtendEligiblePermitApplicationsList = new ArrayList<>();
        applicableTransTypes = new ArrayList<>();
        AvailTransType extensionTransType = null;
        if (StringUtils.hasLength(permitApplication.getExtendedDate())) {
          extensionTransType =
              new AvailTransType("Extend (" + permitApplication.getExtendedDate() + ")", "EXT");
        } else {
          extensionTransType = new AvailTransType(EXTEND_TRANS_TYPE, "EXT");
        }
        applicableTransTypes.add(extensionTransType);
        applicableTransTypes.add(modifyRequestTransType);
        applicableTransTypes.add(transRequestTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtendEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalGeneralPermitBatchId);
        permitModExtendApplications.put(existingNonRenewalGeneralPermitBatchId,
            modExtendEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalGeneralPermitBatchId);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        permitModExtendApplications.get(existingNonRenewalGeneralPermitBatchId)
            .add(permitApplication);
      }
    });

    existingRenewableRegularPermits.forEach(existingRenewableRegularPermit -> {
      // String existingRenewableRegularPermitBatchId = "L";
      String existingRenewableRegularPermitBatchId =
          String.valueOf(existingRenewableRegularPermit.getBatchId()).concat("000RP");
      PermitApplication permitApplication = new PermitApplication();
      permitApplication.setEdbApplicationId(existingRenewableRegularPermit.getApplId());
      permitApplication.setPermitTypeCode(existingRenewableRegularPermit.getPermitType());
      permitApplication.setPermitTypeDesc(existingRenewableRegularPermit.getPermitDesc());
      List<AvailTransType> applicableTransTypes = null;
      if (permitModExtendApplications.get(existingRenewableRegularPermitBatchId) == null) {
        List<PermitApplication> modExtendEligiblePermitApplicationsList = new ArrayList<>();
        applicableTransTypes = new ArrayList<>();
        applicableTransTypes.add(modifyRequestTransType);
        applicableTransTypes.add(renewalRequestTransType);
        applicableTransTypes.add(transRequestTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtendEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingRenewableRegularPermitBatchId);
        permitModExtendApplications.put(existingRenewableRegularPermitBatchId,
            modExtendEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingRenewableRegularPermitBatchId);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        permitModExtendApplications.get(existingRenewableRegularPermitBatchId).add(permitApplication);
      }
    });

    existingNonRenewableRegularPermits.forEach(existingNonRenewableRegularPermit -> {
      // String existingNonRenewalRegularPermitBatchId = "N";
      String existingNonRenewalRegularPermitBatchId =
          String.valueOf(existingNonRenewableRegularPermit.getBatchId()).concat("000NRP");
      PermitApplication permitApplication = new PermitApplication();
      permitApplication.setEdbApplicationId(existingNonRenewableRegularPermit.getApplId());
      permitApplication.setPermitTypeCode(existingNonRenewableRegularPermit.getPermitType());
      permitApplication.setPermitTypeDesc(existingNonRenewableRegularPermit.getPermitDesc());
      if (existingNonRenewableRegularPermit.getNonRenewableEffDate() != null
          && existingNonRenewableRegularPermit.getMaxPermitTerm() != null) {
        permitApplication.setExtendedDate(mmDDYYYFormat
            .format(DateUtils.addYears(existingNonRenewableRegularPermit.getNonRenewableEffDate(),
                existingNonRenewableRegularPermit.getMaxPermitTerm())));
      }
      List<AvailTransType> applicableTransTypes = null;
      if (permitModExtendApplications.get(existingNonRenewalRegularPermitBatchId) == null) {
        List<PermitApplication> modExtendEligiblePermitApplicationsList = new ArrayList<>();
        applicableTransTypes = new ArrayList<>();
        AvailTransType extensionTransType = null;
        if (StringUtils.hasLength(permitApplication.getExtendedDate())) {
          extensionTransType =
              new AvailTransType("Extend (" + permitApplication.getExtendedDate() + ")", "EXT");
        } else {
          extensionTransType = new AvailTransType(EXTEND_TRANS_TYPE, "EXT");
        }
        applicableTransTypes.add(extensionTransType);
        applicableTransTypes.add(modifyRequestTransType);
        applicableTransTypes.add(transRequestTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtendEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalRegularPermitBatchId);
        permitModExtendApplications.put(existingNonRenewalRegularPermitBatchId,
            modExtendEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalRegularPermitBatchId);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        permitModExtendApplications.get(existingNonRenewalRegularPermitBatchId)
            .add(permitApplication);
      }
    });

    Map<Long, List<AvailTransType>> permitApplicationResult = new HashMap<>();
    permitModExtendApplications.keySet().forEach(key -> {
      permitModExtendApplications.get(key).forEach(existingPermit -> {
        permitApplicationResult.put(existingPermit.getEdbApplicationId(),
            existingPermit.getAvailableTransTypes());
      });
    });
    return permitApplicationResult;
  }
}
