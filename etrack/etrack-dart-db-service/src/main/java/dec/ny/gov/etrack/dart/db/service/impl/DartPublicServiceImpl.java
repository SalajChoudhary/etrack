package dec.ny.gov.etrack.dart.db.service.impl;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.ADDR_HIST_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.INDIVIDUAL;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLIC_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLIC_HIST_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.SOLE_PROPRIETOR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectActivity;
import dec.ny.gov.etrack.dart.db.entity.Public;
import dec.ny.gov.etrack.dart.db.entity.PublicDetail;
import dec.ny.gov.etrack.dart.db.entity.PublicAssociatedFacility;
import dec.ny.gov.etrack.dart.db.entity.history.AddressHistory;
import dec.ny.gov.etrack.dart.db.entity.history.PublicHistoryDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Applicant;
import dec.ny.gov.etrack.dart.db.model.ErrorResponse;
import dec.ny.gov.etrack.dart.db.model.Facility;
import dec.ny.gov.etrack.dart.db.model.PublicType;
import dec.ny.gov.etrack.dart.db.model.Result;
import dec.ny.gov.etrack.dart.db.model.SearchPatternEnum;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.DartDbRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.service.DartPublicService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@Service
public class DartPublicServiceImpl implements DartPublicService {

  private static final Logger logger = LoggerFactory.getLogger(DartPublicServiceImpl.class.getName());
  @Autowired
  private ProjectRepo projectRepo;
  @Autowired
  private ProjectActivityRepo projectActivityRepo;
  @Autowired
  private ApplicantRepo applicantRepo;
  @Autowired
  private DartDbDAO dartDBDAO;
  @Autowired
  private DartDBServiceUtility dartDBServiceUtility;
  @Autowired
  private DartDbRepo dartDbRepo;
//  @Autowired
//  private ContactRepo contactRepo;
  @Autowired
  private TransformationService transformationService;
  
  private static final String APPLICANTS = "applicants";
  private static final String OWNERS = "owners";
  private static final String CONTACT_AGENTS = "contactAgents";
  private List<String> SEARCH_PATTERNS = Arrays.asList("S", "C", "E");
  private List<String> ORG_AGENCY_LIST = Arrays.asList("X", "T", "C", "F", "S", "M");
  private static final String PROJECT_ID = "projectId";

