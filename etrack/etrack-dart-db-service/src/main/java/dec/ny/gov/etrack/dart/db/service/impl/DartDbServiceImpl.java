package dec.ny.gov.etrack.dart.db.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.dao.DashboardDetailDAO;
import dec.ny.gov.etrack.dart.db.dao.SupportDocumentDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.County;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartInCompleteMilestone;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.DartSuspensionMilestone;
import dec.ny.gov.etrack.dart.db.entity.DocumentReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;
import dec.ny.gov.etrack.dart.db.entity.FacilityBIN;
import dec.ny.gov.etrack.dart.db.entity.FacilityLRPDetail;
import dec.ny.gov.etrack.dart.db.entity.GIInquiryAlert;
import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;
import dec.ny.gov.etrack.dart.db.entity.LitigationHold;
import dec.ny.gov.etrack.dart.db.entity.LitigationHoldHistory;
import dec.ny.gov.etrack.dart.db.entity.Municipality;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectAlert;
import dec.ny.gov.etrack.dart.db.entity.ProjectFoilStatusDetail;
import dec.ny.gov.etrack.dart.db.entity.ProjectNote;
import dec.ny.gov.etrack.dart.db.entity.PublicAndFacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.PublicSummary;
import dec.ny.gov.etrack.dart.db.entity.ReviewDocument;
import dec.ny.gov.etrack.dart.db.entity.SignedApplicant;
import dec.ny.gov.etrack.dart.db.entity.SupportDocumentEntity;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Alert;
import dec.ny.gov.etrack.dart.db.model.BatchDetail;
import dec.ny.gov.etrack.dart.db.model.BridgeIdNumber;
import dec.ny.gov.etrack.dart.db.model.CurrentDartStatus;
import dec.ny.gov.etrack.dart.db.model.CurrentStatus;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.Document;
import dec.ny.gov.etrack.dart.db.model.Facility;
import dec.ny.gov.etrack.dart.db.model.FacilityLRP;
import dec.ny.gov.etrack.dart.db.model.Invoice;
import dec.ny.gov.etrack.dart.db.model.LitigationRequest;
import dec.ny.gov.etrack.dart.db.model.MileStoneStatus;
import dec.ny.gov.etrack.dart.db.model.Milestone;
import dec.ny.gov.etrack.dart.db.model.MilestoneDesc;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;
import dec.ny.gov.etrack.dart.db.model.ProgramApplication;
import dec.ny.gov.etrack.dart.db.model.ProgramDistrict;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.model.ProjectNoteView;
import dec.ny.gov.etrack.dart.db.model.ProjectRejectDetail;
import dec.ny.gov.etrack.dart.db.model.ProjectSummary;
import dec.ny.gov.etrack.dart.db.model.RegionUserEntity;
import dec.ny.gov.etrack.dart.db.model.ReviewerDocumentDetail;
import dec.ny.gov.etrack.dart.db.model.SupportDocument;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.repo.CountyRepo;
import dec.ny.gov.etrack.dart.db.repo.DartInCompleteMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.DartMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.DartSuspendedMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.DocumentReviewRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityBINRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.repo.FoilRequestRepo;
import dec.ny.gov.etrack.dart.db.repo.GIInquiryAlertRepo;
import dec.ny.gov.etrack.dart.db.repo.InvoiceRepo;
import dec.ny.gov.etrack.dart.db.repo.LitigationHoldRequestHistoryRepo;
import dec.ny.gov.etrack.dart.db.repo.LitigationHoldRequestRepo;
import dec.ny.gov.etrack.dart.db.repo.MunicipalityRepo;
import dec.ny.gov.etrack.dart.db.repo.PendingAppRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.repo.ReviewDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.SignedApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.UserAssignmentRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@Service
public class DartDbServiceImpl implements DartDbService {

  @Autowired
  private DartDbDAO dartDBDAO;

  @Autowired
  private TransformationService transformationService;

  @Autowired
  private ProjectRepo projectRepo;

  @Autowired
  private FacilityBINRepo facilityBinRepo;

  @Autowired
  private PendingAppRepo pendingAppRepo;

  @Autowired
  private PermitRepo permitRepo;

  @Autowired
  private FacilityRepo facilityRepo;

  @Autowired
  private SignedApplicantRepo signedApplicantRepo;

  @Autowired
  private ProjectNoteRepo projectNoteRepo;

  @Autowired
  private ApplicationRepo applicationRepo;

  @Autowired
  private SupportDocumentRepo supportDocumentRepo;

  @Autowired
  private InvoiceRepo invoiceRepo;

  @Autowired
  private DashboardDetailDAO dashboardDetailDAO;

  @Autowired
  private UserAssignmentRepo userAssignmentRepo;

  @Autowired
  private ProjectAlertRepo projectAlertRepo;

  @Autowired
  private ReviewDocumentRepo reviewDocumentRepo;

  @Autowired
  private DocumentReviewRepo documentReviewRepo;

  @Autowired
  private SupportDocumentDAO supportDocumentDao;

  @Autowired
  private LitigationHoldRequestRepo litigationRequestRepo;

  @Autowired
  private LitigationHoldRequestHistoryRepo litigationRequestHistoryRepo;

  @Autowired
  private FoilRequestRepo foilRequestRepo;
  @Autowired
  private MunicipalityRepo municipalityRepo;
  @Autowired
  private CountyRepo countyRepo;
  @Autowired
  private ProjectActivityRepo projectActivityRepo;
  @Autowired
  private RestTemplate eTrackOtherServiceRestTemplate;
  @Autowired
  private DartMilestoneRepo dartMilestoneRepo;
  @Autowired
  private DartDBServiceUtility dartDBServiceUtility;
  @Autowired
  private GIInquiryAlertRepo giInquiryAlertRepo;
  @Autowired
  private DartSuspendedMilestoneRepo dartSuspendedMilestoneRepo;
  @Autowired
  private DartInCompleteMilestoneRepo dartInCompleteMilestoneRepo;
  
  
  private static final Logger logger = LoggerFactory.getLogger(DartDbServiceImpl.class.getName());
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
  private static final String PROJ_NOT_AVAIL_ERR_CODE = "NO_PROJECT_AVAIL";
  private static Map<String, String> non_releasable_status = new HashMap<>();

  static {
    non_releasable_status.put("NODET", "No Determination");
    non_releasable_status.put("NOREL", "Non - Releasable");
    non_releasable_status.put("REL", "Releasable");
  }

  @Transactional
  @Override
  public ProjectInfo getProjectInformation(String userId, String contextId, Long projectId) {
    logger.info(
        "Project details received from database for the project Id {} User Id {} Context Id {}",
        projectId, userId, contextId);
    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      logger.error(
          "There is no project information available "
              + "for the input project Id {} , User Id: {} , Context Id {}",
          projectId, userId, contextId);
      throw new NoDataFoundException("NO_PROJECT_AVAIL",
          "There is no project information available for the inout data project id " + projectId);
    }
    List<FacilityBIN> bridgeIdNumbers = facilityBinRepo.findByProjectId(projectId);
    List<Integer> activityList =
        projectActivityRepo.findProjectActivityStatusId(projectId, DartDBConstants.SIGNATURE);

    logger.info("Retrieve the Bridge Id number. User Id {}, Context Id {}", userId, contextId);
    ProjectInfo projectInfo = transformationService.transformProjectEntity(userId, contextId,
        projectAvailability.get(), bridgeIdNumbers);

    List<String> xtraIds = projectRepo.findAllProgramXTRAIDSByProjectId(projectId);
    if (!CollectionUtils.isEmpty(xtraIds)) {
      List<ProgramApplication> programApplications = new ArrayList<>();
      xtraIds.forEach(xtraId -> {
        String[] xtraIdSplitted = xtraId.split(",");
        ProgramApplication programApplication = new ProgramApplication();
        programApplication.setProgramApplicationCode(xtraIdSplitted[0]);
        programApplication.setProgramApplicationIdentifier(xtraIdSplitted[1]);
        if (xtraIdSplitted.length == 3) {
          programApplication.setEdbProgramApplicationIdentifier(xtraIdSplitted[2]);
        }
        programApplications.add(programApplication);
      });
      projectInfo.setXtraIds(programApplications);
    }

