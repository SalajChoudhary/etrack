package dec.ny.gov.etrack.dart.db.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.ContactAgent;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;
import dec.ny.gov.etrack.dart.db.entity.FacilityBIN;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.GIInquiryAlert;
import dec.ny.gov.etrack.dart.db.entity.Municipality;
import dec.ny.gov.etrack.dart.db.entity.OutForReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectAlert;
import dec.ny.gov.etrack.dart.db.entity.ProjectDevelopment;
import dec.ny.gov.etrack.dart.db.entity.ProjectResidential;
import dec.ny.gov.etrack.dart.db.entity.ProjectSICNAICSCode;
import dec.ny.gov.etrack.dart.db.entity.ProjectSWFacilityType;
import dec.ny.gov.etrack.dart.db.entity.PublicDetail;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDetail;
import dec.ny.gov.etrack.dart.db.entity.history.AddressHistory;
import dec.ny.gov.etrack.dart.db.entity.history.PublicHistoryDetail;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Address;
import dec.ny.gov.etrack.dart.db.model.Alert;
import dec.ny.gov.etrack.dart.db.model.Applicant;
import dec.ny.gov.etrack.dart.db.model.AvailTransType;
import dec.ny.gov.etrack.dart.db.model.BridgeIdNumber;
import dec.ny.gov.etrack.dart.db.model.Contact;
import dec.ny.gov.etrack.dart.db.model.CurrentDartStatus;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.Facility;
import dec.ny.gov.etrack.dart.db.model.Individual;
import dec.ny.gov.etrack.dart.db.model.Organization;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;
import dec.ny.gov.etrack.dart.db.model.PermitContact;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.model.SWFacilitySubType;
import dec.ny.gov.etrack.dart.db.model.SWFacilityType;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryCategory;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@Service
public class TransformationService {

  @Autowired
  private DartDBServiceUtility dartDBServiceUtility;
  
  @Autowired
  private ApplicationRepo applicationRepo;
  
  private static final Logger logger =
      LoggerFactory.getLogger(TransformationService.class.getName());
  private static final String PROJECT_ID = "projectId";
  private static final String APPLICANTS = "applicants";
  private static final String OWNER = "1";

  private SimpleDateFormat mmDDYYYFormat = new SimpleDateFormat("MM/dd/yyyy");
  private SimpleDateFormat mmDDYYYFormatAMPM = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//  private final String[] categorizeExistingBatch = new String[] {"F","G", "H", "I", "J", "K", "M", "N", "O", "P", "Q","R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
  
  /**
   * Prepare the Owner summary based on the results from eTrack database.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param resultSet - Data result set from the database.
   * @param projectId - Project Id.
   * @param assignedProject - Boolean indicator to represents whether they are assigned/associated to that project or not.
   * 
   * @return - Owner Summary details.
   */
  public Map<String, Object> getOwnerSummary(String userId, String contextId,
      Map<String, Object> resultSet, Long projectId, boolean assignedProject) {

    logger.info("Entering into Owner summary. User Id {},  Context Id :{}", userId, contextId);
    Map<String, Object> summary = null;
    @SuppressWarnings("unchecked")
    List<PublicDetail> publicDetails =
        (List<PublicDetail>) resultSet.get(DartDBConstants.PUBLICS_CURSOR);
    if (!CollectionUtils.isEmpty(publicDetails)) {
      summary = new HashMap<>();
      summary.put(PROJECT_ID, projectId);
      Set<Applicant> applicantSummary = new HashSet<>();
      for (PublicDetail publicDetail : publicDetails) {
        logger.debug("Selected in ETrack Indicator {} for the Public id {}",
            publicDetail.getSelectedInEtrackInd(), publicDetail.getPublicId());
        Applicant applicant = new Applicant();
        applicant.setApplicantId(publicDetail.getPublicId());
        applicant.setDisplayName(publicDetail.getDisplayName());
        if (OWNER.equals(publicDetail.getLegallyResponsibleTypeCode())) {
          if (assignedProject) {
            if (publicDetail.getSelectedInEtrackInd() != null
                && publicDetail.getSelectedInEtrackInd() == 1) {
              applicantSummary.add(applicant);
            }
          } else {
            applicantSummary.add(applicant);
          }
        }
      }
      summary.put(APPLICANTS, applicantSummary);
    }
    logger.info("Exiting from Owner summary. User Id {},  Context Id :{}", userId, contextId);
    return summary;
  }

  /**
   * Transform the eTrack Public Entity data into UI/Consumer readable format.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param searchPublicType - Public Type , I - Individual, S - Sole Proprietor etc.
   * @param publicDetails - Public details in Entity format which needs to be transformed.
   * @param publicId - Public Id.
   * 
   * @return - Transformed Public details {@link Applicant}
   */
  public Applicant transformPublicEntityToApplicant(final String userId, final String contextId,
      final String searchPublicType, List<PublicDetail> publicDetails, final Long publicId) {

    logger.info("Entering into transformPublicEntityToApplicant. User Id {}, Context Id {}", userId, contextId); 
    Applicant applicant = null;
    if (!CollectionUtils.isEmpty(publicDetails)) {
      PublicDetail publicDetail = publicDetails.get(0);
      applicant = new Applicant();
      if (publicId != null && publicId > 0) {
        applicant.setApplicantId(publicId);
      } else {
        applicant.setApplicantId(publicDetail.getPublicId());
      }
      applicant.setEdbApplicantId(publicDetail.getEdbPublicId());
      // applicant.setDisplayName(publicDetail.getDisplayName());
      String publicType = null;
      if (StringUtils.isEmpty(searchPublicType)) {
        publicType = publicDetail.getPublicTypeCode();
      } else {
        publicType = searchPublicType;
      }
      applicant.setPublicTypeCode(publicType);
      if ((DartDBConstants.INDIVIDUAL.equals(publicType)
          || DartDBConstants.SOLE_PROPRIETOR.equals(publicType))) {

        Individual individual = new Individual();
        individual.setFirstName(publicDetail.getFirstName());
        individual.setLastName(publicDetail.getLastName());
        individual.setMiddleName(publicDetail.getMiddleName());
        individual.setSuffix(publicDetail.getSuffix());
        applicant.setIndividual(individual);
      } else if (DartDBConstants.INCORPORATED_BIZ.equals(publicType)
          || DartDBConstants.CORPN_PARTNER.equals(publicType)
          || DartDBConstants.TRUST_OR_ASSOCIATION.equals(publicType)) {

        Organization organization = new Organization();
        organization.setBusOrgName(publicDetail.getPublicName());
        if (publicDetail.getIncorpInd() != null) {
          if (publicDetail.getIncorpInd() == 1) {
            organization.setIsIncorporated("Y");
          } else if (publicDetail.getIncorpInd() == 0) {
            organization.setIsIncorporated("N");
          }
        }

        if (publicDetail.getSelectedInEtrackInd() == null
            || publicDetail.getSelectedInEtrackInd() == 0) {
          organization.setBusinessVerified(null);
        } else {
          if (publicDetail.getBusinessValidatedInd() != null) {
            if (publicDetail.getBusinessValidatedInd() == 1) {
              organization.setBusinessVerified("Y");
            } else if (publicDetail.getBusinessValidatedInd() == 0) {
              organization.setBusinessVerified("N");
            }
          }
        }

        organization.setTaxPayerId(publicDetail.getTaxpayerId());
        organization.setIncorporationState(publicDetail.getIncorpState());
        organization.setIncorporateCountry(publicDetail.getTerritoryOrCountry());
        applicant.setOrganization(organization);
      } else {
        applicant.setGovtAgencyName(publicDetail.getPublicName());
      }
      Address address = new Address();
      address.setAddressId(publicDetail.getAddressId());
      address.setEdbAddressId(publicDetail.getEdbAddressId());

      if (publicDetail.getForeignAddressInd() != null) {
        if (publicDetail.getForeignAddressInd().equals(1)) {
          address.setPostalCode(publicDetail.getZip());
        } else {
          address.setZipCode(publicDetail.getZip());
        }
        address.setAdrType(String.valueOf(publicDetail.getForeignAddressInd()));
      } else {
        if (StringUtils.isEmpty(publicDetail.getCountry())
            || (StringUtils.hasLength(publicDetail.getCountry())
                && ("USA".equals(publicDetail.getCountry())
                    || "US".equals(publicDetail.getCountry())))) {
          address.setAdrType("0");
          address.setZipCode(publicDetail.getZip());
        } else {
          address.setPostalCode(publicDetail.getZip());
          address.setAdrType("1");
        }
      }
      address.setStreetAdr1(publicDetail.getStreet1());
      address.setStreetAdr2(publicDetail.getStreet2());
      address.setCity(publicDetail.getCity());
      address.setState(publicDetail.getState());
      address.setCountry(publicDetail.getCountry());
      address.setAttentionName(publicDetail.getAttentionName());
      applicant.setAddress(address);
      Contact contact = new Contact();
      contact.setEmailAddress(publicDetail.getEmailAddress());
      contact.setCellNumber(publicDetail.getCellPhoneNumber());
      contact.setWorkPhoneNumber(publicDetail.getBusinessPhoneNumber());
      contact.setWorkPhoneNumberExtn(publicDetail.getBusinessPhoneExt());
      contact.setHomePhoneNumber(publicDetail.getHomePhoneNumber());
      applicant.setContact(contact);

      applicant.setDba(publicDetail.getDbaName());
      if (publicDetail.getValidatedInd() != null) {
        if (publicDetail.getValidatedInd() == 0) {
          applicant.setValidatedInd("N");
        } else if (publicDetail.getValidatedInd() == 1) {
          applicant.setValidatedInd("Y");
        }
      }
      List<String> propertyRelationShip = new ArrayList<>();
      for (PublicDetail publicDetail1 : publicDetails) {
        propertyRelationShip.add(publicDetail1.getLegallyResponsibleTypeCode());
      }
      applicant.setPropertyRelationships(propertyRelationShip);
    }
    logger.info("Exiting from transformPublicEntityToApplicant. User Id {}, Context Id {}", userId, contextId);
    return applicant;
  }