  /**
   * This method is used to retrieve the list of applicants associated with the project
   * 
   * @param userId - User initiated this request
   * @param contextId - context id to track the request
   * @param categoryCode - Applicants category - C : Contact/Agents, P - Publics, O- Owners
   * 
   * @return - Returns the list of applicants and status of the request.
   */
  @Transactional
  @Override
  public ResponseEntity<Object> retrieveApplicants(final String userId, final String contextId,
      final String categoryCode, final Long projectId,
      final Integer applicantAssociatedToProjectInd) {

    logger.info(
        "Entering into retrieveApplicants()  User Id {}, Context Id {}, Project Id {} . Category {} ",
        userId, contextId, projectId, categoryCode);

    List<ApplicantDto> publicLists = null;
    Map<String, Object> applicantsSummary = new HashMap<>();
    List<ApplicantDto> sortedPublicList = null;
    // List<PublicSummary> publicSummaryList = null;
    List<ProjectActivity> validatedIndList = null;
    Optional<Project> projectOptional = null;

    // List<Long> activityStatusList = null;
    projectOptional = projectRepo.findById(projectId);
    int activityStatusId = 0;
    switch (categoryCode) {
      case "C":// Contact/Agents
        publicLists = applicantRepo.findAllContactsByAssociatedInd(projectId,
            applicantAssociatedToProjectInd);
        if (applicantAssociatedToProjectInd != null && applicantAssociatedToProjectInd.equals(1)) {
          validatedIndList = projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              projectId, DartDBConstants.CONTACT_AGENT_VALIDATED);
          activityStatusId = DartDBConstants.CONTACT_AGENT_VALIDATED;
        }
        break;
      case "O": // Owners
        publicLists =
            applicantRepo.findAllOwnersByAssociatedInd(projectId, applicantAssociatedToProjectInd);
        if (applicantAssociatedToProjectInd != null && applicantAssociatedToProjectInd.equals(1)) {
          validatedIndList = projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              projectId, DartDBConstants.OWNER_VALIDATED);
          activityStatusId = DartDBConstants.OWNER_VALIDATED;
        }
        break;
      case "P": // publics
        publicLists =
            applicantRepo.findAllPublicsByAssociatedInd(projectId, applicantAssociatedToProjectInd);
        if (applicantAssociatedToProjectInd != null && applicantAssociatedToProjectInd.equals(1)) {
          validatedIndList = projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              projectId, DartDBConstants.APPLICANT_VALIDATED);
          activityStatusId = DartDBConstants.APPLICANT_VALIDATED;
        }
        break;
      default:
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    // Integer enterprisePublics = 0;
    Integer validatedPublics = 0;
    ProjectActivity projectActivity = null;
    if (!CollectionUtils.isEmpty(publicLists)) {
      logger.info(
          "Identify the list of Publics individually validated for this project id {}. User Id {}, Context Id {}",
          projectId, userId, contextId);
      for (ApplicantDto publicDetail : publicLists) {
        if (publicDetail.getValidatedInd() != null && publicDetail.getValidatedInd().equals(1)) {
          ++validatedPublics;
        }
      }
      if (applicantAssociatedToProjectInd != null && applicantAssociatedToProjectInd.equals(1)) {
        List<Integer> signSubmitActivityList =
            projectActivityRepo.findProjectSignedAndSubmitted(projectId, 5);

        if (!CollectionUtils.isEmpty(signSubmitActivityList)) {
          if (validatedPublics != 0 && publicLists.size() == validatedPublics) {
            applicantsSummary.put(DartDBConstants.VALIDATED_IND, "Y");
          } else {
            applicantsSummary.put(DartDBConstants.VALIDATED_IND, "N");
          }
          if (CollectionUtils.isEmpty(validatedIndList)) {
            projectActivity = new ProjectActivity();
            projectActivity.setProjectId(projectId);
            projectActivity.setCreatedById(userId);
            projectActivity.setCreateDate(new Date());
            projectActivity.setActivityStatusId(activityStatusId);
            projectActivity.setStartDate(new Date());
            if (applicantsSummary.get(DartDBConstants.VALIDATED_IND).equals("Y")) {
              projectActivity.setCompletionDate(new Date());
            }
            projectActivityRepo.save(projectActivity);
          } else {
            projectActivity = validatedIndList.get(0);
            if (projectActivity.getCompletionDate() != null
                && applicantsSummary.get(DartDBConstants.VALIDATED_IND).equals("N")) {
              projectActivityRepo.updateProjectActivityStatusIdAsIncomplete(userId, projectId,
                  projectActivity.getProjectActivityStatusId(), activityStatusId);
            } else if (projectActivity.getCompletionDate() == null
                && applicantsSummary.get(DartDBConstants.VALIDATED_IND).equals("Y")) {
              projectActivityRepo.updateProjectActivityStatusId(userId, projectId,
                  projectActivity.getProjectActivityStatusId(), activityStatusId, new Date());
            }
          }
        }
      }

      sortedPublicList = publicLists.stream()
          .sorted(Comparator.comparing(ApplicantDto::getDisplayName, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
    }
    applicantsSummary.put(APPLICANTS, sortedPublicList);
    if (projectOptional.isPresent()) {
      applicantsSummary.put("applicantTypeCode", projectOptional.get().getApplicantTypeCode());
      applicantsSummary.put("mailInInd", projectOptional.get().getMailInInd());
    }
    return new ResponseEntity<>(applicantsSummary, HttpStatus.OK);

  }

  @Override
  public ResponseEntity<Object> retrieveAllPublicsAssociatedWithThisProject(String userId,
      String contextId, Long projectId) {
    logger.info(
        "Entering into retrieveAllPublicsAssociatedWithThisProject(). User Id {}, Context Id {}, Project Id {}",
        userId, contextId, projectId);

    Map<String, Object> applicantsSummary = new HashMap<>();
    // List<PublicSummary> publicSummaryList = null;
    List<ProjectActivity> validatedIndList = null;
    Optional<Project> projectAvailability = projectRepo.findById(projectId);
    if (!projectAvailability.isPresent()) {
      throw new BadRequestException("PROJECT_NOT_AVAIL",
          "Project is not available for the input Project Id " + projectId, projectAvailability);
    }
    List<ApplicantDto> publicLists = applicantRepo.findAllContactsByAssociatedInd(projectId, 1);
    validatedIndList = projectActivityRepo.findProjectActivityStatusByActivityStatusId(projectId,
        DartDBConstants.CONTACT_AGENT_VALIDATED);

    int onlineSubmitterInd = 0;
    categorizedPublicSummary(userId, contextId, projectId, publicLists, applicantsSummary,
        validatedIndList, CONTACT_AGENTS, DartDBConstants.CONTACT_AGENT_VALIDATED,
        onlineSubmitterInd);

    publicLists = applicantRepo.findAllOwnersByAssociatedInd(projectId, 1);
    validatedIndList = projectActivityRepo.findProjectActivityStatusByActivityStatusId(projectId,
        DartDBConstants.OWNER_VALIDATED);

    categorizedPublicSummary(userId, contextId, projectId, publicLists, applicantsSummary,
        validatedIndList, OWNERS, DartDBConstants.OWNER_VALIDATED, onlineSubmitterInd);

    publicLists = applicantRepo.findAllPublicsByAssociatedInd(projectId, 1);

    validatedIndList = projectActivityRepo.findProjectActivityStatusByActivityStatusId(projectId,
        DartDBConstants.APPLICANT_VALIDATED);
    categorizedPublicSummary(userId, contextId, projectId, publicLists, applicantsSummary,
        validatedIndList, APPLICANTS, DartDBConstants.APPLICANT_VALIDATED, onlineSubmitterInd);

    // Integer enterprisePublics = 0;
    if (onlineSubmitterInd > 0) {
      applicantsSummary.put("onlineSubmitterInd", 1);
    } else {
      applicantsSummary.put("onlineSubmitterInd", 0);
    }
    applicantsSummary.put("applicantTypeCode", projectAvailability.get().getApplicantTypeCode());
    applicantsSummary.put("mailInInd", projectAvailability.get().getMailInInd());
    logger.info(
        "Exiting from retrieveAllPublicsAssociatedWithThisProject().  User Id {}, Context Id {}, Project Id {}",
        userId, contextId, projectId);
    return new ResponseEntity<>(applicantsSummary, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Object> getAllMatchedApplicants(final String userId, final String contextId,
      final PublicType publicType, String firstName, 
      SearchPatternEnum fType, String lastName, SearchPatternEnum lType) {

    try {
      if ((StringUtils.hasLength(fType.name()) && !SEARCH_PATTERNS.contains(fType.name()))
          || (StringUtils.hasLength(lType.name()) && !SEARCH_PATTERNS.contains(lType.name()))) {
        logger.error(
            "Incorrect search Pattern is passed in public search. User Id {}: Context Id {}",
            userId, contextId);
        return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
      }

      List<PublicAssociatedFacility> publicAndAssociatedFacilities = null;
      if (INDIVIDUAL.equals(publicType.name()) || SOLE_PROPRIETOR.equals(publicType.name())) {
        if (StringUtils.isEmpty(lastName)
            || (StringUtils.hasLength(firstName) && StringUtils.isEmpty(fType))
            || StringUtils.isEmpty(lType)) {

          logger.error("Missing the search parameters in public search. User Id {}: Context Id {}",
              userId, contextId);
          return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
        firstName = StringUtils.hasLength(firstName) ? firstName.toUpperCase() : null;
//        fType = StringUtils.hasLength(fType.name()) ? fType.name().toUpperCase() : null;
        publicAndAssociatedFacilities = dartDBDAO.searchAllMatchedApplicants(
            userId, contextId, firstName, fType.name().toUpperCase(),            
            lastName.toUpperCase(), lType.name().toUpperCase());
      } else if (ORG_AGENCY_LIST.contains(publicType.name())) {
        if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(fType.name())) {
          logger.error(
              "Missing the search parameters in Organization/agency search. User Id {}: Context Id {}",
              userId, contextId);
          return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
        if (DartDBConstants.INCORPORATED_BIZ.equals(publicType.name())
            || DartDBConstants.TRUST_OR_ASSOCIATION.equals(publicType.name())) {

          logger.info(" Name {}, Search Type {} , Public Type {}", firstName.toUpperCase(),
              fType.name().toUpperCase(), DartDBConstants.CORPN_PARTNER);
          publicAndAssociatedFacilities = dartDBDAO.searchAllMatchedPublicOrganizations(userId, contextId,
              firstName.toUpperCase(), fType.name().toUpperCase(), DartDBConstants.CORPN_PARTNER);
        } else {
          publicAndAssociatedFacilities = dartDBDAO.searchAllMatchedPublicOrganizations(userId, contextId,
              firstName.toUpperCase(), fType.name().toUpperCase(), publicType.name().toUpperCase());
        }
      } else {
        logger.error(
            "Invalid public type {} has received for the search. User Id {}: Context Id {}",
            publicType, userId, contextId);
        return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
      }

      if (CollectionUtils.isEmpty(publicAndAssociatedFacilities)) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        Map<Long, dec.ny.gov.etrack.dart.db.model.Public> matchedApplicationMap = new HashMap<>();
        publicAndAssociatedFacilities.forEach(publicAndAssociatedFacility -> {
          dec.ny.gov.etrack.dart.db.model.Public matchedPublic = null;
          if (matchedApplicationMap.get(publicAndAssociatedFacility.getPublicId()) == null) {
            matchedPublic = new dec.ny.gov.etrack.dart.db.model.Public();
            matchedPublic.setPublicId(publicAndAssociatedFacility.getPublicId());
            if (StringUtils.hasLength(publicAndAssociatedFacility.getLname())
                && StringUtils.hasLength(publicAndAssociatedFacility.getFname())) {
              matchedPublic
                  .setDisplayName(dartDBServiceUtility.preparePublicNameINSearchResultFormat(
                      publicAndAssociatedFacility.getLname(), publicAndAssociatedFacility.getFname(), 
                      publicAndAssociatedFacility.getMname()));
            } else if (StringUtils.hasLength(publicAndAssociatedFacility.getName())) {
              matchedPublic.setDisplayName(publicAndAssociatedFacility.getName().replaceAll("\\*", " "));
            } else {
              matchedPublic.setDisplayName(dartDBServiceUtility.preparePublicNameFormat(
                  publicAndAssociatedFacility.getLname(), publicAndAssociatedFacility.getFname(), publicAndAssociatedFacility.getMname()));
            }

            // Facility details
            Facility facility = retrieveFacilityAndAddressDetail(publicAndAssociatedFacility);
            List<Facility> facilityList = new ArrayList<>();
            if (facility != null) {
              facility.setFacilityName(publicAndAssociatedFacility.getDistrictName());
              // facility.setLocationDirections(publicResult.getLocationDirections());
              // facility.setCity(publicResult.getCity());
              facilityList.add(facility);
            }
            matchedPublic.setFacilities(facilityList);
            matchedApplicationMap.put(publicAndAssociatedFacility.getPublicId(), matchedPublic);
          } else {
            matchedPublic = matchedApplicationMap.get(publicAndAssociatedFacility.getPublicId());
            Facility facility = retrieveFacilityAndAddressDetail(publicAndAssociatedFacility);
            if (facility != null) {
              facility.setFacilityName(publicAndAssociatedFacility.getDistrictName());
              matchedPublic.getFacilities().add(facility);
            }
          }
        });
        if (matchedApplicationMap.keySet().size() > 200) {
          return new ResponseEntity<Object>(new ErrorResponse("TOO_MANY_RESULTS_MSG",
              "Too many records are being returned. Narrow down the selection criteria and try again."),
              HttpStatus.EXPECTATION_FAILED);
        }

        List<dec.ny.gov.etrack.dart.db.model.Public> matchedApplicants = new ArrayList<>();
        if (!CollectionUtils.isEmpty(matchedApplicationMap)) {
          for ( dec.ny.gov.etrack.dart.db.model.Public publicEntity : matchedApplicationMap.values()) {
            if (!CollectionUtils.isEmpty(publicEntity.getFacilities())) {
              List<Facility> sortedFacilities =
                  publicEntity
                      .getFacilities().stream().sorted(Comparator
                          .comparing(Facility::getFacilityName, String.CASE_INSENSITIVE_ORDER))
                      .collect(Collectors.toList());
              publicEntity.setFacilities(sortedFacilities);
            }
            matchedApplicants.add(publicEntity);
          }
        }
        Map<String, List<dec.ny.gov.etrack.dart.db.model.Public>> matchedApplicantsMap =
            new HashMap<>();
        matchedApplicants = matchedApplicants.stream()
            .sorted(Comparator.comparing(dec.ny.gov.etrack.dart.db.model.Public::getDisplayName,
                String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
        matchedApplicantsMap.put(APPLICANTS, matchedApplicants);
        return new ResponseEntity<>(matchedApplicantsMap, HttpStatus.OK);
      }
    } catch (Exception e) {
      logger.error("Error while retrieving the matched applicant results {}", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<Object> getApplicantInfo(final String userId, final String contextId,
      final Long projectId, Long publicId, final String aplctType) {

    logger.info("Entering into getApplicantInfo Context Id {}", contextId);
    try {
      List<Public> existingPublic = dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId);

      // List<PublicDetail> response = null;
      Map<String, Object> response = null;
      if (!CollectionUtils.isEmpty(existingPublic)) {
        publicId = existingPublic.get(0).getPublicId();
      }
      response = dartDBDAO.getApplicantDetails(userId, contextId, projectId, publicId);

      if (CollectionUtils.isEmpty(response)) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      @SuppressWarnings("unchecked")
      Applicant applicant = transformationService.transformPublicEntityToApplicant(userId,
          contextId, aplctType, (List<PublicDetail>) response.get(PUBLIC_CURSOR), publicId);

      // if (existingPublic.get(0).getOnlineSubmitterInd() != null
      // && existingPublic.get(0).getOnlineSubmitterInd().equals(1)) {
      // applicant.setOnlineSubmitterInd(1);
      // } else {
      // applicant.setOnlineSubmitterInd(0);
      // }
      return new ResponseEntity<>(applicant, HttpStatus.OK);
    } catch (DataIntegrityViolationException e) {
      logger.error("Error while interacting with database  ", e);
      throw new BadRequestException("INVALID_REQ", "Invalid Request is passed", projectId);
    } catch (NoDataFoundException | DartDBException | BadRequestException e) {
      throw e;
    } catch (Exception e) {
      logger.error(" ", e);
      throw new DartDBException("APLCT_RETRIEVE_ERR",
          "General error is occurred while getting applicant information ", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseEntity<Object> getEdbApplicantInfo(final String userId, final String contextId,
      final Long projectId, Long edbPublicId, final String aplctType) {

    logger.info("Entering into getEdbApplicantInfo Context Id {}", contextId);
    try {
      Map<String, Object> response =
          dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null);

      if (CollectionUtils.isEmpty(response)) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      Applicant applicant = transformationService.transformPublicEntityToApplicant(userId,
          contextId, aplctType, (List<PublicDetail>) response.get(PUBLIC_CURSOR), null);

      return new ResponseEntity<>(applicant, HttpStatus.OK);
    } catch (DataIntegrityViolationException e) {
      logger.error("Error while interacting with database  ", e);
      throw new BadRequestException("INVALID_REQ", "Invalid Request is passed", projectId);
    } catch (NoDataFoundException | DartDBException | BadRequestException nfe) {
      throw nfe;
    } catch (Exception e) {
      logger.error("General error is occurred while getting DART "
          + "applicant information. User Id {}, Context Id {} ", userId, contextId, e);
      throw new DartDBException("GENERAL_ERR_EXISTING_PUBLIC_RETRIEVAL",
          "General error while retrieving the existing applicant info ", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseEntity<Object> retrieveApplicantHistory(String userId, String contextId,
      Long projectId, Long applicantId) {
    logger.info("Entering into retrieveApplicantHistory Context Id {}", contextId);;
    List<Public> existingPublic =
        dartDbRepo.findAllPublicsAssociatedProject(projectId, applicantId);
    // List<PublicDetail> response = null;
    Map<String, Object> response = null;
    if (!CollectionUtils.isEmpty(existingPublic) && existingPublic.size() > 0) {
      applicantId = existingPublic.get(0).getPublicId();
    }
    response = dartDBDAO.getApplicantDetails(userId, contextId, projectId, applicantId);

    if (CollectionUtils.isEmpty(response)) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    Applicant applicant = transformationService.transformPublicEntityToApplicant(userId, contextId,
        null, (List<PublicDetail>) response.get(PUBLIC_CURSOR), applicantId);

    // if (existingPublic.get(0).getOnlineSubmitterInd() != null
    // && existingPublic.get(0).getOnlineSubmitterInd().equals(1)) {
    // applicant.setOnlineSubmitterInd(1);
    // } else {
    // applicant.setOnlineSubmitterInd(0);
    // }

    List<PublicHistoryDetail> publicHistory =
        (List<PublicHistoryDetail>) response.get(PUBLIC_HIST_CURSOR);
    List<AddressHistory> addressHistory = (List<AddressHistory>) response.get(ADDR_HIST_CURSOR);
    Applicant applicantHistory = null;
    if (applicant.getEdbApplicantId() != null) {
      applicantHistory = transformationService.transformPublicHistoryIntoApplicant(userId,
          contextId, publicHistory, addressHistory);
    }
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("applicant", applicant);
    result.put("applicantHistory", applicantHistory);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Transactional
  @Override
  public Object validateEdbPublicId(String userId, String contextId, Long projectId, Long publicId,
      Long edbPublicId) {

    List<Public> anyExistingAssociatedPublic =
        dartDbRepo.findAllPublicsAssociatedProject(projectId, edbPublicId);
    if (!CollectionUtils.isEmpty(anyExistingAssociatedPublic)) {
      return new ResponseEntity<>(
          new Result("PUBLIC_ALREADY_EXIST", "There is a public already associated with this "
              + "project for this existing Public Id " + publicId),
          HttpStatus.CONFLICT);
    }

    List<Public> existingPublic = dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId);

    if (CollectionUtils.isEmpty(existingPublic)) {
      return new ResponseEntity<>(
          new Result("NO_PUBLIC_AVAILABLE",
              "There is no Public Associated with this Public Id " + publicId),
          HttpStatus.BAD_REQUEST);
    }

    String publicTypeCode = existingPublic.get(0).getPublicTypeCode();
    if (!StringUtils.hasLength(publicTypeCode)) {
      return new ResponseEntity<>(
          new Result("PUBLIC_TYPE_CODE_NOT_AVAILABLE",
              "There is no Public Type code available with this Public Id " + publicId),
          HttpStatus.BAD_REQUEST);
    }

    if (publicTypeCode.equals(SOLE_PROPRIETOR) || publicTypeCode.equals(INDIVIDUAL)) {
      publicTypeCode = INDIVIDUAL;
    } else if (publicTypeCode.equals(DartDBConstants.INCORPORATED_BIZ)
        || publicTypeCode.equals(DartDBConstants.TRUST_OR_ASSOCIATION)) {
      publicTypeCode = DartDBConstants.CORPN_PARTNER;
    }
    logger.info("Retrieve the Existing public details for the Public Id  {} and Type code {}",
        edbPublicId, publicTypeCode);
    Map<String, Object> edbPublics =
        dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, publicTypeCode);
    if (CollectionUtils.isEmpty(edbPublics)) {
      return new ResponseEntity<>(
          new Result("NO_EXISTING_PUBLIC",
              "There is no existing public available for the public id " + edbPublicId),
          HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private static Facility retrieveFacilityAndAddressDetail(PublicAssociatedFacility publicResult) {
    StringBuilder facilityAddresses = new StringBuilder();
    String locationDirections = publicResult.getLocationDirections();
    if (StringUtils.hasLength(locationDirections)) {
      facilityAddresses.append(locationDirections);
    }
    String city = publicResult.getCity();
    if (StringUtils.hasLength(city)) {
      // facility.setCity(city);
      if (locationDirections != null) {
        facilityAddresses.append(" ").append(city);
      } else {
        facilityAddresses.append(city);
      }
    }
    Facility facility = null;
    if (StringUtils.hasLength(facilityAddresses.toString())) {
      facility = new Facility();
      facility.setFormattedAddress(facilityAddresses.toString());
    }
    return facility;
  }

  /**
   * Assign the public summary into the category which is passed as an input.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param publicLists - Public Lists.
   * @param applicantsSummary - Applicant Summary which is used to store the public in the categoy
   *        passed.
   * @param validatedIndList - Validated Indicator list.
   * @param category - Category . Applicants, Owners or Contact/Agent.
   * @param activityStatusId - Activity Status Id.
   * @param onlineSubmitterInd - Online Submitter indicator.
   */
  private void categorizedPublicSummary(final String userId, final String contextId,
      final Long projectId, List<ApplicantDto> publicLists, Map<String, Object> applicantsSummary,
      List<ProjectActivity> validatedIndList, final String category, final Integer activityStatusId,
      Integer onlineSubmitterInd) {

    Integer validatedPublics = 0;
    ProjectActivity projectActivity = null;

    if (!CollectionUtils.isEmpty(publicLists)) {
      logger.info(
          "There is no public is validated for this project id {}. User Id {}, Context Id {}",
          projectId, userId, contextId);
      List<Integer> signSubmitActivityList =
          projectActivityRepo.findProjectActivityStatusId(projectId, 5);

      if (!CollectionUtils.isEmpty(signSubmitActivityList)) {
        if (validatedPublics != 0 && publicLists.size() == validatedPublics) {
          applicantsSummary.put(category + DartDBConstants.VALIDATED_IND, "Y");
        } else {
          applicantsSummary.put(category + DartDBConstants.VALIDATED_IND, "N");
        }
        if (CollectionUtils.isEmpty(validatedIndList)) {
          projectActivity = new ProjectActivity();
          projectActivity.setProjectId(projectId);
          projectActivity.setCreatedById(userId);
          projectActivity.setCreateDate(new Date());
          projectActivity.setActivityStatusId(activityStatusId);
          projectActivity.setStartDate(new Date());
          if (applicantsSummary.get(category + DartDBConstants.VALIDATED_IND).equals("Y")) {
            projectActivity.setCompletionDate(new Date());
          }
          projectActivityRepo.save(projectActivity);
        } else {
          projectActivity = validatedIndList.get(0);
          if (projectActivity.getCompletionDate() != null
              && applicantsSummary.get(category + DartDBConstants.VALIDATED_IND).equals("N")) {
            projectActivityRepo.updateProjectActivityStatusIdAsIncomplete(userId, projectId,
                projectActivity.getProjectActivityStatusId(), activityStatusId);
          } else if (projectActivity.getCompletionDate() == null
              && applicantsSummary.get(category + DartDBConstants.VALIDATED_IND).equals("Y")) {
            projectActivityRepo.updateProjectActivityStatusId(userId, projectId,
                projectActivity.getProjectActivityStatusId(), activityStatusId, new Date());
          }
        }
      }
      List<ApplicantDto> sortedPublicList = publicLists.stream()
          .sorted(Comparator.comparing(ApplicantDto::getDisplayName, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      applicantsSummary.put(category, sortedPublicList);
    }
  }

  @Override
  public ResponseEntity<Object> getApplicantsSummary(final String userId, final String contextId,
      final Long projectId, final Integer assignedProject) {
    logger.info("Entering into getApplicantSummary(). User Id {}, Context Id{}, Project Id {}",
        userId, contextId, projectId);

    List<Public> publicLists =
        dartDbRepo.findAllApplicantsByProjectIdAndSelectedInEtrackInd(projectId, assignedProject);
    List<Public> sortedPublicList = null;
    Map<String, Object> applicantsSummary = new HashMap<>();
    if (!CollectionUtils.isEmpty(publicLists)) {
      sortedPublicList = publicLists.stream()
          .sorted(Comparator.comparing(Public::getDisplayName, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      sortedPublicList.forEach(publicName -> {
        publicName.setProjectId(null);
//        publicName.setSelectedInEtrackInd(null);
        if (publicName.getEdbPublicId() == null) {
          publicName.setEdbPublicId(0l);
        }
      });
    }
    applicantsSummary.put(PROJECT_ID, projectId);
    applicantsSummary.put(APPLICANTS, sortedPublicList);
    return new ResponseEntity<>(applicantsSummary, HttpStatus.OK);
  }
  
//  /**
//   * 
//   */
//  @Override
//  public Map<String, Object> getPropertyOwnerSummary(String userId, String contextId,
//      Long projectId) {
//
//    Map<String, Object> response = dartDBDAO.geETrackFacilityDetails(userId, contextId, projectId);
//    return transformationService.getOwnerSummary(userId, contextId, response, projectId, true);
//  }
//  
//  @Override
//  public ResponseEntity<Object> getContactsSummary(String userId, String contextId, Long projectId,
//      Integer assignedProjectInd) {
//
//    List<ContactAgent> contactAgentsList =
//        contactRepo.findAllContactAgentsByProjectId(projectId, assignedProjectInd);
//    List<ContactAgent> sortedContactAgentsList = null;
//    Map<String, Object> contactsSummary = new HashMap<>();
//    if (!CollectionUtils.isEmpty(contactAgentsList)) {
//      sortedContactAgentsList = contactAgentsList.stream()
//          .sorted(Comparator.comparing(ContactAgent::getDisplayName, String.CASE_INSENSITIVE_ORDER))
//          .collect(Collectors.toList());
//      sortedContactAgentsList.forEach(publicName -> {
//      });
//    }
//    contactsSummary.put(PROJECT_ID, projectId);
//    contactsSummary.put(APPLICANTS, sortedContactAgentsList);
//    return new ResponseEntity<>(contactsSummary, HttpStatus.OK);
//  }
  
}