    List<String> programIds = projectRepo.findAllProgramProgramIdsByProjectId(projectId);
    if (!CollectionUtils.isEmpty(programIds)) {
      List<ProgramDistrict> programDistrictIdentifiers = new ArrayList<>();
      programIds.forEach(programId -> {
        String[] programIdSplitted = programId.split(",");
        ProgramDistrict programDistrictIdentifier = new ProgramDistrict();
        programDistrictIdentifier.setProgramDistrictCode(programIdSplitted[0]);
        programDistrictIdentifier.setProgramDistrictIdentifier(programIdSplitted[1]);
        if (programIdSplitted.length == 3) {
          programDistrictIdentifier.setEdbProgramDistrictIdentifier(programIdSplitted[2]);
        }
        programDistrictIdentifiers.add(programDistrictIdentifier);
      });
      projectInfo.setProgramIds(programDistrictIdentifiers);
    }
    projectInfo.setSplAttnCodes(projectRepo.findAllProjectSpecialAttnByProjectId(projectId));
    activityList =
        projectActivityRepo.findProjectActivityStatusId(projectId, DartDBConstants.PROJ_DESC_VAL);
    if (CollectionUtils.isEmpty(activityList)) {
      projectInfo.setValidatedInd("N");
    } else {
      projectInfo.setValidatedInd("Y");
    }
    return projectInfo;
  }


  @Override
  public List<BridgeIdNumber> getFacilityBins(String userId, String contextId, Long projectId) {
    logger.info("Entering into retrieving Facility Bin  User Id {}, Context Id {}", userId,
        contextId);
    List<FacilityBIN> existingFacilityBins = facilityBinRepo.findByProjectId(projectId);
    List<BridgeIdNumber> bridgeNumbers = new ArrayList<>();
    if (!CollectionUtils.isEmpty(existingFacilityBins)) {
      existingFacilityBins.forEach(bridgeIdNumber -> {
        bridgeNumbers.add(new BridgeIdNumber(bridgeIdNumber.getBin(), bridgeIdNumber.getEdbBin()));
      });
    }
    return bridgeNumbers;
  }

  @Override
  public List<DashboardDetail> getUnsubmittedApps(String userId, String contextId) {
    logger.info("Entering into getUnsubmittedApps method User id {}, Context Id {}", userId,
        contextId);
    List<PendingApplication> unSubmittedApplicationsByTheUser =
        pendingAppRepo.findAllUnSubmittedApplications(userId);
    logger.info("Collecting LRP details. User Id {}, Context Id {}", userId, contextId);
    Map<Long, ApplicantDto> lrpsMap =
        dartDBServiceUtility.getLegalResponsePartyDetails(userId, contextId, false);
    logger.info("Prepare the Application information details. User Id {}, Context Id {}", userId,
        contextId);
    List<DashboardDetail> unsubmittedApps = transformationService.prepareApplicationInfo(userId,
        contextId, unSubmittedApplicationsByTheUser, lrpsMap);
    logger.info("Amend the Municipality details. User Id {}, Context Id {}", userId, contextId);
    dartDBServiceUtility.amendMunicipalityDetails(unsubmittedApps);
    logger.info("Exiting from getUnsubmittedApps method User id {}, Context Id {}", userId,
        contextId);
    return unsubmittedApps;
  }

  @Override
  public Object retrieveSupportDocumentSummary(String userId, String contextId, Long projectId) {
    logger.info("Entering into retrieveSupportDocumentSummary. User Id {}, Context Id {}", userId,
        contextId);
    List<ETrackPermit> eTrackPermits = permitRepo.findETrackPermits(projectId);
    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException(PROJ_NOT_AVAIL_ERR_CODE,
          "There is no Project associated with this project Id", projectId);
    }
    Project project = projectAvailability.get();
    SupportDocument supportDocument = new SupportDocument();
    supportDocument.setEaInd(project.getEaInd());
    supportDocument.setSeqrInd(project.getSeqrInd());
    Map<String, dec.ny.gov.etrack.dart.db.model.PermitType> permitTypes = new HashMap<>();
    // Set<String> permitTypes = new HashSet<>();
    if (!CollectionUtils.isEmpty(eTrackPermits)) {
      supportDocument.setDistrictId(eTrackPermits.get(0).getEdbDistrictId());
      for (ETrackPermit permit : eTrackPermits) {
        String permitTypeCode = permit.getPermitTypeCode();
        if (permitTypes.get(permitTypeCode) == null) {
          dec.ny.gov.etrack.dart.db.model.PermitType permitType =
              new dec.ny.gov.etrack.dart.db.model.PermitType();
          permitType.setPermitType(permitTypeCode);
          permitType.setPermitTypeDesc(permit.getPermitTypeDesc());
          permitType.setRefLink(permit.getRefLink());
          permitTypes.put(permitTypeCode, permitType);
        }
      }
    }
    supportDocument.setPermitTypes(permitTypes);
    List<Document> supportDocumentsList = new ArrayList<>();
    List<Document> seqrDocumentsList = new ArrayList<>();
    List<Document> shpaDocumentsList = new ArrayList<>();
    List<Document> relatedDocumentList = new ArrayList<>();

    logger.info(
        "Identify all the documents to be submitted "
            + "(required/optional/seqr and shpa) for the project Id {}. User Id {}, Context Id {}",
        projectId, userId, contextId);
    List<dec.ny.gov.etrack.dart.db.entity.SupportDocument> supportDocumentList =
        supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(userId, contextId, projectId);

    Map<Long, Map<String, dec.ny.gov.etrack.dart.db.entity.SupportDocument>> supportDocumentWithCategoryMap =
        new HashMap<>();

    if (!CollectionUtils.isEmpty(supportDocumentList)) {
      supportDocumentList.forEach(supportDoc -> {
        Map<String, dec.ny.gov.etrack.dart.db.entity.SupportDocument> supportDocWithCategory =
            new HashMap<>();
        supportDocWithCategory.put(supportDoc.getReqType(), supportDoc);
        supportDocumentWithCategoryMap.put(supportDoc.getDocumentSubTypeTitleId(),
            supportDocWithCategory);
      });

      Map<Long, SupportDocumentEntity> uploadedDocumentMap = new HashMap<>();
      List<SupportDocumentEntity> othersDocumentList = new ArrayList<>();

      logger.info("Retrieve all the documents submitted "
          + "(required/optional/seqr and shpa) for the project Id {}. User Id {}, Context Id {}",
          projectId, userId, contextId);

      List<SupportDocumentEntity> uploadedDocuments =
          supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(projectId);

      logger.info("Categorize the uploaded documents "
          + "(required/optional/seqr and shpa) for the project Id {}. User Id {}, Context Id {}",
          projectId, userId, contextId);

      if (!CollectionUtils.isEmpty(uploadedDocuments)) {
        uploadedDocuments.forEach(uploadedDoc -> {
          if (uploadedDoc.getAddlDocInd() == null) {
            if (uploadedDoc.getDocumentSubTypeTitleId() != null
                && uploadedDoc.getDocumentSubTypeTitleId() > 0) {
              uploadedDocumentMap.put(uploadedDoc.getDocumentSubTypeTitleId(), uploadedDoc);
            } else {
              othersDocumentList.add(uploadedDoc);
            }
          }
        });
      }

      Map<Long, Long> documentSubTypeTitleIdsOfPermitRelatedDocuments = new HashMap<>();

      logger.info("Categorize and Mark the document whether its uploaded or not. "
          + "User Id {}, Context Id {}", userId, contextId);

      supportDocumentWithCategoryMap.keySet().forEach(documentSubTypeTitleId -> {
        Map<String, dec.ny.gov.etrack.dart.db.entity.SupportDocument> supportDocumentWithCategory =
            supportDocumentWithCategoryMap.get(documentSubTypeTitleId);

        supportDocumentWithCategory.keySet().forEach(category -> {
          documentSubTypeTitleIdsOfPermitRelatedDocuments.put(documentSubTypeTitleId,
              documentSubTypeTitleId);
          dec.ny.gov.etrack.dart.db.entity.SupportDocument supportDoc =
              supportDocumentWithCategory.get(category);
          Document document = new Document();
          // document.setSupportDocRefId(supportDoc.getDocumentSubTypeTitleId());
          document.setDocumentTitleId(supportDoc.getDocumentSubTypeTitleId());
          SupportDocumentEntity supportDocumentEntity =
              uploadedDocumentMap.get(documentSubTypeTitleId);
          if (supportDocumentEntity != null) {
            document.setDocumentTitle(supportDocumentEntity.getDocumentNm());
            document.setDocumentId(supportDocumentEntity.getDocumentId());
            document.setRefDocumentDesc(supportDocumentEntity.getRefDocumentDesc());
            document.setUploadInd("Y");
          } else {
            document.setDocumentTitle(supportDoc.getDocumentTitle());
            document.setUploadInd("N");
          }
          if ("PERMIT".equals(supportDoc.getReqType())) {
            supportDocumentsList.add(document);
          } else if ("OPT".equals(supportDoc.getReqType())) {
            if (supportDoc.getDocumentTypeId() != null
                && supportDoc.getDocumentTypeId().intValue() == 21) {
              seqrDocumentsList.add(document);
            } else if (supportDoc.getDocumentTypeId() != null
                && supportDoc.getDocumentTypeId().intValue() == 23) {
              shpaDocumentsList.add(document);
            } else {
              relatedDocumentList.add(document);
            }
          } else if ("SEQR".equals(supportDoc.getReqType())) {
            // EAF then assign to PERMIT Required list instead of SEQR list
            if (supportDoc.getDocumentTitleId() == 71) {
              supportDocumentsList.add(document);
            } else {
              seqrDocumentsList.add(document);
            }
          } else if ("SHPA".equals(supportDoc.getReqType())) {
            shpaDocumentsList.add(document);
          }
        });
      });

      if (!CollectionUtils.isEmpty(uploadedDocumentMap)) {
        logger.info(
            "Categorize the Documents uploaded earlier for the Permit applied and "
                + "removed/deleted by the user/staff. User Id {}, Context Id {}",
            userId, contextId);

        uploadedDocumentMap.keySet().forEach(documentSubTypeTitleId -> {
          if (documentSubTypeTitleIdsOfPermitRelatedDocuments.get(documentSubTypeTitleId) == null) {
            SupportDocumentEntity supportDocumentEntity =
                uploadedDocumentMap.get(documentSubTypeTitleId);
            Document document = new Document();
            document.setDocumentTitleId(supportDocumentEntity.getDocumentSubTypeTitleId());
            document.setDocumentTitle(supportDocumentEntity.getDocumentNm());
            document.setDocumentId(supportDocumentEntity.getDocumentId());
            document.setRefDocumentDesc(supportDocumentEntity.getRefDocumentDesc());
            document.setUploadInd("Y");
            if (supportDocumentEntity.getSupportDocCategoryCode() != null) {
              if (supportDocumentEntity.getSupportDocCategoryCode().equals(1)) {
                supportDocumentsList.add(document);
              } else if (supportDocumentEntity.getSupportDocCategoryCode().equals(2)) {
                relatedDocumentList.add(document);
              } else if (supportDocumentEntity.getSupportDocCategoryCode().equals(3)) {
                seqrDocumentsList.add(document);
              } else if (supportDocumentEntity.getSupportDocCategoryCode().equals(4)) {
                shpaDocumentsList.add(document);
              }
            } else {
              relatedDocumentList.add(document);
            }
          }
        });
      }

      othersDocumentList.forEach(uploadedOtherDocument -> {
        Document document = new Document();
        document.setDocumentTitleId(uploadedOtherDocument.getDocumentSubTypeTitleId());
        document.setDocumentType(uploadedOtherDocument.getDocumentTypeId());
        document.setDocumentSubType(uploadedOtherDocument.getDocumentSubTypeId());
        document.setDocumentTitle(uploadedOtherDocument.getDocumentNm());
        document.setDocumentId(uploadedOtherDocument.getDocumentId());
        document.setRefDocumentDesc(uploadedOtherDocument.getRefDocumentDesc());
        document.setUploadInd("Y");
        if (uploadedOtherDocument.getSupportDocCategoryCode() != null) {
          if (uploadedOtherDocument.getSupportDocCategoryCode().equals(1)) {
            supportDocumentsList.add(document);
          } else if (uploadedOtherDocument.getSupportDocCategoryCode().equals(2)) {
            relatedDocumentList.add(document);
          } else if (uploadedOtherDocument.getSupportDocCategoryCode().equals(3)) {
            seqrDocumentsList.add(document);
          } else if (uploadedOtherDocument.getSupportDocCategoryCode().equals(4)) {
            shpaDocumentsList.add(document);
          }
        } else {
          relatedDocumentList.add(document);
        }
      });
      List<Document> sortedSupportDocumentsList = supportDocumentsList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setRequiredDoc(sortedSupportDocumentsList);

      List<Document> sortedSeqrDocumentsList = seqrDocumentsList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setSeqrDoc(sortedSeqrDocumentsList);

      List<Document> sortedShpaDocumentsList = shpaDocumentsList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setShpaDoc(sortedShpaDocumentsList);
    }
    List<Document> sortedRelatedDocumentList = relatedDocumentList.stream()
        .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
    supportDocument.setRelatedDoc(sortedRelatedDocumentList);
    List<Integer> activityList =
        projectActivityRepo.findProjectActivityStatusId(projectId, DartDBConstants.SUPPORT_DOC_VAL);
    if (CollectionUtils.isEmpty(activityList)) {
      supportDocument.setValidatedInd("N");
    } else {
      supportDocument.setValidatedInd("Y");
    }
    return supportDocument;
  }

  private List<DashboardDetail> transformReviewAppsDataIntoDashboardData(
      List<ReviewerDocumentDetail> reviewerDocumentDetails) {

    List<DashboardDetail> dashboardApps = new ArrayList<>();
    // Map<Long, DashboardDetail> dashboardAppsMap = new HashMap<>();
    if (CollectionUtils.isEmpty(reviewerDocumentDetails)) {
      return dashboardApps;
    }
    for (ReviewerDocumentDetail reviewApp : reviewerDocumentDetails) {
      DashboardDetail dashboardDetail = new DashboardDetail();
      dashboardDetail.setApplicant(reviewApp.getDisplayName());
      dashboardDetail.setProjectId(reviewApp.getProjectId());
      dashboardDetail.setEdbDistrictId(reviewApp.getEdbDistrictId());
      dashboardDetail.setEdbPublicId(reviewApp.getEdbPublicId());
      Facility facility = new Facility();
      facility.setFacilityName(reviewApp.getFacilityName());
      facility.setCounty(reviewApp.getCounty());
      facility.setMunicipality(reviewApp.getMunicipality());
      facility.setDecId(reviewApp.getDecId());
      facility.setDecIdFormatted(reviewApp.getDecId());
      facility.setDistrictId(reviewApp.getEdbDistrictId());
      dashboardDetail.setFacility(facility);
      if (reviewApp.getDueDate() != null) {
        dashboardDetail.setDueDate(reviewApp.getDueDate());
      }
      if (reviewApp.getDateAssigned() != null) {
        dashboardDetail.setDateAssigned((reviewApp.getDateAssigned()));
      }
      dashboardDetail.setPermitType(reviewApp.getPermitTypeCode());
      dashboardDetail.setPermitTypeDesc(reviewApp.getPermitTypeDesc());
      dashboardDetail.setAnalystName(reviewApp.getProgramManager());
      dashboardDetail.setProgramStaff(reviewApp.getProgramStaff());
      if ("1".equals(reviewApp.getEaInd())) {
        dashboardDetail.setEaInd("E");
      } else {
        dashboardDetail.setEaInd("N");
      }
      dashboardDetail.setGpInd(reviewApp.getGpInd());
      dashboardDetail.setDartStatus(reviewApp.getAppStatus());
      dashboardApps.add(dashboardDetail);
    }
    return dashboardApps;
  }


  @Override
  public List<DashboardDetail> retrieveAllOnlineUserDartApplications(final String userId,
      final String contextId, final Map<Long, DartMilestone> dartMilestoneMapByBatchId) {
    logger.info("Entering to retrieveAllTasksDueApplications "
        + "for the input user. User Id {}, Context Id {}", userId, contextId);
    List<DartApplication> tasksDueFromDart =
        dashboardDetailDAO.retrieveDARTDueApps(userId, contextId);
    List<DashboardDetail> transformedTasksDue = transformationService
        .transformDataIntoDashboardData(tasksDueFromDart, null, dartMilestoneMapByBatchId);
    List<DartApplication> applicantResponseDueFromDart =
        dashboardDetailDAO.retrieveDARTAplctResponseDueApps(userId, contextId);
    List<DashboardDetail> transformedApplicantResponsesDue =
        transformationService.transformDataIntoDashboardData(applicantResponseDueFromDart, null,
            dartMilestoneMapByBatchId);
    List<DartApplication> suspendedApps =
        dashboardDetailDAO.retrieveDARTSuspendedApps(userId, contextId);
    List<DashboardDetail> transformedSuspendedApps = transformationService
        .transformDataIntoDashboardData(suspendedApps, null, dartMilestoneMapByBatchId);
    List<DashboardDetail> applicationsFromDart = new ArrayList<>();
    applicationsFromDart.addAll(transformedTasksDue);
    applicationsFromDart.addAll(transformedApplicantResponsesDue);
    applicationsFromDart.addAll(transformedSuspendedApps);
    return applicationsFromDart;
  }


  @Override
  public Object getUserDashboardDetails(String userId, String contextId) {
    logger.info("Entering into getUserDashboardDetails() User Id {}, Context Id {}", userId,
        contextId);

    logger.info("Collecting Unsubmitted projects. User Id {}, Context Id {}", userId, contextId);
    // Map<String, Object> dashboardDetails = new HashMap<>();

    List<DashboardDetail> unsubmittedProjects = getUnsubmittedApps(userId, contextId).stream()
        .sorted(Comparator.comparing(DashboardDetail::getProjectId).reversed())
        .collect(Collectors.toList());
    // dashboardDetails.put("resume-entry", unsubmittedProjects);
    return unsubmittedProjects;
  }

  @Override
  public Object getProgramReviewerDashboardDetails(final String userId, final String contextId,
      final Integer facilityRegionId) {
    logger.info("Entering into getProgramReviewerDashboardDetails. User Id {}, Context Id {}",
        userId, contextId);
    List<ReviewerDocumentDetail> reviewerDocumentDetails =
        dashboardDetailDAO.findAllReviewProjectDetailsByUserId(userId, contextId, facilityRegionId);
    List<DashboardDetail> sortedReviewerDashboardDetails = transformReviewAppsDataIntoDashboardData(
        amendCountyAndMunicipalityForReviewr(reviewerDocumentDetails).stream()
            .sorted(Comparator.comparing(ReviewerDocumentDetail::getProjectId).reversed())
            .collect(Collectors.toList()));
    logger.info("Exiting from getProgramReviewerDashboardDetails. User Id {}, Context Id {}",
        userId, contextId);
    return sortedReviewerDashboardDetails;
  }

  private List<ReviewerDocumentDetail> amendCountyAndMunicipalityForReviewr(
      List<ReviewerDocumentDetail> reviewerDocumentDetails) {

    Map<Long, ReviewerDocumentDetail> programReviewerMap = new HashMap<>();
    reviewerDocumentDetails.forEach(reviewerDocumentApp -> {
      programReviewerMap.put(reviewerDocumentApp.getProjectId(), reviewerDocumentApp);
    });

    List<Municipality> municipalities =
        municipalityRepo.findMunicipalitiesForProjectIds(programReviewerMap.keySet());
    Map<Long, Set<String>> municipalitiesMap = new HashMap<>();
    municipalities.forEach(municipality -> {

      if (municipalitiesMap.get(municipality.getProjectId()) != null) {
        municipalitiesMap.get(municipality.getProjectId()).add(municipality.getMunicipalityName());
      } else {
        Set<String> municipalityDetails = new HashSet<String>();
        municipalityDetails.add(municipality.getMunicipalityName());
        municipalitiesMap.put(municipality.getProjectId(), municipalityDetails);
      }
    });

    programReviewerMap.keySet().forEach(projectId -> {
      if (municipalitiesMap.get(projectId) != null) {
        programReviewerMap.get(projectId)
            .setMunicipality(String.join(",", municipalitiesMap.get(projectId)));
      }
    });

    List<County> counties = countyRepo.findCountiesForProjectIds(programReviewerMap.keySet());
    Map<Long, Set<String>> countiesMap = new HashMap<>();
    counties.forEach(county -> {
      if (countiesMap.get(county.getProjectId()) != null) {
        countiesMap.get(county.getProjectId()).add(county.getCounty());
      } else {
        Set<String> countyDetails = new HashSet<String>();
        countyDetails.add(county.getCounty());
        countiesMap.put(county.getProjectId(), countyDetails);
      }
    });
    programReviewerMap.keySet().forEach(projectId -> {
      if (countiesMap.get(projectId) != null) {
        programReviewerMap.get(projectId).setCounty(String.join(",", countiesMap.get(projectId)));
      }
    });

    /*
     * List<DocumentReviewEntity> documentReviewEntities =
     * documentReviewRepo.findAllReviewersByProjectIds(programReviewerMap.keySet());
     * 
     * Map<Long, Set<String>> programStaffMap = new HashMap<>();
     * 
     * documentReviewEntities.forEach(documentReview -> { if
     * (programStaffMap.get(documentReview.getProjectId()) != null) {
     * programStaffMap.get(documentReview.getProjectId()).add(documentReview.getDocReviewerName());
     * } else { Set<String> staffDetails = new HashSet<String>();
     * staffDetails.add(documentReview.getDocReviewerName());
     * programStaffMap.put(documentReview.getProjectId(), staffDetails); } });
     * 
     * programReviewerMap.keySet().forEach(projectId -> { if (programStaffMap.get(projectId) !=
     * null) { programReviewerMap.get(projectId) .setProgramStaff(String.join(",",
     * programStaffMap.get(projectId))); } });
     */
    return new ArrayList<>(programReviewerMap.values());
  }

  @Override
  public Object retrieveRequiredApplicantsToSign(String userId, String contextId, Long projectId) {
    List<SignedApplicant> applicantDtoList =
        signedApplicantRepo.findOwnerAndApplicantDetails(projectId);

    List<dec.ny.gov.etrack.dart.db.model.Public> applicantsList = new ArrayList<>();
    Map<String, Object> reqdSignedApplicantMap = new HashMap<>();

    if (CollectionUtils.isEmpty(applicantDtoList)) {
      return applicantsList;
    }
    Map<Long, dec.ny.gov.etrack.dart.db.model.Public> publicRoleMap = new HashMap<>();
    applicantDtoList.forEach(applicantDto -> {
      Long publicId = Long.valueOf(applicantDto.getPublicId().split(",")[0]);
      dec.ny.gov.etrack.dart.db.model.Public applicant = publicRoleMap.get(publicId);
      if (applicant == null) {
        applicant = new dec.ny.gov.etrack.dart.db.model.Public();
        applicant.setPublicId(publicId);
        applicant.setDisplayName(applicantDto.getDisplayName());
        if (applicantDto.getPublicSignedInd() != null && applicantDto.getPublicSignedInd() == 1) {
          applicant.setAcknowledgeInd("Y");
        } else {
          applicant.setAcknowledgeInd("N");
        }
        applicant.setRole(applicantDto.getRole());
        applicant.setLrpCode(applicantDto.getLegallyResponsibleTypeCode());
        publicRoleMap.put(publicId, applicant);
      } else {
        if (publicRoleMap.get(publicId).getRole().equals("Owner")) {
          applicant.setRole(applicantDto.getRole() + "/" + publicRoleMap.get(publicId).getRole());
        } else if (publicRoleMap.get(publicId).getRole().contains("Contact")) {
          applicant.setRole(applicantDto.getRole());
        } else {
          if (!publicRoleMap.get(publicId).getRole().equals(applicantDto.getRole())) {
            applicant.setRole(publicRoleMap.get(publicId).getRole() + "/" + applicantDto.getRole());
          }
        }
      }
    });
    publicRoleMap.keySet().forEach(publicId -> {
      dec.ny.gov.etrack.dart.db.model.Public applicant = publicRoleMap.get(publicId);
      if (applicant.getRole().equals("Owner") && applicant.getLrpCode() != null
          && applicant.getLrpCode() == 1) {
        logger.info("Owner is added via Applicants steps. "
            + "so, During sign and acknowledge, the role should be displayed as Applcant/Owner. User Id {}, Context Id {}");
        applicant.setRole("Applicant/Owner");
      }
      applicantsList.add(applicant);
    });
    List<Integer> activityStatusId = projectActivityRepo.findProjectActivityStatusId(projectId, 14);
    if (CollectionUtils.isEmpty(activityStatusId)) {
      reqdSignedApplicantMap.put("validatedInd", "N");
    } else {
      reqdSignedApplicantMap.put("validatedInd", "Y");
    }
    reqdSignedApplicantMap.put("reqdsigneddoc", applicantsList);
    return reqdSignedApplicantMap;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object retrieveProjectSummary(final String userId, String contextId, final String jwtToken,
      final Long projectId) {

    logger.info("Entering into retrieve Project summary. User Id {}, Context Id {}", userId,
        contextId);

    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    
    Map<String, Object> enterpriseDataDetails = null;
    try {
      enterpriseDataDetails =
          dartDBDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId);
    } catch (NoDataFoundException e) {
      logger.info("Update Email Correspondence as read as this project is not available in enterprise.");      
      projectRepo.updateEmailCorrespondenceAsRead(projectId);
      throw e;
    } catch (DartDBException e) {
      throw e;
    }
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException(PROJ_NOT_AVAIL_ERR_CODE, "There is no Project available",
          projectId);
    }
    // List<Integer> activityIds =
    // projectActivityRepo.findProjectActivityStatusId(projectId, DartDBConstants.SUBMIT_PROJ_VAL);

    ProjectSummary projectSummary = new ProjectSummary();
    Integer onlineApplnIndicator = 0;
    Project project = projectAvailability.get();
    // onlineApplnIndicator = project.getOnlineApplnInd();
    if (project.getDimsrInd() != null && project.getDimsrInd().equals(1)) {
      projectSummary.setDimsrInd("Y");
    } else {
      projectSummary.setDimsrInd("N");
    }
    try {
      logger.info(
          "Entering into Milestone Refresh for the Project id {}, User Id {}, Context Id {}",
          projectId, userId, contextId);
      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("contextId", contextId);
      headers.add("projectId", String.valueOf(projectId));
      headers.add(HttpHeaders.AUTHORIZATION, jwtToken);
      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-dart-district/refresh-project-milestone").build().toString();
      ResponseEntity<Void> response = eTrackOtherServiceRestTemplate.exchange(uri,
          HttpMethod.POST, requestEntity, Void.class);
      if (!response.getStatusCode().equals(HttpStatus.OK)) {
        return new ResponseEntity<>(response.getStatusCode());
      }
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      logger.error("Error while requesting to etrack-dart-district to refresh the milestone ", ex);
      return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
    } catch (Exception e) {
      logger.error(
          "Unexpected error happened while refreshing the milestone details for the project "
              + "{}. User Id {}, Context Id {}",
          projectId, userId, contextId);
      throw new DartDBException("UNABLE_TO_REFRESH_MILESTONE",
          "Unexpected error happened while refreshing the milestone details for the project "
              + projectId);
    }
    logger.info("Exiting from Milestone Refresh for the Project id {}, User Id {}, Context Id {}",
        projectId, userId, contextId);
    projectSummary.setDescription(project.getProjectDesc());
    projectSummary.setEmergencyInd(project.getEaInd());
    projectSummary.setAssignedAnalystId(project.getAnalystAssignedId());
    try {
      List<RegionUserEntity> userDetails =
    		  dartDBDAO.retrieveStaffDetailsByUserId(userId, contextId);
      if (!CollectionUtils.isEmpty(userDetails)) {
        projectSummary.setLoggedInUserName(userDetails.get(0).getDisplayName());
      }
    } catch (NoDataFoundException ndfe) {
      projectSummary.setLoggedInUserName(userId);
    }
    if (StringUtils.hasLength(project.getAssignedAnalystName())) {
      StringBuilder concatenateAnalystName = new StringBuilder();
      String[] splittedAnalystNameBySpace = project.getAssignedAnalystName().split(" ");
      int length = splittedAnalystNameBySpace.length;
      if (length == 1) {
        projectSummary.setAssignedAnalystName(project.getAssignedAnalystName());
      } else if (length > 1) {
        concatenateAnalystName.append(splittedAnalystNameBySpace[1]).append(" ")
            .append(splittedAnalystNameBySpace[0]);
        if (length == 3) {
          concatenateAnalystName.append(" ").append(splittedAnalystNameBySpace[2]);
        }
        projectSummary.setAssignedAnalystName(concatenateAnalystName.toString());
      }
    }
    if (project.getAnalystAssignedDate() != null) {
      projectSummary.setAnalystAssignedDate(dateFormat.format(project.getAnalystAssignedDate()));
    }
    logger.info("Collecting Contact details for Virtual Workspace. User Id {}, Context Id {}",
        userId, contextId);

    List<PublicAndFacilityDetail> publicAndFacilityDetails =
        (List<PublicAndFacilityDetail>) enterpriseDataDetails.get(DartDBConstants.PUBLIC_CURSOR);

    List<PublicSummary> publics = new ArrayList<>();
    List<PublicSummary> owners = new ArrayList<>();
    List<PublicSummary> contacts = new ArrayList<>();

    // List<PublicSummary> contactsList =
    // publicSummaryRepo.findAllContactsHistoryByProjectId(projectId);
    // if (!CollectionUtils.isEmpty(contactsList)) {
    // contactsList.forEach(contact -> {
    // contacts.add(createContactSummary(contact));
    // });
    // }

    logger.info(
        "Collecting Public, Owner and Contact details for Virtual Workspace. User Id {}, Context Id {}",
        userId, contextId);
    // List<PublicSummary> publicsList =
    // publicSummaryRepo.findAllPublicsHistoryByProjectId(projectId);
    // // Map<Long, Long> publicsIdMap = new HashMap<>();
    // if (!CollectionUtils.isEmpty(publicsList)) {
    // Map<Long, PublicSummary> publicsMap = new HashMap<>();
    // publicsList.forEach(publicView -> {
    // publicsMap.put(publicView.getPublicId(), publicView);
    // });
    // publicsMap.keySet().forEach(publicId -> {
    // publics.add(createPublicSummary(publicsMap.get(publicId)));
    // });
    // }
    if (!CollectionUtils.isEmpty(publicAndFacilityDetails)) {
      publicAndFacilityDetails.forEach(publicAndFacilityDetail -> {
        if (publicAndFacilityDetail.getOwnerRec() != null) {
          if (publicAndFacilityDetail.getOwnerRec().equals(0)) {
            if (publicAndFacilityDetail.getRoleTypeId() != null
                && publicAndFacilityDetail.getRoleTypeId().equals(80)) {
              contacts.add(createPublicSummary(publicAndFacilityDetail));
            } else {
              publics.add(createPublicSummary(publicAndFacilityDetail));
            }
          } else if (publicAndFacilityDetail.getOwnerRec().equals(1)) {
            owners.add(createPublicSummary(publicAndFacilityDetail));
          }
        }
      });
    }
    projectSummary.setPublics(publics);
    projectSummary.setOwners(owners);
    projectSummary.setContactAgents(contacts);

    // logger.info("Collecting Owners details for VW . User Id {}, Context Id {}", userId,
    // contextId);
    // List<PublicSummary> ownersList =
    // publicSummaryRepo.findAllOwnersHistoryByProjectId(projectId);
    //
    //
    // if (!CollectionUtils.isEmpty(ownersList)) {
    // Map<Long, PublicSummary> ownersMap = new HashMap<>();
    // ownersList.forEach(owner -> {
    // ownersMap.put(owner.getPublicId(), owner);
    // });
    // ownersMap.keySet().forEach(publicId -> {
    // owners.add(createPublicSummary(ownersMap.get(publicId)));
    // });
    // }
    // projectSummary.setOwners(owners);

    logger.info("Collecting Facility details for VW . User Id {}, Context Id {}", userId,
        contextId);

    // dec.ny.gov.etrack.dart.db.entity.Facility facility = facilityRepo.findByProjectId(projectId);
    // if (facility != null) {
    // facilitySummary.setFacilityName(facility.getFacilityName());
    // String decId = facility.getDecId();
    // if (StringUtils.hasLength(decId)) {
    // StringBuilder sb = new StringBuilder();
    // sb.append(decId.substring(0, 1)).append("-").append(decId.substring(1, 5)).append("-")
    // .append(decId.substring(5));
    // decId = sb.toString();
    // }
    // facilitySummary.setDecId(decId);
    // facilitySummary.setEdbDistrictId(facility.getEdbDistrictId());
    // projectSummary.setFacility(facilitySummary);
    // }

    PublicAndFacilityDetail publicAndFacilityDetail = publicAndFacilityDetails.get(0);

    if (publicAndFacilityDetail != null) {
      dec.ny.gov.etrack.dart.db.entity.Facility facilitySummary =
          new dec.ny.gov.etrack.dart.db.entity.Facility();
      facilitySummary.setFacilityName(publicAndFacilityDetail.getFacilityName());
      String decId = publicAndFacilityDetail.getDecId();
      if (StringUtils.hasLength(decId)) {
        StringBuilder sb = new StringBuilder();
        sb.append(decId.substring(0, 1)).append("-").append(decId.substring(1, 5)).append("-")
            .append(decId.substring(5));
        decId = sb.toString();
      }
      facilitySummary.setDecId(decId);
      facilitySummary.setEdbDistrictId(publicAndFacilityDetail.getEdbDistrictId());
      projectSummary.setFacility(facilitySummary);
    }

    logger.info("Collecting Notes details for VW . User Id {}, Context Id {}", userId, contextId);
    List<ProjectNote> notesList = projectNoteRepo.findAllByProjectId(projectId);
    List<String> noteActionTypeDescList = projectNoteRepo.findAllProjectNoteActionType();
    if (CollectionUtils.isEmpty(noteActionTypeDescList)) {
      throw new DartDBException("ACTION_TYPE_CONFIG_NOT_AVAIL",
          "Project Note Action type configuration is missing");
    }
    Map<Integer, String> noteActionTypeMap = new HashMap<>();
    noteActionTypeDescList.forEach(noteActionTypeDesc -> {
      String[] actionType = noteActionTypeDesc.split(",");
      noteActionTypeMap.put(Integer.valueOf(actionType[0]), actionType[1]);
    });
    List<ProjectNoteView> projectNotes = new ArrayList<>();
    for (ProjectNote noteDto : notesList) {
      ProjectNoteView note = new ProjectNoteView();
      note.setActionDate(dateFormat.format(noteDto.getActionDate()));
      note.setActionTypeCode(noteDto.getActionTypeCode());
      note.setActionTypeDesc(noteActionTypeMap.get(noteDto.getActionTypeCode()));
      if (noteDto.getActionTypeCode().equals(DartDBConstants.REQUIRED_DOCUMENTS_NOT_RECEIVED)) {
        if (StringUtils.hasLength(noteDto.getActionNote())) {
          Set<Integer> documentTitleList = new HashSet<>();
          String[] documentTitleIds = noteDto.getActionNote().split(",");
          for (String title : documentTitleIds) {
            documentTitleList.add(Integer.parseInt(title));
          }
          note.setMissingReqdDoc(projectNoteRepo.findAllDocumentTitleByIds(documentTitleList));
          note.setActionNote(String.join(",", note.getMissingReqdDoc()));
          // note.setActionNote("Project Id " + projectId + " missing the following documents");
        }
      } else {
        note.setActionNote(noteDto.getActionNote());
      }
      if (noteDto.getActionTypeCode().equals(DartDBConstants.INVOICE_CANCELLED_NOTE)) {
        note.setCancelledUserId(noteDto.getCreatedById());
        if (StringUtils.hasLength(noteDto.getActionNote())) {
          note.setActionNote(noteDto.getActionNote().split("\\|")[0]);
        }
      }
      note.setComments(noteDto.getComments());
      note.setProjectNoteId(noteDto.getProjectNoteId());
      if (StringUtils.hasLength(noteDto.getCreatedById())
          && (StringUtils.hasLength(note.getCancelledUserId())
              || DartDBConstants.SYSTEM_USER_ID.equals(noteDto.getCreatedById())
              || DartDBConstants.ENTERPRISE_SYSTEM_USER_ID.equals(noteDto.getCreatedById()))) {
        note.setSystemGenerated("Y");
      } else {
        note.setSystemGenerated("N");
      }
      projectNotes.add(note);
    }

    projectSummary.setNotes(projectNotes);

    logger.info("Collecting Document details for VW . User Id {}, Context Id {}", userId,
        contextId);
    final Map<Long, List<String>> batchIdAndPermitTypes = new HashMap<>();