  /**
   * Transform the Public Historical data(typically existing public data from DART)
   *  into UI/Consumer readable format which helps to show the Yield icon if any difference. 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param publicHistoryList - History of the Public.
   * @param addressHistoryList - History of the Public's address.
   * 
   * @return - Returns the transformed Applicant History details. {@link Applicant}
   */
  public Applicant transformPublicHistoryIntoApplicant(final String userId, final String contextId, 
      List<PublicHistoryDetail> publicHistoryList, List<AddressHistory> addressHistoryList) {
    
    logger.info("Entering into transformPublicHistoryIntoApplicant. User Id {}, Context Id {}", userId, contextId);
    Applicant historyApplicant = null;
    if (!CollectionUtils.isEmpty(publicHistoryList)) {
      PublicHistoryDetail publicHistoryDetail = publicHistoryList.get(0);
      String publicType = publicHistoryDetail.getHPublicTypeCode();
      historyApplicant = new Applicant();
      historyApplicant.setApplicantId(publicHistoryDetail.getHPublicId());
      historyApplicant.setEdbApplicantId(publicHistoryDetail.getHEdbPublicId());
      historyApplicant.setPublicTypeCode(publicType);
      if ((DartDBConstants.INDIVIDUAL.equals(publicType)
          || DartDBConstants.SOLE_PROPRIETOR.equals(publicType))) {

        Individual individual = new Individual();
        individual.setFirstName(publicHistoryDetail.getHFirstName());
        individual.setLastName(publicHistoryDetail.getHLastName());
        individual.setMiddleName(publicHistoryDetail.getHMiddleName());
        individual.setSuffix(publicHistoryDetail.getHSuffix());
        historyApplicant.setIndividual(individual);
      } else if (DartDBConstants.INCORPORATED_BIZ.equals(publicType)
          || DartDBConstants.CORPN_PARTNER.equals(publicType)
          || DartDBConstants.TRUST_OR_ASSOCIATION.equals(publicType)) {

        Organization organization = new Organization();
        organization.setBusOrgName(publicHistoryDetail.getHPublicName());
        if (publicHistoryDetail.getHIncorpInd() != null) {
          if (publicHistoryDetail.getHIncorpInd() == 1) {
            organization.setIsIncorporated("Y");
          } else if (publicHistoryDetail.getHIncorpInd() == 0) {
            organization.setIsIncorporated("N");
          }
        }

        if (publicHistoryDetail.getHBusinessValidatedInd() != null) {
          if (publicHistoryDetail.getHBusinessValidatedInd() == 1) {
            organization.setBusinessVerified("Y");
          } else if (publicHistoryDetail.getHBusinessValidatedInd() == 0) {
            organization.setBusinessVerified("N");
          }
        }
        organization.setTaxPayerId(publicHistoryDetail.getHTaxpayerId());
        organization.setIncorporationState(publicHistoryDetail.getHIncorpState());
        organization.setIncorporateCountry(publicHistoryDetail.getHTerritoryOrCountry());
        historyApplicant.setOrganization(organization);
      } else {
        historyApplicant.setGovtAgencyName(publicHistoryDetail.getHPublicName());
      }
      historyApplicant.setDba(publicHistoryDetail.getHDbaName());
      Address address = new Address();
      Contact contact = new Contact();
      if (!CollectionUtils.isEmpty(addressHistoryList)) {
        AddressHistory addressHistory = addressHistoryList.get(0);
        address.setAddressId(addressHistory.getHAddressId());
        if (StringUtils.isEmpty(addressHistory.getHCountry())
            || "USA".equals(addressHistory.getHCountry().toUpperCase())
            || "US".equals(addressHistory.getHCountry().toUpperCase())) {
          address.setAdrType("0");
          address.setZipCode(addressHistory.getHZip());
        } else {
          address.setAdrType("1");
          address.setPostalCode(addressHistory.getHZip());
        }
        address.setStreetAdr1(addressHistory.getHStreet1());
        address.setStreetAdr2(addressHistory.getHStreet2());
        address.setCity(addressHistory.getHCity());
        address.setState(addressHistory.getHState());
        address.setCountry(addressHistory.getHCountry());
        address.setAttentionName(addressHistory.getHAttentionName());
        contact.setEmailAddress(addressHistory.getHEmailAddr());
        contact.setCellNumber(addressHistory.getHCellNumber());
        contact.setWorkPhoneNumber(addressHistory.getHBusPhoneNumber());
        contact.setWorkPhoneNumberExtn(addressHistory.getHBusExt());
        contact.setHomePhoneNumber(addressHistory.getHHomePhoneNumber());
      }
      historyApplicant.setAddress(address);
      historyApplicant.setContact(contact);
    }
    logger.info("Exiting from transformPublicHistoryIntoApplicant. User Id {}, Context Id {}", userId, contextId);
    return historyApplicant;
  }

  /**
   * Retrieve the Facility details from the DB Result set and Transform the UI readable format.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param resultSet - Data result set from eTrack database.
   * @param projectId - Project Id to get the Facility details.
   * 
   * @return - Transformed Facility details. {@link FacilityDetail}
   */
  public FacilityDetail getFacilityDetails(String userId, String contextId,
      Map<String, Object> resultSet, Long projectId) {

    logger.info("Entering into getFacilityDetails User Id {} Context Id :{} , projectId {}", userId,
        contextId, projectId);
    @SuppressWarnings("unchecked")
    List<FacilityDetail> facilityDetails =
        (List<FacilityDetail>) resultSet.get(DartDBConstants.FACILITY_CURSOR);
    if (!CollectionUtils.isEmpty(facilityDetails)) {
      FacilityDetail facilityDetail = facilityDetails.get(0);
      if (!StringUtils.hasText(facilityDetail.getDecId())) {
        facilityDetail.setDecIdFormatted(null);
      }
      if (!StringUtils.isEmpty(facilityDetail.getStreet1())) {
        StringBuilder street = new StringBuilder();
        street.append(facilityDetail.getStreet1());
        if (!StringUtils.isEmpty(facilityDetail.getStreet2())) {
          street.append(" ").append(facilityDetail.getStreet2());
        }
        facilityDetail.setLocationDirections(street.toString());
      } else {
        facilityDetail.setLocationDirections("");
      }
      logger.info("Exiting from getFacilityDetails User Id {} Context Id :{} , projectId {}", userId,
          contextId, projectId);
      return facilityDetail;
    } else {
      logger.error("Facility is not available for the project Id {} User Id {}, Context Id{}",
          projectId, userId, contextId);
      throw new NoDataFoundException("NO_FACILITY_FOUND", "Facility detail is not available");
    }
  }

  /**
   * Transform the Project Entity details into UI readable and display in Step 3 sub step 2.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param project - Project Entity details.
   * @param binNumbers - BIN numbers associated with this Facility/Project.
   * 
   * @return - Transformed Project Informations like Description, BIN numbers, SIC, NAICS code etc. details. {@link ProjectInfo}
   */
  public ProjectInfo transformProjectEntity(final String userId, final String contextId,
      final Project project, List<FacilityBIN> binNumbers) {
    logger.info("Entering into transformProjectEntity into ProjectInfo User Id {}, Context Id {}",
        userId, contextId);
    ProjectInfo projectInfo = null;
    if (project != null) {
      projectInfo = new ProjectInfo();
      projectInfo.setProjectId(project.getProjectId());
      projectInfo.setBriefDesc(project.getProjectDesc());
      projectInfo.setProposedUse(project.getProposedUseCode());
      projectInfo.setConstrnType(project.getConstrnType());
      projectInfo.setDamType(project.getDamType());
      projectInfo.setClassifiedUnderSeqr(project.getSeqrInd());
      if (!CollectionUtils.isEmpty(binNumbers)) {
        List<BridgeIdNumber> bridgeIdNumbers = new ArrayList<>();
        List<BridgeIdNumber> historyBridgeIdNumbers = new ArrayList<>();
        binNumbers.forEach(bridgeIdNumber -> {
          if (StringUtils.hasLength(bridgeIdNumber.getDeletedInd())
              && bridgeIdNumber.getDeletedInd().equals("1")) {
            historyBridgeIdNumbers
                .add(new BridgeIdNumber(bridgeIdNumber.getBin(), bridgeIdNumber.getEdbBin()));
          } else {
            bridgeIdNumbers
                .add(new BridgeIdNumber(bridgeIdNumber.getBin(), bridgeIdNumber.getEdbBin()));
          }
        });

        List<BridgeIdNumber> sortedBridgeIdHistoryNumbers = historyBridgeIdNumbers.stream()
            .sorted(Comparator.comparing(BridgeIdNumber::getBin).reversed())
            .collect(Collectors.toList());

        List<BridgeIdNumber> sortedBridgeIdNumbers =
            bridgeIdNumbers.stream().sorted(Comparator.comparing(BridgeIdNumber::getBin).reversed())
                .collect(Collectors.toList());
        projectInfo.setBinNumbersHistory(sortedBridgeIdHistoryNumbers);
        projectInfo.setBinNumbers(sortedBridgeIdNumbers);
      }
      projectInfo.setStrWaterbodyName(project.getStrWaterbodyName());
      projectInfo.setWetlandIds(project.getWetlandIds());
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      if (project.getProposedStartDate() != null) {
        projectInfo.setProposedStartDate(sdf.format(project.getProposedStartDate()));
      }
      if (project.getEstmtdCompletionDate() != null) {
        projectInfo.setEstmtdCompletionDate(sdf.format(project.getEstmtdCompletionDate()));
      }
      List<ProjectDevelopment> projectDevList = project.getProjectDevelopments();
      if (!CollectionUtils.isEmpty(projectDevList)) {
        logger.info("Processing project development list UserId {} , Context Id {}", userId,
            contextId);
        List<Integer> developmentList = new ArrayList<>();
        projectDevList.forEach(development -> {
          developmentList.add(development.getDevelopmentTypeCode());
        });
        projectInfo.setDevelopmentType(developmentList);
      }

      List<ProjectResidential> projectResidentialList = project.getProjectResidentials();
      if (!CollectionUtils.isEmpty(projectResidentialList)) {
        logger.info("Processing project Structured list UserId {} , Context Id {}", userId,
            contextId);
        List<Integer> residentialList = new ArrayList<>();
        projectResidentialList.forEach(residential -> {
          residentialList.add(residential.getResDevTypeCode());
        });
        projectInfo.setStructureType(residentialList);
      }

      List<ProjectSICNAICSCode> projectSicNaicsList = project.getProjectSicNaicsCodes();
      if (!CollectionUtils.isEmpty(projectSicNaicsList)) {
        logger.info("Processing project SIC and NAICS list UserId {} , Context Id {}", userId,
            contextId);
        List<Map<String, String>> sicNaicsCodeList = new ArrayList<>();
        projectSicNaicsList.forEach(sicNaicsCode -> {
          Map<String, String> sicCNaics = new HashMap<>();
          if (!StringUtils.isEmpty(sicNaicsCode.getSicCode())) {
            sicCNaics.put(sicNaicsCode.getSicCode(), sicNaicsCode.getNaicsCode());
            sicNaicsCodeList.add(sicCNaics);
          }
        });
        projectInfo.setSicCodeNaicsCode(sicNaicsCodeList);
      }

      List<ProjectSWFacilityType> projectSWFacilityTypes = project.getProjectSWFacilityType();
      List<SWFacilityType> swFacilityTypes = new ArrayList<>();
      if (!CollectionUtils.isEmpty(projectSWFacilityTypes)) {
        logger.info("Processing project SW Facility Type and Sub Type UserId {} , Context Id {}",
            userId, contextId);
        Map<Integer, SWFacilityType> swFacilityTypeMap = new HashMap<>();
        projectSWFacilityTypes.forEach(projectSWFacType -> {
          SWFacilityType swFacilityType =
              swFacilityTypeMap.get(projectSWFacType.getSwFacilityTypeId());
          if (swFacilityType == null) {
            swFacilityType = new SWFacilityType();
            swFacilityType.setSwFacilityType(projectSWFacType.getSwFacilityTypeId());
            if (projectSWFacType.getSwFacilitySubTypeId() != null) {
              List<SWFacilitySubType> subTypes = new ArrayList<>();
              SWFacilitySubType subType = new SWFacilitySubType();
              subType.setSwfacilitySubType(projectSWFacType.getSwFacilitySubTypeId());
              subTypes.add(subType);
              swFacilityType.setSwFacilitySubTypes(subTypes);
            }
            swFacilityTypeMap.put(projectSWFacType.getSwFacilityTypeId(), swFacilityType);
          } else {
            if (projectSWFacType.getSwFacilitySubTypeId() != null) {
              SWFacilitySubType subType = new SWFacilitySubType();
              subType.setSwfacilitySubType(projectSWFacType.getSwFacilitySubTypeId());
              swFacilityType.getSwFacilitySubTypes().add(subType);
            }
          }
        });
        swFacilityTypeMap.keySet().iterator().forEachRemaining(key -> {
          swFacilityTypes.add(swFacilityTypeMap.get(key));
        });
      }
      projectInfo.setSwFacilityTypes(swFacilityTypes);
    }
    logger.info("Exiting from transformProjectEntity. User Id {}, Context Id {}",
        userId, contextId);
    return projectInfo;
  }

