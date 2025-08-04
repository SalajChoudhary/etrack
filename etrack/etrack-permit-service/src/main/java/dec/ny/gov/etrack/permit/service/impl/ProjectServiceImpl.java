package dec.ny.gov.etrack.permit.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.management.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dec.ny.gov.etrack.permit.dao.ETrackKeywordDAO;
import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.Application;
import dec.ny.gov.etrack.permit.entity.DocumentReviewEntity;
import dec.ny.gov.etrack.permit.entity.EmailCorrespondence;
import dec.ny.gov.etrack.permit.entity.Facility;
import dec.ny.gov.etrack.permit.entity.FacilityAddr;
import dec.ny.gov.etrack.permit.entity.FacilityBIN;
import dec.ny.gov.etrack.permit.entity.FacilityPolygon;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonCounty;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonMunicipality;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonRegion;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonTaxMap;
import dec.ny.gov.etrack.permit.entity.Project;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;
import dec.ny.gov.etrack.permit.entity.ProjectAlert;
import dec.ny.gov.etrack.permit.entity.ProjectDevelopment;
import dec.ny.gov.etrack.permit.entity.ProjectInquiryAssociate;
import dec.ny.gov.etrack.permit.entity.ProjectKeywordEntity;
import dec.ny.gov.etrack.permit.entity.ProjectNote;
import dec.ny.gov.etrack.permit.entity.ProjectPolygon;
import dec.ny.gov.etrack.permit.entity.ProjectProgramAppln;
import dec.ny.gov.etrack.permit.entity.ProjectProgramDistrict;
import dec.ny.gov.etrack.permit.entity.ProjectResidential;
import dec.ny.gov.etrack.permit.entity.ProjectSICNAICSCode;
import dec.ny.gov.etrack.permit.entity.ProjectSWFacilityType;
import dec.ny.gov.etrack.permit.entity.ProjectSpecialAttention;
import dec.ny.gov.etrack.permit.entity.RegionUserEntity;
import dec.ny.gov.etrack.permit.entity.SupportDocument;
import dec.ny.gov.etrack.permit.entity.SystemDetectedKeyword;
import dec.ny.gov.etrack.permit.entity.UploadPolygonEntity;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.ActivityTaskStatus;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.BridgeIdNumber;
import dec.ny.gov.etrack.permit.model.DIMSRRequest;
import dec.ny.gov.etrack.permit.model.DartUploadDetail;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.EmailContent;
import dec.ny.gov.etrack.permit.model.IngestionRequest;
import dec.ny.gov.etrack.permit.model.IngestionResponse;
import dec.ny.gov.etrack.permit.model.PermitTaskStatus;
import dec.ny.gov.etrack.permit.model.PolygonStatus;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.ProjectInfo;
import dec.ny.gov.etrack.permit.model.ReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;
import dec.ny.gov.etrack.permit.model.SWFacilitySubType;
import dec.ny.gov.etrack.permit.model.SWFacilityType;
import dec.ny.gov.etrack.permit.repo.ApplicationRepo;
import dec.ny.gov.etrack.permit.repo.DocumentReviewRepo;
import dec.ny.gov.etrack.permit.repo.EmailCorrespondenceRepo;
import dec.ny.gov.etrack.permit.repo.FacilityAddrRepo;
import dec.ny.gov.etrack.permit.repo.FacilityBINRepo;
import dec.ny.gov.etrack.permit.repo.FacilityPolygonCountyRepo;
import dec.ny.gov.etrack.permit.repo.FacilityPolygonMunicipalityRepo;
import dec.ny.gov.etrack.permit.repo.FacilityPolygonRegionRepo;
import dec.ny.gov.etrack.permit.repo.FacilityPolygonRepo;
import dec.ny.gov.etrack.permit.repo.FacilityPolygonTaxMapRepo;
import dec.ny.gov.etrack.permit.repo.FacilityRepo;
import dec.ny.gov.etrack.permit.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.permit.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.permit.repo.ProjectDevelopRepo;
import dec.ny.gov.etrack.permit.repo.ProjectInquiryAssociateRepo;
import dec.ny.gov.etrack.permit.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.permit.repo.ProjectPolygonRepo;
import dec.ny.gov.etrack.permit.repo.ProjectProgramApplnRepo;
import dec.ny.gov.etrack.permit.repo.ProjectProgramDistrictRepo;
import dec.ny.gov.etrack.permit.repo.ProjectRepo;
import dec.ny.gov.etrack.permit.repo.ProjectResidentialRepo;
import dec.ny.gov.etrack.permit.repo.ProjectSICNAICSCodeRepo;
import dec.ny.gov.etrack.permit.repo.ProjectSWFacilityTypeRepo;
import dec.ny.gov.etrack.permit.repo.ProjectSpecialAttentionRepo;
import dec.ny.gov.etrack.permit.repo.PublicRepo;
import dec.ny.gov.etrack.permit.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.permit.repo.UploadPolygonRepo;
import dec.ny.gov.etrack.permit.service.ETrackKeywordService;
import dec.ny.gov.etrack.permit.service.ProjectService;
import dec.ny.gov.etrack.permit.util.ETrackPermitConstant;

@Service
public class ProjectServiceImpl implements ProjectService {

  @Autowired
  private ProjectDevelopRepo projectDevelopRepo;

  @Autowired
  private ProjectResidentialRepo projectResidentialRepo;

  @Autowired
  private ProjectSICNAICSCodeRepo projectSICNAICSCodeRepo;

  @Autowired
  private ProjectRepo projectRepo;

  @Autowired
  private FacilityBINRepo facilityBinRepo;

  @Autowired
  private ProjectNoteRepo projectNoteRepo;

  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;

  @Autowired
  private ProjectActivityRepo projectActivityRepo;
  @Autowired
  private ETrackPermitDAO eTrackPermitDAO;
  
  @Autowired
  private SupportDocumentRepo supportDocumentRepo;

  @Autowired
  private ProjectAlertRepo projectAlertRepo;

  @Autowired
  private ProjectSWFacilityTypeRepo projectSWFacilityTypeRepo;

  @Autowired
  private EmailCorrespondenceRepo emailCorrespondenceRepo;

  @Autowired
  private FacilityRepo facilityRepo;

  @Autowired
  private FacilityAddrRepo facilityAddrRepo;
  @Autowired
  private FacilityPolygonRepo facilityPolygonRepo;
  @Autowired
  private FacilityPolygonCountyRepo countyRepo;
  @Autowired
  private FacilityPolygonMunicipalityRepo municipalityRepo;
  @Autowired
  private FacilityPolygonRegionRepo facilityPolygonRegionRepo;
  @Autowired
  private FacilityPolygonTaxMapRepo taxMapRepo;
  @Autowired
  private DocumentReviewRepo documentReviewRepo;
  @Autowired
  private PublicRepo publicRepo;
  @Autowired
  private ApplicationRepo applicationRepo;
  @Autowired
  private UploadPolygonRepo uploadEligiblePolygonRepo;
  @Autowired
  private ProjectPolygonRepo projectPolygonRepo;
  @Autowired
  private ProjectProgramApplnRepo programApplnRepo;
  @Autowired
  private ProjectProgramDistrictRepo programDistrictRepo;
  @Autowired
  private ProjectSpecialAttentionRepo projectSpecialAttentionRepo;
  @Autowired
  private TransformationService transformationService;
  @Value("${etrack.email.correspondence.from.address}")
  private String emailCorrespondenceFromAddress;
  @Autowired
  private DocumentUploadService documentUploadService;
  @Autowired
  private ETrackKeywordDAO eTrackKeywordDAO;
  @Autowired
  private ETrackKeywordService eTrackKeywordService;
  @Autowired
  private ProjectInquiryAssociateRepo projectInquiryAssociateRepo;
  