//    List<Application> applcationList = applicationRepo.findAllUploadedApplnByProjectId(projectId);
    List<Application> applicationList = (List<Application>) enterpriseDataDetails.get(DartDBConstants.APPLICATION_CURSOR);
    if (!CollectionUtils.isEmpty(applicationList)) {
      applicationList.forEach(application -> {
        StringBuilder permitTransTypeAndApplId = new StringBuilder();

        if (StringUtils.hasLength(application.getRelatedRegPermit()) 
            && !application.getRelatedRegPermit().startsWith("00-")) {
          
          permitTransTypeAndApplId.append(application.getRelatedRegPermit());
        } else {
          permitTransTypeAndApplId.append(application.getPermitTypeCode());
          application.setRelatedRegPermit(null);       
        }
        permitTransTypeAndApplId.append(":").append(application.getTransTypeCode());
        permitTransTypeAndApplId.append(":").append(application.getProgId());

        if (batchIdAndPermitTypes.get(application.getBatchIdEdb()) == null) {
          List<String> applicationDetails = new ArrayList<>();
          applicationDetails.add(permitTransTypeAndApplId.toString());
          batchIdAndPermitTypes.put(application.getBatchIdEdb(), applicationDetails);
        } else {
          batchIdAndPermitTypes.get(application.getBatchIdEdb())
              .add(permitTransTypeAndApplId.toString());
        }
      });
    }
    projectSummary.setApplication(applicationList);

    // projectSummary.setApplication(applcationList.stream()
    // .sorted(Comparator.comparing(Application::getProgId)).collect(Collectors.toList()));

    prepareDocumentsAndFilesDetails(userId, contextId, projectId, projectSummary);
    prepareReviewDocuments(userId, contextId, projectId, projectSummary);

    logger.info("Collecting Invoice details for VW . User Id {}, Context Id {}", userId, contextId);
    List<Invoice> invoiceList = new ArrayList<>();
    if (projectAvailability.get().getDimsrInd() == null
        || !projectAvailability.get().getDimsrInd().equals(1)) {
      List<InvoiceEntity> invoices = invoiceRepo.findAllByProjectIdOrderByCreateDateDesc(projectId);
      if (!CollectionUtils.isEmpty(invoices)) {
        List<String> invoiceStatuses = invoiceRepo.findAllInvoiceStatus();
        Map<String, String> invoiceStatusMap = new HashMap<>();
        if (CollectionUtils.isEmpty(invoiceStatuses)) {
          throw new BadRequestException("INVOICE_STATUS_NA",
              "Invoice Status is not available in the database", projectId);
        }
        invoiceStatuses.forEach(invoiceStatus -> {
          String[] invoiceStatusCodeAndDesc = invoiceStatus.split(",");
          invoiceStatusMap.put(invoiceStatusCodeAndDesc[0], invoiceStatusCodeAndDesc[1]);
        });
        invoices.forEach(invoiceEntity -> {
          Invoice invoice = new Invoice();
          invoice.setInvoiceId(invoiceEntity.getFmisInvoiceNum());
          invoice.setPayReference(invoiceEntity.getPaymentConfirmnId());
          if (invoiceEntity.getCreateDate() != null) {
            invoice.setInvoiceDate(dateFormat.format(invoiceEntity.getCreateDate()));
          }
          Long invoiceAmount = 0L;
          if (invoiceEntity.getInvoiceFeeType1() != null) {
            invoiceAmount += invoiceEntity.getInvoiceFeeTypeFee1();
          }
          if (invoiceEntity.getInvoiceFeeType2() != null) {
            invoiceAmount += invoiceEntity.getInvoiceFeeTypeFee2();
          }
          if (invoiceEntity.getInvoiceFeeType3() != null) {
            invoiceAmount += invoiceEntity.getInvoiceFeeTypeFee3();
          }
          invoice.setStatus(invoiceStatusMap.get(invoiceEntity.getInvoiceStatusCode()));
          invoice.setCheckAmt(invoiceEntity.getCheckAmt());
          invoice.setCheckNumber(invoiceEntity.getCheckNumber());
          invoice.setReason(invoiceEntity.getCancelReason());
          if (invoiceEntity.getCheckRcvdDate() != null) {
            invoice.setCheckRcvdDate(dateFormat.format(invoiceEntity.getCheckRcvdDate()));
          }
          if (StringUtils.hasLength(invoiceEntity.getInvoiceStatusCode())
              && invoiceEntity.getPaidAmt() != null) {
            invoice.setPaidAmount(invoiceEntity.getPaidAmt());
            invoice.setDueAmount(invoiceAmount - invoiceEntity.getPaidAmt());
          } else {
            invoice.setPaidAmount(0L);
            invoice.setDueAmount(invoiceAmount);
          }
          invoiceList.add(invoice);
        });
      }
      List<String> invoiceFeeEligiblePermits = invoiceRepo.findInvoiceFeeEligiblePermits(projectId);
      if (!CollectionUtils.isEmpty(invoiceFeeEligiblePermits)) {
        projectSummary.setInvoiceReq("Y");
      } else {
        projectSummary.setInvoiceReq("N");
      }
    } else {
      projectSummary.setInvoiceReq("N");
    }
    projectSummary.setInvoice(invoiceList);
    logger.info("Collecting Alerts details for VW . User Id {}, Context Id {}", userId, contextId);
    List<ProjectAlert> alertsList =
        projectAlertRepo.findAllAlertsByUserIdAndProjectId(userId, projectId);
    List<Alert> alerts = null;
    if (CollectionUtils.isEmpty(alertsList)) {
      alerts = new ArrayList<>();
    } else {
      alerts = transformationService.transformAlertMessage(userId, contextId, alertsList);

    }
    projectSummary.setAlerts(alerts);

    logger.info("Collecting Pending Applications details for VW . User Id {}, Context Id {}",
        userId, contextId);
    List<DartApplication> pendingApplicationsList =
        (List<DartApplication>) enterpriseDataDetails.get(DartDBConstants.P_PENDING_APPS_CURSOR);

    // List<DartApplication> pendingApplicationsList =
    // dashboardDetailDAO.retrieveDARTPendingApplications(null, contextId, edbDistrictId, null);
    List<DashboardDetail> pendingApps =
        transformationService.transformDataIntoDashboardData(pendingApplicationsList, null, null);

    projectSummary.setPendingApplications(pendingApps);

    logger.info("Collecting Renewal eligible details for VW . User Id {}, Context Id {}", userId,
        contextId);


    // Map<String, Object> existingApplicationsData =
    // dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId,
    // facility.getEdbDistrictId());
    projectSummary.setActiveAuthorizations(
        (List<DartPermit>) enterpriseDataDetails.get(DartDBConstants.EXISTING_APPS_CURSOR));

    logger.info("Collecting Milestone details for Virtual Workspace. User Id {}, Context Id {}",
        userId, contextId);

    Milestone milestone = new Milestone();
    prepareProjectMilestoneDetails(userId, contextId, projectId, batchIdAndPermitTypes, milestone);
    Integer foilReqIndicator = projectAvailability.get().getFoilReqInd();
    if (foilReqIndicator != null && foilReqIndicator.equals(1)) {
      projectSummary.setFoilReqInd("Y");
    } else {
      projectSummary.setFoilReqInd("N");
    }

    logger.info("Collecting Foil and Litigation details for VW . User Id {}, Context Id {}", userId,
        contextId);

    List<String> foilRequestNumbers = new ArrayList<>();

    List<ProjectFoilStatusDetail> foilStatusDetails = foilRequestRepo.findByProjectId(projectId);
    if (!CollectionUtils.isEmpty(foilStatusDetails)) {
      foilStatusDetails.forEach(foilStatusDetail -> {
        foilRequestNumbers.add(foilStatusDetail.getFoilReqNum());
      });
    }
    projectSummary.setFoilRequestNumber(foilRequestNumbers);

    List<LitigationRequest> litigationRequestHistory = new LinkedList<>();
    LitigationRequest litigationRequest = getLitigationRequest(projectId, litigationRequestHistory);
    projectSummary.setLitigationRequest(litigationRequest);

    if (litigationRequest != null) {
      List<LitigationRequest> litigationDateFromHistory =
          getLitigationRequestHistory(projectId, litigationRequestHistory);
      if (CollectionUtils.isEmpty(litigationDateFromHistory)) {
        projectSummary.setLitigationRequestHistory(litigationRequestHistory);
      } else {
        litigationDateFromHistory.forEach(litigationHistory -> {
          litigationRequestHistory.add(litigationHistory);
        });
        projectSummary.setLitigationRequestHistory(litigationRequestHistory);
      }
    }
    String reviewDate = documentReviewRepo.findReviewDateByProjectId(projectId);
    milestone.setReviewDate(reviewDate);
    projectSummary.setMilestone(milestone);
    projectSummary.setInquiries(projectRepo.findAllInquiriesByProjectId(projectId));
    logger.info("Exiting from retrieve Project summary. User Id {}, Context Id {}", userId,
        contextId);
    return projectSummary;
  }

  private void prepareProjectMilestoneDetails(String userId, String contextId, Long projectId,
      Map<Long, List<String>> batchIdAndPermitTypes, Milestone milestone) {
    
    List<String> permitTypeDetails = applicationRepo.findPermitTypesAndTransTypesByProjectId(projectId);
    logger.info("Permit details for ");    
    Map<Long, String> edbBatchAndGpPermit = new HashMap<>();;
    if (!CollectionUtils.isEmpty(permitTypeDetails)) {
      permitTypeDetails.forEach(permitType -> {
        String[] permitSplit = permitType.split(",");
        if (permitSplit[3].startsWith("GP")) {
          edbBatchAndGpPermit.put(Long.valueOf(permitSplit[2]), permitSplit[3]);
        }
      });
    }
    List<DartMilestone> dartMilestonesList =
        dartMilestoneRepo.findAllMilestoneByProjectIdOrderByBatchIdAsc(projectId);
    Map<Long, List<DartMilestone>> dartMilestoneMap = new LinkedHashMap<>();
    if (!CollectionUtils.isEmpty(dartMilestonesList)) {
      dartMilestonesList.forEach(dartMilestone -> {
        if (dartMilestoneMap.get(dartMilestone.getBatchId()) == null) {
          List<DartMilestone> dartMilestones = new ArrayList<>();
          dartMilestones.add(dartMilestone);
          dartMilestoneMap.put(dartMilestone.getBatchId(), dartMilestones);
        } else {
          dartMilestoneMap.get(dartMilestone.getBatchId()).add(dartMilestone);
        }
      });

      List<DartSuspensionMilestone> dartSuspendedMilestones = 
          dartSuspendedMilestoneRepo.findAllSuspendedMilestoneByProjectIdOrderByBatchIdAsc(projectId);
      if (!CollectionUtils.isEmpty(dartSuspendedMilestones)) {
        dartSuspendedMilestones.forEach(dartSuspendedMilestone -> {
          DartMilestone dartMilestone = new DartMilestone();
          dartMilestone.setProjectId(projectId);
          dartMilestone.setBatchId(dartSuspendedMilestone.getBatchId());
          dartMilestone.setApplId(dartSuspendedMilestone.getApplId());
          dartMilestone.setSuspendedDate(dartSuspendedMilestone.getSuspendedDate());
          dartMilestone.setUnsuspendedDate(dartSuspendedMilestone.getUnsuspendedDate());
          dartMilestone.setSuspensionReason(dartSuspendedMilestone.getSuspensionReason());
          if (dartMilestoneMap.get(dartMilestone.getBatchId()) == null) {
            List<DartMilestone> dartMilestones = new ArrayList<>();
            dartMilestones.add(dartMilestone);
            dartMilestoneMap.put(dartMilestone.getBatchId(), dartMilestones);
          } else {
            dartMilestoneMap.get(dartMilestone.getBatchId()).add(dartMilestone);
          }
        }); 
      }

      List<DartInCompleteMilestone> dartInCompleteMilestones = 
          dartInCompleteMilestoneRepo.findAllInCompleteMilestoneByProjectIdOrderByBatchIdAsc(projectId);
      if (!CollectionUtils.isEmpty(dartInCompleteMilestones)) {
        dartInCompleteMilestones.forEach(dartInCompleteMilestone -> {
          DartMilestone dartMilestone = new DartMilestone();
          dartMilestone.setProjectId(projectId);
          dartMilestone.setBatchId(dartInCompleteMilestone.getBatchId());
          dartMilestone.setApplId(dartInCompleteMilestone.getApplId());
          dartMilestone.setIncompleteSentDate(dartInCompleteMilestone.getIncompleteSentDate());
          dartMilestone.setResubmissionRecvdDate(dartInCompleteMilestone.getResubmissionDate());
          if (dartMilestoneMap.get(dartMilestone.getBatchId()) == null) {
            List<DartMilestone> dartMilestones = new ArrayList<>();
            dartMilestones.add(dartMilestone);
            dartMilestoneMap.put(dartMilestone.getBatchId(), dartMilestones);
          } else {
            dartMilestoneMap.get(dartMilestone.getBatchId()).add(dartMilestone);
          }
        }); 
      }
      
      List<CurrentStatus> statuses = new LinkedList<>();
      dartMilestonesList.forEach(dartMilestoneStatus -> {
        // DartMilestone dartMilestoneStatus = currentStatusMap.get(currentStatus);
        CurrentStatus status = new CurrentStatus();
        status.setBatchId(dartMilestoneStatus.getBatchId());
        status.setStatus(dartMilestoneStatus.getEdbCurrentStatusDesc());
        if (dartMilestoneStatus.getEdbCurrentStatusCode()
            .equals(CurrentDartStatus.SUSPEND_PERMIT.name())) {
          status.setReason(dartMilestoneStatus.getSuspensionReason());
        }
        
        String currentStatus = dartMilestoneStatus.getEdbCurrentStatusCode();
        String dueDate = null;
        if (StringUtils.hasLength(currentStatus)) {

          if (CurrentDartStatus.COMPLETENESS_DETERMINATION_DUE.getCurrrentStatus()
              .equals(currentStatus)
              || CurrentDartStatus.SUSPENDED_TO_A_DATE.getCurrrentStatus()
                  .equals(currentStatus)) {
            if (dartMilestoneStatus.getCompletenessDueDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getCompletenessDueDate());
            }
          } else if (CurrentDartStatus.WRITTEN_COMMENTS_DEADLINE.getCurrrentStatus()
              .equals(currentStatus)) {
            if (dartMilestoneStatus.getCommentsDeadlineDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getCommentsDeadlineDate());
            }
          } else if (CurrentDartStatus.HEARING_DECISION_DUE.getCurrrentStatus()
              .equals(currentStatus)) {
            if (dartMilestoneStatus.getHearingDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getHearingDate());
            }
          } else if (CurrentDartStatus.FINAL_DECISION_DUE.getCurrrentStatus()
              .equals(currentStatus)) {
            if (dartMilestoneStatus.getFinalDispositionDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getFinalDispositionDate());
            }
          } else if (CurrentDartStatus.PERMITTEE_RESPONSE_DUE.getCurrrentStatus()
              .equals(currentStatus)) {
            if (dartMilestoneStatus.getPermitteeRespDueDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getPermitteeRespDueDate());
            }
          } else if (CurrentDartStatus.DIMSR_DECISION_DUE.getCurrrentStatus()
              .equals(currentStatus)) {
            if (dartMilestoneStatus.getDimsrDecisionDueDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getDimsrDecisionDueDate());
            }
          } else if (CurrentDartStatus.INCOMPLETE.getCurrrentStatus().equals(currentStatus)) {
            if (dartMilestoneStatus.getCompletenessDueDate() != null) {
              dueDate = dateFormat.format(dartMilestoneStatus.getCompletenessDueDate());
            }
          }
          if (StringUtils.hasLength(dueDate)) {
            status.setStatusDate(dueDate);
          } else if (dartMilestoneStatus.getUpdateDate() != null) {
            status.setStatusDate(dateFormat.format(dartMilestoneStatus.getUpdateDate()));
          }
        }
        statuses.add(status);
      });
      
      milestone.setBatchDetails(new ArrayList<>());
      milestone.setCurrentStatuses(statuses);
      dartMilestoneMap.values().forEach(dartMilestones -> {
        // List<DartMilestone> dartMilestones = dartMilestoneMap.get(batchId);
        BatchDetail batchDetail = new BatchDetail();
        DartMilestone batchMileStone = dartMilestones.get(0);
        batchDetail.setBatchNumber(batchMileStone.getBatchId());
        batchDetail.setGpInd(batchMileStone.getEdbGpInd());
        batchDetail.setEaInd(batchMileStone.getEdbEaInd());
        // List<String> permitTransTypeApplIds = new ArrayList<>();
        List<MileStoneStatus> milestones = new ArrayList<>();
        dartMilestones.forEach(dartMileStone -> {
          if (dartMileStone.getProjectReceivedDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.PROJECT_RECEIVED.getValue(),
                dateFormat.format(dartMileStone.getProjectReceivedDate()),
                dartMileStone.getProjectReceivedDate()));
          }

//          if (dartMileStone.getIncompleteSentDate() != null) {
//            milestones.add(new MileStoneStatus(MilestoneDesc.INCOMPLETE_SENT.getValue(),
//                dateFormat.format(dartMileStone.getIncompleteSentDate()),
//                dartMileStone.getIncompleteSentDate()));
//          }

          if (dartMileStone.getCompleteSentDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.COMPLETENESS_DATE.getValue(),
                dateFormat.format(dartMileStone.getCompleteSentDate()),
                dartMileStone.getCompleteSentDate()));
          }

          if (dartMileStone.getFinalDispositionDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.FINAL_DECISION_DATE.getValue(),
                dateFormat.format(dartMileStone.getFinalDispositionDate()),
                dartMileStone.getFinalDispositionDate()));
          }

          if (dartMileStone.getAdditionalInfoRequestDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.ADDL_INFO_REQD.getValue(),
                dateFormat.format(dartMileStone.getAdditionalInfoRequestDate()),
                dartMileStone.getAdditionalInfoRequestDate()));
          }

          if (dartMileStone.getAdditionalInfoRecvdDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.ADDL_INFO_RCVD.getValue(),
                dateFormat.format(dartMileStone.getAdditionalInfoRecvdDate()),
                dartMileStone.getAdditionalInfoRecvdDate()));
          }