  /**
   * Transform the Permit Applications from eTrack database into display format.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique id to track the transaction.
   * @param edbDistrictId - Dart district id
   * @param eTrackPermits - List of Permits applied so far for this facility or project.
   * @param contactAgents - List of Contact/Agents associated with this project.
   * @param permitSummaryInd - Boolean indicator helps whether the Contact/Agents 
   *    should be included in each permit or not for Application form assignment later.
   *    
   * @return - Transformed Permit applications grouped by Permit Category.
   */
  public Map<Integer, List<PermitApplication>> transformPermitApplicationData(final String userId,
      final String contextId, final Long edbDistrictId, List<ETrackPermit> eTrackPermits,
      List<ContactAgent> contactAgents, final boolean permitSummaryInd) {

    logger.info("Entering into transformETrackPermitApplication Data. User Id {}, Context Id {}",
        userId, contextId);
    Map<Integer, List<PermitApplication>> permitApplicationsMap = new HashMap<>();
    logger.debug("Etrack Permits  {}", eTrackPermits);
    for (ETrackPermit permit : eTrackPermits) {
      if (permit.getEdbApplId() == null || permit.getEdbApplId() <= 0) {
        if (CollectionUtils.isEmpty(permitApplicationsMap.get(permit.getPermitCategoryId()))) {
          List<PermitApplication> permitApplications = new LinkedList<>();
          // Add new
          prepareEtrackPermitsToPermitApplication(userId, contextId, permit, edbDistrictId, permitApplications, contactAgents,
              permitSummaryInd);
          permitApplicationsMap.put(permit.getPermitCategoryId(), permitApplications);
        } else {
          prepareEtrackPermitsToPermitApplication(userId, contextId, permit, edbDistrictId,
              permitApplicationsMap.get(permit.getPermitCategoryId()), contactAgents,
              permitSummaryInd);
        }
      }
    }
    logger.info("Exiting from transformETrackPermitApplication Data. User Id {}, Context Id {}",
        userId, contextId);
    return permitApplicationsMap;
  }

  /**
   * Transform the eTrack Permit Entity into consumer readable format. 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param permit - eTrack permit applied earlier for this Facility/Project.
   * @param edbDistrictId - DART District Id.
   * @param permitApplications - Permit Applications will be holding the transformed data.
   * @param contactAgents - Contact/Agents associated with this facility/project.
   * @param permitSummaryInd - Boolean indicator to indicate whether the Contact/Agent should be included or not in each permit or not.
   */
  public void prepareEtrackPermitsToPermitApplication(final String userId, final String contextId, ETrackPermit permit, Long edbDistrictId,
      List<PermitApplication> permitApplications, List<ContactAgent> contactAgents,
      final boolean permitSummaryInd) {
    
    logger.info("Entering into prepareEtrackPermitsToPermitApplication. User Id {}, Context Id {}", userId, contextId);    
    PermitApplication permitApplication = new PermitApplication();
    permitApplication.setPermitTypeCode(permit.getPermitTypeCode());
    permitApplication.setPermitTypeDesc(permit.getPermitTypeDesc());
    permitApplication.setTransType(permit.getTransTypeCode());
    permitApplication.setEdbTransType(permit.getEdbTransTypeCode());
    permitApplication.setEdbDistrictId(edbDistrictId);
    permitApplication.setApplicationId(permit.getApplicationId());
    permitApplication.setEdbApplicationId(permit.getEdbApplId());
    permitApplication.setBatchId(permit.getBatchIdEdb());
    permitApplication.setProgramId(permit.getProgId());
    permitApplication.setProgramIdFormatted(permit.getProgIdFormatted());
    permitApplication.setFormSubmittedInd(permit.getFormSubmittedInd());
    permitApplication.setTrackingInd(permit.getTrackingInd());
    permitApplication.setEdbTrackingInd(permit.getEdbTrackingInd());
    permitApplication.setEffectiveStartDate(permit.getEffectiveStartDate());
    permitApplication.setEffectiveEndDate(permit.getEffectiveEndDate());
    permitApplication.setBatchGroup(permit.getBatchGroupEtrack());
    permitApplication.setEdbAuthId(permit.getEdbAuthId());
    if (permit.getUserSelNewInd() != null && permit.getUserSelNewInd().equals(1)) {
      permitApplication.setNewReqInd("Y");
    } else {
      permitApplication.setNewReqInd("N");
    }
    if (permit.getUserSelExtInd() != null && permit.getUserSelExtInd().equals(1)) {
      permitApplication.setExtnReqInd("Y");
    } else {
      permitApplication.setExtnReqInd("N");
    }
    if (permit.getUserSelModInd() != null && permit.getUserSelModInd().equals(1)) {
      permitApplication.setModReqInd("Y");
      if (permit.getChgOriginalProjectInd() != null
          && permit.getChgOriginalProjectInd().equals(1)) {
        permitApplication.setModQuestionAnswer("Y");
      } else {
        permitApplication.setModQuestionAnswer("N");
      }
    } else {
      permitApplication.setModReqInd("N");
    }
    if (permit.getUserSelRenInd() != null && permit.getUserSelRenInd().equals(1)) {
      permitApplication.setRenewReqInd("Y");
    } else {
      permitApplication.setRenewReqInd("N");
    }
    
    if (permit.getUserSelTransferInd() != null && permit.getUserSelTransferInd().equals(1)) {
      permitApplication.setTransferReqInd("Y");
    } else {
      permitApplication.setTransferReqInd("N");
    }

    if (permit.getPendingInd() != null && permit.getPendingInd().equals(1)) {
      permitApplication.setPendingAppTransferReqInd("Y");
    } else {
      permitApplication.setPendingAppTransferReqInd("N");
    }

    if (permit.getEdbApplId() != null && permit.getEdbApplId() > 0) {
      permitApplication.setDartPendingApp("Y");
    } else {
      permitApplication.setDartPendingApp("N");
    }

    List<PermitContact> contacts = new ArrayList<>();
    for (ContactAgent contact : contactAgents) {
      PermitContact assignedContact = new PermitContact();

      assignedContact.setPublicId(contact.getPublicId());
      assignedContact.setRoleId(contact.getRoleId());
      assignedContact.setDisplayName(contact.getDisplayName());
      assignedContact.setEdbPublicId(contact.getEdbPublicId());
      if (permit.getContactAssignedId() != null
          && permit.getContactAssignedId().equals(contact.getRoleId())) {
        assignedContact.setPermitAssignedInd("Y");
      } else {
        assignedContact.setPermitAssignedInd("N");
      }

      if (assignedContact.getPermitAssignedInd().equals("Y") || !permitSummaryInd) {
        contacts.add(assignedContact);
      }
      permitApplication.setContacts(contacts);
    }
    permitApplications.add(permitApplication);
    logger.info("Exiting from prepareEtrackPermitsToPermitApplication. User Id {}, Context Id {}", userId, contextId);
  }

  /**
   * Transform the existing DART Permit Entity into consumer readable format.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param dartExistingPermit - DART Permit information.
   * @param permitSummaryInd - Boolean indicator to indicate whether the Contact/Agent should be included or not in each permit or not.
   * @param contactAgents - Contact/Agents associated with this facility/project.
   * @param edbDistrictId - DART District Id.
   * 
   * @return - Transformed Permit Application.
   */
  private PermitApplication preparePermitApplicationFromExistingPermit(
      final String userId, final String contextId,
      final DartPermit dartExistingPermit, final boolean permitSummaryInd,
      final List<ContactAgent> contactAgents, final Long edbDistrictId) {
    
    logger.debug("Entering into preparePermitApplicationFromExistingPermit. User Id {}, Context Id {}", userId, contextId);
    PermitApplication permitApplication = new PermitApplication();
    Long gpAuthId = dartExistingPermit.getGpAuthId();
    permitApplication.setEdbAuthId(gpAuthId);
    permitApplication.setProjectDesc(dartExistingPermit.getProjectDesc());
    permitApplication.setEdbTransType(dartExistingPermit.getTransType());
    permitApplication.setBatchId(dartExistingPermit.getBatchId());
    permitApplication.setEdbDistrictId(edbDistrictId);
    permitApplication.setDartPendingApp("Y");
    
//    if (dartExistingPermit.getExpiryDate() != null) {
//      permitApplication
//          .setEffectiveEndDate(mmDDYYYFormat.format(dartExistingPermit.getExpiryDate()));
//    }
//
//    if (dartExistingPermit.getStartDate() != null) {
//      permitApplication
//          .setEffectiveStartDate(mmDDYYYFormat.format(dartExistingPermit.getStartDate()));
//    }
//    if (StringUtils.hasLength(dartExistingPermit.getReceivedDate())) {
//      permitApplication.setReceivedDate(dartExistingPermit.getReceivedDate());
//    }

    permitApplication.setProgramId(dartExistingPermit.getTrackedId());
    permitApplication.setProgramIdFormatted(dartExistingPermit.getTrackedIdFormatted());
    permitApplication.setEdbApplicationId(dartExistingPermit.getApplId());
    permitApplication.setPermitRenewedInd(dartExistingPermit.getRenewedInd());
    permitApplication.setEdbTrackingInd(dartExistingPermit.getTrackingInd());

    if (!permitSummaryInd) {
      List<PermitContact> contacts = new ArrayList<>();
      for (ContactAgent contact : contactAgents) {
        PermitContact assignedContact = new PermitContact();
        assignedContact.setPublicId(contact.getPublicId());
        assignedContact.setRoleId(contact.getRoleId());
        assignedContact.setDisplayName(contact.getDisplayName());
        assignedContact.setEdbPublicId(contact.getEdbPublicId());
        assignedContact.setPermitAssignedInd("N");
        contacts.add(assignedContact);
      }
      permitApplication.setContacts(contacts);
    }
    logger.debug("Exiting from preparePermitApplicationFromExistingPermit. User Id {}, Context Id {}", userId, contextId);
    return permitApplication;
  }