  private final SimpleDateFormat MM_DD_YYYY_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
  private final DateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyyMMddHHmmss");
  private final SimpleDateFormat mmDDYYYFormatAMPM = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class.getName());

  @Override
  public ProjectDetail saveProject(String userId, String contextId, ProjectDetail projectDetail) {
    logger.info("Save the project details into database Context id {}", contextId);
    Date receivedDate = null;
    try {
      MM_DD_YYYY_FORMAT.setLenient(false);
      if (StringUtils.hasText(projectDetail.getReceivedDate())) {
        receivedDate = MM_DD_YYYY_FORMAT.parse(projectDetail.getReceivedDate());
      }
    } catch (ParseException e) {
      throw new BadRequestException("INVALID_RCVD_DATE", "Received invalid format ReceivedDate",
          projectDetail);
    }

    if (!StringUtils.hasLength(projectDetail.getRegions())) {
      throw new BadRequestException("REGION_NOT_AVAIL", "Region is not available in the request",
          projectDetail);
    }

    String[] regions = projectDetail.getRegions().split(",");
    if (!StringUtils.hasLength(projectDetail.getPrimaryRegion())) {
      projectDetail.setPrimaryRegion(regions[0]);
    }

    if (!StringUtils.hasLength(projectDetail.getMunicipalities())) {
      throw new BadRequestException("MUNICIPALITY_NOT_AVAIL",
          "Municipality is not available in the request", projectDetail);
    }
    
    if (!StringUtils.hasLength(projectDetail.getPrimaryMunicipality())) {
      String[] municipalities = projectDetail.getMunicipalities().split(",");
      projectDetail.setPrimaryMunicipality(municipalities[0]);;
    } else {
      String primaryMunicipality = projectDetail.getPrimaryMunicipality();
      List<String> municipalities = Arrays.asList(projectDetail.getMunicipalities().split(","));
      List<String> updatedMunicipalities = new ArrayList<>();
      if (!municipalities.contains(primaryMunicipality)) {
        updatedMunicipalities.add(primaryMunicipality);
        municipalities.forEach(municipality -> {
          updatedMunicipalities.add(municipality);
        });
        projectDetail.setMunicipalities(String.join(",", updatedMunicipalities));
      }
    }
    // New facility
    if (projectDetail.getFacility() != null
        && projectDetail.getFacility().getEdbDistrictId() == null) {
      saveNewFacilityAndProject(userId, contextId, projectDetail);
    } else {
      // Create a project for the existing facility
      eTrackPermitDAO.saveProjectDetails(userId, contextId, projectDetail, receivedDate);
      eTrackPermitDAO.updateProjectDetails(userId, contextId, projectDetail, receivedDate);

      // if (StringUtils.hasLength(projectDetail.getOnlineSubmissionInd())
      // && projectDetail.getOnlineSubmissionInd().equalsIgnoreCase("Y")) {
      // projectRepo.updateOnlineSubmissionInd(userId, projectDetail.getProjectId());
      // }
    }
    /*
     * LoginUser loginUser = projectDetail.getLoggedInUser(); if (loginUser != null) {
     * logger.info("Received the application from Online Portal. " +
     * "Applicant details needs to be marked as Online Submitter. User Id {}, Context Id {}",
     * userId, contextId); OnlineUser onlineUser = new OnlineUser(); onlineUser.setCreateDate(new
     * Date()); onlineUser.setCreatedById(userId);
     * onlineUser.setFirstName(loginUser.getFirstName());
     * onlineUser.setLastName(loginUser.getLastName());
     * onlineUser.setEmailAddress(loginUser.getEmailAddress());
     * onlineUser.setProjectId(projectDetail.getProjectId()); onlineUser.setOnlineUserTypeCode("O");
     * //Online Submitter onlineUserRepo.save(onlineUser); }
     */

    try {
      if (projectDetail.getPolygonStatus().equals(PolygonStatus.APPLICANT_SUBMITTED)) {
        ProjectActivity projectActivity = new ProjectActivity();
        projectActivity.setActivityStatusId(ActivityTaskStatus.SEL_PROJ_LOC.getActivityStatus());
        projectActivity.setProjectId(projectDetail.getProjectId());
        projectActivity.setStartDate(new Date());
        projectActivity.setCompletionDate(new Date());
        projectActivity.setCreateDate(new Date());
        projectActivity.setCreatedById(userId);
        projectActivityRepo.save(projectActivity);
      }
    } catch (Exception e) {
      throw new ETrackPermitException("SAVE_PROJ_ACTIVITY_DB_ERROR",
          "Error while saving the Project activity details ", e);
    }
    return projectDetail;
  }

  @Override
  @Transactional(rollbackFor = {DataExistException.class})
  public ProjectDetail updateProject(final String userId, final String contextId,
      final String token, final ProjectDetail projectDetail) {

    logger.info("Update the project details into database. User Id {} Context id {}", userId,
        contextId);

    Long projectId = projectDetail.getProjectId();
    dec.ny.gov.etrack.permit.model.FacilityDetail facility = projectDetail.getFacility();
    if (facility == null || facility.getAddress() == null) {
      throw new BadRequestException("FACILITY_DETAIL_EMPTY",
          "Facility details or Facility Address details is empty", projectDetail);
    }
    Facility facilityEntity = facilityRepo.findByProjectId(projectId);
    if (facilityEntity == null) {
      throw new DataNotFoundException("NO_PROJECT_FOUND",
          "There is no project/facility associated with this project " + projectId);
    }
    try {
      if (StringUtils.hasLength(projectDetail.getValidatedInd())) {
        if (projectDetail.getValidatedInd().equals("Y")) {
          projectRepo.updateValidateInd(userId, projectId, new Date(), 1);
          projectDetail.setPolygonStatus(PolygonStatus.ANALYST_APPROVED);
          // facilityPolygonRepo.updateFacilityPolygonDetail(ETrackPermitConstant.ANALYST_APPROVED,
          // projectId);
        } else {
          projectRepo.updateValidateInd(userId, projectId, new Date(), 0);
        }
      } else if (projectDetail.getPolygonStatus() != null
          && projectDetail.getPolygonStatus().compareTo(PolygonStatus.ANALYST_APPROVED) == 1) {
        projectRepo.updateValidateInd(userId, projectId, new Date(), 1);
        projectDetail.setPolygonStatus(PolygonStatus.ANALYST_APPROVED);
      }
      if (projectDetail.getHasSameGeometry() != null) {
        projectRepo.updateApprovedPolygonChangeInd(userId, projectId, new Date(),
            projectDetail.getHasSameGeometry());
      }
    } catch (Exception e) {
      throw new ETrackPermitException("FACILITY_POLYGON_UPDATE_DB_ERROR",
          "Error while updating the Facility Polygon Update details", e);
    }

    if (StringUtils.isEmpty(facilityEntity.getDecId())
        && StringUtils.hasLength(projectDetail.getFacility().getDecIdFormatted())) {
      logger.info("Attaching the Public Facility into eTrack. User Id {}, Context Id {}", userId,
          contextId);
      eTrackPermitDAO.populateFaciltyDataIntoETrack(userId, contextId, projectId,
          projectDetail.getFacility().getDecIdFormatted());
    }
    
    String primaryMunicipality = projectDetail.getPrimaryMunicipality();
    if (StringUtils.hasLength(primaryMunicipality)) {
      List<String> municipalities = Arrays.asList(projectDetail.getMunicipalities().split(","));
      List<String> updatedMunicipalities = new ArrayList<>();
      if (!municipalities.contains(primaryMunicipality)) {
        updatedMunicipalities.add(primaryMunicipality);
        municipalities.forEach(municipality -> {
          updatedMunicipalities.add(municipality);
        });
        projectDetail.setMunicipalities(String.join(",", updatedMunicipalities));
      }
    }
    Date receivedDate = null;
    try {
      MM_DD_YYYY_FORMAT.setLenient(false);
      if (StringUtils.hasText(projectDetail.getReceivedDate())) {
        receivedDate = MM_DD_YYYY_FORMAT.parse(projectDetail.getReceivedDate());
      }
      eTrackPermitDAO.updateProjectDetails(userId, contextId, projectDetail, receivedDate);
    } catch (ParseException e) {
      throw new BadRequestException("INVALID_RCVD_DATE", "Received invalid format ReceivedDate",
          projectDetail);
    } catch (DataExistException e) {
      projectRepo.updateValidateInd(userId, projectId, new Date(), 0);
      if (projectDetail.getFacility().getEdbDistrictId() == null 
          || projectDetail.getFacility().getEdbDistrictId().equals(0L)) {
        throw new DataExistException(e.getErrorCode(),"[]");
      }
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-dart-db/active-authorization-permit/"
              + projectDetail.getFacility().getEdbDistrictId())
          .build().toString();
      logger.info("Requesting DART DB Service to get the eTrack Active Authorizations. "
          + "User Id: {}, Context Id: {}", userId, contextId);
      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("contextId", contextId);
      headers.add("projectId", String.valueOf(projectId));
      headers.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
      ParameterizedTypeReference<List<String>> typeRef =
          new ParameterizedTypeReference<List<String>>() {};
      try {
        List<String> permits = eTrackOtherServiceRestTemplate
            .exchange(uri, HttpMethod.GET, requestEntity, typeRef).getBody();
        throw new DataExistException(e.getErrorCode(),
            new ObjectMapper().writeValueAsString(permits));
      } catch (DataExistException de) {
        throw de;
      } catch (HttpServerErrorException hse) {
        throw new ETrackPermitException(hse.getStatusCode(), "RECEIVED_ACTIVE_PERMIT_ERR",
            "Received error from Dart DB service " + "while requesting Active Authorization "
                + hse.getResponseBodyAsString());
      } catch (Exception ex) {
        throw new ETrackPermitException("RECEIVED_ACTIVE_PERMIT_GENERAL_ERR",
            "Received error from Dart DB service " + "while requesting Active Authorization", ex);
      }
    }
    // Update Region details by calling separate statement.
    boolean applicantSubmitted = false;
    try {
      if (projectDetail.getPolygonStatus().equals(PolygonStatus.APPLICANT_SUBMITTED)) {
        applicantSubmitted = true;
        List<ProjectActivity> projectActivityList =
            projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId,
                ActivityTaskStatus.SEL_PROJ_LOC.getActivityStatus());
        if (CollectionUtils.isEmpty(projectActivityList)) {
          ProjectActivity projectActivity = new ProjectActivity();
          projectActivity.setActivityStatusId(ActivityTaskStatus.SEL_PROJ_LOC.getActivityStatus());
          projectActivity.setProjectId(projectDetail.getProjectId());
          projectActivity.setStartDate(new Date());
          projectActivity.setCompletionDate(new Date());
          projectActivity.setCreateDate(new Date());
          projectActivity.setCreatedById(userId);
          projectActivityRepo.save(projectActivity);
        }
      }
    } catch (Exception e) {
      throw new ETrackPermitException("PROJECT_ACTIVITY_DB_ERROR",
          "Error while updating the Project Activity details", e);
    }
    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (projectAvailability.isPresent()
        && projectAvailability.get().getOriginalSubmittalInd() != null
        && projectAvailability.get().getOriginalSubmittalInd().equals(1)) {
      associateInquiriesToProject(userId, projectId, projectDetail.getInquiries(), false);
    }
    if (projectDetail.getPolygonStatus().equals(PolygonStatus.APPLICANT_SCRATCH)) {
      applicantSubmitted = true;
    }
    if (StringUtils.hasLength(projectDetail.getPrintUrl())) {
      try {
        documentUploadService.uploadGISPrintFormattedMapDocumentToDMS(userId, contextId, token,
            projectId, projectDetail.getPrintUrl(), facility.getDecId(), applicantSubmitted, false);        
      } catch (ETrackPermitException e) {
       ProjectNote projectNote = new ProjectNote();
       if (StringUtils.hasLength(e.getMessage()) 
           && (e.getMessage().equals("UNSUCCESSFUL_TO_RETRIEVE_MAP") 
               || e.getMessage().equals("UNABLE_TO_RETRIEVE_MAP"))) {
         
         projectNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
         projectNote.setCreateDate(new Date());
         projectNote.setActionDate(new Date());
         projectNote.setProjectId(projectId);
         projectNote.setActionTypeCode(17);
         projectNote.setActionNote("Failed to rtrieve the Facility Map for the " + projectId + " from GIS System.");
         projectNoteRepo.save(projectNote);
       }
       throw e;
      }
    }
    return projectDetail;
  }

  private void associateInquiriesToProject(final String userId, final Long projectId,
      Set<Long> inquiries, final boolean requestedFromVW) {

    logger.info("Associate the inquiries {} with this input project Id {}", inquiries, projectId);
    if (!CollectionUtils.isEmpty(inquiries)) {
      Set<Long> validInquiries = new HashSet<>();
      inquiries.forEach(inquiry -> {
        if (inquiry != null && inquiry > 0) {
          validInquiries.add(inquiry);
        }
      });

      int count = projectInquiryAssociateRepo.findByInquiriesList(validInquiries);
      if (count != validInquiries.size()) {
        throw new BadRequestException("INQUIRY_DOESNOT_EXIST",
            "Invalid GI ID. Please verify your entry.", inquiries);
      }
      List<ProjectInquiryAssociate> projectInquiryAssociates = new ArrayList<>();
      validInquiries.forEach(inquiryId -> {
        ProjectInquiryAssociate projectInquiryAssociate = new ProjectInquiryAssociate();
        projectInquiryAssociate.setProjectId(projectId);
        projectInquiryAssociate.setInquiryId(inquiryId);
        projectInquiryAssociate.setCreatedById(userId);
        projectInquiryAssociate.setCreateDate(new Date());
        projectInquiryAssociates.add(projectInquiryAssociate);
      });
      if (!requestedFromVW) {
        projectInquiryAssociateRepo.deleteByProjectId(projectId);
      }
      if (!CollectionUtils.isEmpty(projectInquiryAssociates)) {
        projectInquiryAssociateRepo.saveAll(projectInquiryAssociates);
      }
    }
  }

  /**
   * This method is used to store all the details for the new Facility.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique transaction request.
   * @param projectDetail - new facility detail.
   * 
   * @return
   */
  @Transactional(rollbackFor = {BadRequestException.class, ETrackPermitException.class})
  private ProjectDetail saveNewFacilityAndProject(String userId, String contextId,
      ProjectDetail projectDetail) {
    try {
      logger.info(
          "Save the new facility and project details into database. User Id {}. Context Id {}",
          userId, contextId);

      if (projectDetail.getFacility() == null || projectDetail.getFacility().getAddress() == null) {
        throw new BadRequestException("FACILITY_DETAIL_EMPTY",
            "Facility details or Facility Address details is empty", projectDetail);
      }

      Project project =
          transformationService.transformToProjectEntity(userId, contextId, projectDetail);
      projectRepo.save(project);
      final Long projectId = project.getProjectId();
      projectDetail.setProjectId(projectId);

      // Save facility
      Facility facility = transformationService.transformToFacilityEntity(userId, contextId,
          projectDetail.getFacility(), projectId);
      facility.setComments(projectDetail.getReason());
      facility.setChgBoundaryReason(projectDetail.getBoundaryChangeReason());
      logger.info("Add the facility details into database");
      facilityRepo.save(facility);
      logger.info("Facility is persisted in the table. Context Id {}", contextId);

      // Save Facility address
      FacilityAddr facilityAddr = transformationService.transformToFacilityAddressEntity(userId,
          contextId, projectDetail.getFacility(), projectId);

      logger.info("Add the facility address details into database");
      facilityAddrRepo.save(facilityAddr);
      logger.info("Facility Address record is created in the table. Context Id {}", contextId);

      // Save Facility Polygon
      FacilityPolygon facilityPolygon = new FacilityPolygon();
      facilityPolygon.setPolygonGisId(projectDetail.getPolygonId());
      facilityPolygon.setCreateDate(new Date());
      facilityPolygon.setCreatedById(userId);
      facilityPolygon.setPolygonTypeCode(projectDetail.getPolygonStatus().getStatus());
      facilityPolygon.setProjectId(projectId);
      facilityPolygon.setLatitude(projectDetail.getLatitude());
      facilityPolygon.setLongitude(projectDetail.getLongitude());
      facilityPolygon.setWorkAreaPolygonId(projectDetail.getWorkAreaId());
      if (projectDetail.getNytmx() == null 
          || projectDetail.getNytmy() == null) {
        throw new BadRequestException("CO_ORDINATE_REF_PTS_EMPTY", 
            "Co-Ordinates reference points cannot be empty/blank", projectId);
      }
      facilityPolygon.setNytmnCoordinate(projectDetail.getNytmx());
      facilityPolygon.setNytmeCoordinate(projectDetail.getNytmy());
      logger.info("Add the facility Polygon details into database User id {}.  Context Id {}",
          userId, contextId);
      facilityPolygon = facilityPolygonRepo.save(facilityPolygon);
      logger.info("Facility Polygon record is created in the table. User id {}.  Context Id {}",
          userId, contextId);

      if (StringUtils.hasLength(projectDetail.getRegions())) {
        logger.info(
            "Add the facility Polygon region details into database User id {}.  Context Id {}",
            userId, contextId);
        String[] regions = projectDetail.getRegions().split(",");
        boolean primaryRegionAvailableWithRegionlist = false;
        for (String region : regions) {
          if (StringUtils.hasLength(projectDetail.getPrimaryRegion())
              && projectDetail.getPrimaryRegion().equals(region.trim())) {
            primaryRegionAvailableWithRegionlist = true;
          }
        }
        
        for (int index= 0; index < regions.length; index++) {
          FacilityPolygonRegion facilityPolygonRegion = new FacilityPolygonRegion();
          facilityPolygonRegion.setCreateDate(new Date());
          facilityPolygonRegion.setCreatedById(userId);
          facilityPolygonRegion.setDepRegionId(Integer.parseInt(regions[index].trim()));
          facilityPolygonRegion.setFacilityPolygonId(facilityPolygon.getFacilityPolygonId());
          if (index == 0 && StringUtils.hasLength(projectDetail.getPrimaryRegion()) 
              &&  !primaryRegionAvailableWithRegionlist) {
            facilityPolygonRegion.setPrimaryInd(1); 
          } else if (StringUtils.hasLength(projectDetail.getPrimaryRegion())
              && projectDetail.getPrimaryRegion().equals(regions[index].trim())) {
            facilityPolygonRegion.setPrimaryInd(1);
          } else {
            facilityPolygonRegion.setPrimaryInd(0);
          }
          facilityPolygonRegionRepo.save(facilityPolygonRegion);
        }
        logger.info(
            "Added the facility Polygon region details into database User id {}.  Context Id {}",
            userId, contextId);
      }

      if (StringUtils.hasLength(projectDetail.getCounties())) {
        logger.info("Add the facility County details into database User id {}.  Context Id {}",
            userId, contextId);
        String[] countiesWithSwisCodeList = projectDetail.getCounties().split(",");
        for (String countiesWithSwis : countiesWithSwisCodeList) {
          String[] countySwisAndCtTypeArr = countiesWithSwis.split("-");
          FacilityPolygonCounty facilityPolygonCounty = new FacilityPolygonCounty();
          facilityPolygonCounty.setCreateDate(new Date());
          facilityPolygonCounty.setCreatedById(userId);
          if (countySwisAndCtTypeArr.length >= 2 && countySwisAndCtTypeArr[1].length() > 2) {
            facilityPolygonCounty.setCountySwisCode(countySwisAndCtTypeArr[1].substring(0, 2));
          }
          facilityPolygonCounty.setCounty(countySwisAndCtTypeArr[0]);
          facilityPolygonCounty.setFacilityPolygonId(facilityPolygon.getFacilityPolygonId());
          countyRepo.save(facilityPolygonCounty);
        }
        logger.info(
            "Added the facility Polygon County details into database User id {}.  Context Id {}",
            userId, contextId);
      }

      if (StringUtils.hasLength(projectDetail.getMunicipalities())) {
        logger.info(
            "Add the facility polygon Municipality details into database User id {}.  Context Id {}",
            userId, contextId);
        String[] municipalitiesWithSwisCodeList = projectDetail.getMunicipalities().split(",");
        for (String municipalitiesWithSwis : municipalitiesWithSwisCodeList) {
          String[] municipalityAndSwis = municipalitiesWithSwis.split("-");
          FacilityPolygonMunicipality facilityPolygonMunicipality =
              new FacilityPolygonMunicipality();
          if (StringUtils.hasLength(projectDetail.getPrimaryMunicipality())
              && projectDetail.getPrimaryMunicipality().equalsIgnoreCase(municipalitiesWithSwis)) {
            facilityPolygonMunicipality.setPrimaryInd(1);
          } else {
            facilityPolygonMunicipality.setPrimaryInd(0);
          }
          facilityPolygonMunicipality.setCreateDate(new Date());
          facilityPolygonMunicipality.setCreatedById(userId);
          if (municipalityAndSwis != null && municipalityAndSwis.length >= 2) {
            facilityPolygonMunicipality.setMuniSwisCode(municipalityAndSwis[1]);
          }
          if (municipalityAndSwis.length >= 3) {
            facilityPolygonMunicipality
                .setCtType(Integer.parseInt(municipalityAndSwis[2].substring(0, 1)));
          }
          facilityPolygonMunicipality.setMunicipality(municipalityAndSwis[0]);
          facilityPolygonMunicipality.setFacilityPolygonId(facilityPolygon.getFacilityPolygonId());
          municipalityRepo.save(facilityPolygonMunicipality);
        }
        logger.info(
            "Added the facility Polygon Municipality details into database User id {}.  Context Id {}",
            userId, contextId);
      }
      if (StringUtils.hasLength(projectDetail.getTaxmaps())) {
        FacilityPolygonTaxMap facilityPolygonTaxMap = new FacilityPolygonTaxMap();
        facilityPolygonTaxMap.setCreateDate(new Date());
        facilityPolygonTaxMap.setCreatedById(userId);
        facilityPolygonTaxMap.setFacilityPolygonId(facilityPolygon.getFacilityPolygonId());
        facilityPolygonTaxMap.setTaxmapNumber(projectDetail.getTaxmaps());
        taxMapRepo.save(facilityPolygonTaxMap);
      }
    } catch (BadRequestException be) {
      throw be;
    } catch (Exception e) {
      throw new ETrackPermitException("POPULATE_NEW_FACILITY_ERROR",
          "Error while saving new facility and associated tables. Error details " + e.getMessage(),
          e);
    }
    return projectDetail;
  }

  @Override
  public ProjectDetail retrieveProjectDetail(String userId, String contextId, Long projectId) {
    logger.info("Entering into retrieveProjectDetail User Id {}, Context Id {}", userId, contextId);

    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException("INVALID_REQ", "Invalid Request body", projectId);
    }

    Facility facility = facilityRepo.findByProjectId(projectId);
    if (facility == null) {
      throw new BadRequestException("NO_FACILITY_AVAILABLE",
          "There is no facility associated with this project id " + projectId, projectId);
    }

    FacilityAddr facilityAddress = facilityAddrRepo.findByProjectId(projectId);
    if (facilityAddress == null) {
      throw new BadRequestException("NO_FACILITY_ADR_AVAILABLE",
          "There is no Facility address associated with this project Id " + projectId, projectId);
    }
    try {

      FacilityPolygon facilityPolygon = null;
      facilityPolygon = facilityPolygonRepo.findAnalystScratchPolygonByProjectIdAndPolygonTypeCode(
          projectId, ETrackPermitConstant.ANALYST_SCRATCH_POLYGON);
      if (facilityPolygon == null) {
        List<FacilityPolygon> facilityPolygonList =
            facilityPolygonRepo.findByProjectIdOrderByFacilityPolygonIdAsc(projectId);
        if (CollectionUtils.isEmpty(facilityPolygonList)) {
          throw new BadRequestException("NO_POLYGON_AVAILABLE",
              "There is no Polygon created for this facility ", projectId);
        }
        facilityPolygon = facilityPolygonList.get(facilityPolygonList.size() - 1);
      }
      String region = null;
      Set<String> countiesList = new HashSet<>();
      Set<String> municipalitiesList = new HashSet<>();
      String counties = null;
      String municipalities = null;
      String taxMapNumber = null;
      String primaryFacilityMunicipality = null;
      Integer primaryFacilityRegion = null;
      Long facilityPolygonId = facilityPolygon.getFacilityPolygonId();
      if (facilityPolygonId != null && facilityPolygonId > 0) {
        List<FacilityPolygonRegion> facilityPolygonRegionList =
            facilityPolygonRegionRepo.findAllRegionsByFacilityPolygonId(facilityPolygonId);
        List<String> regions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(facilityPolygonRegionList)) {
          for (FacilityPolygonRegion facilityPolygonRegion : facilityPolygonRegionList) {
            regions.add(String.valueOf(facilityPolygonRegion.getDepRegionId()));
            if (facilityPolygonRegion.getPrimaryInd() != null
                && facilityPolygonRegion.getPrimaryInd() == 1) {
              primaryFacilityRegion = facilityPolygonRegion.getDepRegionId();
            }
          }
          region = String.join(",", regions);
        }
        List<FacilityPolygonCounty> countiesDetails =
            countyRepo.findAllCountiesByFacilityPolygonId(facilityPolygonId);

        if (!CollectionUtils.isEmpty(countiesDetails)) {
          for (FacilityPolygonCounty county : countiesDetails) {
            if (StringUtils.hasLength(county.getCounty())) {
              StringBuilder sb = new StringBuilder();
              sb.append(county.getCounty());
              if (StringUtils.hasLength(county.getCountySwisCode())) {
                sb.append("-").append(county.getCountySwisCode());
              }
              countiesList.add(sb.toString());
            }
          }
          counties = String.join(",", countiesList);
        }

        List<FacilityPolygonMunicipality> municipalitiesDetails =
            municipalityRepo.findAllMunicipalitiesByFacilityPolygonId(facilityPolygonId);
        if (!CollectionUtils.isEmpty(municipalitiesDetails)) {
          for (FacilityPolygonMunicipality municipality : municipalitiesDetails) {
            if (StringUtils.hasLength(municipality.getMunicipality())) {
              StringBuilder sb = new StringBuilder();
              sb.append(municipality.getMunicipality());
              if (StringUtils.hasLength(municipality.getMuniSwisCode())) {
                sb.append("-").append(municipality.getMuniSwisCode());
              }
              if (municipality.getCtType() != null) {
                sb.append("-").append(municipality.getCtType());
              }
              if (municipality.getPrimaryInd() == 1) {
                primaryFacilityMunicipality = sb.toString();
              }
              municipalitiesList.add(sb.toString());
            }
            municipalities = String.join(",", municipalitiesList);
          }
        }
        taxMapNumber = taxMapRepo.findTaxmapNumberByFacilityPolygonId(facilityPolygonId);
        logger.debug("List of Counties {} and Municipalities {}  for the Facility Polygon Id {}",
            counties, municipalities, facilityPolygonId);
      }
      ProjectDetail projectDetail = transformationService.transformFacilityToProjectDetail(userId,
          contextId, projectAvailability.get(), facility, facilityAddress, facilityPolygon, region,
          counties, municipalities, taxMapNumber);
      projectDetail.setPrimaryMunicipality(primaryFacilityMunicipality);
      projectDetail.setPrimaryRegion(String.valueOf(primaryFacilityRegion));
      logger.info("Exiting from retrieveProjectDetail User Id {}, Context Id {}", userId,
          contextId);
      return projectDetail;
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      throw new ETrackPermitException("PROJ_DETAIL_RETRIEVAL_ERROR",
          "Error while retrieving the Project details for the Project " + projectId, e);
    }
  }

  @Override
  public List<PermitTaskStatus> getProjectPermitStatus(final String userId, final String contextId,
      final Long projectId, Integer mode) {
    logger.info("Entering into Project permit status User Id: {}, Context Id {}", userId,
        contextId);

    List<ProjectActivity> projectActivities = null;
    Set<PermitTaskStatus> permitTaskStatus = new HashSet<>();
    try {

      if (mode == null || mode == 0) {
        projectActivities = projectActivityRepo.findAllByProjectIdAndActivityStatusIdLTOREQ(
            projectId, ActivityTaskStatus.SIGNATURE.getActivityStatus());
        projectActivities.forEach(projectActivity -> {
          PermitTaskStatus taskStatus = new PermitTaskStatus();
          taskStatus.setProjectId(projectActivity.getProjectId());
          taskStatus.setActivityStatusId(projectActivity.getActivityStatusId());
//          taskStatus.setProjectActivityStatusId(projectActivity.getProjectActivityStatusId());
          taskStatus.setCompleted(projectActivity.getCompletionDate() != null ? "Y" : "N");
          permitTaskStatus.add(taskStatus);
        });
      } else if (mode == 1) {
        projectActivities = projectActivityRepo.findAllByProjectIdAndActivityStatusIdGT(projectId,
            ActivityTaskStatus.SIGNATURE.getActivityStatus());
        Optional<Project> projectAvailability = projectRepo.findById(projectId);
        if (projectAvailability.isPresent()) {
          Project project = projectAvailability.get();
          Integer validatedInd = project.getValidatedInd();
          PermitTaskStatus taskStatus = new PermitTaskStatus();
          taskStatus.setProjectId(project.getProjectId());
          taskStatus.setActivityStatusId(ETrackPermitConstant.SEL_PROJ_LOC);
          if (validatedInd == null || validatedInd == 0) {
            taskStatus.setCompleted("N");
          } else if (validatedInd == 1) {
            taskStatus.setCompleted("Y");
          }
          permitTaskStatus.add(taskStatus);
        }
        List<Long> contacts = publicRepo.findAllContactsByProjectId(projectId);
        projectActivities.forEach(projectActivity -> {
          PermitTaskStatus taskStatus = new PermitTaskStatus();
          taskStatus.setProjectId(projectActivity.getProjectId());
//          taskStatus.setProjectActivityStatusId(projectActivity.getProjectActivityStatusId());
          if (projectActivity.getActivityStatusId().equals(ETrackPermitConstant.SUPPORT_DOC_VAL)) {
            taskStatus.setActivityStatusId(ETrackPermitConstant.UPLOAD_DOC);
            taskStatus.setCompleted(projectActivity.getCompletionDate() != null ? "Y" : "N");
          } else if (projectActivity.getActivityStatusId()
              .equals(ETrackPermitConstant.SUBMIT_PROJ_VAL)) {
            taskStatus.setActivityStatusId(ETrackPermitConstant.SIGNATURE);
            taskStatus.setCompleted(projectActivity.getCompletionDate() != null ? "Y" : "N");
          } else if (projectActivity.getActivityStatusId()
              .equals(ETrackPermitConstant.APPLICANT_VAL)) {
            List<Integer> activityStatusIds = new ArrayList<>();
            activityStatusIds.add(ETrackPermitConstant.APPLICANT_VAL);
            activityStatusIds.add(ETrackPermitConstant.PROP_OWNER_VAL);
            if (!CollectionUtils.isEmpty(contacts) && contacts.size() > 1) {
              activityStatusIds.add(ETrackPermitConstant.CONTACT_AGENT_VAL);
              if (StringUtils.isEmpty(taskStatus.getCompleted())) {
                taskStatus
                    .setCompleted(getTaskStatus(userId, contextId, activityStatusIds, projectId));
              }
            } else {
              logger.info("There is no contacts or only one contacts. "
                  + "So, checking whether Public and Owners are Marked the status as completed  {}",
                  contacts);
              taskStatus
                  .setCompleted(getTaskStatus(userId, contextId, activityStatusIds, projectId));
            }
            taskStatus.setActivityStatusId(ETrackPermitConstant.APPLICANT_INFO);
          } else if (projectActivity.getActivityStatusId()
              .equals(ETrackPermitConstant.PERMIT_SUMMARY_VAL)) {
            List<Integer> activityStatusIds = new ArrayList<>();
            activityStatusIds.add(ETrackPermitConstant.PERMIT_SUMMARY_VAL);
            activityStatusIds.add(ETrackPermitConstant.PROJ_DESC_VAL);
            if (CollectionUtils.isEmpty(contacts) || contacts.size() == 1) {
              logger.info(
                  "There is no contacts or only one contacts. Marked the status as completed  {}",
                  contacts);
              taskStatus.setCompleted(getTaskStatus(userId, contextId, activityStatusIds, projectId));
              if ("Y".equals(taskStatus.getCompleted())) {
                taskStatus.setCompleted("Y");
              }
            } else {
              activityStatusIds.add(ETrackPermitConstant.ASSIGN_CONTACT_VAL);
              taskStatus.setCompleted(getTaskStatus(userId, contextId, activityStatusIds, projectId));
            }
            taskStatus.setActivityStatusId(ETrackPermitConstant.PROJECT_INFO);
          }
          if ("Y".equals(taskStatus.getCompleted())) {
            permitTaskStatus.add(taskStatus);
          }
        });
      } else {
        throw new BadRequestException("INVALID_MODE_PASSED",
            "Mode of operation is neither Data Entry nor Validate passed ", mode);
      }

      if (CollectionUtils.isEmpty(projectActivities)) {
        throw new DataNotFoundException("NO_PROJECT_ACTIVITY_FOUND",
            "No data found for the project " + projectId);
      }
      logger.info("Exiting from Project permit status User Id: {}, Context Id: {}", userId,
          contextId);
    } catch (BadRequestException | DataNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("PERMIT_STATUS_ERROR",
          "Error while retrieving the Project Permit Status.", e);
    }
    return new ArrayList<>(permitTaskStatus);
  }

  private String getTaskStatus(final String userId, final String contextId,
      List<Integer> activityStatusIds, Long projectId) {

    List<ProjectActivity> activitiesList =
        projectActivityRepo.findProjectActivitiesByIds(activityStatusIds, projectId);
    if (!CollectionUtils.isEmpty(activitiesList)
        && activityStatusIds.size() == activitiesList.size()) {
      return "Y";
    } else {
      return "N";
    }
  }

  /**
   * This method is used to store/update the facility bridge ID numbersepo;
   * 
   * @param projectInfo - Project information details
   * @param userId - User who initiates this request
   * @param contextId - Unique id to track the request
   */
  private void storeFacilityBINDetails(final ProjectInfo projectInfo, final Long projectId,
      final String userId, final String contextId) {

    logger.info("Entering into Store facility Bridge Id number details. Context Id {}", contextId);
    List<FacilityBIN> facilityBinNumbersList = facilityBinRepo.findByProjectId(projectId);

    List<FacilityBIN> inputFacilityBinNumbersList = null;
    Map<String, String> enterpriseBinNumberMap = new HashMap<>();

    Date createDate = null;
    String createdBy = null;
    if (!CollectionUtils.isEmpty(facilityBinNumbersList)) {
      createDate = facilityBinNumbersList.get(0).getCreateDate();
      createdBy = facilityBinNumbersList.get(0).getCreatedById();
      logger.info(
          "Delete the existing facility Bridge Id number details for update. User Id {} Context Id {}",
          userId, contextId);
      facilityBinNumbersList.forEach(facilityBinNumber -> {
        if (StringUtils.hasLength(facilityBinNumber.getEdbBin())) {
          enterpriseBinNumberMap.put(facilityBinNumber.getEdbBin(), facilityBinNumber.getBin());
        }
      });
      facilityBinNumbersList.forEach(facilityBin -> {
        if (StringUtils.hasLength(facilityBin.getEdbBin())
            && (facilityBin.getDeletedInd() == null || facilityBin.getDeletedInd() == 0)) {
          facilityBinRepo.updateDeleteInd(facilityBin.getFacilityBinId(), 1, userId, new Date());
        } else if (StringUtils.isEmpty(facilityBin.getEdbBin())) {
          facilityBinRepo.delete(facilityBin);
        }
      });
    }

    if (!CollectionUtils.isEmpty(projectInfo.getBinNumbers())) {
      inputFacilityBinNumbersList = new ArrayList<>();
      for (BridgeIdNumber bridgeIdNumber : projectInfo.getBinNumbers()) {
        if (StringUtils.hasLength(bridgeIdNumber.getEdbBin())) {
          facilityBinRepo.updateEdbBinAndDeleteInd(bridgeIdNumber.getEdbBin(), 0, userId,
              new Date());
        } else if (enterpriseBinNumberMap.get(bridgeIdNumber.getBin()) != null) {
          logger.info(
              "Mapping the eTrack Bin number with existing enterprise BIN if its already mapped to this facility");
          facilityBinRepo.updateEdbBinAndDeleteInd(bridgeIdNumber.getBin(), 0, userId, new Date());
        } else {
          FacilityBIN facilityBIN = new FacilityBIN();
          facilityBIN.setBin(bridgeIdNumber.getBin());
          facilityBIN.setProjectId(projectId);
          if (createDate != null) {
            facilityBIN.setCreateDate(createDate);
            facilityBIN.setCreatedById(createdBy);
            facilityBIN.setModifiedDate(new Date());
            facilityBIN.setModifiedById(userId);
          } else {
            facilityBIN.setCreateDate(new Date());
            facilityBIN.setCreatedById(userId);
          }
          inputFacilityBinNumbersList.add(facilityBIN);
        }
      }
      facilityBinRepo.saveAll(inputFacilityBinNumbersList);
    }
    logger.info("Exiting from Store facility Bridge Id number details. Context Id {}", contextId);
  }

  @Override
  @Transactional
  public ProjectInfo storeProjectInfo(final String userId, final String contextId,
      final Long projectId, final ProjectInfo projectInfo) {

    // Add project Development details
    logger.info("Entering into storeProjectInfo(). User Id {} Context Id {}", userId, contextId);

    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException("INVALID_PROJ_ID", "Project Id is invalid",
          projectAvailability);
    }

    if (projectInfo != null) {
      Project project = projectAvailability.get();
      if (project.getOriginalSubmittalInd() == null
          || project.getOriginalSubmittalInd().equals(0)) {

        if (StringUtils.hasLength(projectInfo.getBriefDesc())
            && (!StringUtils.hasLength(project.getProjectDesc())
                || !project.getProjectDesc().equals(projectInfo.getBriefDesc()))) {

          List<SystemDetectedKeyword> systemDetectedKeywords =
              eTrackKeywordDAO.retrieveSystemDetectedKeywords(userId, contextId, projectId,
                  projectInfo.getBriefDesc());
          if (!CollectionUtils.isEmpty(systemDetectedKeywords)) {
            List<ProjectKeywordEntity> projectKeywordTexts = new ArrayList<>();

            systemDetectedKeywords.forEach(systemDetectedKeyword -> {
              ProjectKeywordEntity projectKeywordEntity = new ProjectKeywordEntity();
              projectKeywordEntity.setCreatedById(userId);
              projectKeywordEntity.setCreateDate(new Date());
              projectKeywordEntity.setKeywordId(systemDetectedKeyword.getKeywordId());
              projectKeywordEntity.setSystemDetected(1);
              projectKeywordTexts.add(projectKeywordEntity);
            });
            eTrackKeywordService.persistSystemDetecteKeywordTextToProject(userId, contextId,
                projectId, projectKeywordTexts);
          }
        }
      }
      List<ProjectDevelopment> existingProjectDevelopement =
          projectDevelopRepo.findByProjectId(projectId);
      Date createDate = null;
      String createdBy = null;
      boolean updateInd = false;

      if (!CollectionUtils.isEmpty(existingProjectDevelopement)) {
        updateInd = true;
        createDate = existingProjectDevelopement.get(0).getCreateDate();
        createdBy = existingProjectDevelopement.get(0).getCreatedById();
        projectDevelopRepo.deleteAll(existingProjectDevelopement);
      }

      if (!CollectionUtils.isEmpty(projectInfo.getDevelopmentType())) {
        List<ProjectDevelopment> projectDevelopmentList = new ArrayList<>();
        for (Integer developmentType : projectInfo.getDevelopmentType()) {
          ProjectDevelopment development = new ProjectDevelopment();
          development.setProjectId(projectId);
          development.setDevelopmentTypeCode(developmentType);
          if (updateInd) {
            development.setCreatedById(createdBy);
            development.setCreateDate(createDate);
            development.setModifiedById(userId);
            development.setModifiedDate(new Date());
          } else {
            development.setCreatedById(userId);
            development.setCreateDate(new Date());
          }
          projectDevelopmentList.add(development);
        }
        projectDevelopRepo.saveAll(projectDevelopmentList);
      }
      updateInd = false;
      List<ProjectResidential> existingProjectResidentials =
          projectResidentialRepo.findByProjectId(projectId);
      if (!CollectionUtils.isEmpty(existingProjectResidentials)) {
        updateInd = true;
        createDate = existingProjectResidentials.get(0).getCreateDate();
        createdBy = existingProjectResidentials.get(0).getCreatedById();
        projectResidentialRepo.deleteAll(existingProjectResidentials);
      }
      if (!CollectionUtils.isEmpty(projectInfo.getStructureType())) {
        List<ProjectResidential> projectResidentialList = new ArrayList<>();
        for (Integer structureType : projectInfo.getStructureType()) {
          ProjectResidential residential = new ProjectResidential();
          if (updateInd) {
            residential.setCreatedById(createdBy);
            residential.setCreateDate(createDate);
            residential.setModifiedById(userId);
            residential.setModifiedDate(new Date());
          } else {
            residential.setCreatedById(userId);
            residential.setCreateDate(new Date());
          }
          residential.setProjectId(projectId);
          residential.setResDevTypeCode(structureType);
          projectResidentialList.add(residential);
        }
        projectResidentialRepo.saveAll(projectResidentialList);
      }

      updateInd = false;
      List<ProjectSICNAICSCode> existingProjectSICNaicsCode =
          projectSICNAICSCodeRepo.findByProjectId(projectId);
      if (!CollectionUtils.isEmpty(existingProjectSICNaicsCode)) {
        updateInd = true;
        createDate = existingProjectSICNaicsCode.get(0).getCreateDate();
        createdBy = existingProjectSICNaicsCode.get(0).getCreatedById();
        projectSICNAICSCodeRepo.deleteAll(existingProjectSICNaicsCode);
      }
      if (!CollectionUtils.isEmpty(projectInfo.getSicCodeNaicsCode())) {
        List<ProjectSICNAICSCode> projecSICNaicsCodeList = new ArrayList<>();

        for (Map<String, String> sicNaicsCode : projectInfo.getSicCodeNaicsCode()) {
          ProjectSICNAICSCode projectSICNAICSCode = new ProjectSICNAICSCode();
          if (updateInd) {
            projectSICNAICSCode.setCreatedById(createdBy);
            projectSICNAICSCode.setCreateDate(createDate);
            projectSICNAICSCode.setModifiedById(userId);
            projectSICNAICSCode.setModifiedDate(new Date());
          } else {
            projectSICNAICSCode.setCreatedById(userId);
            projectSICNAICSCode.setCreateDate(new Date());
          }
          projectSICNAICSCode.setProjectId(projectId);
          String naicsCode = null;
          for (String sicCode : sicNaicsCode.keySet()) {
            naicsCode = sicNaicsCode.get(sicCode);
            projectSICNAICSCode.setSicCode(sicCode);
            projectSICNAICSCode.setNaicsCode(naicsCode);
            projecSICNaicsCodeList.add(projectSICNAICSCode);
          }
        }
        projectSICNAICSCodeRepo.saveAll(projecSICNaicsCodeList);
      }

      Integer seqrInd = null;
      if (projectInfo.getClassifiedUnderSeqr() == null) {
        seqrInd = projectAvailability.get().getSeqrInd();
      } else {
        seqrInd = projectInfo.getClassifiedUnderSeqr();
      }
      projectRepo.updateProjectInfo(projectInfo.getBriefDesc(), projectInfo.getProposedUse(),
          projectId, userId, new Date(), projectInfo.getConstrnType(),
          projectInfo.getProposedStartDateVal(), projectInfo.getEstmtdCompletionDateVal(),
          projectInfo.getStrWaterbodyName(), projectInfo.getWetlandIds(), projectInfo.getDamType(),
          seqrInd);

      storeFacilityBINDetails(projectInfo, projectId, userId, contextId);
      logger.info("Updateig the Project SolidWaste Facility Type . User Id {}, Context Id {}",
          userId, contextId);
      List<ProjectSWFacilityType> projectSWFacilityTypes =
          projectSWFacilityTypeRepo.findAllByProjectId(projectId);

      List<SWFacilityType> swFacilityTypes = projectInfo.getSwFacilityTypes();

      Date createdDate = null;
      if (!CollectionUtils.isEmpty(projectSWFacilityTypes)) {
        logger.info("Deleting the existing Project facility types associated with this "
            + "project {}. User Id {}, Context Id {}", projectId, userId, contextId);
        createdBy = projectSWFacilityTypes.get(0).getCreatedById();
        createdDate = projectSWFacilityTypes.get(0).getCreateDate();
        projectSWFacilityTypeRepo.deleteAll(projectSWFacilityTypes);
      }

      if (!CollectionUtils.isEmpty(swFacilityTypes)) {
        projectSWFacilityTypes = new ArrayList<>();
        for (SWFacilityType swFacilityType : swFacilityTypes) {
          if (CollectionUtils.isEmpty(swFacilityType.getSwFacilitySubTypes())) {
            ProjectSWFacilityType prjSwFacilityType = prepareProjecSWFacilityType(userId, createdBy,
                createdDate, projectId, swFacilityType.getSwFacilityType());
            projectSWFacilityTypes.add(prjSwFacilityType);
          } else {
            for (SWFacilitySubType subType : swFacilityType.getSwFacilitySubTypes()) {
              ProjectSWFacilityType prjSwFacilityType = prepareProjecSWFacilityType(userId,
                  createdBy, createdDate, projectId, swFacilityType.getSwFacilityType());
              prjSwFacilityType.setSwFacilitySubTypeId(subType.getSwfacilitySubType());
              projectSWFacilityTypes.add(prjSwFacilityType);
            }
          }
        }
        projectSWFacilityTypeRepo.saveAll(projectSWFacilityTypes);
      }
    }

    logger.info("Updating the project {} information activity detail as completed Context id {}",
        projectId, contextId);

    List<ProjectActivity> projectActivityList =
        projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId,
            ActivityTaskStatus.PROJECT_INFO.getActivityStatus());


    logger.info("Program Application refresh if any data is passed. User Id {}, Context Id {}",
        userId, contextId);
    List<ProjectProgramAppln> projectProgramApplnList = programApplnRepo.findByProjectId(projectId);
    if (!CollectionUtils.isEmpty(projectProgramApplnList)) {
      programApplnRepo.deleteAll(projectProgramApplnList);
    }
    List<ProjectProgramAppln> projectProgramApplns =
        prepareProjectProgramAppln(userId, projectId, projectInfo);
    if (!CollectionUtils.isEmpty(projectProgramApplns)) {
      programApplnRepo.saveAll(projectProgramApplns);
    }

    logger.info("Program District refresh if any data is passed. User Id {}, Context Id {}", userId,
        contextId);
    List<ProjectProgramDistrict> projectProgramDistrictList =
        programDistrictRepo.findByProjectId(projectId);
    if (!CollectionUtils.isEmpty(projectProgramDistrictList)) {
      programDistrictRepo.deleteAll(projectProgramDistrictList);
    }
    List<ProjectProgramDistrict> newProjectProgramDistricts =
        prepareProjectProgramDistrict(userId, projectId, projectInfo);
    if (!CollectionUtils.isEmpty(newProjectProgramDistricts)) {
      programDistrictRepo.saveAll(newProjectProgramDistricts);
    }

    List<ProjectSpecialAttention> projectSpecialAttentionList =
        projectSpecialAttentionRepo.findByProjectId(projectId);
    logger.info(
        "Project Special Attention refresh if any data is passed. User Id {}, Context Id {}",
        userId, contextId);
    if (!CollectionUtils.isEmpty(projectSpecialAttentionList)) {
      logger.info("Delete all the existing special attention for the project id {}. "
          + "User Id {}, Context Id {}", projectId, userId, contextId);
      projectSpecialAttentionRepo.deleteAll(projectSpecialAttentionList);
    }
    List<ProjectSpecialAttention> newProjectSpecialAttnCodes =
        prepareProjectSpecialAttention(userId, projectId, projectInfo);
    if (!CollectionUtils.isEmpty(newProjectSpecialAttnCodes)) {
      projectSpecialAttentionRepo.saveAll(newProjectSpecialAttnCodes);
    }

    ProjectActivity projectActivity = null;
    if (CollectionUtils.isEmpty(projectActivityList)) {
      projectActivity = new ProjectActivity();
      projectActivity.setProjectId(projectId);
      projectActivity.setActivityStatusId(ActivityTaskStatus.PROJECT_INFO.getActivityStatus());
      projectActivity.setStartDate(new Date());
      projectActivity.setCreateDate(new Date());
      projectActivity.setCreatedById(userId);
      projectActivity.setModifiedDate(new Date());
      projectActivity.setModifiedById(userId);
      projectActivity.setCompletionDate(new Date());
      projectActivityRepo.save(projectActivity);
    } else {
      projectActivity = projectActivityList.get(0);
      if (projectActivity.getCompletionDate() == null) {
        logger.info("Updating project {} activity status as completed  Context Id : {}", projectId,
            contextId);
        projectActivity.setStartDate(projectActivity.getStartDate());
        projectActivity.setCreateDate(projectActivity.getCreateDate());
        projectActivity.setCreatedById(projectActivity.getCreatedById());
        projectActivity.setModifiedDate(new Date());
        projectActivity.setModifiedById(userId);
        projectActivity.setCompletionDate(new Date());
        projectActivityRepo.save(projectActivity);
      }
    }
    /** - ends here */
    projectInfo.setProjectId(projectId);
    return projectInfo;
  }

  private List<ProjectProgramDistrict> prepareProjectProgramDistrict(final String userId,
      final Long projectId, final ProjectInfo projectInfo) {

    if (!CollectionUtils.isEmpty(projectInfo.getProgramIds())) {
      List<ProjectProgramDistrict> programDistricts = new ArrayList<>();

      projectInfo.getProgramIds().keySet().forEach(programId -> {
        projectInfo.getProgramIds().get(programId).forEach(programIdValue -> {
          ProjectProgramDistrict projectProgramDistrict = new ProjectProgramDistrict();
          projectProgramDistrict.setProjectId(projectId);
          projectProgramDistrict.setProgramDistrictCode(programId);
          projectProgramDistrict.setProgramDistrictIdentifier(programIdValue);
          projectProgramDistrict.setCreateDate(new Date());
          projectProgramDistrict.setCreatedById(userId);
          programDistricts.add(projectProgramDistrict);
        });
      });
      return programDistricts;
    }
    return null;
  }

  private List<ProjectProgramAppln> prepareProjectProgramAppln(final String userId,
      final Long projectId, final ProjectInfo projectInfo) {
    if (!CollectionUtils.isEmpty(projectInfo.getXtraIds())) {
      List<ProjectProgramAppln> programApplications = new ArrayList<>();
      projectInfo.getXtraIds().keySet().forEach(xtraId -> {
        ProjectProgramAppln projectProgramAppln = new ProjectProgramAppln();
        projectProgramAppln.setProjectId(projectId);
        projectProgramAppln.setCreatedById(userId);
        projectProgramAppln.setCreateDate(new Date());
        projectProgramAppln.setProgramApplicationCode(xtraId);
        projectProgramAppln.setProgramApplicationIdentifier(projectInfo.getXtraIds().get(xtraId));
        programApplications.add(projectProgramAppln);
      });
      return programApplications;
    }
    return null;
  }

  private List<ProjectSpecialAttention> prepareProjectSpecialAttention(final String userId,
      final Long projectId, final ProjectInfo projectInfo) {

    if (!CollectionUtils.isEmpty(projectInfo.getSplAttnCodes())) {
      List<ProjectSpecialAttention> specialAttnCodes = new ArrayList<>();
      projectInfo.getSplAttnCodes().forEach(splAttnCode -> {
        ProjectSpecialAttention specialAttentionCode = new ProjectSpecialAttention();
        specialAttentionCode.setProjectId(projectId);
        specialAttentionCode.setSpecialAttentionCode(splAttnCode);
        specialAttentionCode.setCreatedById(userId);
        specialAttentionCode.setCreateDate(new Date());
        specialAttnCodes.add(specialAttentionCode);
      });
      return specialAttnCodes;
    }
    return null;
  }

  private ProjectSWFacilityType prepareProjecSWFacilityType(final String userId,
      final String createdBy, final Date createdDate, final Long projectId,
      final Integer swFacilityTypeId) {
    ProjectSWFacilityType prjSwFacilityType = new ProjectSWFacilityType();
    prjSwFacilityType.setProjectId(projectId);
    if (createdDate != null) {
      prjSwFacilityType.setCreateDate(createdDate);
      prjSwFacilityType.setCreatedById(createdBy);
      prjSwFacilityType.setModifiedById(userId);
      prjSwFacilityType.setModifiedDate(new Date());
    } else {
      prjSwFacilityType.setCreateDate(new Date());
      prjSwFacilityType.setCreatedById(userId);
    }
    prjSwFacilityType.setSwFacilityTypeId(swFacilityTypeId);
    return prjSwFacilityType;
  }


  @Transactional(rollbackFor = {ETrackPermitException.class})
  @Override
  public void submitProject(String userId, String contextId, Long projectId) {
    logger.info("Entering into submit project User Id: {}, Context Id: {}", userId, contextId);

    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException("NO_PROJECT_AVAIL",
          "There is no project available with this id " + projectId, projectId);
    }
    List<Application> applications = applicationRepo.findByProjectId(projectId);
    if (CollectionUtils.isEmpty(applications)) {
      throw new BadRequestException("ATLEAST_ONE_PERMIT_REQD",
          "At least one Permit Type selection Required.", projectId);
    }
    if (projectAvailability.get().getRejectedInd() != null
        && projectAvailability.get().getRejectedInd().equals(1)) {
      throw new BadRequestException("PROJ_CANNOT_BE_SUBMITTED",
          "This reject project cannot be re-submitted. Please delete this one and submit new one",
          projectId);
    }

    List<ProjectActivity> projectActivities = projectActivityRepo.findAllByProjectId(projectId);
    if (CollectionUtils.isEmpty(projectActivities) || projectActivities.size() != 5) {
      throw new BadRequestException("INVALID_REQ",
          "This project is in incomplete status or not available " + projectId, projectId);
    }

    eTrackPermitDAO.recordTheProjectSubmissionDetails(userId, contextId, projectId);
    projectActivities = projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId, 1);
    List<ProjectActivity> exitingSubmissionActivities =
        projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId, 5);
    ProjectActivity submittedActivity = exitingSubmissionActivities.get(0);
    ProjectActivity existingActivity = projectActivities.get(0);
    submittedActivity.setStartDate(existingActivity.getStartDate());
    projectActivityRepo.save(submittedActivity);
    logger.info("Generate System note for the Project Submission. User Id {}, Context Id {}",
        userId, contextId);
    ProjectNote projectNote = new ProjectNote();
    projectNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
    projectNote.setCreateDate(new Date());
    projectNote.setActionDate(new Date());
    projectNote.setProjectId(projectId);
    projectNote.setActionTypeCode(18);
    projectNote.setActionNote("Project ID " + projectId + " has been submitted");
    projectNoteRepo.save(projectNote);
    logger.info(
        "Generated System note for the Project Submission successfully. User Id {}, Context Id {}",
        userId, contextId);
    logger.info("Exiting from submit project User Id: {}, Context Id: {}", userId, contextId);
  }


  @Override
  public void addSupportDocument(final String userId, final String contextId,
      final Long projectId) {
    logger.info(
        "Entering into add project Support document Details project User Id: {}, Context Id: {}",
        userId, contextId);
    List<ProjectActivity> projectActivities =
        projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId, 4);
    if (CollectionUtils.isEmpty(projectActivities)) {
      ProjectActivity projectActivity = new ProjectActivity();
      projectActivity.setActivityStatusId(4);
      projectActivity.setProjectId(projectId);
      projectActivity.setStartDate(new Date());
      projectActivity.setCompletionDate(new Date());
      projectActivity.setCreatedById(userId);
      projectActivity.setCreateDate(new Date());
      projectActivityRepo.save(projectActivity);
    }
    logger.info(
        "Exiting from add project Support document Details project User Id: {}, Context Id: {}",
        userId, contextId);
  }

  @Transactional
  @Override
  public void deleteProject(String userId, String contextId, String token, Long projectId) {
    logger.info("Entering into delete project User Id: {}, Context Id: {}", userId, contextId);
    try {

      Long unSubmittedProjectId = projectRepo.findUnsubmittedProjectByProjectId(projectId);

      // List<ProjectActivity> projectActivities =
      // projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId, 5);
      if (unSubmittedProjectId != null) {
        logger.info("Requesting to delete the project {} , User Id {}, Context Id {}", projectId,
            userId, contextId);
        List<SupportDocument> supportDocumentList =
            supportDocumentRepo.findAllDocumentsByProjectId(projectId);
        if (!CollectionUtils.isEmpty(supportDocumentList)) {
          String uri = UriComponentsBuilder.newInstance()
              .pathSegment("/etrack-dms/dms/sdds/delete-documents").build().toString();
          List<String> documentGuids = new ArrayList<>();
          String clientId = "DEC_".concat(supportDocumentList.get(0).getDocumentClassNm()).concat("_P8");
          logger.info(
              "Requesting DMS to delete the documents from ECMaaS. User Id {}, Context Id {}",
              userId, contextId);
          for (SupportDocument supportDocument : supportDocumentList) {
            documentGuids.add(supportDocument.getEcmaasGuid());
          }
          HttpHeaders headers = new HttpHeaders();
          headers.add("userId", userId);
          headers.add("contextId", contextId);
          headers.add("clientId", clientId);
          headers.add(HttpHeaders.AUTHORIZATION, token);
          HttpEntity<List<String>> requestEntity = new HttpEntity<>(documentGuids, headers);
          eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Object.class);
        }
        eTrackPermitDAO.deleteEtrackProject(userId, contextId, projectId);
      } else {
        logger.error(
            "This project {} is already submitted, can't be deleted. User Id {}, Context Id {}",
            projectId, userId, contextId);
        throw new BadRequestException("INVALID_PROJ_ID", "Project is already submitted", projectId);
      }
    } catch (BadRequestException | ETrackPermitException e) {
      throw e;
    } catch (HttpClientErrorException e) {
      throw new ETrackPermitException(e.getStatusCode(), "DOCUMENT_DELETE_REQ_FAILURE",
          e.getResponseBodyAsString());
    } catch (Exception e) {
      throw new ETrackPermitException("PROJECT_DELETION_FAILURE",
          "Error while deleting the Project details. Error details ", e);
    }
    logger.info("Exiting from delete project User Id: {}, Context Id: {}", userId, contextId);
  }

  @Transactional
  @Override
  public void updateSignatureReceived(String userId, String contextId, Long projectId,
      List<Long> publicIdList) {
    logger.info(
        "Entering into update the signature received for the project {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
    projectRepo.updatePublicSignatureReceivedInd(publicIdList, userId, new Date(), projectId);

    logger.info(
        "Exiting from update the signature received for the project {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
  }

  @Transactional
  @Override
  public void updateProjectAssignment(final String userId, final String contextId,
      final Long projectId, final AssignmentNote assignmentNote) {
    logger.info("Entering into updating the assignment details . User Id {}. Context Id {} ",
        userId, contextId);
    projectRepo.updateProjectAssignment(userId, projectId, assignmentNote.getAnalystId(),
        assignmentNote.getAnalystName(), assignmentNote.getAnalystRoleId());
    logger.info("Updating the project note details. User Id {}, Context Id {}", userId, contextId);

    ProjectNote projectNote = new ProjectNote();
    projectNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
    projectNote.setCreateDate(new Date());
    projectNote.setActionDate(new Date());
    projectNote.setProjectId(projectId);
    projectNote.setActionTypeCode(12);
    projectNote.setActionDate(new Date());
    projectNote.setComments(assignmentNote.getComments());
    projectNote.setActionNote(assignmentNote.getAnalystName().toUpperCase()
        + " has been assigned to project id: " + projectId);

    StringBuilder sb = new StringBuilder();
    sb.append("Project ").append(projectId).append(" has been assigned.");
    if (StringUtils.hasLength(assignmentNote.getComments())) {
      sb.append(" View the assignment note");
    }
    ProjectAlert projectAlert = new ProjectAlert();
    projectAlert.setProjectId(projectId);
    // projectAlert.setAlertNote("Project Assigned to " + assignmentNote.getAnalystName());
    projectAlert.setAlertNote(sb.toString());
    projectAlert.setAlertDate(new Date());
    projectAlert.setCreateDate(new Date());
    projectAlert.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
    projectAlert.setMsgReadInd(0);
    projectAlert.setAlertRcvdUserId(assignmentNote.getAnalystId());
    projectNote = projectNoteRepo.save(projectNote);
    projectAlert.setProjectNoteId(projectNote.getProjectNoteId());
    projectAlertRepo.save(projectAlert);
    logger.info("Exiting from updating the assignment details . User Id {}. Context Id {} ", userId,
        contextId);      
  }

  @Transactional
  @Override
  public EmailContent updateDocumentReviewerDetails(String userId, String contextId, Long projectId,
      DocumentReview documentReview) {
    try {
      MM_DD_YYYY_FORMAT.setLenient(false);
      final Date assignedDate = MM_DD_YYYY_FORMAT.parse(documentReview.getDateAssigned());
      final Date dueDate = MM_DD_YYYY_FORMAT.parse(documentReview.getDueDate());
      Date currentDate = MM_DD_YYYY_FORMAT.parse(MM_DD_YYYY_FORMAT.format(new Date()));

      if (assignedDate.before(currentDate) || assignedDate.after(dueDate)
          || dueDate.before(currentDate)) {
        throw new BadRequestException("INVALID_REQ", "Invalid dates are passed", documentReview);
      }

      List<SupportDocument> supportDocumentList = null;
      if (!CollectionUtils.isEmpty(documentReview.getDocumentIds())) {
        supportDocumentList =
            supportDocumentRepo.findAllByDocumentIds(projectId, documentReview.getDocumentIds());
      }

      List<RegionUserEntity> regionalUserEntityList =
          eTrackPermitDAO.retrieveStaffDetailsByUserId(userId, contextId);
      if (CollectionUtils.isEmpty(regionalUserEntityList)) {
        throw new DataNotFoundException("NO_USER_AVAILBLE",
            "There is no staff associated with this user id " + userId);
      }

      RegionUserEntity regionUserEntity = regionalUserEntityList.get(0);
      Facility facility = facilityRepo.findByProjectId(projectId);
      if (facility == null) {
        throw new DataNotFoundException("NO_FACILITY_FOUND",
            "There is no facility associated with this project id " + projectId);
      }

      StringBuilder emailBodyContent = new StringBuilder();
      emailBodyContent.append(String.format(ETrackPermitConstant.DOC_REVIEW_RQST_EMAIL_CONTENT,
          regionUserEntity.getDisplayName(), facility.getFacilityName(),
          formatDECId(facility.getDecId()), projectId, documentReview.getDueDate()));
      if (!CollectionUtils.isEmpty(supportDocumentList)) {
        emailBodyContent.append("<br>Documents below requested for the review:<br>");
        int count = 1;
        for (SupportDocument supportDocument : supportDocumentList) {
          emailBodyContent.append(count).append(". ").append(supportDocument.getDocumentNm())
              .append("<br>");
          count++;
        }
      }
      List<String> predefinedOrExistingContent = new ArrayList<>();
      StringBuilder subject = new StringBuilder();
      EmailCorrespondence emailCorrespondence = new EmailCorrespondence();
      subject.append("Review Request: ").append(facility.getFacilityName()).append(" project/PID ")
          .append(projectId).append("/DEC ID ").append(formatDECId(facility.getDecId())).append("/")
          .append(regionUserEntity.getDisplayName());
      emailCorrespondence.setFromEmailAdr(regionUserEntity.getEmailAddress());
      emailCorrespondence.setEmailRqstdUserId(regionUserEntity.getUserId());
      emailCorrespondence.setEmailRcvdUserId(documentReview.getReviewerId());
      emailCorrespondence.setCreateDate(new Date());
      emailCorrespondence.setCreatedById(userId);
      emailCorrespondence.setEmailContent(emailBodyContent.toString());
      emailCorrespondence.setEmailSubject(subject.toString());
      emailCorrespondence.setProjectId(projectId);
      StringBuilder subjectShortDesc = new StringBuilder();
      subjectShortDesc.append(subject.toString());
      emailCorrespondence.setSubShortDesc(subjectShortDesc.toString());
      if (StringUtils.hasLength(documentReview.getReviewerEmail())) {
        emailCorrespondence.setToEmailAdr(documentReview.getReviewerEmail());
      }
      emailCorrespondence.setEmailStatus("P");
      emailCorrespondenceRepo.save(emailCorrespondence);

      emailCorrespondence.setTopicId(emailCorrespondence.getCorrespondenceId());
      emailCorrespondence.setRefCorrespondenceId(emailCorrespondence.getCorrespondenceId());
      logger.info("Updating the Topic id after first success insertion ");
      emailCorrespondenceRepo.save(emailCorrespondence);
      EmailContent emailContent = new EmailContent();
      List<String> toEmailIds = new ArrayList<>();
      toEmailIds.add(documentReview.getReviewerEmail());
      emailContent.setFromEmailId(regionUserEntity.getEmailAddress());
      emailContent.setToEmailId(toEmailIds);
      emailContent.setEmailBody(emailBodyContent.toString());
      emailContent.setSubject(subject.toString());
      predefinedOrExistingContent.add(emailBodyContent.toString());
      emailContent.setExistingContents(predefinedOrExistingContent);
      emailContent.setEmailCorrespondenceId(emailCorrespondence.getCorrespondenceId());
      if (!CollectionUtils.isEmpty(documentReview.getDocumentIds())) {
        documentReview.getDocumentIds().forEach(documentId -> {
          DocumentReviewEntity documentReviewEntity = new DocumentReviewEntity();
          documentReviewEntity.setDocumentId(documentId);
          documentReviewEntity.setCorrespondenceId(emailCorrespondence.getCorrespondenceId());
          // documentReviewEntity.setProjectId(projectId);
          documentReviewEntity.setDocReviewerName(documentReview.getReviewerName());
          documentReviewEntity.setAssignedReviewerRoleId(documentReview.getReviewerRoleId());
          if (documentReview.getReviewerId() != null
              && StringUtils.hasLength(documentReview.getReviewerId().trim())) {
            documentReviewEntity.setDocReviewerId(documentReview.getReviewerId());
          }
          documentReviewEntity.setReviewAssignedDate(assignedDate);
          documentReviewEntity.setReviewDueDate(dueDate);
          documentReviewEntity.setCreatedById(userId);
          documentReviewEntity.setCreateDate(new Date());
          documentReviewRepo.save(documentReviewEntity);
        });
      } else {
        DocumentReviewEntity documentReviewEntity = new DocumentReviewEntity();
        documentReviewEntity.setCorrespondenceId(emailCorrespondence.getCorrespondenceId());
        documentReviewEntity.setDocReviewerName(documentReview.getReviewerName());
        documentReviewEntity.setAssignedReviewerRoleId(documentReview.getReviewerRoleId());
        if (documentReview.getReviewerId() != null
            && StringUtils.hasLength(documentReview.getReviewerId().trim())) {
          documentReviewEntity.setDocReviewerId(documentReview.getReviewerId());
        }
        documentReviewEntity.setReviewAssignedDate(assignedDate);
        documentReviewEntity.setReviewDueDate(dueDate);
        documentReviewEntity.setCreatedById(userId);
        documentReviewEntity.setCreateDate(new Date());
        documentReviewRepo.save(documentReviewEntity);
      }
      return emailContent;
    } catch (ParseException e) {
      logger.error("Invalid date fields are passed. User Id {} , Context Id {}", userId, contextId);
      throw new BadRequestException("INVALID_REQ", "Invalid dates are passed incorrect format ",
          documentReview);
    }
  }

  @Transactional
  @Override
  public void updateDocumentReviewerCompletionDetails(String userId, String contextId,
      final String token, Long projectId, ReviewCompletionDetail reviewCompletionDetail) {

    if (!(StringUtils.hasLength(reviewCompletionDetail.getDocReviewerName())
        && reviewCompletionDetail.getDocumentReviewId() != null
        && StringUtils.hasLength(reviewCompletionDetail.getReviewerId()))) {
      throw new BadRequestException("NO_DOC_REVIEW",
          "One of the field Document Reviewer Id, Reviewer name and Reviewer id is missing",
          reviewCompletionDetail);
    }

    List<DocumentReviewEntity> documentReviewsInputCorrespondenceId =
        documentReviewRepo.findByCorrespondenceId(reviewCompletionDetail.getCorrespondenceId());

    if (CollectionUtils.isEmpty(documentReviewsInputCorrespondenceId)) {
      throw new BadRequestException("NO_DOC_REVIEW",
          "There is no document review available for this Correspondence id "
              + reviewCompletionDetail.getDocumentReviewId(),
          reviewCompletionDetail);
    }

    if (documentReviewsInputCorrespondenceId.get(0).getDocReviewedInd() != null
        && documentReviewsInputCorrespondenceId.get(0).getDocReviewedInd().equals(1)) {

      logger.info(
          "Reseting this review as incomplete. "
              + "So, delete the existing review document if any. User Id {}, Context Id {}",
          userId, contextId);
      documentReviewRepo.updateDocumentReviewCompletionDetails(userId,
          reviewCompletionDetail.getCorrespondenceId(), reviewCompletionDetail.getReviewerId(), 0);

      StringBuilder documentNameBuilder = new StringBuilder();
      documentNameBuilder.append(reviewCompletionDetail.getDocReviewerName().replace(" ", "_"))
          .append("_").append(reviewCompletionDetail.getCorrespondenceId().toString()).append("%");

      logger.info(
          "Reseting this review as incomplete. " + "So, delete the existing review document {} in"
              + " DMS if present. User Id {}, Context Id {}",
          documentNameBuilder.toString(), userId, contextId);
      List<Long> documentIds = supportDocumentRepo.findByDocumentNameAndProjectId(projectId,
          documentNameBuilder.toString());

      if (!CollectionUtils.isEmpty(documentIds)) {
        List<String> documentIdStr = new ArrayList<>();
        documentIds.forEach(documentId -> {
          documentIdStr.add(String.valueOf(documentId));
        });
        logger.info(
            "Delete the documents from DMS associated "
                + "with the document Ids {}, User Id {}, Context Id {}",
            documentIdStr, userId, contextId);;
        deleteEmailCorrespondenceDocumentFromDMS(userId, contextId, projectId, token,
            String.join(",", documentIdStr));
      }
    } else {
      logger.info(
          "Setting this review as complete. "
              + "Upload the correspondence document into DMS User Id {}, Context Id {}",
          userId, contextId);

      documentReviewRepo.updateDocumentReviewCompletionDetails(userId,
          reviewCompletionDetail.getCorrespondenceId(), reviewCompletionDetail.getReviewerId(), 1);
      List<EmailCorrespondence> emailCorrespondences = emailCorrespondenceRepo
          .findAllCorrespondenceByReviewerIdAndDocReviewId(reviewCompletionDetail.getReviewerId(),
              documentReviewsInputCorrespondenceId.get(0).getCorrespondenceId());

      EmailContent emailCorrespondenceContent =
          retrievePrintableEmailContent(userId, contextId, projectId, emailCorrespondences);
      if (!CollectionUtils.isEmpty(emailCorrespondences)) {
        logger.info("Upload the correspondence document into DMS. User Id {}, Context Id {}",
            userId, contextId);
        uploadEmailCorrespondenceDocumentToDMS(userId, projectId, contextId, reviewCompletionDetail,
            emailCorrespondenceContent.getExistingContents(), token, false,
            documentReviewsInputCorrespondenceId.get(0).getReviewAssignedDate(),
            documentReviewsInputCorrespondenceId.get(0).getReviewDueDate());
      }
    }
  }

  private EmailContent retrievePrintableEmailContent(final String userId, final String contextId,
      final Long projectId, List<EmailCorrespondence> emailCorrespondences) {

    EmailContent emailContent = new EmailContent();
    List<String> emailContents = new ArrayList<>();
//    EmailCorrespondence emailCorrespondence = emailCorrespondences.get(0);
//    emailContents.add(emailCorrespondence.getEmailContent());
    for (int index = 0; index < emailCorrespondences.size(); index++) {
      StringBuilder sb = new StringBuilder();
      EmailCorrespondence existingEmailCorrespondence = emailCorrespondences.get(index);
      if (index == 0) {
        Facility facility = facilityRepo.findByProjectId(projectId);
        sb.append("Project Review Message : ").append(facility.getFacilityName()).append(" PID ").append(projectId).append("<br><br>");
        emailContent.setTopicId(existingEmailCorrespondence.getTopicId());
      }
      sb.append("From: ").append(existingEmailCorrespondence.getFromEmailAdr()).append("\n");
      sb.append("To: ").append(existingEmailCorrespondence.getToEmailAdr()).append("\n");
      if (StringUtils.hasLength(existingEmailCorrespondence.getCcEmailAdr())) {
        sb.append("Cc: ").append(existingEmailCorrespondence.getCcEmailAdr()).append("\n");
      }
      if (existingEmailCorrespondence.getCorrespondenceId()
          .equals(existingEmailCorrespondence.getTopicId())
          && existingEmailCorrespondence.getModifiedDate() != null) {
        sb.append("Sent at: ")
            .append(mmDDYYYFormatAMPM.format(existingEmailCorrespondence.getModifiedDate()))
            .append("\n");
      } else {
        sb.append("Sent at: ")
            .append(mmDDYYYFormatAMPM.format(existingEmailCorrespondence.getCreateDate()))
            .append("\n");
      }
      if (StringUtils.hasLength(existingEmailCorrespondence.getEmailContent())) {
        sb.append("\n").append(existingEmailCorrespondence.getEmailContent()).append("\n");
      }
      sb.append(
          "-----------------------------------------------------------------------------------------------------------")
          .append("\n");
      emailContents.add(sb.toString());
    }
    emailContent.setExistingContents(emailContents);
    return emailContent;
  }

  private void deleteEmailCorrespondenceDocumentFromDMS(String userId, String contextId,
      Long projectId, String token, String documentIds) {
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      // httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      httpHeaders.add("userId", userId);
      httpHeaders.add("projectId", String.valueOf(projectId));
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
      String uri = UriComponentsBuilder.newInstance()
          .path("/etrack-dcs/support-document/document/" + documentIds).build().toUriString();
      logger.info("Making call to DMS to Delete the email correspondence "
          + "document id: {} . User id: {} context Id: {}", documentIds, userId, contextId);
      HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, JsonNode.class);
      logger.info("Deleted the email Correspondence successful. User id: {} context id: {}", userId,
          contextId);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error("Error while requesting etrack-dcs service to delete the email correspondence. "
          + "User Id {}, Context Id {}. Error details ", userId, contextId, e);
      throw new ETrackPermitException(e.getStatusCode(), "EMAIL_CORRESP_DOC_DELETE_ERROR",
          e.getResponseBodyAsString());
    } catch (Exception e) {
      throw new ETrackPermitException("EMAIL_CORRESP_DOC_DELETE_ERROR",
          "Error while requesting to delete from DMS for the document Id " + documentIds, e);
    }
  }

  /**
   * This method is used to prepare the Email Correspondence document and upload into DMS.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * @param contextId - Unique UUID to track this request.
   * @param reviewCompletionDetail - Review Completion details.
   * @param emailCorrespondences - Email Correspondences details.
   * @param token - JWT Token
   * @param documentReviewId - Document Review Id.
   * @param reviewAssignedDate - Review Assigned Date.
   * @param reviewDueDate - Review due date.
   */
  private void uploadEmailCorrespondenceDocumentToDMS(final String userId, final Long projectId,
      final String contextId, final ReviewCompletionDetail reviewCompletionDetail,
      final List<String> exitingEmailCorrespondences, final String token,
      boolean aplctCorrespondenceInd, final Date reviewAssignedDate, final Date reviewDueDate) {

    IngestionRequest ingestionRequest = new IngestionRequest();
    Long documentId = reviewCompletionDetail.getCorrespondenceId();
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> filesDate = new HashMap<>();
    String currentDate = YYYY_MM_DD_HH_MM_SS.format(new Date());
    filesDate.put("0", currentDate);
    ingestionRequest.setFileDates(filesDate);
    Map<String, Object> metadataProperties = new HashMap<>();
    if (aplctCorrespondenceInd) {
      metadataProperties.put("Description",
          "Applicant amended correspondences which needs to be revisited later");
    } else {
      metadataProperties.put("Description",
          "Review Period from : " + MM_DD_YYYY_FORMAT.format(reviewAssignedDate) + " to : "
              + MM_DD_YYYY_FORMAT.format(reviewDueDate));
    }
    metadataProperties.put("docCategory", "3");
    metadataProperties.put("docSubCategory", "284");
    metadataProperties.put("docCreationType", "TEXT");
    StringBuilder documentName = new StringBuilder();
    if (aplctCorrespondenceInd) {
      documentName.append(reviewCompletionDetail.getDocReviewerName()).append("_")
          .append(currentDate);
    } else {
      documentName.append(reviewCompletionDetail.getDocReviewerName().replace(" ", "_")).append("_")
          .append(documentId);
    }
    metadataProperties.put("DocumentTitle", documentName.toString());
    metadataProperties.put("historic", "0");
    metadataProperties.put("docCreator", userId);
    metadataProperties.put("indexDate", currentDate);
    metadataProperties.put("docLastModifier", userId);
    metadataProperties.put("source", "ETRACK");
    metadataProperties.put("projectID", projectId);
    if (aplctCorrespondenceInd) {
      metadataProperties.put("applicationID", documentId);
    } else {
      metadataProperties.put("applicationID", reviewCompletionDetail.getCorrespondenceId());
    }
    metadataProperties.put("foilStatus", "NODET");
    metadataProperties.put("deleteFlag", "F");
    metadataProperties.put("renewalNumber", "0");
    metadataProperties.put("modificationNumber", "0");
    metadataProperties.put("trackedAppId", "");
    metadataProperties.put("access", "0");
    metadataProperties.put("nonRelReasonCodes", "");
    metadataProperties.put("receivedDate", currentDate);
    metadataProperties.put("permitType", "");
    ingestionRequest.setMetadataProperties(metadataProperties);
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);
    document.open();
    Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    fontTiltle.setSize(20);

    StringBuilder sb = new StringBuilder();
    exitingEmailCorrespondences.forEach(emailCorrespondence -> {
      if (StringUtils.hasLength(emailCorrespondence)) {
        if (!emailCorrespondence.equals("<br>")) {
          emailCorrespondence = emailCorrespondence.replaceAll("<br>", "\n");
          sb.append(emailCorrespondence).append("\n");
        }
      }
    });
    Paragraph paragraph = new Paragraph(sb.toString());
    document.add(paragraph);
    document.close();
    ByteArrayResource byteArrayResource = new ByteArrayResource(outputStream.toByteArray()) {
      @Override
      public String getFilename() {
        if (aplctCorrespondenceInd) {
          return reviewCompletionDetail.getDocReviewerName() + "_" + currentDate + ".pdf";
        } else {
          return "Project" + "_" + projectId + "_PAR_" + reviewCompletionDetail.getDocReviewerName()
              + "_" + currentDate + ".pdf";
        }
      }
    };
    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    try {
      HttpHeaders attachFile = new HttpHeaders();
      attachFile.setContentType(MediaType.TEXT_PLAIN);
      HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(byteArrayResource, attachFile);
      filesAndMetadataMap.add("uploadFiles", attachment);
      HttpHeaders fileMetadataHeaders = new HttpHeaders();
      fileMetadataHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IngestionRequest> metaDataEntity =
          new HttpEntity<>(ingestionRequest, fileMetadataHeaders);
      filesAndMetadataMap.add("ingestionMetaData", metaDataEntity);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
      httpHeaders.add("userId", userId);
      StringBuilder clientId = new StringBuilder();
      clientId.append("DEC_").append("CORRESPONDENCE").append("_P8");
      httpHeaders.add("projectId", String.valueOf(projectId));
      httpHeaders.add("clientId", clientId.toString());
      httpHeaders.add("contextId", contextId);
      httpHeaders.add("docClassification", String.valueOf(2));;
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);

      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(filesAndMetadataMap, httpHeaders);
      String uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/support-document/upload")
          .build().toUriString();
      logger.info("Making call to DMS to upload document id: {} . User id: {} context Id: {}",
          documentId, userId, contextId);
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);
      logger.info("Upload the email Correspondence successful. User id: {} context id: {}", userId,
          contextId);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error("Error while requesting etrack-dcs service to upload the document. "
          + "User Id {}, Context Id {}. Error details ", userId, contextId, e);
      throw new ETrackPermitException(e.getStatusCode(), "EMAIL_CORRESP_DOC_UPLOAD_ERROR",
          e.getResponseBodyAsString());
    } catch (Exception e) {
      throw new ETrackPermitException("EMAIL_CORRESP_DOC_UPLOAD_ERROR",
          "Error while preparing the document " + "and upload into DMS for the document Id "
              + documentId,
          e);

    }
  }

  /**
   * This method is used to upload the report pdf document to DMS by calling
   * etrack-dcs/support-document/upload end point.
   * 
   * @param userId - User who initiates this request
   * @param contextId - Unique Id to track this transaction.
   * @param token - JWT Token
   * @param projectId - Project Id
   */
  private IngestionResponse uploadDocumentReportToDMS(final String userId, final String contextId,
      final String token, final Long projectId) {
    logger.info("Entering into upload the Document Report to DMS . User Id {}, Context Id {}",
        userId, contextId);
    byte[] reportFileInBytes = null;
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("userId", userId);
      httpHeaders.add("contextId", contextId);
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);
      String uri = UriComponentsBuilder.newInstance()
          .path("/etrack-dart-db/report/uploaded-document/" + projectId).build().toUriString();
      logger.info(
          "Making call to Dart Service to retrieve the report for the project Id : {} . User id: {} context Id: {}",
          projectId, userId, contextId);
      reportFileInBytes = eTrackOtherServiceRestTemplate
          .exchange(uri, HttpMethod.GET, requestEntity, byte[].class).getBody();
    } catch (HttpServerErrorException | HttpClientErrorException ex) {
      logger
          .error("Unsuccessful status error while retrieving the support document uploaded report. "
              + "User Id {}, Context Id {}", userId, contextId, ex);
      throw new ETrackPermitException(ex.getStatusCode(), "REPORT_RETRIEVAL_ERR",
          "Unsuccessful status error while retrieving the support document uploaded report."
              + projectId);
    } catch (Exception e) {
      logger.error("Received general error while retrieving the support document uploaded report. "
          + "User Id {}, Context Id {}", userId, contextId, e);
      throw new ETrackPermitException("REPORT_RETRIEVAL_GEN_ERR",
          "Error while retrieving the Support document uploaded report", e);
    }
    try {
      // List<String> permitTypesList = applicationRepo.findAllPermitTypesByProjectId(projectId);
      // String permitTypes = String.join(",", permitTypesList);
      IngestionRequest ingestionRequest = new IngestionRequest();
      ingestionRequest.setAttachmentFilesCount(1);
      Map<String, String> filesDate = new HashMap<>();
      String currentDate = YYYY_MM_DD_HH_MM_SS.format(new Date());
      filesDate.put("0", currentDate);
      ingestionRequest.setFileDates(filesDate);
      Map<String, Object> metadataProperties = new HashMap<>();
      metadataProperties.put("docCategory", "26");
      metadataProperties.put("docSubCategory", "9");
      metadataProperties.put("docCreationType", "TEXT");
      metadataProperties.put("DocumentTitle", "Project Checklist");
      metadataProperties.put("historic", "0");
      metadataProperties.put("docCreator", userId);
      metadataProperties.put("indexDate", currentDate);
      metadataProperties.put("docLastModifier", userId);
      metadataProperties.put("source", "ETRACK");
      metadataProperties.put("projectID", projectId);
      metadataProperties.put("applicationID", projectId);
      metadataProperties.put("foilStatus", "NODET");
      metadataProperties.put("deleteFlag", "F");
      metadataProperties.put("renewalNumber", "0");
      metadataProperties.put("modificationNumber", "0");
      metadataProperties.put("trackedAppId", "");
      metadataProperties.put("access", "0");
      metadataProperties.put("nonRelReasonCodes", "");
      metadataProperties.put("receivedDate", currentDate);
      metadataProperties.put("permitType", ""); // FileNet is allowed only 2 characters in this
                                                // field. so, not able to able send the Permits for
                                                // project level.
      ingestionRequest.setMetadataProperties(metadataProperties);
      ByteArrayResource byteArrayResource = new ByteArrayResource(reportFileInBytes) {
        @Override
        public String getFilename() {
          return projectId + "_OriginalChecklist.pdf";
        }
      };
      MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
      HttpHeaders attachFile = new HttpHeaders();
      attachFile.setContentType(MediaType.TEXT_PLAIN);
      HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(byteArrayResource, attachFile);
      filesAndMetadataMap.add("uploadFiles", attachment);
      HttpHeaders fileMetadataHeaders = new HttpHeaders();
      fileMetadataHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IngestionRequest> metaDataEntity =
          new HttpEntity<>(ingestionRequest, fileMetadataHeaders);
      filesAndMetadataMap.add("ingestionMetaData", metaDataEntity);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
      httpHeaders.add("userId", userId);
      StringBuilder clientId = new StringBuilder();
      clientId.append("DEC_").append("SUPPORTINGDOCUMENTS").append("_P8");
      httpHeaders.add("projectId", String.valueOf(projectId));
      httpHeaders.add("clientId", clientId.toString());
      httpHeaders.add("contextId", contextId);
      httpHeaders.add("docClassification", String.valueOf(2));
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(filesAndMetadataMap, httpHeaders);
      String uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/support-document/upload")
          .build().toUriString();
      logger.info(
          "Making call to DMS to upload document report Project id: {} . User id: {} context Id: {}",
          projectId, userId, contextId);
      return eTrackOtherServiceRestTemplate
          .exchange(uri, HttpMethod.POST, requestEntity, IngestionResponse.class).getBody();
    } catch (HttpServerErrorException | HttpClientErrorException ex) {
      logger
          .error("Unsuccessful status error while uploading the support document uploaded report. "
              + "User Id {}, Context Id {}", userId, contextId, ex);
      throw new ETrackPermitException(ex.getStatusCode(), "REPORT_UPLOAD_ERR",
          "Unsuccessful status error while uploading the support document uploaded report."
              + projectId);
    } catch (Exception e) {
      logger.error("Received general error while uploading the support document uploaded report. "
          + "User Id {}, Context Id {}", userId, contextId, e);
      throw new ETrackPermitException("REPORT_UPLOAD_GEN_ERR",
          "Error while uploading the Support document uploaded report", e);
    }
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


  @Transactional(rollbackFor = ETrackPermitException.class, propagation = Propagation.REQUIRED)
  public Project createDIMSRRecordInETrack(final String userId, final String contextId,
      final String token, final String ppid, final DIMSRRequest dimsrRequest) {

    Project project = new Project();
    project.setProjectDesc(dimsrRequest.getProjectDesc());
    project.setDimsrInd(1);
    try {
      if (CollectionUtils.isEmpty(dimsrRequest.getPermits())) {
        throw new BadRequestException("INVALID_REQ", "There is no permit requested for this DIMSR",
            dimsrRequest);
      }
      MM_DD_YYYY_FORMAT.setLenient(false);
      if (StringUtils.hasLength(dimsrRequest.getIntentMailingDate())) {
        Date intentMailingDate = MM_DD_YYYY_FORMAT.parse(dimsrRequest.getIntentMailingDate());
        project.setIntentMailingDate(intentMailingDate);
      }
      if (StringUtils.hasLength(dimsrRequest.getProposedEffDate())) {
        Date proposedEffDate = MM_DD_YYYY_FORMAT.parse(dimsrRequest.getProposedEffDate());
        project.setProposedEffDate(proposedEffDate);
      }
    } catch (ParseException e) {
      logger.error("Incorrect date format is passed in the input ", e);
      throw new BadRequestException("INCORRECT_DATE_FORMAT",
          "Incorrect Proposed Effective date format is passed in the input", dimsrRequest);
    }

    try {
      Date submissionDate = new Date();
      project.setApplicantTypeCode(1);
      project.setCreateDate(submissionDate);
      project.setCreatedById(userId);
      project.setAnalystAssignedId(dimsrRequest.getAnalystAssignedId());
      project.setAssignedAnalystName(dimsrRequest.getAssignedAnalystName());
      project.setAssignedAnalystRoleId(dimsrRequest.getAnalystRoleId());
      project.setAnalystAssignedDate(submissionDate);
      project.setProjectInitiatedUserId(userId);
      project.setReceivedDate(submissionDate);
      project.setOriginalSubmittalInd(0);
      project.setDartProcessingCompleteInd(0);      
      projectRepo.save(project);
      Facility facility = new Facility();
      facility.setDecId(dimsrRequest.getDecId());
      facility.setProjectId(project.getProjectId());
      facility.setEdbDistrictId(dimsrRequest.getEdbDistrictId());
      facility.setFacilityName(dimsrRequest.getFacilityName());
      facility.setCreateDate(submissionDate);
      facility.setCreatedById(userId);
      facilityRepo.save(facility);

      // List<Application> applicationPermits = new ArrayList<>();
      Map<Long, List<Application>> applicationPermitGroupByBatchId = new HashMap<>();
      Map<Long, Integer> batchAndTrackingInd = new HashMap<>();

      dimsrRequest.getPermits().forEach(permit -> {
        Application application = new Application();
        application.setProjectId(project.getProjectId());
        application.setEdbApplId(permit.getEdbApplnId());
        application.setBatchIdEdb(permit.getBatchId());
        application.setPermitTypeCode(permit.getPermitTypeCode());
        application.setTransTypeCode(permit.getTransType());
        application.setUploadTransTypeCode(permit.getTransType());
        application.setEdbTrackingInd(permit.getEdbTrackingInd());
        application.setTrackingInd(permit.getEdbTrackingInd());
        application.setCreatedById(userId);
        application.setCreateDate(submissionDate);
        application.setProgId(permit.getProgramId());
        if (applicationPermitGroupByBatchId.get(permit.getBatchId()) != null) {
          applicationPermitGroupByBatchId.get(permit.getBatchId()).add(application);
        } else {
          List<Application> applications = new ArrayList<>();
          applications.add(application);
          applicationPermitGroupByBatchId.put(permit.getBatchId(), applications);
          if (permit.getEdbTrackingInd() != null && permit.getEdbTrackingInd().equals(1)) {
            batchAndTrackingInd.put(permit.getBatchId(), permit.getEdbTrackingInd());
          }
        }
      });

      applicationPermitGroupByBatchId.keySet().forEach(batchId -> {
        List<Application> applications = applicationPermitGroupByBatchId.get(batchId);
        if (batchAndTrackingInd.get(batchId) == null) {
          applications.get(0).setTrackingInd(1);
          applications.get(0).setEdbTrackingInd(0);
        }
        applicationRepo.saveAll(applications);
      });
    } catch (Exception e) {
      logger.error("Error while saving DIMSR Applications. User Id {}, Context Id {}", userId,
          contextId, e);
      throw new ETrackPermitException("DIMSR_PERSIST_ERROR",
          "Error while saving DIMSR Applications " + e.getMessage(), e);
    }
    return project;
  }

  @Override
  public DIMSRRequest saveDIMSRDetails(final String userId, final String contextId,
      final String token, final String ppid, final DIMSRRequest dimsrRequest) {

    logger.info("Entering into saveDIMSRDetails. User Id {}, Context Id {}", userId, contextId);

    Project project = null;
    if (dimsrRequest == null || CollectionUtils.isEmpty(dimsrRequest.getPermits())) {
      throw new BadRequestException("NO_DIMSR_APPLN", "There is no DIMSR application requested",
          dimsrRequest);
    }
    try {
      project = createDIMSRRecordInETrack(userId, contextId, token, ppid, dimsrRequest);
      logger.info("DIMSR record created successfully in eTrack. User Id {}, Context Id {}", userId,
          contextId, project.getProjectId());

      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("contextId", contextId);
      headers.add("guid", ppid);
      headers.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-dart-district/upload-dimsr-appl-dart/" + project.getProjectId())
          .build().toString();
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, Void.class);
      dimsrRequest.setProjectId(project.getProjectId());
    } catch (HttpServerErrorException hse) {
      if (project.getProjectId() != null && project.getProjectId() > 0) {
        Optional<Project> projectAvail = projectRepo.findById(project.getProjectId());
        if (projectAvail.isPresent()) {
          logger.info(
              "Error while uploading the DIMSR details into DART from eTrack database."
                  + " Hence rolling back the eTrack changes. User Id {}, Context Id {}",
              userId, contextId);
          List<Application> applicationList =
              applicationRepo.findByProjectId(project.getProjectId());
          applicationRepo.deleteAll(applicationList);
          Facility facility = facilityRepo.findByProjectId(project.getProjectId());
          facilityRepo.delete(facility);
          projectRepo.delete(projectAvail.get());
        }
      }
      throw new ETrackPermitException(hse.getStatusCode(), "UPLOAD_DIMSR_TO_DART_ERR",
          "Error while uploading the DIMSR details into DART " + hse.getMessage());
    } catch (ETrackPermitException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("UPLOAD_TO_DART_GEN_ERR",
          "General error occurred while uploading the details into DART", e);
    }
    logger.info("Exiting from saveDIMSRDetails. User Id {}, Context Id {}", userId, contextId);
    return dimsrRequest;
  }

  // @Transactional(rollbackOn = ETrackPermitException.class)
  @Override
  public void uploadProjectDetailsToEnterprise(final String userId, final String contextId,
      final String token, final String ppid, final Long projectId,
      final DartUploadDetail dartUploadDetail) {

    logger.info("Entering into uploadProjectDetailsToEnterprise. User Id {}, Context Id {}", userId,
        contextId);
    IngestionResponse ingestionResponse = null;
    try {
      List<Application> applications = applicationRepo.findByProjectId(projectId);
      if (CollectionUtils.isEmpty(applications)) {
        throw new BadRequestException("ATLEAST_ONE_PERMIT_REQD",
            "At least one Permit Type selection Required.", projectId);
      }
      Map<String, List<ReviewedPermit>> reviewedPermits = dartUploadDetail.getReviewedPermits();

      reviewedPermits.keySet().forEach(permitCategory -> {
        reviewedPermits.get(permitCategory).forEach(reviewedPermit -> {
          if (reviewedPermit.getEdbApplnId() != null && reviewedPermit.getEdbApplnId() > 0) {
            String permitTypeCode = reviewedPermit.getPermitTypeCode().replace("-", "");
            logger.info(
                "Check whether this input Permit {} and Trans type "
                    + "{} combinations are valid or not. User Id {}, Context Id {}",
                permitTypeCode, reviewedPermit.getModifiedTransType(), userId, contextId);
            eTrackPermitDAO.isValidPermitAndTransTypeMapping(userId, contextId, permitTypeCode,
                reviewedPermit.getModifiedTransType());
          }
        });
      });

      reviewedPermits.keySet().forEach(permitCategory -> {
        reviewedPermits.get(permitCategory).forEach(reviewedPermit -> {
          if ("NEW".equals(reviewedPermit.getTransType())) {
            reviewedPermit.setModifiedTransType(reviewedPermit.getTransType());
          }
          if (reviewedPermit.getEdbApplnId() != null && reviewedPermit.getEdbApplnId() > 0) {
            logger.info("Update the Application details {} Trans Type {}, Tracking Ind {}. User Id {}, Context Id {}."
                , reviewedPermit.getApplicationId(),  reviewedPermit.getModifiedTransType(), 
                reviewedPermit.getTrackingInd(), userId, contextId);
            applicationRepo.updateReviewedDARTPermitsByApplicationId(userId,
                reviewedPermit.getModifiedTransType(), reviewedPermit.getApplicationId(),
                reviewedPermit.getTrackingInd());
          } else {
            applicationRepo.updateReviewedETrackPermitsByApplicationId(userId,
                reviewedPermit.getModifiedTransType(), reviewedPermit.getApplicationId(),
                reviewedPermit.getTrackingInd(), reviewedPermit.getBatchGroup());
          }
        });
      });
      
      MM_DD_YYYY_FORMAT.setLenient(false);
      Date receivedDate = MM_DD_YYYY_FORMAT.parse(dartUploadDetail.getReceivedDate());
      int count = projectRepo.updateProjectUploadedDetail(userId, projectId, receivedDate);
      if (count == 0) {
        throw new BadRequestException("NO_PROJECT_TO_UPLOAD",
            "There is no project details to associated with this project Id " + projectId,
            projectId);
      }
      logger.info("Exiting from uploadProjectDetailsToEnterprise. User Id {}, Context Id {}",
          userId, contextId);
      
      List<Long> documentIds = supportDocumentRepo.findDocumentNameExistByProjectIdAndDocumentName(
          "Project Checklist".toUpperCase(), projectId);
      if (!CollectionUtils.isEmpty(documentIds)) {
        logger.info("Delete the existing uploaded Project Check list report "
            + "(Document Id) {} from DMS for this this project {}", projectId, documentIds);
        IngestionResponse deleteExistingOnes = new IngestionResponse();
        deleteExistingOnes.setDocumentId(String.valueOf(documentIds.get(count)));        
        deleteUploadedReport(userId, contextId, token, projectId, deleteExistingOnes);
      }
          
      ingestionResponse = uploadDocumentReportToDMS(userId, contextId, token, projectId);
      if (ingestionResponse == null || ingestionResponse.getDocumentId() == null) {
        throw new ETrackPermitException("MISSING_REQD_DOC_REPORT_UPLOAD_ERR",
            "Unable to Upload Missing required documents report to DMS for the project Id "
                + projectId);
      }

      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("contextId", contextId);
      headers.add("guid", ppid);
      headers.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-dart-district/upload-etrack-appl-dart/" + projectId).build()
          .toString();
      logger
          .info("Making a call to eTrack-dart-district-service to invoke upload to Dart procedure. "
              + "User Id {}, Context Id {}, Project Id {}", userId, contextId, projectId);
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, Void.class);
      projectRepo.updateUploadToDartAfterSuccessful(userId, projectId);
    } catch (BadRequestException | ETrackPermitException e) {
      if (ingestionResponse != null && ingestionResponse.getDocumentId() != null) {
        logger.error("Rollback/Delete the Missing required document upload report. Document Id {}, "
            + "Project Id {}", ingestionResponse.getDocumentId(), projectId, e);
        deleteUploadedReport(userId, contextId, token, projectId, ingestionResponse);
      }
      throw e;
    } catch (Exception e) {
      if (ingestionResponse != null && ingestionResponse.getDocumentId() != null) {
        logger.error(
            "General Error. Rollback/Delete the Missing required document upload report. Document Id {}, "
                + "Project Id {}",
            ingestionResponse.getDocumentId(), projectId, e);
        deleteUploadedReport(userId, contextId, token, projectId, ingestionResponse);
      }
      throw new ETrackPermitException("UPLOAD_TO_DART_FAILURE",
          "Unexpected error occurred while uploading the Permit details into DART ", e);
    }
  }

  @Transactional
  @Scheduled(fixedDelay = 3 * Timer.ONE_MINUTE)
  public void uploadGISApprovedPolygonToEFind() {
    logger.info("Identify the eligible approved Polygon to upload into eFind ");
    try {
      List<ProjectPolygon> uploadPolygonEligibleProjects = 
          projectPolygonRepo.findPolygonUploadEligibleProjects();
      if (!CollectionUtils.isEmpty(uploadPolygonEligibleProjects)) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<List<ProjectPolygon>> requestEntity =
            new HttpEntity<>(uploadPolygonEligibleProjects, headers);
        String uri = UriComponentsBuilder.newInstance()
            .pathSegment("/etrack-gis/upload-polygon-efind").build().toString();
        logger.info(
            "Making a call to eTrack-gis-service to invoke upload the eligible approved polygon into eFind.");
        ParameterizedTypeReference<Map<Long, String>> typeRef =
            new ParameterizedTypeReference<Map<Long, String>>() {};
        ResponseEntity<Map<Long, String>> requestStatusResponse =
            eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, typeRef);
        Map<Long, String> statusOfEachRequest = requestStatusResponse.getBody();

        if (!CollectionUtils.isEmpty(statusOfEachRequest)) {
          statusOfEachRequest.keySet().forEach(projectId -> {
            if (StringUtils.hasLength(statusOfEachRequest.get(projectId))
                && statusOfEachRequest.get(projectId).equals("S")) {
              uploadEligiblePolygonRepo.deleteByProjectId(projectId);
            } else {
              UploadPolygonEntity regionEntity =
                  uploadEligiblePolygonRepo.findByProjectId(projectId);
              Integer retryCount =
                  regionEntity.getRetryCounter() == null ? 1 : regionEntity.getRetryCounter() + 1;
              uploadEligiblePolygonRepo.updateRetryCountByProjectId(projectId, retryCount);
            }
          });
        }
      } else {
        logger.info("There is no eligible projects to upload into eFind ");
      }
    } catch (Exception e) {
      logger.error("Error while processing the eligible projects approved polygon into eFind", e);
    }
  }

  /**
   * This method is used to delete the existing document created earlier in the upload process.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique Id to track this transaction.
   * @param projectId - Project Id
   * @param ingestionResponse - Document details.
   */
  private void deleteUploadedReport(final String userId, final String contextId, final String token,
      Long projectId, IngestionResponse ingestionResponse) {
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("userId", userId);
      httpHeaders.add("projectId", String.valueOf(projectId));
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
      String uri = UriComponentsBuilder.newInstance()
          .path("/etrack-dcs/support-document/document/" + ingestionResponse.getDocumentId())
          .build().toUriString();
      logger.info(
          "Making call to DMS to upload document report Project id: {} . User id: {} Context Id: {}",
          projectId, userId, contextId);
      HttpEntity<?> deleteRequestEntity = new HttpEntity<>(httpHeaders);
      eTrackOtherServiceRestTemplate
          .exchange(uri, HttpMethod.DELETE, deleteRequestEntity, JsonNode.class).getBody();
    } catch (HttpServerErrorException | HttpClientErrorException ex) {
      logger.error("Unsuccessful status error while deleting the support document uploaded report. "
          + "User Id {}, Context Id {}", userId, contextId, ex);
      throw new ETrackPermitException(ex.getStatusCode(), "REPORT_DELETION_ERR",
          "Unsuccessful status error while deleting the support document uploaded report."
              + projectId);
    } catch (Exception e) {
      logger.error("Received general error while deleting the support document uploaded report. "
          + "User Id {}, Context Id {}", userId, contextId, e);
      throw new ETrackPermitException("REPORT_DELETION_GEN_ERR",
          "Error while deleting the Support document uploaded report", e);
    }
  }

  @Transactional
  @Override
  public void rejectProjectValidation(final String userId, final String contextId,
      final Long projectId, final String rejectedReason) {
    logger.info("Entering into rejectProjectValidation. User Id {}, Context Id {}", userId,
        contextId);
    int count = projectRepo.retrieveRetrieveEligibleProjectId(userId, projectId);
    if (count == 0) {
      throw new BadRequestException("NOT_ELIGIBLE_TO_REJECT", "This project cannot be deleted",
          projectId);
    }
    logger.info("Delete all the validation activities if any for the "
        + "input project Id {}. User Id {}, Context Id {}", projectId, userId, contextId);
    projectActivityRepo.deleteAllValidationRelatedActivities(projectId);
    logger.info("Update the rejected reason for the input project id {}. User Id {}, Context Id {}",
        projectId, userId, contextId);
    projectRepo.revertProjectToDataEntry(userId, projectId, rejectedReason);
    logger.info("Exiting from rejectProjectValidation. User Id {}, Context Id {}", userId,
        contextId);
  }

  @Override
  public void associateGeographicalInquiryToProject(String userId, String contextId, Long projectId,
      Long inquiryId) {
    Set<Long> inquiries = new HashSet<>();
    inquiries.add(inquiryId);
    associateInquiriesToProject(userId, projectId, inquiries, true);
  }
}
