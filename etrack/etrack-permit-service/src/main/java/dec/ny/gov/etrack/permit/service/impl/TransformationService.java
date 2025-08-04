package dec.ny.gov.etrack.permit.service.impl;

import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.CONTACT_AGENT;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PROPERTY_OWNER;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PUBLIC;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.permit.entity.Address;
import dec.ny.gov.etrack.permit.entity.Facility;
import dec.ny.gov.etrack.permit.entity.FacilityAddr;
import dec.ny.gov.etrack.permit.entity.FacilityPolygon;
import dec.ny.gov.etrack.permit.entity.Project;
import dec.ny.gov.etrack.permit.entity.Public;
import dec.ny.gov.etrack.permit.entity.Role;
import dec.ny.gov.etrack.permit.entity.SpatialInquiryDetail;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.ApplicantAddress;
import dec.ny.gov.etrack.permit.model.Contact;
import dec.ny.gov.etrack.permit.model.FacilityAddress;
import dec.ny.gov.etrack.permit.model.FacilityDetail;
import dec.ny.gov.etrack.permit.model.Individual;
import dec.ny.gov.etrack.permit.model.Organization;
import dec.ny.gov.etrack.permit.model.PolygonStatus;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.permit.repo.RoleRepo;

@Service
public class TransformationService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransformationService.class.getName());

  private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  @Autowired
  private RoleRepo roleRepo;
  
  /**
   * Transform the user entered Facility details to Facility entity to persist in E_FACILITY.
   *  
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityDetail - Facility details passed as an input parameter.
   * @param projectId - Project Id.
   * 
   * @return - Transformed to {@link Facility}
   */
  public Facility transformToFacilityEntity(final String userId, final String contextId,
      final FacilityDetail facilityDetail, final Long projectId) {
    logger.info("Entering into transformToFacilityEntity. User Id {}, Context Id {}", userId, contextId);
    Facility facilityEntity = null;
    if (facilityDetail != null) {
      facilityEntity = new Facility();
      facilityEntity.setProjectId(projectId);
      facilityEntity.setFacilityName(facilityDetail.getFacilityName());
      facilityEntity.setEdbDistrictId(facilityDetail.getEdbDistrictId());
      facilityEntity.setCreatedById(userId);
      facilityEntity.setCreateDate(new Date());
    }
    logger.info("Existing from transformToFacilityEntity. User Id {}, Context Id {}", userId, contextId);
    return facilityEntity;
  }

  /**
   * Extract the Facility address from the user entered and transform to an entity to store in E_FACILITY_ADDRESS.
   *  
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityDetail - Facility details passed as an input parameter.
   * @param projectId - Project Id.
   * 
   * @return - Transformed {@link FacilityAddr}
   */
  public FacilityAddr transformToFacilityAddressEntity(final String userId, final String contextId,
      final FacilityDetail facilityDetail, final Long projectId) {
    logger.info("Entering into transformToFacilityEntity. User Id {}, Context Id {}", userId, contextId);
    FacilityAddr address = null;
    if (facilityDetail != null) {
      FacilityAddress facilityAddress = facilityDetail.getAddress();
      if (facilityAddress != null) {
        address = new FacilityAddr();
        address.setCity(facilityAddress.getCity());
        address.setState(facilityAddress.getState());
        address.setCountry(facilityAddress.getCountry());
        address.setZip(facilityAddress.getZip());
        address.setZipExtension(facilityAddress.getZipExtension());
        address.setCreateDate(new Date());
        address.setCreatedById(userId);
        address.setPhoneNumber(facilityAddress.getPhoneNumber());
        address.setStreet1(facilityAddress.getStreet1());
        address.setStreet2(facilityAddress.getStreet2());
        address.setProjectId(projectId);
      }
    }
    logger.info("Existing from transformToFacilityAddressEntity. User Id {}, Context Id {}", userId, contextId);
    return address;
  }

  /**
   * Transform the user entered details to Public Entity which can be stored in E_PUBLIC table.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicant - Applicant details passed by the user.
   * @param publicDetail - Existing public details if its already associated.
   * 
   * @return - Transformed format of {@link Public}
   */
  public Public transformApplicantToPublicEntity(final String userId, final String contextId,
      final Long projectId, final Applicant applicant, Public publicDetail) {

    logger.info("Entering into transform Applicant into Public Entity UserId {}, Context Id {}",
        userId, contextId);

    if (publicDetail.getPublicTypeCode() == null) {
      publicDetail.setPublicTypeCode(applicant.getPublicTypeCode());
    }
    publicDetail.setProjectId(projectId);
    publicDetail.setEdbPublicId(applicant.getEdbApplicantId());
    Individual individual = applicant.getIndividual();
    if (individual != null) {
      StringBuffer publicNameFormat = new StringBuffer();
      StringBuffer displayNameFormat = new StringBuffer();
      publicDetail.setFirstName(individual.getFirstName());
      publicDetail.setMiddleName(individual.getMiddleName());
      publicDetail.setLastName(individual.getLastName());
      publicDetail.setSuffix(individual.getSuffix());
      String lastName = individual.getLastName();
      lastName = StringUtils.hasLength(lastName) ? lastName.trim() : "";
      publicNameFormat.append(lastName);

      String firstName = individual.getFirstName();
      firstName = StringUtils.hasLength(firstName) ? firstName.trim() : "";
      publicNameFormat.append("*").append(firstName);
      
      String middleName = individual.getMiddleName();
      if (StringUtils.hasLength(middleName)) {
        publicNameFormat.append("*").append(middleName);
      }
      
      if (StringUtils.hasLength(individual.getSuffix())) {
        publicNameFormat.append("*").append(individual.getSuffix().trim());
      }
      publicDetail.setPublicName(publicNameFormat.toString());
      
      displayNameFormat.append(firstName);
      if (StringUtils.hasLength(middleName)) {
        displayNameFormat.append(" ").append(middleName);  
      }
      displayNameFormat.append(" ").append(lastName);
      if (StringUtils.hasLength(individual.getSuffix())) {
        displayNameFormat.append(" ").append(individual.getSuffix());
      }      
      publicDetail.setDisplayName(displayNameFormat.toString());
    }

    if (!StringUtils.isEmpty(applicant.getGovtAgencyName())) {
      publicDetail.setPublicName(applicant.getGovtAgencyName());
      publicDetail.setDisplayName(applicant.getGovtAgencyName());
    }
    publicDetail.setDbaName(applicant.getDba());
    if ("N".equals(applicant.getValidatedInd())) {
      publicDetail.setValidatedInd(0);
    } else if ("Y".equals(applicant.getValidatedInd())) {
      publicDetail.setValidatedInd(1);
    }
    Organization organization = applicant.getOrganization();
    if (organization != null) {
      publicDetail.setTaxpayerId(organization.getTaxPayerId());
      if (organization.getIsIncorporated() != null) {
        if ("Y".equals(organization.getIsIncorporated())) {
          publicDetail.setIncorpInd(1);
          if (organization.getBusinessVerified() != null) {
            if ("Y".equals(organization.getBusinessVerified())) {
              publicDetail.setBusinessValidatedInd(1);
            } else if ("N".equals(organization.getBusinessVerified())) {
              publicDetail.setBusinessValidatedInd(0);
            }
          } else {
            if (StringUtils.hasLength(organization.getVerifiedLegalName())) {
              publicDetail.setBusinessValidatedInd(1);
            } else {
              publicDetail.setBusinessValidatedInd(0);
            }
          }
        } else if ("N".equals(organization.getIsIncorporated())) {
          publicDetail.setIncorpInd(0);
          publicDetail.setBusinessValidatedInd(null);
        }
      }
      publicDetail.setIncorpState(organization.getIncorporationState());
      publicDetail.setTerritoryOrCountry(organization.getIncorporateCountry());
      publicDetail.setPublicName(applicant.getOrganization().getBusOrgName());
      publicDetail.setDisplayName(applicant.getOrganization().getBusOrgName());
    }    
    logger.info("Exiting from transform Applicant into Public Entity UserId : {}  context Id :{}",
        userId, contextId);
    return publicDetail;
  }
  
  /**
   * Transform the input Property relationship details into Role Entity.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param applicant - Input applicant details
   * @param addressId - Public address id
   * @param existingPublic - Existing applicant details
   * @param category - Applicant category detail
   * 
   * @return - Transformed {@link Public} with updated role details.
   */
  public List<Role> transformPropertyRelationshipToRoleEntity(final String userId, final String contextId, 
      final Applicant applicant, final Long addressId, Public existingPublic, String category, List<Role> existingRoles) {
    
    logger.info("Entering into transformPropertyRelationshipToRoleEntity. User id {}, Context id {}", userId, contextId);
    List<Integer> propertyRelationShips = applicant.getPropertyRelationships();    
    List<Role> roles = new ArrayList<>();
    switch (category) {
      case CONTACT_AGENT:
        if (CollectionUtils.isEmpty(existingRoles)) {
//          List<Role> roles = new ArrayList<>();
          Role role = new Role();
          role.setBeginDate(new Date());
          role.setRoleTypeId(5);
          role.setPublicId(existingPublic.getPublicId());
          role.setCreateDate(new Date());
          role.setAddressId(addressId); 
          role.setCreatedById(userId);
          role.setSelectedInEtrackInd(1);
//          role.setChangeCtr(0);
          roles.add(role);
//          existingPublic.setRoles(roles);
        } else {
          if (existingRoles.get(0).getSelectedInEtrackInd().equals(0)) {
            logger.info("Updating the existing role for the unassigned Contact User Id {}, Context Id {}", userId, contextId);
            if (existingRoles.size() >= 1) {
              Role role = existingRoles.get(0);
              role.setAddressId(addressId);
              role.setRoleTypeId(5);
              role.setEdbRoleId(role.getEdbRoleId());
              role.setLegallyResponsibleTypeCode(null);
              role.setModifiedById(userId);
              role.setModifiedDate(new Date());
              role.setSelectedInEtrackInd(1);			  
              roles.add(role);
            }
          }
        }
        break;
      case PUBLIC:
        if (!CollectionUtils.isEmpty(propertyRelationShips)) {
          logger.info("Set the Public role type based on the Property relationship.");
//          List<Role> roles = new ArrayList<>();
          propertyRelationShips.forEach(relatioship -> {
            Role role = new Role();
            role.setAddressId(addressId);
            role.setBeginDate(new Date());
            if (relatioship.equals(1)) {
              role.setRoleTypeId(6);
            } else {
              role.setRoleTypeId(1);
            }
            role.setLegallyResponsibleTypeCode(relatioship);
            role.setPublicId(existingPublic.getPublicId());
            role.setCreateDate(new Date());
            role.setCreatedById(userId);
            role.setSelectedInEtrackInd(1);			
            if (!CollectionUtils.isEmpty(existingRoles)) {
              role.setEdbRoleId(existingRoles.get(0).getEdbRoleId());
            }
            role.setChangeCtr(0);
            roles.add(role);
          });
          logger.info("Public name Roles {}. User Id {}, Context Id {}", existingRoles, userId, contextId);
          
          if (!CollectionUtils.isEmpty(existingRoles)) {
            String createdBy = existingRoles.get(0).getCreatedById();
            Date createDate = existingRoles.get(0).getCreateDate();
            if (!CollectionUtils.isEmpty(existingRoles)) {
              existingRoles.forEach(existingRole -> {
                roleRepo.deleteById(existingRole.getRoleId());
              });
            }
//            existingRoles.clear(); // Remove the existing before update
//            existingRoles.addAll(roles);
            roles.forEach(role -> {
              role.setSelectedInEtrackInd(1);
              role.setAddressId(addressId);
              role.setCreatedById(createdBy);
              role.setCreateDate(createDate);
              // role.setChangeCtr(changeCounter);
              role.setModifiedById(userId);
              role.setModifiedDate(new Date());
            });
          }
        }
        break;
      case PROPERTY_OWNER: 
        if (CollectionUtils.isEmpty(existingRoles)) {
          logger.info("Adding new role for this public User Id {}, Context Id {}", userId, contextId);
//          List<Role> roles = new ArrayList<>();
          Role role = new Role();
          role.setAddressId(addressId);
          role.setBeginDate(new Date());
          role.setRoleTypeId(6);
          role.setPublicId(existingPublic.getPublicId());
          role.setCreateDate(new Date());
          role.setCreatedById(userId);
//          role.setChangeCtr(0);
          role.setSelectedInEtrackInd(1);
          roles.add(role);
//          existingPublic.setRoles(roles);
        } else {
          if (existingRoles.get(0).getSelectedInEtrackInd().equals(0)) {
            logger.info("Updating the existing role for the unassigned public User Id {}, Context Id {}", userId, contextId);
            if (existingRoles.size() == 1) {
              Role role = existingRoles.get(0);
              role.setRoleTypeId(6);
              role.setEdbRoleId(role.getEdbRoleId());
              role.setAddressId(addressId);
              role.setLegallyResponsibleTypeCode(null);
              role.setSelectedInEtrackInd(1);
              role.setModifiedById(userId);
              role.setModifiedDate(new Date());
              roles.add(role);
            }
          } else {
            boolean applicantExistAsAnOwner = false;
            for (Role role : existingRoles) {
              if (role.getRoleTypeId().equals(6)) {
                logger.info("This applicant is already added as applicant. Now including the same applicant as Owner too");
                applicantExistAsAnOwner = true;
              }
            }
            if (!applicantExistAsAnOwner) {
              Role role = new Role();
              role.setAddressId(addressId);
              role.setBeginDate(new Date());
              role.setRoleTypeId(6);
              role.setPublicId(existingPublic.getPublicId());
              role.setCreateDate(new Date());
              role.setCreatedById(userId);
			  role.setSelectedInEtrackInd(1);
              roles.addAll(existingRoles);
              roles.add(role);
            }
          }
        }
        break;
      default:
        throw new BadRequestException("INVALID_CATEGORY", "Invalid Category Code is passed.", category);
    }
    logger.info("Exiting from transformPropertyRelationshipToRoleEntity User id {}, Context id {}", userId, contextId);
    return roles;
  }

  /**
   * Transform the user entered applicant/public address to address entity format to store in E_ADDRESS table.
   *  
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicant - Applicant details passed the user.
   * @param entityAddress - Address object which will be used to store the entity formatted Address.
   * 
   * @return - Transformed to {@link Address}
   */
  public Address transformInputAddressToEntityAddress(final String userId, final String contextId,
      final Long projectId, final Applicant applicant, Address entityAddress) {

    logger.info("Entering into transform Address details to Entity format UserId : {}  context Id :{}",
        userId, contextId);
    ApplicantAddress inputAddress = applicant.getAddress();
    entityAddress.setStreet1(inputAddress.getStreetAdr1());
    entityAddress.setStreet2(inputAddress.getStreetAdr2());
    entityAddress.setCity(inputAddress.getCity());
    entityAddress.setState(inputAddress.getState());
    entityAddress.setEdbAddressId(inputAddress.getEdbAddressId());
    if (inputAddress.getAdrType() == null) {
      inputAddress.setAdrType(0);
    }
    entityAddress.setForeignAddressInd(inputAddress.getAdrType());
    if (StringUtils.isEmpty(inputAddress.getCountry())) {
      inputAddress.setCountry("US");
    }
    entityAddress.setCountry(inputAddress.getCountry());
    entityAddress.setZip(inputAddress.getZipCode());
    entityAddress.setAttentionName(inputAddress.getAttentionName());
    entityAddress.setForeignAddressInd(inputAddress.getAdrType());
    Contact contact = applicant.getContact();
    entityAddress.setHomePhoneNumber(contact.getHomePhoneNumber());
    entityAddress.setCellPhoneNumber(contact.getCellNumber());
    entityAddress.setBusinessPhoneNumber(contact.getWorkPhoneNumber());
    entityAddress.setBusinessPhoneExt(contact.getWorkPhoneNumberExtn());
    entityAddress.setEmailAddress(contact.getEmailAddress());
    logger.info("Exiting from transform Address details to Entity format. User Id {}, Context Id {}",
        userId, contextId);
    return entityAddress;
  }

  /**
   * Transform the user entered details like mode of application, SEQR type, applicant who is applying for etc..,
   * to Project Entity format which can be stored E_PROJECT.
   *   
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectDetail - User entered project details.
   * 
   * @return - Transformed to {@link Project}
   */
  public Project transformToProjectEntity(final String userId, final String contextId,
      ProjectDetail projectDetail) {

    logger.info("Entering into transformToProjectEntity. User Id {}, Context Id {}", userId, contextId);
    Project project = null;
    if (projectDetail != null) {
      project = new Project();
      project.setMailInInd(projectDetail.getMailInInd());
      project.setApplicantTypeCode(projectDetail.getApplicantTypeCode());
      project.setCreatedById(userId);
      project.setCreateDate(new Date());
      project.setProposedUseCode(projectDetail.getProposedUseCode());
      project.setOriginalSubmittalInd(0);
      project.setDartProcessingCompleteInd(0);
      project.setProjectInitiatedUserId(userId);
      /*
      if (StringUtils.hasLength(projectDetail.getOnlineApplicationInd())
          && projectDetail.getOnlineApplicationInd().equalsIgnoreCase("Y")) {
        project.setOnlineApplnInd(1);
      } else {
        project.setOnlineApplnInd(0);
      }
      */
      if (StringUtils.hasLength(projectDetail.getClassifiedUnderSeqr())) {
        project.setSeqrInd(Integer.parseInt(projectDetail.getClassifiedUnderSeqr()));
      }
      try {
        if (StringUtils.hasText(projectDetail.getReceivedDate())) {
          project.setReceivedDate(sdf.parse(projectDetail.getReceivedDate()));
        }
      } catch (ParseException e) {
        throw new BadRequestException("RECEIVED_DATE_INCORRECT_FORMAT", "Received Date is passed incorrect format.", project);
      };
    }
    logger.info("Exiting from transformToProjectEntity User Id {}, Context Id {}", userId, contextId);
    return project;
  }

  /**
   * Transform Project Entity to User readable format.
   *  
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param project - Project entity.
   * 
   * @return Transform to {@link ProjectDetail}
   */
  public ProjectDetail transformProjectEntity(String userId, String contextId, Project project) {
    logger.info("Entering into transformProjectEntity. User Id {}, Context Id {}", userId, contextId);
    ProjectDetail projectDetail = null;
    try {
      if (project != null) {
        projectDetail = new ProjectDetail();
        projectDetail.setProjectId(project.getProjectId());
        projectDetail.setApplicantTypeCode(project.getApplicantTypeCode());
        projectDetail.setMailInInd(project.getMailInInd());
        projectDetail.setProjectDesc(project.getProjectDesc());
        projectDetail.setProposedUseCode(project.getProposedUseCode());
      }
    } catch (Exception e) {
      logger.error("Error while converting from transform Project "
          + "Entity to Project Details  User Id {}, Context Id {}. Error {}", userId, contextId, e);
    }
    logger.info("Exiting from transformProjectEntity. User Id {}, Context Id {}", userId, contextId);
    return projectDetail;
  }

  /**
   * Transform the Facility entity details into Project detail which will be used in Step 1.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param project - Project.
   * @param facilityDto - Facility entity.
   * @param facilityAddress - Facility Address entity.
   * @param facilityPolygon - Facility Polygon entity.
   * @param region - Region details.
   * @param counties - Counties associated with this facility.
   * @param municipalities - Municipalities associated with this facility.
   * @param taxmapNumber - Tax Map number.
   * 
   * @return - Transformed {@link ProjectDetail}
   */
  public ProjectDetail transformFacilityToProjectDetail(
      final String userId, final String contextId, Project project, Facility facilityDto,
      FacilityAddr facilityAddress, FacilityPolygon facilityPolygon, String region, 
      String counties, String municipalities, String taxmapNumber) {
    
    logger.info("Entering into transformFacilityToProjectDetail User Id {}, Context Id {}", userId, contextId);
    ProjectDetail projectDetail = new ProjectDetail();
    projectDetail.setProjectId(project.getProjectId());
    projectDetail.setMailInInd(project.getMailInInd());
    if (project.getReceivedDate() != null) {
      projectDetail.setReceivedDate(sdf.format(project.getReceivedDate()));
    }
    projectDetail.setApplicantTypeCode(project.getApplicantTypeCode());
//    projectDetail.setLocDirections(facilityAddress.getLocationDirections());
    if (project.getValidatedInd() == null || project.getValidatedInd() == 0) {
        projectDetail.setValidatedInd("N");
      } else if (project.getValidatedInd() == 1) {
        projectDetail.setValidatedInd("Y");
    }
    FacilityDetail facilityDetail = new FacilityDetail();
    facilityDetail.setFacilityName(facilityDto.getFacilityName());
    FacilityAddress address = new FacilityAddress();
    address.setCountry(facilityAddress.getCountry());
    address.setCity(facilityAddress.getCity());
    address.setStreet1(facilityAddress.getStreet1());
    address.setStreet2(facilityAddress.getStreet2());
    address.setState(facilityAddress.getState());
    address.setZip(facilityAddress.getZip());
    address.setZipExtension(facilityAddress.getZipExtension());
    address.setPhoneNumber(facilityAddress.getPhoneNumber());
    facilityDetail.setAddress(address);
    facilityDetail.setEdbDistrictId(facilityDto.getEdbDistrictId());
    projectDetail.setFacility(facilityDetail);
    projectDetail.setReason(facilityDto.getComments());
    projectDetail.setBoundaryChangeReason(facilityDto.getChgBoundaryReason());
    if (facilityPolygon != null) {
      projectDetail.setPolygonId(facilityPolygon.getPolygonGisId());
      projectDetail.setMunicipalities(municipalities);
      projectDetail.setCounties(counties);
      projectDetail.setTaxmaps(taxmapNumber);
      projectDetail.setRegions(region);
      projectDetail.setLatitude(facilityPolygon.getLatitude());
      projectDetail.setLongitude(facilityPolygon.getLongitude());
      projectDetail.setPolygonStatus(PolygonStatus.getValue(facilityPolygon.getPolygonTypeCode()));
      projectDetail.setWorkAreaId(facilityPolygon.getWorkAreaPolygonId());
      if (facilityPolygon.getNytmnCoordinate() == null 
          || facilityPolygon.getNytmeCoordinate() == null) {
        throw new BadRequestException("CO_ORDINATE_REF_PTS_EMPTY", 
            "Co-Ordinates reference points cannot be empty/blank", project.getProjectId());
      }
      projectDetail.setNytmy(facilityPolygon.getNytmnCoordinate());
      projectDetail.setNytmx(facilityPolygon.getNytmeCoordinate());
    }
    logger.info("Exiting from into transformFacilityToProjectDetail User Id {}, Context Id {}", userId, contextId);
    return projectDetail;
  }
  
  public SpatialInquiryDetail transformSpatialInquiryRequestToEntity(
      final String userId, final String contextId, final SpatialInquiryRequest spatialInquiryRequest) {
   
    SpatialInquiryDetail spatialInquiryDetail = new SpatialInquiryDetail();
    spatialInquiryDetail.setPolygonId(spatialInquiryRequest.getPolygonId());
    spatialInquiryDetail.setSpatialInqCategoryId(spatialInquiryRequest.getReason().getCategory());
    spatialInquiryDetail.setRegion(spatialInquiryRequest.getRegion());
    
//    spatialInquiryDetail.setBlockLot(spatialInquiryRequest.getBlockLot());
    spatialInquiryDetail.setRequestorName(spatialInquiryRequest.getRequestorName());
    spatialInquiryDetail.setStreetAddress(spatialInquiryRequest.getStreetAddress());
    spatialInquiryDetail.setMailingAddress(spatialInquiryRequest.getMailingAddress());
    spatialInquiryDetail.setPhoneNumber(spatialInquiryRequest.getPhoneNumber());
    spatialInquiryDetail.setProjectName(spatialInquiryRequest.getProjectName());
    spatialInquiryDetail.setProjectDescription(spatialInquiryRequest.getProjectDescription());
    spatialInquiryDetail.setProjectSponsor(spatialInquiryRequest.getProjectSponsor());
    spatialInquiryDetail.setIssuesQuestions(spatialInquiryRequest.getIssuesQuestions());
//    spatialInquiryDetail.setTagsKeywords(spatialInquiryRequest.getTagsKeywords());
    spatialInquiryDetail.setLeadAgencyName(spatialInquiryRequest.getLeadAgencyName());
    spatialInquiryDetail.setLeadAgencyContact(spatialInquiryRequest.getLeadAgencyContact());
    spatialInquiryDetail.setEfcContact(spatialInquiryRequest.getEfcContact());
    spatialInquiryDetail.setPlanDescription(spatialInquiryRequest.getPlanDescription());
    spatialInquiryDetail.setExtenderName(spatialInquiryRequest.getExtenderName());
    spatialInquiryDetail.setDowContact(spatialInquiryRequest.getDowContact());
    spatialInquiryDetail.setDeveloper(spatialInquiryRequest.getDeveloper());
    spatialInquiryDetail.setPscDocketNum(spatialInquiryRequest.getPscDocketNum());
    spatialInquiryDetail.setDepProjectManager(spatialInquiryRequest.getDepProjectManager());
    spatialInquiryDetail.setOwner(spatialInquiryRequest.getOwner());
//    spatialInquiryDetail.setPersonReporting(spatialInquiryRequest.getPersonReporting());
    spatialInquiryDetail.setComments(spatialInquiryRequest.getComments());
    spatialInquiryDetail.setEmail(spatialInquiryRequest.getEmail());
//    spatialInquiryDetail.setViolationDesc(spatialInquiryRequest.getViolationDesc());
//    spatialInquiryDetail.setAllegedViolator(spatialInquiryRequest.getAllegedViolator());
//    spatialInquiryDetail.setJurisdictions(spatialInquiryRequest.getJurisdictions());
    if (StringUtils.hasLength(spatialInquiryRequest.getTaxParcel()) 
        && spatialInquiryRequest.getTaxParcel().length() > 2000) {
      spatialInquiryDetail.setTaxParcel(spatialInquiryRequest.getTaxParcel().substring(0, 2000));
    } else {
      spatialInquiryDetail.setTaxParcel(spatialInquiryRequest.getTaxParcel());
    }
   
    spatialInquiryDetail.setCounty(spatialInquiryRequest.getCounty());
    spatialInquiryDetail.setMunicipality(spatialInquiryRequest.getMunicipality());
//    spatialInquiryDetail.setSearchBy(spatialInquiryRequest.getSearchBy());
    
    spatialInquiryDetail.setState(spatialInquiryRequest.getState());
    spatialInquiryDetail.setZip(spatialInquiryRequest.getZip());
    spatialInquiryDetail.setCity(spatialInquiryRequest.getCity());
    spatialInquiryDetail.setStreet(spatialInquiryRequest.getStreet());
    spatialInquiryDetail.setBorough(spatialInquiryRequest.getBorough());
    spatialInquiryDetail.setBlock(spatialInquiryRequest.getBlock());
    spatialInquiryDetail.setLot(spatialInquiryRequest.getLot());
    spatialInquiryDetail.setMailingAddressStreet1(spatialInquiryRequest.getMailingAddressStreet1());
    
    spatialInquiryDetail.setMailingAddressStreet2(spatialInquiryRequest.getMailingAddressStreet2());
    spatialInquiryDetail.setMailingAddressZip(spatialInquiryRequest.getMailingAddressZip());
    spatialInquiryDetail.setMailingAddressState(spatialInquiryRequest.getMailingAddressState());
    spatialInquiryDetail.setMailingAddressCity(spatialInquiryRequest.getMailingAddressCity());
    spatialInquiryDetail.setPlanName(spatialInquiryRequest.getPlanName());
    spatialInquiryDetail.setCreatedById(userId);
    spatialInquiryDetail.setCreateDate(new Date());
    spatialInquiryDetail.setRcvdDate(spatialInquiryRequest.getRcvdDate());
    spatialInquiryDetail.setSearchByMunicipality(spatialInquiryRequest.getSearchByMunicipality());
    spatialInquiryDetail.setSearchBy(spatialInquiryRequest.getSearchBy());
    spatialInquiryDetail.setSearchByCounty(spatialInquiryRequest.getSearchByCounty());
    spatialInquiryDetail.setSearchByTaxParcel(spatialInquiryRequest.getSearchByTaxParcel());
    spatialInquiryDetail.setResponse(spatialInquiryRequest.getResponse());
    spatialInquiryDetail.setResponseDate(spatialInquiryRequest.getResponseDate());
    return spatialInquiryDetail;
  }
}