  /**
   * Transform and group the applications based on the eligible transaction type.
   * i.e List of permit eligible
   *    1. Expired 
   *    2. Renewable GP and Non GP
   *    3. Non Renewal GP and Non GP
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param edbDistrictId - Dart district id for the facility.
   * @param dartModExtEligiblePermitApps - List of active authorization applications received from DART.
   * @param dartModExtAssignedPermitInETrackMap - List of active authorization applications applied so far by the user
   * @param dartPendingTransferEligiblePermitApps - Pending transfer eligible applications.
   * @param dartTransferAssignedPermitsMap - List of Transfer only eligible applications.
   * @param contactAgents - Contact/Agents which could be bundled in each permit based on the Permit summary indicator.
   * @param permitSummaryInd - Permit Summary Indicator to decide whether Contact/Agents should be bundled or not.
   * @param extendedDateDetailMap - Extended date details.
   * 
   * @return - Transformed the categorized permit applications.
   */
  public Map<String, Object> transformDartPermitApplicationData(final String userId,
      final String contextId, final Long edbDistrictId,
      Map<Long, DartPermit> dartModExtEligiblePermitApps,
      final Map<Long, ETrackPermit> dartModExtAssignedPermitInETrackMap,
      Map<Long, DartApplication> dartPendingTransferEligiblePermitApps,
      Map<Long, ETrackPermit> dartTransferAssignedPermitsMap,
      final List<ContactAgent> contactAgents, final boolean permitSummaryInd,
      final Map<Long, String> extendedDateDetailMap) {

    logger.info("Entering into transformDartPermitApplicationData. User Id {}, Context Id {}", userId, contextId);
    
    List<DartPermit> existingExpiredPermits = new ArrayList<>();
    List<DartPermit> existingRenewableGeneralPermits = new ArrayList<>();
    List<DartPermit> existingNonRenewableGeneralPermits = new ArrayList<>();
    List<DartPermit> existingRenewableRegularPermits = new ArrayList<>();
    List<DartPermit> existingNonRenewableRegularPermits = new ArrayList<>();
    
    for (Long modExtnPermitApplId : dartModExtEligiblePermitApps.keySet()) {
      if (dartModExtAssignedPermitInETrackMap.get(modExtnPermitApplId) == null) {
        DartPermit dartModExtnPermit = dartModExtEligiblePermitApps.get(modExtnPermitApplId);
        
        if ("REI".equals(dartModExtnPermit.getTransType()) 
            || dartModExtnPermit.getTransType().contains(":E")) {
          existingExpiredPermits.add(dartModExtnPermit);
        } else if (StringUtils.hasLength(dartModExtnPermit.getRenewedInd())
            && "1".equals(dartModExtnPermit.getRenewedInd())) {
          if (dartModExtnPermit.getGpAuthId() != null
              && dartModExtnPermit.getGpAuthId() > 0) {
            existingRenewableGeneralPermits.add(dartModExtnPermit);
          } else {
            existingRenewableRegularPermits.add(dartModExtnPermit);
          }
        } else {
          if (dartModExtnPermit.getGpAuthId() != null && dartModExtnPermit.getGpAuthId() > 0) {
            existingNonRenewableGeneralPermits.add(dartModExtnPermit);
          }  else {
            existingNonRenewableRegularPermits.add(dartModExtnPermit);
          }
        }
      }
    }
    
    Map<String, Object> permitApplicationsMapDetail = new LinkedHashMap<>();
    Map<String, List<PermitApplication>> permitModExtnApplications = new HashMap<>();

    AvailTransType modifyRequestTransType = new AvailTransType("Modify", "MOD");
    AvailTransType transRequestTransType = new AvailTransType("Transfer", "XFER");
    AvailTransType renewalRequestTransType = new AvailTransType("Renew", "REN");

    existingExpiredPermits.forEach(existingExpiredPermit -> {
      PermitApplication permitApplication = preparePermitApplicationFromExistingPermit(userId, contextId,
          existingExpiredPermit, permitSummaryInd, contactAgents, edbDistrictId);
      permitApplication.setPermitTypeCode(existingExpiredPermit.getPermitType());
      permitApplication.setPermitTypeDesc(existingExpiredPermit.getPermitDesc());
      
//      if (existingExpiredPermit.getNonRenewableEffDate() != null
//          && existingExpiredPermit.getMaxPermitTerm() != null) {
//        permitApplication.setExtendedDate(
//            mmDDYYYFormat.format(DateUtils.addYears(existingExpiredPermit.getNonRenewableEffDate(),
//                existingExpiredPermit.getMaxPermitTerm())));
//      }
      
      String expiredBatchId = String.valueOf(existingExpiredPermit.getBatchId()).concat("000E");
      if (permitModExtnApplications.get(expiredBatchId) == null) {
        List<PermitApplication> modExtnEligiblePermitApplicationsList = new ArrayList<>();
        List<AvailTransType> applicableTransTypes = new ArrayList<>();
        AvailTransType extensionTransType = null;
        extensionTransType = new AvailTransType("Extend", "EXT");
        /*
         * if (StringUtils.hasLength(permitApplication.getExtendedDate())) { extensionTransType =
         * new AvailTransType("Extend (" + permitApplication.getExtendedDate() + ")", "EXT"); } else
         * { extensionTransType = new AvailTransType("Extend", "EXT"); }
         */
        applicableTransTypes.add(extensionTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        permitApplication.setCalculatedBatchIdForProcess(expiredBatchId);
        modExtnEligiblePermitApplicationsList.add(permitApplication);
        permitModExtnApplications.put(expiredBatchId, modExtnEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(expiredBatchId);
        permitModExtnApplications.get(expiredBatchId).add(permitApplication);
      }
    });

    existingRenewableGeneralPermits.forEach(existingRenewableGeneralPermit -> {
      String existingGeneralPermitBatchId = null;
      
      if (existingRenewableGeneralPermit.getGpPermitType().equals("GP015001")) {
        logger.info("Categorize this as Openrating Permit as this cannot be renewed. "
            + "Have to be grpuped Separately. User Id {}, Context Id {}", userId, contextId);
        existingGeneralPermitBatchId = String.valueOf(existingRenewableGeneralPermit.getBatchId()).concat("000RGOP");
      } else {
        existingGeneralPermitBatchId = String.valueOf(existingRenewableGeneralPermit.getBatchId()).concat("000RG");
      }
      
      PermitApplication permitApplication = preparePermitApplicationFromExistingPermit(userId, contextId,
          existingRenewableGeneralPermit, permitSummaryInd, contactAgents, edbDistrictId);

      permitApplication
          .setPermitTypeCode(existingRenewableGeneralPermit.getGpPermitTypeFormatted());
      permitApplication.setPermitTypeDesc(existingRenewableGeneralPermit.getGpPermitDesc());
//      permitApplication.setExtendedDate(existingRenewableGeneralPermit.getGpExtendedDate());
      if (permitModExtnApplications.get(existingGeneralPermitBatchId) == null) {
        List<PermitApplication> modExtnEligiblePermitApplicationsList = new ArrayList<>();
        List<AvailTransType> applicableTransTypes = new ArrayList<>();
        if (existingGeneralPermitBatchId.endsWith("RGOP")) {
          applicableTransTypes.add(modifyRequestTransType);
          applicableTransTypes.add(transRequestTransType);          
        } else {
          applicableTransTypes.add(modifyRequestTransType);
          applicableTransTypes.add(renewalRequestTransType);
          applicableTransTypes.add(transRequestTransType);
        }
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtnEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingGeneralPermitBatchId);
        permitModExtnApplications.put(existingGeneralPermitBatchId,
            modExtnEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingGeneralPermitBatchId);
        permitModExtnApplications.get(existingGeneralPermitBatchId).add(permitApplication);
      }
    });

    existingNonRenewableGeneralPermits.forEach(existingNonRenewableGeneralPermit -> {
      String existingNonRenewalGeneralPermitBatchId = String.valueOf(existingNonRenewableGeneralPermit.getBatchId()).concat("000NRG");
      PermitApplication permitApplication = preparePermitApplicationFromExistingPermit(userId, contextId,
          existingNonRenewableGeneralPermit, permitSummaryInd, contactAgents, edbDistrictId);

      permitApplication
          .setPermitTypeCode(existingNonRenewableGeneralPermit.getGpPermitTypeFormatted());
      permitApplication.setPermitTypeDesc(existingNonRenewableGeneralPermit.getGpPermitDesc());
//      permitApplication.setExtendedDate(existingNonRenewableGeneralPermit.getGpExtendedDate());
      if (permitModExtnApplications.get(existingNonRenewalGeneralPermitBatchId) == null) {
        List<PermitApplication> modExtnEligiblePermitApplicationsList = new ArrayList<>();
        List<AvailTransType> applicableTransTypes = new ArrayList<>();
        AvailTransType extensionTransType = new AvailTransType("Extend", "EXT");
//        if (StringUtils.hasLength(permitApplication.getExtendedDate())) {
//          extensionTransType =
//              new AvailTransType("Extend (" + permitApplication.getExtendedDate() + ")", "EXT");
//        } else {
//          extensionTransType = new AvailTransType("Extend", "EXT");
//        }
        applicableTransTypes.add(extensionTransType);
        applicableTransTypes.add(modifyRequestTransType);
        applicableTransTypes.add(transRequestTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtnEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalGeneralPermitBatchId);
        permitModExtnApplications.put(existingNonRenewalGeneralPermitBatchId,
            modExtnEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalGeneralPermitBatchId);
        permitModExtnApplications.get(existingNonRenewalGeneralPermitBatchId)
            .add(permitApplication);
      }
    });

    existingRenewableRegularPermits.forEach(existingRenewableRegularPermit -> {
      String existingRenewableRegularPermitBatchId = String.valueOf(existingRenewableRegularPermit.getBatchId()).concat("000RP");
      PermitApplication permitApplication = preparePermitApplicationFromExistingPermit(userId, contextId,
          existingRenewableRegularPermit, permitSummaryInd, contactAgents, edbDistrictId);
      permitApplication.setPermitTypeCode(existingRenewableRegularPermit.getPermitType());
      permitApplication.setPermitTypeDesc(existingRenewableRegularPermit.getPermitDesc());
      if (permitModExtnApplications.get(existingRenewableRegularPermitBatchId) == null) {
        List<PermitApplication> modExtnEligiblePermitApplicationsList = new ArrayList<>();
        List<AvailTransType> applicableTransTypes = new ArrayList<>();
        applicableTransTypes.add(modifyRequestTransType);
        applicableTransTypes.add(renewalRequestTransType);
        applicableTransTypes.add(transRequestTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtnEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingRenewableRegularPermitBatchId);
        permitModExtnApplications.put(existingRenewableRegularPermitBatchId,
            modExtnEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingRenewableRegularPermitBatchId);
        permitModExtnApplications.get(existingRenewableRegularPermitBatchId).add(permitApplication);
      }
    });

    existingNonRenewableRegularPermits.forEach(existingNonRenewableRegularPermit -> {
      String existingNonRenewalRegularPermitBatchId = String.valueOf(existingNonRenewableRegularPermit.getBatchId()).concat("000NRP");
      PermitApplication permitApplication = preparePermitApplicationFromExistingPermit(userId, contextId,
          existingNonRenewableRegularPermit, permitSummaryInd, contactAgents, edbDistrictId);

      permitApplication.setPermitTypeCode(existingNonRenewableRegularPermit.getPermitType());
      permitApplication.setPermitTypeDesc(existingNonRenewableRegularPermit.getPermitDesc());
      
//      if (existingNonRenewableRegularPermit.getNonRenewableEffDate() != null
//          && existingNonRenewableRegularPermit.getMaxPermitTerm() != null) {
//        permitApplication.setExtendedDate(mmDDYYYFormat
//            .format(DateUtils.addYears(existingNonRenewableRegularPermit.getNonRenewableEffDate(),
//                existingNonRenewableRegularPermit.getMaxPermitTerm())));
//      }

      if (permitModExtnApplications.get(existingNonRenewalRegularPermitBatchId) == null) {
        List<PermitApplication> modExtnEligiblePermitApplicationsList = new ArrayList<>();
        List<AvailTransType> applicableTransTypes = new ArrayList<>();
        AvailTransType extensionTransType = new AvailTransType("Extend", "EXT");
//        if (StringUtils.hasLength(permitApplication.getExtendedDate())) {
//          extensionTransType =
//              new AvailTransType("Extend (" + permitApplication.getExtendedDate() + ")", "EXT");
//        } else {
//          extensionTransType = new AvailTransType("Extend", "EXT");
//        }
        applicableTransTypes.add(extensionTransType);
        applicableTransTypes.add(modifyRequestTransType);
        applicableTransTypes.add(transRequestTransType);
        permitApplication.setAvailableTransTypes(applicableTransTypes);
        modExtnEligiblePermitApplicationsList.add(permitApplication);
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalRegularPermitBatchId);
        permitModExtnApplications.put(existingNonRenewalRegularPermitBatchId,
            modExtnEligiblePermitApplicationsList);
      } else {
        permitApplication.setCalculatedBatchIdForProcess(existingNonRenewalRegularPermitBatchId);
        permitModExtnApplications.get(existingNonRenewalRegularPermitBatchId)
            .add(permitApplication);
      }
    });

//    if (!CollectionUtils.isEmpty(permitModExtnApplications)) {
//      int count = 0;
//      for (String key : permitModExtnApplications.keySet()) {
//        for (PermitApplication permitApplication : permitModExtnApplications.get(key)) {
//          permitApplication.setCalculatedBatchIdForProcess(categorizeExistingBatch[count]);
//        }
//        count++;
//      }
//    }
    
    permitApplicationsMapDetail.put("dart-mod-extn", permitModExtnApplications);
    Map<Long, List<PermitApplication>> permitPendingApplications = new HashMap<>();
    for (Long pendingPermitApplId : dartPendingTransferEligiblePermitApps.keySet()) {

      DartApplication dartPendingPermit =
          dartPendingTransferEligiblePermitApps.get(pendingPermitApplId);

      if (dartTransferAssignedPermitsMap.get(pendingPermitApplId) == null) {
        PermitApplication permitApplication = new PermitApplication();
        permitApplication.setPermitTypeCode(dartPendingPermit.getPermitType());
        permitApplication.setPermitTypeDesc(dartPendingPermit.getPermitDesc());
        permitApplication.setProjectDesc(dartPendingPermit.getProjectDesc());
        permitApplication.setEdbTransType(dartPendingPermit.getTransType());
        permitApplication.setEdbDistrictId(edbDistrictId);
        permitApplication.setDartPendingApp("Y");
        permitApplication.setBatchId(dartPendingPermit.getBatchId());

//        if (dartPendingPermit.getExpiryDate() != null && dartPendingPermit.getStartDate() != null) {
//          permitApplication
//              .setEffectiveStartDate(mmDDYYYFormat.format(dartPendingPermit.getStartDate()));
//          permitApplication
//              .setEffectiveEndDate(mmDDYYYFormat.format(dartPendingPermit.getExpiryDate()));
//        }
        
//        if (dartPendingPermit.getReceivedDate() != null) {
//          permitApplication
//              .setReceivedDate(mmDDYYYFormat.format(dartPendingPermit.getReceivedDate()));
//        }
        
        permitApplication.setProgramId(dartPendingPermit.getTrackedId());
        permitApplication.setProgramIdFormatted(dartPendingPermit.getTrackedIdFormatted());
        permitApplication.setEdbApplicationId(dartPendingPermit.getApplId());
        if (!permitSummaryInd) {
          List<PermitContact> contacts = new ArrayList<>();
          for (ContactAgent contact : contactAgents) {
            PermitContact assignedContact = new PermitContact();
            assignedContact.setPublicId(contact.getPublicId());
            assignedContact.setRoleId(contact.getRoleId());
            assignedContact.setDisplayName(contact.getDisplayName());
            assignedContact.setEdbPublicId(contact.getEdbPublicId());
            assignedContact.setPermitAssignedInd("N");
            contacts.add(assignedContact);
          }
          permitApplication.setContacts(contacts);
        }
        if (CollectionUtils
            .isEmpty(permitPendingApplications.get(permitApplication.getBatchId()))) {
          List<PermitApplication> pendingTransferPermitApplicationsList = new ArrayList<>();
          pendingTransferPermitApplicationsList.add(permitApplication);
          permitPendingApplications.put(permitApplication.getBatchId(),
              pendingTransferPermitApplicationsList);
        } else {
          permitPendingApplications.get(permitApplication.getBatchId()).add(permitApplication);
        }
      }
    }
    permitApplicationsMapDetail.put("dart-pending-txr", permitPendingApplications);
    logger.info("Exiting from transformDartPermitApplicationData. User Id {}, Context Id {}", userId, contextId);
    return permitApplicationsMapDetail;
  }