//          if (dartMileStone.getSuspendedDate() != null) {
//            milestones.add(new MileStoneStatus(MilestoneDesc.SUSPENDED.getValue(),
//                dateFormat.format(dartMileStone.getSuspendedDate()),
//                dartMileStone.getSuspendedDate()));
//          }

          if (dartMileStone.getUnsuspendedDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.UNSUSPENDED.getValue(),
                dateFormat.format(dartMileStone.getUnsuspendedDate()),
                dartMileStone.getUnsuspendedDate()));
          }

          if (dartMileStone.getAuthEffectiveDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.EFFECTIVE.getValue(),
                dateFormat.format(dartMileStone.getAuthEffectiveDate()),
                dartMileStone.getAuthEffectiveDate()));
          }

          if (dartMileStone.getAuthExpDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.EXPIRATION.getValue(),
                dateFormat.format(dartMileStone.getAuthExpDate()), dartMileStone.getAuthExpDate()));
          }

          if (dartMileStone.getHearingDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.HEARING_DATE.getValue(),
                dateFormat.format(dartMileStone.getHearingDate()), dartMileStone.getHearingDate()));
          }

          if (dartMileStone.getEnbDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.ENB_PUBLICATION.getValue(),
                dateFormat.format(dartMileStone.getEnbDate()), dartMileStone.getEnbDate()));
          }

          if (dartMileStone.getFiveDayLetterRecvdDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.FIVE_DAY_LTR_RCVD.getValue(),
                dateFormat.format(dartMileStone.getFiveDayLetterRecvdDate()),
                dartMileStone.getFiveDayLetterRecvdDate()));
          }

          if (dartMileStone.getFiveDayLetterResponseDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.FIVE_DAY_LTR_RESP.getValue(),
                dateFormat.format(dartMileStone.getFiveDayLetterResponseDate()),
                dartMileStone.getFiveDayLetterResponseDate()));
          }

          if (dartMileStone.getResubmissionRecvdDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.RESUBMISSION_RCVD.getValue(),
                dateFormat.format(dartMileStone.getResubmissionRecvdDate()),
                dartMileStone.getResubmissionRecvdDate()));
          }

          if (dartMileStone.getDeisCompleteDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.DEIS_COMPLETE.getValue(),
                dateFormat.format(dartMileStone.getDeisCompleteDate()),
                dartMileStone.getDeisCompleteDate()));
          }

          if (dartMileStone.getFeisCompleteDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.FEIS_COMPLETE.getValue(),
                dateFormat.format(dartMileStone.getFeisCompleteDate()),
                dartMileStone.getFeisCompleteDate()));
          }

          if (dartMileStone.getSeqrFindingsIssuedDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.FINDINGS_ISSUED.getValue(),
                dateFormat.format(dartMileStone.getSeqrFindingsIssuedDate()),
                dartMileStone.getSeqrFindingsIssuedDate()));
          }

          if (dartMileStone.getCommentsDeadlineDate() != null) {
            milestones.add(new MileStoneStatus(MilestoneDesc.COMMENT_DEADLINE.getValue(),
                dateFormat.format(dartMileStone.getCommentsDeadlineDate()),
                dartMileStone.getCommentsDeadlineDate()));
          }
          if (!StringUtils.hasLength(batchDetail.getEaInd())) {
            batchDetail.setEaInd(dartMileStone.getEdbEaInd());
          }
        });
        batchDetail.setMilestones(milestones.stream().sorted(Comparator
            .comparing(MileStoneStatus::getMilestoneDateFormat, Comparator.reverseOrder()))
            .collect(Collectors.toList()));
        milestone.getBatchDetails().add(batchDetail);
      });
      milestone.getBatchDetails().forEach(batchDetail -> {
        if (!CollectionUtils.isEmpty(edbBatchAndGpPermit) 
            && StringUtils.hasLength(edbBatchAndGpPermit.get(batchDetail.getBatchNumber()))) {
          batchDetail.setGpPermitTypeCode(edbBatchAndGpPermit.get(batchDetail.getBatchNumber()));        
        }
        batchDetail
            .setPermitTransTypeApplId(batchIdAndPermitTypes.get(batchDetail.getBatchNumber()));
      });
    }
  }


  private void prepareDocumentsAndFilesDetails(String userId, String contextId, Long projectId,
      ProjectSummary projectSummary) {
    Map<String, Object> documents = new HashMap<>();
    List<SupportDocumentEntity> uploadedDocuments =
        supportDocumentRepo.findAllUploadedSupportDocumentsByProjectIdWithFilesCount(projectId);

    if (!CollectionUtils.isEmpty(uploadedDocuments)) {
      Map<Long, SupportDocumentEntity> documentIdAndFileCount = new HashMap<>();
      uploadedDocuments.forEach(uploadDocument -> {
        Integer fileCount = uploadDocument.getFileCount();
        logger.debug("Document Id {} File Count {}. User Id {}, Context Id {}",
            uploadDocument.getDocumentId(), fileCount, userId, contextId);
        if (fileCount != null) {
          if (documentIdAndFileCount.get(uploadDocument.getDocumentId()) != null) {
            documentIdAndFileCount.get(uploadDocument.getDocumentId()).setFileCount(
                documentIdAndFileCount.get(uploadDocument.getDocumentId()).getFileCount()
                    + fileCount);
          } else {
            documentIdAndFileCount.put(uploadDocument.getDocumentId(), uploadDocument);
          }
        }
      });
      List<String> documentNames = new ArrayList<>();
      List<Document> documentsList = new ArrayList<>();
      int documentLimit = 1;
      // int maxDocumentAllowed = 0;
      // if (onlineApplnIndicator != null && onlineApplnIndicator.equals(1)) {
      // projectSummary.setOnlineApplnInd("Y");
      // maxDocumentAllowed = documentIdAndFileCount.size()+1;
      // } else {
      // projectSummary.setOnlineApplnInd("N");
      // maxDocumentAllowed = 13;
      // }
      int maxDocumentAllowed = 13;
      for (SupportDocumentEntity uploadedDoc : documentIdAndFileCount.values()) {
        documentNames.add(uploadedDoc.getDocumentNm());
        if (documentLimit < maxDocumentAllowed) {
          Document document = new Document();
          document.setDocumentId(uploadedDoc.getDocumentId());
          document.setDescription(uploadedDoc.getDocumentDesc());
          document.setDocumentTitle(uploadedDoc.getDocumentNm());
          document.setRefDocumentDesc(uploadedDoc.getRefDocumentDesc());
          document.setReleasableCode(non_releasable_status.get(uploadedDoc.getDocReleasableCode()));
          document.setFileCount(uploadedDoc.getFileCount());
          if (uploadedDoc.getCreateDate() != null) {
            document.setUploadDate(dateFormat.format(uploadedDoc.getCreateDate()));
            document.setUploadDateFormat(uploadedDoc.getCreateDate());
          }
          documentsList.add(document);
        }
        documentLimit++;
      }
      documents.put("documentNames", documentNames);
      documentsList = documentsList.stream()
          .sorted(Comparator.comparing(Document::getUploadDateFormat, Comparator.reverseOrder()))
          .collect(Collectors.toList());
      documents.put("documents", documentsList);
    }
    projectSummary.setDocuments(documents);
  }


  private void prepareReviewDocuments(String userId, String contextId, final Long projectId,
      ProjectSummary projectSummary) {
    logger.info("Collecting Document Reviewer details for VW. User Id {}, Context Id {}", userId,
        contextId);
    List<DocumentReviewEntity> reviewDocumentsList =
        documentReviewRepo.findAllByProjectId(projectId);
    List<Document> reviewDocumentList = new ArrayList<>();
    Map<Long, Document> reviewDocumentDetailsMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(reviewDocumentsList)) {
      reviewDocumentsList.forEach(uploadDocument -> {
        if (uploadDocument.getDocReviewerName() != null) {
          if (reviewDocumentDetailsMap.get(uploadDocument.getCorrespondenceId()) == null) {
            Document reviewDocument = new Document();
            reviewDocument.setDocumentReviewId(uploadDocument.getDocumentReviewId());
            reviewDocument.setDocumentId(uploadDocument.getDocumentId());
            // reviewDocument.setDocumentReviewId(uploadDocument.getCorrespondenceId());
            reviewDocument.setDocReviewerName(uploadDocument.getDocReviewerName());
            reviewDocument.setDocReviewerId(uploadDocument.getDocReviewerId());
            reviewDocument.setDescription(uploadDocument.getDocumentDesc());
            reviewDocument.setDocumentTitle(uploadDocument.getDocumentNm());
            reviewDocument.setCorrespondenceId(uploadDocument.getCorrespondenceId());
            if (uploadDocument.getDocReviewedInd() != null
                && uploadDocument.getDocReviewedInd().equals(1)) {
              reviewDocument.setDocReviewedInd("Y");
            } else {
              reviewDocument.setDocReviewedInd("N");
            }
            if (uploadDocument.getReviewAssignedDate() != null) {
              reviewDocument
                  .setReviewAssignedDate(dateFormat.format(uploadDocument.getReviewAssignedDate()));
            }
            if (uploadDocument.getReviewDueDate() != null) {
              reviewDocument.setReviewDueDate(dateFormat.format(uploadDocument.getReviewDueDate()));
            }
            List<String> reviewDocumentNames = new ArrayList<>();
            reviewDocumentNames.add(uploadDocument.getDocumentNm());
            reviewDocument.setDocumentTitles(reviewDocumentNames);
            reviewDocumentDetailsMap.put(uploadDocument.getCorrespondenceId(), reviewDocument);
            reviewDocumentList.add(reviewDocument);
          } else {
            reviewDocumentDetailsMap.get(uploadDocument.getCorrespondenceId()).getDocumentTitles()
                .add(uploadDocument.getDocumentNm());
            reviewDocumentDetailsMap.get(uploadDocument.getCorrespondenceId())
                .setDocumentTitle(String.join(", ", reviewDocumentDetailsMap
                    .get(uploadDocument.getCorrespondenceId()).getDocumentTitles()));
          }
        }
      });
    }
    projectSummary.setReviewDocuments(reviewDocumentList);
  }


  private List<LitigationRequest> getLitigationRequestHistory(final Long projectId,
      List<LitigationRequest> litigationRequestHistoryList) {

    List<LitigationHoldHistory> litigationHoldHistoryList =
        litigationRequestHistoryRepo.findByProjectIdOrderByLitigationHoldHIdDesc(projectId);
    if (!CollectionUtils.isEmpty(litigationHoldHistoryList)) {
      if (!CollectionUtils.isEmpty(litigationHoldHistoryList)) {
        litigationRequestHistoryList = new LinkedList<>();
      }
      for (LitigationHoldHistory litigationHoldHistory : litigationHoldHistoryList) {
        LitigationRequest litigationRequestHistory = new LitigationRequest();
        litigationRequestHistory.setLitigationHoldId(litigationHoldHistory.getLitigationHoldId());
        if (litigationHoldHistory.getLitigationHoldStartDate() != null) {
          litigationRequestHistory.setLitigationStartDate(
              dateFormat.format(litigationHoldHistory.getLitigationHoldStartDate()));
        }
        if (litigationHoldHistory.getLitigationHoldEndDate() != null) {
          litigationRequestHistory.setLitigationEndDate(
              dateFormat.format(litigationHoldHistory.getLitigationHoldEndDate()));
        }
        litigationRequestHistoryList.add(litigationRequestHistory);
      }
      return litigationRequestHistoryList;
    }
    return null;
  }

  private LitigationRequest getLitigationRequest(final Long projectId,
      List<LitigationRequest> historyRequest) {

    LitigationHold litigationHold = litigationRequestRepo.findByProjectId(projectId);
    LitigationRequest litigationHistory = null;
    if (litigationHold != null) {
      LitigationRequest litigationRequest = new LitigationRequest();
      litigationRequest.setLitigationHoldId(litigationHold.getLitigationHoldId());

      if (litigationHold.getLitigationHoldEndDate() != null) {
        String litigationHoldEndDateString =
            dateFormat.format(litigationHold.getLitigationHoldEndDate());
        if (litigationHold.getLitigationHoldEndDate().before(new Date())) {
          litigationHistory = new LitigationRequest();
          litigationHistory.setLitigationHoldId(litigationHold.getLitigationHoldId());
          litigationHistory.setLitigationEndDate(litigationHoldEndDateString);
          historyRequest.add(litigationHistory);
        } else {
          litigationRequest.setLitigationEndDate(litigationHoldEndDateString);
        }
      }

      if (litigationHold.getLitigationHoldStartDate() != null) {
        String litigationHoldStartDateString =
            dateFormat.format(litigationHold.getLitigationHoldStartDate());

        if (CollectionUtils.isEmpty(historyRequest)) {
          litigationRequest.setLitigationStartDate(litigationHoldStartDateString);
        } else {
          historyRequest.get(0).setLitigationStartDate(litigationHoldStartDateString);
        }
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

  private PublicSummary createPublicSummary(PublicAndFacilityDetail publicSummary) {
    // private PublicSummary createPublicSummary(PublicSummary publicSummary) {
    PublicSummary summary = new PublicSummary();
    summary.setPublicId(publicSummary.getPublicId());
    summary.setEdbPublicId(publicSummary.getPublicId());
    summary.setDisplayName(publicSummary.getDisplayName());
    summary.setStreet1(publicSummary.getStreet1());
    if (StringUtils.hasLength(publicSummary.getCity())) {
      summary.setCity(publicSummary.getCity());
    } else {
      summary.setCity("");
    }
    if (StringUtils.hasLength(publicSummary.getState())) {
      summary.setState(publicSummary.getState());
    } else {
      summary.setState("");
    }
    if (StringUtils.hasLength(publicSummary.getZip())) {
      summary.setZip(publicSummary.getZip());
    } else {
      summary.setZip("");
    }
    summary.setBusinessPhoneNumber(
        publicSummary.getBusinessPhoneNumber() != null ? publicSummary.getBusinessPhoneNumber()
            : "");
    summary.setEmailAddress(publicSummary.getEmailAddress());
    return summary;
  }

  // private PublicSummary createContactSummary(PublicSummary publicSummary) {
  // PublicSummary summary = new PublicSummary();
  // summary.setPublicId(publicSummary.getPublicId());
  // summary.setEdbPublicId(publicSummary.getPublicId());
  // summary.setDisplayName(publicSummary.getDisplayName());
  // summary.setStreet1(publicSummary.getStreet1());
  // if (StringUtils.hasLength(publicSummary.getCity())) {
  // summary.setCity(publicSummary.getCity());
  // } else {
  // summary.setCity("");
  // }
  // if (StringUtils.hasLength(publicSummary.getState())) {
  // summary.setState(publicSummary.getState());
  // } else {
  // summary.setState("");
  // }
  // if (StringUtils.hasLength(publicSummary.getZip())) {
  // summary.setZip(publicSummary.getZip());
  // } else {
  // summary.setZip("");
  // }
  // summary.setBusinessPhoneNumber(
  // publicSummary.getBusinessPhoneNumber() != null ? publicSummary.getBusinessPhoneNumber()
  // : "");
  // summary.setEmailAddress(publicSummary.getEmailAddress());
  // return summary;
  // }

  @Override
  public Object getUsersByRegionAndRoleTypeId(final String userId, final String contextId,
      final Integer regionId, final Integer roleTypeId) {
    List<RegionUserEntity> regionUsersList =
        dartDBDAO.findAllTheUsersByRoleTypeId(userId, contextId, regionId, roleTypeId);
    List<RegionUserEntity> sortedRegionUsersList = null;
    if (!CollectionUtils.isEmpty(regionUsersList)) {
      regionUsersList.forEach(regionUser -> {
        String displayName = regionUser.getDisplayName();
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasLength(displayName)) {
          String[] splittedName = displayName.split(" ");
          if (splittedName.length == 2) {
            sb.append(splittedName[1]).append(" ").append(splittedName[0]);
          } else if (splittedName.length == 3) {
            sb.append(splittedName[2]).append(" ").append(splittedName[1]).append(" ")
                .append(splittedName[0]);
          } else {
            sb.append(displayName);
          }
        }
        regionUser.setDisplayName(sb.toString());
      });

      sortedRegionUsersList =
          regionUsersList.stream().sorted(Comparator.comparing(RegionUserEntity::getDisplayName))
              .collect(Collectors.toList());
    }
    return sortedRegionUsersList;
  }

  @Override
  public List<RegionUserEntity> getUsersWithValidEmailAddress(final String userId,
      final String contextId) {
    logger.info("Entering into getUsersWithValidEmailAddress. User Id {}, Context Id {}", userId,
        contextId);

    List<RegionUserEntity> regionUsersList =
        dartDBDAO.findAllTheUsersWithValidEmailAddress(userId, contextId);
    List<RegionUserEntity> sortedRegionUsersList = null;
    List<RegionUserEntity> filteredRegionUsersList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(regionUsersList)) {
      Map<String, String> userIdMap = new HashMap<>();
      regionUsersList.forEach(regionUser -> {
        if (StringUtils.hasLength(regionUser.getUserId())
            && userIdMap.get(regionUser.getUserId()) == null) {
          userIdMap.put(regionUser.getUserId(), regionUser.getUserId());
          String displayName = regionUser.getDisplayName();
          StringBuilder sb = new StringBuilder();
          if (StringUtils.hasLength(displayName)) {
            String[] splittedName = displayName.split(" ");
            if (splittedName.length == 2) {
              sb.append(splittedName[1]).append(" ").append(splittedName[0]);
            } else if (splittedName.length == 3) {
              sb.append(splittedName[2]).append(" ").append(splittedName[1]).append(" ")
                  .append(splittedName[0]);
            } else {
              sb.append(displayName);
            }
          }
          regionUser.setDisplayName(sb.toString());
          filteredRegionUsersList.add(regionUser);
        }
      });
      sortedRegionUsersList = filteredRegionUsersList.stream()
          .sorted(Comparator.comparing(RegionUserEntity::getDisplayName))
          .collect(Collectors.toList());
    }
    logger.info("Exiting from getUsersWithValidEmailAddress. User Id {}, Context Id {}", userId,
        contextId);
    return sortedRegionUsersList;
  }

  @Override
  public Object retrieveAssignmentDetails(String userId, String contextId, Long projectId) {
    return userAssignmentRepo.findUserAssignment(projectId);
  }

  @Override
  public List<Alert> retrieveAnalystsAlerts(String userId, String contextId) {
    List<ProjectAlert> alertsList = projectAlertRepo.findAllAlertsByUserId(userId);
    List<GIInquiryAlert> inquiryAlerts = giInquiryAlertRepo.findAllAlertsByUserId(userId);
    List<Alert> projectAndInquiryAlerts = new ArrayList<>();
    if (!CollectionUtils.isEmpty(alertsList)) {
      projectAndInquiryAlerts =
          transformationService.transformAlertMessage(userId, contextId, alertsList);
    }

    if (!CollectionUtils.isEmpty(inquiryAlerts)) {
      projectAndInquiryAlerts.addAll(
          transformationService.transformInquiryAlertMessage(userId, contextId, inquiryAlerts));
    }
    projectAndInquiryAlerts = projectAndInquiryAlerts.stream()
        .sorted(Comparator.comparing(Alert::getAlertDateFormat).reversed())
        .collect(Collectors.toList());
    return projectAndInquiryAlerts;
  }

  @Override
  public List<ReviewDocument> retrieveEligibleReviewDocuments(final String userId,
      final String contextId, final Long projectId) {
    return reviewDocumentRepo.findAllReviewEligibleDocuments(projectId);
  }

  @Override
  public Long findRegionIdByUserId(String userId, String contextId) {
    logger.info("Requesting to return Region Id for the input user id {}, Region Id {}", userId,
        contextId);
    return dartDBDAO.getUserRegionId(userId, contextId);
  }


  @SuppressWarnings("unchecked")
  @Override
  public Object retrieveSupportDetailsForDIMSR(final String userId, final String contextId,
      final String decId) {
    Map<String, Object> supportDetailsOfDIMSR =
        dartDBDAO.retrieveSupportDetailsForDIMSR(userId, contextId, decId);
    Map<String, Object> dimsrReports = new HashMap<>();

    List<FacilityLRPDetail> facilityLRPDetailsList =
        (List<FacilityLRPDetail>) supportDetailsOfDIMSR.get(DartDBConstants.FACILITY_CURSOR);
    if (CollectionUtils.isEmpty(facilityLRPDetailsList)) {
      throw new NoDataFoundException("NO_FAC_AVAILBLE",
          "There is no facility associated with this DEC ID" + decId);
    }

    FacilityLRP facilityLrp = new FacilityLRP();
    FacilityLRPDetail facilityLRPDetail = facilityLRPDetailsList.get(0);
    facilityLrp.setDecId(facilityLRPDetail.getDecId());
    facilityLrp.setEdbDistrictId(facilityLRPDetail.getEdbDistrictId());
    facilityLrp.setFacilityname(facilityLRPDetail.getFacilityName());
    List<String> activeLrps = new ArrayList<>();

    facilityLRPDetailsList.forEach(facilityLrpDetail -> {
      activeLrps.add(facilityLrpDetail.getPublicName());
    });
    facilityLrp.setActiveLRPs(activeLrps);
    dimsrReports.put("facility", facilityLrp);

    List<DartPermit> existingApplicationsDetailsList =
        (List<DartPermit>) supportDetailsOfDIMSR.get(DartDBConstants.EXISTING_APPS_CURSOR);

    Map<Long, List<PermitApplication>> existingPermits = new HashMap<>();

    if (!CollectionUtils.isEmpty(existingApplicationsDetailsList)) {
      existingApplicationsDetailsList.forEach(dartExistingPermit -> {
        PermitApplication permitApplication = new PermitApplication();
        permitApplication.setBatchId(dartExistingPermit.getBatchId());
        permitApplication.setProgramId(dartExistingPermit.getTrackedId());
        permitApplication.setProgramIdFormatted(dartExistingPermit.getTrackedIdFormatted());
        permitApplication.setProjectDesc(dartExistingPermit.getProjectDesc());
        permitApplication.setEdbDistrictId(dartExistingPermit.getDistrictId());
        permitApplication.setPermitTypeCode(dartExistingPermit.getPermitType());
        permitApplication.setPermitTypeDesc(dartExistingPermit.getPermitDesc());
        permitApplication.setEdbApplicationId(dartExistingPermit.getApplId());
        permitApplication.setTransType(dartExistingPermit.getTransType());
        permitApplication.setEdbTrackingInd(dartExistingPermit.getTrackingInd());

        if (StringUtils.hasLength(dartExistingPermit.getReceivedDate())) {
          permitApplication.setReceivedDate(dartExistingPermit.getReceivedDate());
        }
        if (dartExistingPermit.getStartDate() != null) {
          permitApplication
              .setEffectiveStartDate(dateFormat.format(dartExistingPermit.getStartDate()));
        }
        if (dartExistingPermit.getExpiryDate() != null) {
          permitApplication
              .setEffectiveEndDate(dateFormat.format(dartExistingPermit.getExpiryDate()));
        }
        if (existingPermits.get(dartExistingPermit.getBatchId()) == null) {
          List<PermitApplication> existingPermitsList = new ArrayList<>();
          existingPermitsList.add(permitApplication);
          existingPermits.put(dartExistingPermit.getBatchId(), existingPermitsList);
        } else {
          existingPermits.get(dartExistingPermit.getBatchId()).add(permitApplication);
        }
      });
    }

    /*
     * if (!CollectionUtils.isEmpty(existingApplicationsDetailsList)) {
     * existingApplicationsDetailsList.forEach(dartExistingPermit -> { String renewIndicator =
     * dartExistingPermit.getRenewedInd(); String formattedBatchIdWithRenewInd =
     * String.valueOf(dartExistingPermit.getBatchId()).concat("000").concat(renewIndicator);
     * 
     * PermitApplication permitApplication = new PermitApplication();
     * permitApplication.setBatchId(dartExistingPermit.getBatchId());
     * permitApplication.setProgramId(dartExistingPermit.getTrackedId());
     * permitApplication.setProgramIdFormatted(dartExistingPermit.getTrackedIdFormatted());
     * permitApplication.setProjectDesc(dartExistingPermit.getProjectDesc());
     * permitApplication.setEdbDistrictId(dartExistingPermit.getDistrictId());
     * permitApplication.setPermitTypeCode(dartExistingPermit.getPermitType());
     * permitApplication.setPermitTypeDesc(dartExistingPermit.getPermitDesc());
     * permitApplication.setEdbApplicationId(dartExistingPermit.getApplId());
     * permitApplication.setTransType(dartExistingPermit.getTransType());
     * permitApplication.setEdbTrackingInd(dartExistingPermit.getTrackingInd());
     * 
     * if (StringUtils.hasLength(dartExistingPermit.getReceivedDate())) {
     * permitApplication.setReceivedDate(dartExistingPermit.getReceivedDate()); } if
     * (dartExistingPermit.getStartDate() != null) { permitApplication
     * .setEffectiveStartDate(dateFormat.format(dartExistingPermit.getStartDate())); } if
     * (dartExistingPermit.getExpiryDate() != null) { permitApplication
     * .setEffectiveEndDate(dateFormat.format(dartExistingPermit.getExpiryDate())); } if
     * (existingPermits.get(formattedBatchIdWithRenewInd) == null) { List<PermitApplication>
     * existingPermitsList = new ArrayList<>(); existingPermitsList.add(permitApplication);
     * existingPermits.put(formattedBatchIdWithRenewInd, existingPermitsList); } else {
     * existingPermits.get(formattedBatchIdWithRenewInd).add(permitApplication); } }); }
     */
    dimsrReports.put("existingPermits", existingPermits);

    List<DartApplication> pendingApplicationsList =
        (List<DartApplication>) supportDetailsOfDIMSR.get(DartDBConstants.P_PENDING_APPS_CURSOR);

    Map<Long, List<PermitApplication>> pendingPermits = new HashMap<>();
    Map<String, String> applicationExists = new HashMap<>();
    if (!CollectionUtils.isEmpty(pendingApplicationsList)) {
      pendingApplicationsList.forEach(pendingApplication -> {
        if (applicationExists.get(pendingApplication.getTrackedId()) == null) {
          applicationExists.put(pendingApplication.getTrackedId(),
              pendingApplication.getTrackedId());
          PermitApplication permitApplication = new PermitApplication();
          permitApplication.setBatchId(pendingApplication.getBatchId());
          permitApplication.setProgramId(pendingApplication.getTrackedId());
          permitApplication.setProgramIdFormatted(pendingApplication.getTrackedIdFormatted());
          permitApplication.setProjectDesc(pendingApplication.getProjectDesc());
          permitApplication.setEdbDistrictId(pendingApplication.getDistrictId());
          permitApplication.setPermitTypeCode(pendingApplication.getPermitType());
          permitApplication.setPermitTypeDesc(pendingApplication.getPermitDesc());
          permitApplication.setTransType(pendingApplication.getTransType());
          if (pendingApplication.getReceivedDate() != null) {
            permitApplication
                .setReceivedDate(dateFormat.format(pendingApplication.getReceivedDate()));
          }
          if (pendingApplication.getStartDate() != null) {
            permitApplication
                .setEffectiveStartDate(dateFormat.format(pendingApplication.getStartDate()));
          }
          if (pendingApplication.getExpiryDate() != null) {
            permitApplication
                .setEffectiveEndDate(dateFormat.format(pendingApplication.getExpiryDate()));
          }
          if (pendingPermits.get(pendingApplication.getBatchId()) == null) {
            List<PermitApplication> pendingPermitsList = new ArrayList<>();
            pendingPermitsList.add(permitApplication);
            pendingPermits.put(pendingApplication.getBatchId(), pendingPermitsList);
          } else {
            pendingPermits.get(pendingApplication.getBatchId()).add(permitApplication);
          }
        }
      });
    }
    dimsrReports.put("pendingPermits", pendingPermits);
    return dimsrReports;
  }

  @Override
  public List<String> retrieveActiveAuthorizationPermits(String userId, String contextId,
      Long projectId, Long edbDistrictId) {

    logger.info("Entering into retrieveActiveAuthorizationPermits. User Id {}, Context Id {}",
        userId, contextId);

    if (projectId == null || projectId <= 0 || edbDistrictId == null || edbDistrictId <= 0) {
      throw new BadRequestException("PRJ_ID_DIST_ID_NA",
          "Project ID or Edb District Id is passed incorrect/empty", projectId);
    }
    dec.ny.gov.etrack.dart.db.entity.Facility facilityEntity =
        facilityRepo.findByProjectId(projectId);

    if (facilityEntity == null || !edbDistrictId.equals(facilityEntity.getEdbDistrictId())) {
      throw new BadRequestException("NO_FAC_AVAIL",
          "There is no existing facility is associated with this project " + projectId, projectId);
    }
    Map<String, Object> existingAuthorizationsData =
        dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId);

    @SuppressWarnings("unchecked")
    List<DartPermit> dartModifyExtendEligiblePermits =
        (List<DartPermit>) existingAuthorizationsData.get(DartDBConstants.EXISTING_APPS_CURSOR);
    List<String> activeAuthorizationPermits = new ArrayList<>();
    if (!CollectionUtils.isEmpty(dartModifyExtendEligiblePermits)) {
      dartModifyExtendEligiblePermits.forEach(dartActivePermit -> {
        activeAuthorizationPermits.add(dartActivePermit.getPermitDesc());
      });
    }
    logger.info("Exiting from retrieveActiveAuthorizationPermits. User Id {}, Context Id {}",
        userId, contextId);
    return activeAuthorizationPermits;
  }

  @Override
  public List<Alert> viewAnalystDashboardAlerts(final String userId, final String contextId) {
    List<ProjectAlert> alertsList = projectAlertRepo.findAllAlertsByUserId(userId);
    List<GIInquiryAlert> inquiryAlerts = giInquiryAlertRepo.findAllAlertsByUserId(userId);
    List<Alert> alerts = new ArrayList<>();
    
    if (!CollectionUtils.isEmpty(alertsList)) {
      alertsList.forEach(alert -> {
        if (!StringUtils.hasLength(alert.getComments())) {
          projectAlertRepo.deleteByAlertId(alert.getProjectId(), alert.getProjectAlertId());
        }
      });
      alerts = transformationService.transformAlertMessage(userId, contextId, alertsList);
    }
    if (!CollectionUtils.isEmpty(inquiryAlerts)) {
      inquiryAlerts.forEach(alert -> {
        if (!StringUtils.hasLength(alert.getComments())) {
          giInquiryAlertRepo.deleteByInquiryIdAndAlertId(alert.getInquiryId(), alert.getInquiryAlertId());
        }
      });
      alerts.addAll(transformationService.transformInquiryAlertMessage(userId, contextId, inquiryAlerts));
    }
    alerts = alerts.stream()
        .sorted(Comparator.comparing(Alert::getAlertDateFormat).reversed())
        .collect(Collectors.toList());
    return alerts;
  }

  @Override
  public ProjectRejectDetail retrieveProjectRejectionDetails(final String userId,
      final String contextId, final Long projectId) {
    List<String> rejectionDetails = projectRepo.retrieveProjectRejectionDetails(projectId);
    ProjectRejectDetail projectRejectDetail = new ProjectRejectDetail();
    if (!CollectionUtils.isEmpty(rejectionDetails)) {
      String[] projectRejectedDetails = rejectionDetails.get(0).split(",");
      projectRejectDetail.setProjectId(projectRejectedDetails[0]);
      projectRejectDetail.setRejectReason(projectRejectedDetails[1]);
      projectRejectDetail.setFacilityName(projectRejectedDetails[2]);
    }
    return projectRejectDetail;
  }
}