  /**
   * Transform and Categorize the applications applied so far in eTrack to display in the Permit Summary screen.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param edbDistrictId - Dart District Id for the applied facility/project.
   * @param eTrackPermits - List of permits/applications applied so far for this facility.
   * @param contactAgents - Contact/Agents.
   * @param permitApplicationResult  - Group the applications based on the category in this object.
   * @param narrativeDescMapping - Narrative Description pulled from DART for the permit. This text will be displayed when the user hover over on the permit.
   * @param availableTransTypesMap - Trans type mapping.
   * 
   * @return - Categorized permits.
   */
  public Map<String, List<PermitApplication>> transformPermitSummaryDetails(final String userId,
      final String contextId, final Long edbDistrictId, final List<ETrackPermit> eTrackPermits,
      final List<ContactAgent> contactAgents,
      Map<String, List<PermitApplication>> permitApplicationResult,
      final Map<Long, String> narrativeDescMapping, Map<Long, List<AvailTransType>> availableTransTypesMap) {

    List<PermitApplication> dartPermitApplications = new LinkedList<>();
    List<PermitApplication> permitApplications = new LinkedList<>();

    for (ETrackPermit permit : eTrackPermits) {
      if (permit.getEdbApplId() == null || permit.getEdbApplId() <= 0) {
        logger.info("Adding permit {}. User Id {}, Context Id {}", permit.getApplicationId(), userId, contextId);
        prepareEtrackPermitsToPermitApplication(userId, contextId, permit, edbDistrictId, permitApplications, contactAgents, false);
      } else {
        prepareEtrackPermitsToPermitApplication(userId, contextId, permit, edbDistrictId, dartPermitApplications, contactAgents, false);
      }
    }

    dartPermitApplications.forEach(dartPermitApplication -> {
      dartPermitApplication.setApplnPermitDesc(
          narrativeDescMapping.get(dartPermitApplication.getEdbApplicationId()));
    });

    permitApplicationResult.put("etrack-permits", permitApplications);
    List<PermitApplication> dartPendingTransferPermits = new ArrayList<>();
    List<PermitApplication> dartExtnPermits = new ArrayList<>();
    List<PermitApplication> dartExtnModPermits = new ArrayList<>();
    List<PermitApplication> dartExtnRenewPermits = new ArrayList<>();
    List<PermitApplication> dartExtnTransferPermits = new ArrayList<>();
    List<PermitApplication> dartExtnModRenewPermits = new ArrayList<>();
    List<PermitApplication> dartExtnModTransferPermits = new ArrayList<>();
    List<PermitApplication> dartExtnRenewTransferPermits = new ArrayList<>();
    List<PermitApplication> dartExtnModRenewTransferPermits = new ArrayList<>();
    List<PermitApplication> dartModPermits = new ArrayList<>();
    List<PermitApplication> dartModRenewPermits = new ArrayList<>();
    List<PermitApplication> dartModTransferPermits = new ArrayList<>();
    List<PermitApplication> dartModRenewTransferPermits = new ArrayList<>();
    List<PermitApplication> dartRenewPermits = new ArrayList<>();
    List<PermitApplication> dartRenewTransferPermits = new ArrayList<>();
    List<PermitApplication> dartTransferPermits = new ArrayList<>();

    
    if (!CollectionUtils.isEmpty(dartPermitApplications)) {
      dartPermitApplications.forEach(dartPermitApplication -> {
        if (dartPermitApplication.getPendingAppTransferReqInd().equals("Y")) {
          dartPendingTransferPermits.add(dartPermitApplication);
        } else {
          if (!CollectionUtils.isEmpty(availableTransTypesMap)) {
            dartPermitApplication.setAvailableTransTypes(
                availableTransTypesMap.get(dartPermitApplication.getEdbApplicationId()));
          }
          if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getModReqInd().equals("Y")
              && dartPermitApplication.getRenewReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartExtnModRenewTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getModReqInd().equals("Y")
              && dartPermitApplication.getRenewReqInd().equals("Y")) {
            dartExtnModRenewPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getModReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartExtnModTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getRenewReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartExtnRenewTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getModReqInd().equals("Y")) {
            dartExtnModPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getRenewReqInd().equals("Y")) {
            dartExtnRenewPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartExtnTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getExtnReqInd().equals("Y")) {
            dartExtnPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getModReqInd().equals("Y")
              && dartPermitApplication.getRenewReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartModRenewTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getModReqInd().equals("Y")
              && dartPermitApplication.getRenewReqInd().equals("Y")) {
            dartModRenewPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getModReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartModTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getModReqInd().equals("Y")) {
            dartModPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getRenewReqInd().equals("Y")
              && dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartRenewTransferPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getRenewReqInd().equals("Y")) {
            dartRenewPermits.add(dartPermitApplication);
          } else if (dartPermitApplication.getTransferReqInd().equals("Y")) {
            dartTransferPermits.add(dartPermitApplication);
          }
        }
      });
    }
    permitApplicationResult.put("dart-extn-mod-renew-transfer", dartExtnModRenewTransferPermits);
    permitApplicationResult.put("dart-extn-mod-renew", dartExtnModRenewPermits);
    permitApplicationResult.put("dart-extn-mod-transfer", dartExtnModTransferPermits);
    permitApplicationResult.put("dart-extn-mod", dartExtnModPermits);
    permitApplicationResult.put("dart-extn-renew-transfer", dartExtnRenewTransferPermits);
    permitApplicationResult.put("dart-extn-transfer", dartExtnTransferPermits);
    permitApplicationResult.put("dart-extn-renew", dartExtnRenewPermits);
    permitApplicationResult.put("dart-extn", dartExtnPermits);
    permitApplicationResult.put("dart-mod-ren-transfer", dartModRenewTransferPermits);
    permitApplicationResult.put("dart-mod-ren", dartModRenewPermits);
    permitApplicationResult.put("dart-mod-transfer", dartModTransferPermits);
    permitApplicationResult.put("dart-mod", dartModPermits);
    permitApplicationResult.put("dart-ren-transfer", dartRenewTransferPermits);
    permitApplicationResult.put("dart-ren", dartRenewPermits);
    permitApplicationResult.put("dart-transfer", dartTransferPermits);
    permitApplicationResult.put("dart-pending-txr", dartPendingTransferPermits);
    return permitApplicationResult;
  }

  /**
   * Transform the Alert result set into UI readable format.
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param alertsList - List of Project Alerts.
   * 
   * @return - Transformed alerts list.
   */
  public List<Alert> transformAlertMessage(final String userId, final String contextId, List<ProjectAlert> alertsList) {
    logger.info("Entering into transformAlertMessage. User Id {}, Context Id {}", userId, contextId);    
    List<Alert> alerts = new ArrayList<>();
//    alertsList =
//        alertsList.stream().sorted(Comparator.comparing(ProjectAlert::getAlertDate).reversed())
//            .collect(Collectors.toList());
    alertsList.forEach(alertMsg -> {
      Alert alert = new Alert();
      alert.setAlertId(alertMsg.getProjectAlertId());
      alert.setFacilityName(alertMsg.getFacilityName());
      alert.setProjectId(alertMsg.getProjectId());
      alert.setAssignmentNote(alertMsg.getComments());
      Integer readInd = alertMsg.getReadInd();
      if (readInd != null) {
        if (readInd == 0) {
          alert.setMsgRead("N");
        } else if (readInd == 1) {
          alert.setMsgRead("Y");
        }
      }
      alert.setNote(alertMsg.getAlertNote());
      Date alertDate = alertMsg.getAlertDate();
      if (alertDate != null) {
        alert.setAlertDate(mmDDYYYFormatAMPM.format(alertDate));
        alert.setAlertDateFormat(alertDate);
      }
      alerts.add(alert);
    });
    logger.info("Exiting from transformAlertMessage. User Id {}, Context Id {}", userId, contextId);
    return alerts;
  }

  /**
   * Transform the Geographical Inquiry alerts result set into UI readable format.
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param alertsList - List of Geographical Inquiry Alerts.
   * 
   * @return - Transformed alerts list.
   */
  public List<Alert> transformInquiryAlertMessage(
      final String userId, final String contextId, List<GIInquiryAlert> alertsList) {
    logger.info("Entering into transformAlertMessage. User Id {}, Context Id {}", userId, contextId);    
    List<Alert> alerts = new ArrayList<>();
    alertsList.forEach(alertMsg -> {
      Alert alert = new Alert();
      alert.setAlertId(alertMsg.getInquiryAlertId());
      alert.setInquiryId(alertMsg.getInquiryId());
      alert.setAssignmentNote(alertMsg.getComments());
      Integer readInd = alertMsg.getReadInd();
      if (readInd != null) {
        if (readInd == 0) {
          alert.setMsgRead("N");
        } else if (readInd == 1) {
          alert.setMsgRead("Y");
        }
      }
      alert.setNote(alertMsg.getAlertNote());
      Date alertDate = alertMsg.getAlertDate();
      if (alertDate != null) {
        alert.setAlertDate(mmDDYYYFormatAMPM.format(alertDate));
        alert.setAlertDateFormat(alertDate);
      }
      alerts.add(alert);
    });
    logger.info("Exiting from transformAlertMessage. User Id {}, Context Id {}", userId, contextId);
    return alerts;
  }
  
  /**
   * Transform the input applications into Dashboard applications format.
   * 
   * @param applications - List of eTrack/Enterprise applications.
   * @param outForReviewProject - Out for review Projects
   * @param dartMilestoneMapWithBatchId - Dart Milestone status/batch
   * 
   * @return - Transformed Dashboard Applications.
   */
  public List<DashboardDetail> transformDataIntoDashboardData(List<DartApplication> applications,
      Map<Long, Long> outForReviewProject,
      Map<Long, DartMilestone> dartMilestoneMapWithBatchId) {

    List<DashboardDetail> dashboardApps = new ArrayList<>();
    Map<Long, DashboardDetail> dashboardAppsMap = new HashMap<>();
    Map<Long, Map<String, String>> permitTypeDescWithBatchId = new HashMap<>();
    Map<Long, Set<String>> municipalities = new HashMap<>();
    Map<Long, Set<String>> counties = new HashMap<>();
    
    if (!CollectionUtils.isEmpty(applications)) {
      for (DartApplication activeApp : applications) {
        
        if (StringUtils.hasLength(activeApp.getTrackedId()) 
            && activeApp.getTrackedId().startsWith("0")) {
          activeApp.setMunicipality("NEW YORK STATE");
          activeApp.setCounty("STATEWIDE");
        }
        
        DashboardDetail dashboardDetail = dashboardAppsMap.get(activeApp.getBatchId());
        if (dashboardDetail != null) {
          if (activeApp.getTrackingInd() != null && activeApp.getTrackingInd().equals(1)) {
            dashboardDetail.setTrackedId(activeApp.getTrackedId());
            dashboardDetail.setTrackedIdFormatted(activeApp.getTrackedIdFormatted());
          }
          if (!CollectionUtils.isEmpty(municipalities.get(activeApp.getBatchId())) 
              && StringUtils.hasLength(activeApp.getMunicipality())) {
            municipalities.get(activeApp.getBatchId()).add(activeApp.getMunicipality());
          }

          if (!CollectionUtils.isEmpty(counties.get(activeApp.getBatchId())) 
              && StringUtils.hasLength(activeApp.getCounty())) {
                counties.get(activeApp.getBatchId()).add(activeApp.getCounty());
          }
          // dashboardDetail.getPermitTypes().add(activeApp.getPermitType());
          // dashboardDetail.getPermitTypeDescs().add(activeApp.getPermitDesc());
          permitTypeDescWithBatchId.get(activeApp.getBatchId()).put(activeApp.getPermitType(),
              activeApp.getPermitDesc());
        } else {
          dashboardDetail = new DashboardDetail();
          if (dartMilestoneMapWithBatchId != null
              && dartMilestoneMapWithBatchId.get(activeApp.getBatchId()) != null) {
            DartMilestone dartMilestone = dartMilestoneMapWithBatchId.get(activeApp.getBatchId());
            String currentStatus = dartMilestone.getEdbCurrentStatusCode();
            String dueDate = null;
            if (StringUtils.hasLength(currentStatus)) {
              if (CurrentDartStatus.COMPLETENESS_DETERMINATION_DUE.getCurrrentStatus()
                  .equals(currentStatus)
                  || CurrentDartStatus.SUSPENDED_TO_A_DATE.getCurrrentStatus()
                      .equals(currentStatus)) {
                if (dartMilestone.getCompletenessDueDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getCompletenessDueDate());
                }
              } else if (CurrentDartStatus.WRITTEN_COMMENTS_DEADLINE.getCurrrentStatus()
                  .equals(currentStatus)) {
                if (dartMilestone.getCommentsDeadlineDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getCommentsDeadlineDate());
                }
              } else if (CurrentDartStatus.HEARING_DECISION_DUE.getCurrrentStatus()
                  .equals(currentStatus)) {
                if (dartMilestone.getHearingDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getHearingDate());
                }
              } else if (CurrentDartStatus.FINAL_DECISION_DUE.getCurrrentStatus()
                  .equals(currentStatus)) {
                if (dartMilestone.getFinalDispositionDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getFinalDispositionDate());
                }
              } else if (CurrentDartStatus.PERMITTEE_RESPONSE_DUE.getCurrrentStatus()
                  .equals(currentStatus)) {
                if (dartMilestone.getPermitteeRespDueDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getPermitteeRespDueDate());
                }
              } else if (CurrentDartStatus.DIMSR_DECISION_DUE.getCurrrentStatus()
                  .equals(currentStatus)) {
                if (dartMilestone.getDimsrDecisionDueDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getDimsrDecisionDueDate());
                }
              } else if (CurrentDartStatus.INCOMPLETE.getCurrrentStatus().equals(currentStatus)) {
                if (dartMilestone.getCompletenessDueDate() != null) {
                  dueDate = mmDDYYYFormat.format(dartMilestone.getCompletenessDueDate());
                }
              }
              if (StringUtils.hasLength(dueDate)) {
                dashboardDetail.setDueDate(dueDate);
              } else {
                if (dartMilestoneMapWithBatchId.get(activeApp.getBatchId())
                    .getUpdateDate() != null) {
                  dashboardDetail.setDueDate(mmDDYYYFormat.format(
                      dartMilestoneMapWithBatchId.get(activeApp.getBatchId()).getUpdateDate()));
                }
              }
            }
          }
          if (activeApp.getProjectId() == null) {
            activeApp.setProjectId(0L);        
          }
          dashboardDetail.setProjectId(activeApp.getProjectId());
          dashboardDetail.setProjectDesc(activeApp.getProjectDesc());
          dashboardDetail.setEdbPublicId(activeApp.getPublicId());
          dashboardDetail.setEdbDistrictId(activeApp.getDistrictId());

          if (StringUtils.hasLength(activeApp.getProgramManager())) {
            String[] programManager = activeApp.getProgramManager().split(" ");
            StringBuilder sb = new StringBuilder();
            for (int total = programManager.length - 1; total >= 0; --total) {
              sb.append(programManager[total].toUpperCase()).append(" ");
            }
            dashboardDetail.setAnalystName(sb.toString().trim());
          }
          if (StringUtils.hasLength(activeApp.getPublicName())) {
            if (activeApp.getPublicName().contains("*")) {
              String[] splittedDisplayName = activeApp.getPublicName().split("\\*");
              if (splittedDisplayName.length == 2) {
                dashboardDetail.setApplicant(activeApp.getPublicName().replace("*", ", "));
              } else if (splittedDisplayName.length > 2) {
                dashboardDetail.setApplicant(dartDBServiceUtility.preparePublicNameFormat(splittedDisplayName[0],
                    splittedDisplayName[2], splittedDisplayName[1]));
              } else {
                dashboardDetail.setApplicant(activeApp.getDisplayName());
              }
            } else {
              dashboardDetail.setApplicant(activeApp.getDisplayName());
            }
          } else {
            dashboardDetail.setApplicant(activeApp.getDisplayName());
          }

          // dashboardDetail.setApplicant(activeApp.getDisplayName());
          dashboardDetail.setDispositionDate(activeApp.getDispositionDate());
          if (StringUtils.hasText(activeApp.getEmergencyInd())
              && "E".equals(activeApp.getEmergencyInd())) {
            dashboardDetail.setEaInd(activeApp.getEmergencyInd());
          }
          dashboardDetail.setGpInd(activeApp.getGpInd());
          // dashboardDetail.setApplicant(activeApp.getDisplayName());
          Facility facility = new Facility();
          facility.setFacilityName(activeApp.getFacilityName());
          StringBuilder addressFormat = new StringBuilder();
          addressFormat.append(activeApp.getStreet()).append(",").append(activeApp.getCity())
              .append(",").append(activeApp.getState());
          if (activeApp.getZip() != null) {
            addressFormat.append(",").append(activeApp.getZip());
          }
          facility.setFormattedAddress(addressFormat.toString());
          facility.setFacilityName(activeApp.getFacilityName());
          facility.setLocationDirections(activeApp.getStreet());
          facility.setCity(activeApp.getCity());
          facility.setState(activeApp.getState());
          facility.setDistrictId(activeApp.getDistrictId());
          facility.setMunicipality(activeApp.getMunicipality());
          facility.setCounty(activeApp.getCounty());
          Set<String> municipality = new HashSet<>();
          if (StringUtils.hasLength(activeApp.getMunicipality())) {
            municipality.add(activeApp.getMunicipality());
            municipalities.put(activeApp.getBatchId(), municipality);
          }
          Set<String> county = new HashSet<>();
          if (StringUtils.hasLength(activeApp.getCounty())) {
            county.add(activeApp.getCounty());
            counties.put(activeApp.getBatchId(), county);          
          }
          dashboardDetail.setFacility(facility);
          dashboardDetail.setApplicantId(activeApp.getApplId());
          Map<String, String> permitTypeAndDesc = new HashMap<>();
          permitTypeAndDesc.put(activeApp.getPermitType(), activeApp.getPermitDesc());
          permitTypeDescWithBatchId.put(activeApp.getBatchId(), permitTypeAndDesc);
          LinkedHashSet<String> permitTypes = new LinkedHashSet<>();
          Set<String> appTypes = new HashSet<>();
          LinkedHashSet<String> permitTypeDescs = new LinkedHashSet<>();
          permitTypes.add(activeApp.getPermitType());
          permitTypeDescs.add(activeApp.getPermitDesc());
          // dashboardDetail.setPermitTypeDescs(permitTypeDescs);
          // dashboardDetail.setPermitTypes(permitTypes);
          appTypes.add(activeApp.getTransType());
          dashboardDetail.setAppTypes(appTypes);
//        dashboardDetail.setAppType(activeApp.getTransType());
          if (activeApp.getTrackingInd() != null && activeApp.getTrackingInd().equals(1)) {
            dashboardDetail.setTrackedId(activeApp.getTrackedId());
            dashboardDetail.setTrackedIdFormatted(activeApp.getTrackedIdFormatted());
          }
          dashboardDetail.setBatchId(activeApp.getBatchId());
          dashboardDetail.setDartStatus(activeApp.getAppStatus());
          dashboardDetail.setRegion(activeApp.getRegion());
          dashboardDetail.setSapaInd(activeApp.getSapaInd());
          dashboardDetail.setRenOrderNum(activeApp.getRenOrderNum());
          dashboardDetail.setModOrderNum(activeApp.getModOrderNum());

          if (!CollectionUtils.isEmpty(outForReviewProject)
              && outForReviewProject.get(activeApp.getProjectId()) != null) {
            dashboardDetail.setOutForReview("Y");
          }

          if (activeApp.getSapaDate() != null) {
            dashboardDetail.setSapaDate(mmDDYYYFormat.format(activeApp.getSapaDate()));
          }
          if (activeApp.getDueDate() != null) {
            dashboardDetail.setDueDate(mmDDYYYFormat.format(activeApp.getDueDate()));
          }
          if (activeApp.getReceivedDate() != null) {
            dashboardDetail.setRcvdDate(mmDDYYYFormat.format(activeApp.getReceivedDate()));
          }
          if (activeApp.getStartDate() != null) {
            dashboardDetail.setEffectiveDate(mmDDYYYFormat.format(activeApp.getStartDate()));
          }
          if (activeApp.getExpiryDate() != null) {
            dashboardDetail.setExpiryDate(mmDDYYYFormat.format(activeApp.getExpiryDate()));
          }
          dashboardAppsMap.put(activeApp.getBatchId(), dashboardDetail);
        }
      }
    }
    dashboardAppsMap.keySet().forEach(batchId -> {
      Map<String, String> permitTypeAndDesc = permitTypeDescWithBatchId.get(batchId);
      Set<String> permitTypes = permitTypeAndDesc.keySet();
      // Set<String> permitTypes = dashboardAppsMap.get(batchId).getPermitTypes();
      List<String> permitTypeList = permitTypes.stream().sorted().collect(Collectors.toList());
      String permits = String.join(", ", permitTypeList);
      dashboardAppsMap.get(batchId).setPermitType(permits);
      DashboardDetail dashboardDetail = dashboardAppsMap.get(batchId);
      String appType = String.join(",", dashboardDetail.getAppTypes().stream().sorted().collect(Collectors.toList()));
      dashboardDetail.setAppType(appType);
      Set<String> municipalityList = municipalities.get(batchId);
      Set<String> countyList = counties.get(batchId);
      if (!CollectionUtils.isEmpty(municipalityList)) {
        dashboardDetail.getFacility().setMunicipality(String.join(",", municipalityList.stream().sorted().collect(Collectors.toList())));
      } else {
        dashboardDetail.getFacility().setMunicipality("");
      }
      if (!CollectionUtils.isEmpty(countyList)) {
        dashboardDetail.getFacility().setCounty(String.join(",", countyList.stream().sorted().collect(Collectors.toList())));
      } else {
        dashboardDetail.getFacility().setCounty("");
      }
      List<String> permitTypeDescList = new LinkedList<>();
      permitTypeList.forEach(permitType -> {
        permitTypeDescList.add(permitTypeAndDesc.get(permitType));
      });
      // dashboardAppsMap.get(batchId)
      // .setPermitTypeDesc(new ArrayList<>(dashboardAppsMap.get(batchId).getPermitTypeDescs())
      // .stream().collect(Collectors.joining(", ")));
      dashboardAppsMap.get(batchId).setPermitTypeDesc(String.join(", ", permitTypeDescList));
      dashboardApps.add(dashboardAppsMap.get(batchId));
    });
    return dashboardApps;
  }
  
  /**
   * Transform the input Application related informations to Dashboard applications like including EA, LRP etc..
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param applications - Applications
   * @param lrpsMap - LRPs mapping for each project.
   * 
   * @return - Transformed Dashboard applications.
   */
  public List<DashboardDetail> prepareApplicationInfo(String userId, String contextId,
      List<PendingApplication> applications, Map<Long, ApplicantDto> lrpsMap) {
    
    logger.info("Entering into prepareApplicationInfo. User Id {}, Context Id {}", userId, contextId);    
    
    List<DashboardDetail> appplicationInfos = new ArrayList<>();
    if (!CollectionUtils.isEmpty(applications)) {
      applications.forEach(application -> {
        DashboardDetail appInfo = new DashboardDetail();
        if (application.getEaInd() != null && application.getEaInd() == 1) {
          appInfo.setEaInd("E");
        } else {
          appInfo.setEaInd("N");
        }
        if (lrpsMap == null) {
          if (StringUtils.hasLength(application.getFirstName())) {
            appInfo.setApplicant(dartDBServiceUtility.preparePublicNameFormat(application.getLastName(),
                application.getFirstName(), application.getMiddleName()));
          } else {
            appInfo.setApplicant(application.getDisplayName());
          }
          appInfo.setApplicantId(application.getApplicantId());
        } else {
          if (lrpsMap.get(application.getProjectId()) != null) {
            ApplicantDto applicantDetail = lrpsMap.get(application.getProjectId());
            appInfo.setApplicantId(applicantDetail.getPublicId());
            if (StringUtils.hasLength(applicantDetail.getFirstName())) {
              appInfo.setApplicant(dartDBServiceUtility.preparePublicNameFormat(applicantDetail.getLastName(),
                  applicantDetail.getFirstName(), applicantDetail.getMiddleName()));
            } else {
              appInfo.setApplicant(applicantDetail.getDisplayName());
            }
          }
        }
        List<Application> permitApplications =
            applicationRepo.findAllByProjectId(application.getProjectId());
        if (!CollectionUtils.isEmpty(permitApplications)) {
          Map<String, String> permitTypeMap = new HashMap<>();

          Set<String> appTypeList = new HashSet<>();
          permitApplications.forEach(permit -> {
            if (StringUtils.hasLength(permit.getPermitTypeCode())) {
              if (StringUtils.hasLength(permit.getGpInd())
                  && permit.getGpInd().equals("1")) {
                appInfo.setGpInd(Integer.parseInt(permit.getGpInd()));
              }
              permitTypeMap.put(permit.getPermitTypeCode(), permit.getPermitTypeDesc());
            }
            if (StringUtils.hasLength(permit.getTransTypeCode())) {
              appTypeList.add(permit.getTransTypeCode());
            }
          });
          List<String> permitTypeList = new ArrayList<>(permitTypeMap.keySet());
          Collections.sort(permitTypeList);
          List<String> permitTypeDescList = new LinkedList<String>();
          permitTypeList.forEach(permitType -> {
            permitTypeDescList.add(permitTypeMap.get(permitType));
          });
          appInfo.setPermitType(permitTypeList.stream().collect(Collectors.joining(", ")));
          appInfo.setAppType(appTypeList.stream().collect(Collectors.joining(", ")));
          appInfo.setPermitTypeDesc(permitTypeDescList.stream().collect(Collectors.joining(", ")));
        }

        if (application.getReceivedDate() != null) {
          appInfo.setRcvdDate(mmDDYYYFormat.format(application.getReceivedDate()));
        }
        appInfo.setProjectId(application.getProjectId());
        if (application.getRejectedInd() != null && application.getRejectedInd().equals(1)) {
          appInfo.setRejectedProject("Y");
        } else {
          appInfo.setRejectedProject("N");
        }
        appInfo.setRejectedReason(application.getRejectedReason());
        appInfo.setRejectedDate(application.getRejectedDate());
        appInfo.setCreateDate(mmDDYYYFormat.format(application.getCreateDate()));
        appInfo.setEdbPublicId(application.getEdbPublicId());
        appInfo.setEdbDistrictId(application.getEdbDistrictId());
        appInfo.setUserAssigned(application.getAnalystAssignedId());
        appInfo.setAnalystName(application.getAssignedAnalystName());
        Facility facility = new Facility();
        String decId = application.getDecId();
        if (StringUtils.hasLength(decId)) {
          StringBuilder sb = new StringBuilder();
          sb.append(decId.substring(0, 1)).append("-").append(decId.substring(1, 5)).append("-")
              .append(decId.substring(5));
          decId = sb.toString();
        }
        facility.setDecId(decId);
        facility.setDistrictId(application.getEdbDistrictId());
        facility.setFacilityName(application.getFacilityName());
        facility.setLocationDirections(application.getStreet1());
        facility.setCity(application.getCity());
        facility.setState(application.getState());
        facility.setCountry(application.getCountry());
        appInfo.setFacility(facility);
        appplicationInfos.add(appInfo);
      });
    }
    logger.info("Exitig from prepareApplicationInfo. User Id {}, Context Id {}", userId, contextId);
    return appplicationInfos;
  }

  /**
   * Transform the Out For review entity applications to Dashboard applications.
   * 
   * @param outForReviewEntityApps - Out for Review applications.
   * 
   * @return - Transformed Dashboard Review applications.
   */
  public List<DashboardDetail> transformOutForReviewAppsToDashboard(
      List<OutForReviewEntity> outForReviewEntityApps) {

    Map<String, DashboardDetail> dashboardOutForReviewAppsMap = new HashMap<>();
    Map<String, Map<String, String>> permitTypeDescWithBatchId = new HashMap<>();
    if (!CollectionUtils.isEmpty(outForReviewEntityApps)) {
      for (OutForReviewEntity outForReviewEntity : outForReviewEntityApps) {
        StringBuilder concatenateProjectIdAndReviewerName = new StringBuilder();
        concatenateProjectIdAndReviewerName.append(outForReviewEntity.getProjectId()).append("-")
            .append(outForReviewEntity.getDocReviewerName());
        DashboardDetail dashboardDetail =
            dashboardOutForReviewAppsMap.get(concatenateProjectIdAndReviewerName.toString());
        if (dashboardDetail != null) {
          permitTypeDescWithBatchId.get(concatenateProjectIdAndReviewerName.toString())
              .put(outForReviewEntity.getPermitTypeCode(), outForReviewEntity.getPermitTypeDesc());
          // dashboardDetail.getPermitTypes().add(outForReviewEntity.getPermitTypeCode());
          // dashboardDetail.getPermitTypeDescs().add(outForReviewEntity.getPermitTypeDesc());

          if (dashboardDetail.getDueDateVal() != null
              && outForReviewEntity.getReviewDueDate() != null
              && outForReviewEntity.getReviewDueDate().after(dashboardDetail.getDueDateVal())) {
            dashboardDetail.setDueDateVal(outForReviewEntity.getReviewDueDate());
          } else if (dashboardDetail.getDueDateVal() == null
              && outForReviewEntity.getReviewDueDate() != null) {
            dashboardDetail.setDueDateVal(outForReviewEntity.getReviewDueDate());
          }
          if (dashboardDetail.getGpInd() == null || !dashboardDetail.getGpInd().equals(1)) {
            dashboardDetail.setGpInd(outForReviewEntity.getGeneralPermitInd());
          }
        } else {
          dashboardDetail = new DashboardDetail();
          dashboardDetail.setProjectId(outForReviewEntity.getProjectId());
          if (outForReviewEntity.getEaInd() != null && outForReviewEntity.getEaInd() == 1) {
            dashboardDetail.setEaInd("E");
          } else {
            dashboardDetail.setEaInd("N");
          }
          dashboardDetail.setGpInd(outForReviewEntity.getGeneralPermitInd());
          dashboardDetail.setProgramStaff(outForReviewEntity.getDocReviewerName());
          dashboardDetail.setEdbDistrictId(outForReviewEntity.getEdbDistrictId());
          Facility facility = new Facility();
          facility.setFacilityName(outForReviewEntity.getFacilityName());
          StringBuilder addressFormat = new StringBuilder();
          if (StringUtils.hasLength(outForReviewEntity.getStreet1())) {
            addressFormat.append(outForReviewEntity.getStreet1());
          }
          if (StringUtils.hasLength(outForReviewEntity.getCity())) {
            addressFormat.append(",").append(outForReviewEntity.getCity());
          }
          if (StringUtils.hasLength(outForReviewEntity.getState())) {
            addressFormat.append(",").append(outForReviewEntity.getState());
          }
          if (outForReviewEntity.getZip() != null) {
            addressFormat.append(",").append(outForReviewEntity.getZip());
          }
          facility.setFormattedAddress(addressFormat.toString());
          facility.setFacilityName(outForReviewEntity.getFacilityName());
          facility.setLocationDirections(outForReviewEntity.getStreet1());
          facility.setCity(outForReviewEntity.getCity());
          facility.setState(outForReviewEntity.getState());
          facility.setDistrictId(outForReviewEntity.getEdbDistrictId());
          dashboardDetail.setFacility(facility);
          dashboardDetail.setTrackedId(outForReviewEntity.getProgId());
          LinkedHashSet<String> permitTypes = new LinkedHashSet<>();
          LinkedHashSet<String> permitTypeDesc = new LinkedHashSet<>();
          permitTypes.add(outForReviewEntity.getPermitTypeCode());
          permitTypeDesc.add(outForReviewEntity.getPermitTypeDesc());
          dashboardDetail.setTrackedIdFormatted(outForReviewEntity.getTrackedIdFormatted());
          Map<String, String> permitTypeAndDesc = new HashMap<>();
          permitTypeAndDesc.put(outForReviewEntity.getPermitTypeCode(),
              outForReviewEntity.getPermitTypeDesc());
          permitTypeDescWithBatchId.put(concatenateProjectIdAndReviewerName.toString(),
              permitTypeAndDesc);
          // dashboardDetail.setPermitTypes(permitTypes);
          // dashboardDetail.setPermitTypeDescs(permitTypeDesc);
          dashboardDetail.setBatchId(outForReviewEntity.getBatchId());
          dashboardDetail.setDueDateVal(outForReviewEntity.getReviewDueDate());
          dashboardDetail.setDartStatus(outForReviewEntity.getAppStatus());
          dashboardOutForReviewAppsMap.put(concatenateProjectIdAndReviewerName.toString(),
              dashboardDetail);
        }
      }
    }
    List<DashboardDetail> dashboardApps = new ArrayList<>();
    dashboardOutForReviewAppsMap.keySet().forEach(reviewerName -> {
      DashboardDetail dashboardDetail = dashboardOutForReviewAppsMap.get(reviewerName);

      if (dashboardDetail.getDueDateVal() != null) {
        dashboardDetail.setDueDate(mmDDYYYFormat.format(dashboardDetail.getDueDateVal()));
      }

      Map<String, String> permitTypeAndDescMap = permitTypeDescWithBatchId.get(reviewerName);
      List<String> permitTypesList =
          permitTypeAndDescMap.keySet().stream().sorted().collect(Collectors.toList());
      List<String> permitTypeDescList = new LinkedList<>();
      permitTypesList.forEach(permitType -> {
        permitTypeDescList.add(permitTypeAndDescMap.get(permitType));
      });
      // dashboardDetail.getPermitTypes().stream().sorted().collect(Collectors.toList());
      // String permitTypeDescDetails = new
      // ArrayList<>(dashboardDetail.getPermitTypeDescs()).stream()
      // .collect(Collectors.joining(","));
      dashboardDetail.setPermitTypeDesc(String.join(", ", permitTypeDescList));
      dashboardDetail.setPermitType(String.join(", ", permitTypesList));
      dashboardApps.add(dashboardDetail);
    });
    return dashboardApps;
  }
  
  public SpatialInquiryRequest transformSpatialInquiryRequestToEntity(
      final String userId, final String contextId, final SpatialInquiryDetail spatialInquiryDetail,
      Map<Long, String> inquiryCompleteDate) {
   
    SpatialInquiryRequest spatialInquiryRequest = new SpatialInquiryRequest();
    spatialInquiryRequest.setInquiryId(spatialInquiryDetail.getInquiryId());
    spatialInquiryRequest.setPolygonId(spatialInquiryDetail.getPolygonId());
    spatialInquiryRequest.setReason(getSpatialInquiryCategory(spatialInquiryDetail.getSpatialInqCategoryId()));
    spatialInquiryRequest.setRegion(spatialInquiryDetail.getRegion());
//    spatialInquiryRequest.setBlockLot(spatialInquiryDetail.getBlockLot());
    spatialInquiryRequest.setRequestorName(spatialInquiryDetail.getRequestorName());
    spatialInquiryRequest.setStreetAddress(spatialInquiryDetail.getStreetAddress());
    spatialInquiryRequest.setMailingAddress(spatialInquiryDetail.getMailingAddress());
    spatialInquiryRequest.setPhoneNumber(spatialInquiryDetail.getPhoneNumber());
    spatialInquiryRequest.setProjectName(spatialInquiryDetail.getProjectName());
    spatialInquiryRequest.setProjectDescription(spatialInquiryDetail.getProjectDescription());
    spatialInquiryRequest.setProjectSponsor(spatialInquiryDetail.getProjectSponsor());
    spatialInquiryRequest.setIssuesQuestions(spatialInquiryDetail.getIssuesQuestions());
//    spatialInquiryRequest.setTagsKeywords(spatialInquiryDetail.getTagsKeywords());
    spatialInquiryRequest.setLeadAgencyName(spatialInquiryDetail.getLeadAgencyName());
    spatialInquiryRequest.setLeadAgencyContact(spatialInquiryDetail.getLeadAgencyContact());
    spatialInquiryRequest.setEfcContact(spatialInquiryDetail.getEfcContact());
    spatialInquiryRequest.setPlanDescription(spatialInquiryDetail.getPlanDescription());
    spatialInquiryRequest.setExtenderName(spatialInquiryDetail.getExtenderName());
    spatialInquiryRequest.setDowContact(spatialInquiryDetail.getDowContact());
    spatialInquiryRequest.setDeveloper(spatialInquiryDetail.getDeveloper());
    spatialInquiryRequest.setPscDocketNum(spatialInquiryDetail.getPscDocketNum());
    spatialInquiryRequest.setDepProjectManager(spatialInquiryDetail.getDepProjectManager());
//    spatialInquiryRequest.setPersonReporting(spatialInquiryDetail.getPersonReporting());
    spatialInquiryRequest.setComments(spatialInquiryDetail.getComments());
    spatialInquiryRequest.setEmail(spatialInquiryDetail.getEmail());
    spatialInquiryRequest.setOwner(spatialInquiryDetail.getOwner());
//    spatialInquiryRequest.setViolationDesc(spatialInquiryDetail.getViolationDesc());
//    spatialInquiryRequest.setAllegedViolator(spatialInquiryDetail.getAllegedViolator());
//    spatialInquiryRequest.setJurisdictions(spatialInquiryDetail.getJurisdictions());
    spatialInquiryRequest.setTaxParcel(spatialInquiryDetail.getTaxParcel());
    spatialInquiryRequest.setCounty(spatialInquiryDetail.getCounty());
    spatialInquiryRequest.setMunicipality(spatialInquiryDetail.getMunicipality());
    spatialInquiryRequest.setSearchBy(spatialInquiryDetail.getSearchBy());
    spatialInquiryRequest.setState(spatialInquiryDetail.getState());
    spatialInquiryRequest.setZip(spatialInquiryDetail.getZip());
    spatialInquiryRequest.setCity(spatialInquiryDetail.getCity());
    spatialInquiryRequest.setStreet(spatialInquiryDetail.getStreet());
    spatialInquiryRequest.setBorough(spatialInquiryDetail.getBorough());
    spatialInquiryRequest.setBlock(spatialInquiryDetail.getBlock());
    spatialInquiryRequest.setLot(spatialInquiryDetail.getLot());
    spatialInquiryRequest.setMailingAddressStreet1(spatialInquiryDetail.getMailingAddressStreet1());
    spatialInquiryRequest.setMailingAddressStreet2(spatialInquiryDetail.getMailingAddressStreet2());
    spatialInquiryRequest.setMailingAddressZip(spatialInquiryDetail.getMailingAddressZip());
    spatialInquiryRequest.setMailingAddressState(spatialInquiryDetail.getMailingAddressState());
    spatialInquiryRequest.setMailingAddressCity(spatialInquiryDetail.getMailingAddressCity());
    spatialInquiryRequest.setPlanName(spatialInquiryDetail.getPlanName());
    if (spatialInquiryDetail.getOriginalSubmittalDate() != null) {
      spatialInquiryRequest.setRcvdDate(mmDDYYYFormat.format(spatialInquiryDetail.getOriginalSubmittalDate()));
    }
    spatialInquiryRequest.setSearchByMunicipality(spatialInquiryDetail.getSearchByMunicipality());
    spatialInquiryRequest.setSearchByCounty(spatialInquiryDetail.getSearchByCounty());
    spatialInquiryRequest.setSearchByTaxParcel(spatialInquiryDetail.getSearchByTaxParcel());
    spatialInquiryRequest.setResponse(spatialInquiryDetail.getResponse());
    spatialInquiryRequest.setResponseDate(spatialInquiryDetail.getResponseDate());
    spatialInquiryRequest.setAnalystAssignedId(spatialInquiryDetail.getAnalystAssignedId());
    spatialInquiryRequest.setAssignedAnalystName(spatialInquiryDetail.getAssignedAnalystName());
    spatialInquiryRequest.setCompletedDate(inquiryCompleteDate.get(spatialInquiryDetail.getInquiryId()));
    return spatialInquiryRequest;
  }
  
  private SpatialInquiryCategory getSpatialInquiryCategory(final Integer categoryId) {
    switch (categoryId) {
      case 1:
          return SpatialInquiryCategory.BOROUGH_DETERMINATION;
      case 2:
        return SpatialInquiryCategory.JURISDICTION_DETERMINATION;
      case 3:
        return SpatialInquiryCategory.SEQR_LA_REQ;
      case 4:
        return SpatialInquiryCategory.PRE_APPLN_REQ;
      case 5:
        return SpatialInquiryCategory.SERP_CERT;
      case 6:
        return SpatialInquiryCategory.MGMT_COMPRE_PLAN;
      case 7:
        return SpatialInquiryCategory.SANITARY_SEWER_EXT;
      case 8:
        return SpatialInquiryCategory.ENERGY_PROJ;
      default:  
        return null;
    }
  }
}
