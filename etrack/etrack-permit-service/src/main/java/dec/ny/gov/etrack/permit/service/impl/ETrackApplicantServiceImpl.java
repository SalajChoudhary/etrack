package dec.ny.gov.etrack.permit.service.impl;

import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.CONTACT_AGENT;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PROPERTY_OWNER;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PUBLIC;
import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ;
import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ_MSG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.Address;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;
import dec.ny.gov.etrack.permit.entity.Public;
import dec.ny.gov.etrack.permit.entity.Role;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.ActivityTaskStatus;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.BusinessInformation;
import dec.ny.gov.etrack.permit.model.BusinessLegalNameResponse;
import dec.ny.gov.etrack.permit.repo.AddressRepo;
import dec.ny.gov.etrack.permit.repo.ApplicationRepo;
import dec.ny.gov.etrack.permit.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.permit.repo.PublicRepo;
import dec.ny.gov.etrack.permit.repo.RoleRepo;
import dec.ny.gov.etrack.permit.service.ETrackApplicantService;
import dec.ny.gov.etrack.permit.util.ETrackPermitConstant;

@Service
public class ETrackApplicantServiceImpl implements ETrackApplicantService {
  
  private static final Logger logger = LoggerFactory.getLogger(ETrackApplicantServiceImpl.class.getName());
  @Autowired
  private TransformationService transformationService;
  @Autowired
  private PublicRepo publicRepo;
  @Autowired
  private ApplicationRepo applicationRepo;
  @Autowired
  private AddressRepo addressRepo;
  @Autowired
  private ProjectActivityRepo projectActivityRepo;
  @Autowired
  private RoleRepo roleRepo;
  @Autowired
  private ETrackPermitDAO eTrackPermitDao;
  @Value("${akana.dos.basic.auth}")
  private String akanaAuthToken;
  @Autowired
  @Qualifier("businessVerificationRestTemplate")
  private RestTemplate businessVerificationRestTemplate;

  @Override
  @Transactional(rollbackFor = {DataExistException.class, ETrackPermitException.class})
  public Applicant saveApplicant(final String userId, final String contextId, final Long projectId,
      Applicant applicant) {

    logger.info("Entering into Save Applicant userId {}, Context Id {} , projectId {}", userId,
        contextId, projectId);
    try {
      Public publicName = new Public();
      publicName = transformationService.transformApplicantToPublicEntity(userId, contextId,
          projectId, applicant, publicName);

      List<Public> existingPublics =
          publicRepo.findByDisplayNameAndProjectId(publicName.getDisplayName(), projectId);
      if (!CollectionUtils.isEmpty(existingPublics)) {
        throw new DataExistException("DATA_ALREADY_EXIST",
            "Public name is already exist " + publicName.getDisplayName());
      }
      Address address = new Address();
      address = transformationService.transformInputAddressToEntityAddress(userId, contextId,
          projectId, applicant, address);
      address.setCreateDate(new Date());
      address.setCreatedById(userId);
      // address.setProjectId(projectId);
      // address.setChangeCounter(0);
      address = addressRepo.save(address);
      Long addressId = address.getAddressId();
      logger.info(
          "Address details stored successfully userId {}, Context Id {}, new Address Id : {}",
          userId, contextId, addressId);
      List<Role> existingRoles = roleRepo.findByRoleTypeId(applicant.getApplicantId(), Arrays.asList(1, 6));
      List<Role> roles = transformationService.transformPropertyRelationshipToRoleEntity(userId,
          contextId, applicant, addressId, publicName, "P", existingRoles);
      
      roleRepo.saveAll(roles);
      
      publicName.setCreateDate(new Date());
      publicName.setCreatedById(userId);
//      publicName.setSelectedInEtrackInd(1);
      publicName = publicRepo.save(publicName);
      applicant.getAddress().setAddressId(addressId);
      applicant.setApplicantId(publicName.getPublicId());
      return applicant;
    } catch (DataExistException de) {
      throw de;
    } catch (Exception e) {
      logger.error("Error while persist public related details "
          + "into DB User Id: {}, Context Id: {} Error Message: {}", userId, contextId, e);
      throw new ETrackPermitException("SAVE_PUBLIC_DB_ERROR",
          "Error received from DB while persisting new Public details " + e.getMessage(), e);
    }
  }

  private Applicant addNewApplicantDetail(final String userId, final String contextId,
      final Long projectId, Applicant applicant, final String category) {
    try {
      Public publicName = new Public();
      transformationService.transformApplicantToPublicEntity(userId, contextId, projectId,
          applicant, publicName);
      List<Long> existingPublicList = null;
      switch (category) {
        case CONTACT_AGENT:
          existingPublicList = publicRepo.findExistingContacts(publicName.getDisplayName(),
              projectId, applicant.getPublicTypeCode());
          break;
        case PROPERTY_OWNER:
          existingPublicList = publicRepo.findExistingOwner(publicName.getDisplayName(), projectId,
              applicant.getPublicTypeCode());
          break;
        case PUBLIC:
          existingPublicList = publicRepo.findExistingPublic(publicName.getDisplayName(), projectId,
              applicant.getPublicTypeCode());
          break;
        default:
          throw new BadRequestException("INVALID_CATG", "Invalid Category code is passed",
              category);
      }

      if (!CollectionUtils.isEmpty(existingPublicList)) {
        throw new DataExistException("DATA_ALREADY_EXIST",
            "Public name is already exist " + publicName.getDisplayName());
      }

      List<Public> existingPublics = publicRepo.findByDisplayNameAndProjectIdAndPublicTypeCode(
          publicName.getDisplayName(), projectId, applicant.getPublicTypeCode());

      Address address = new Address();
      address = transformationService.transformInputAddressToEntityAddress(userId, contextId,
          projectId, applicant, address);
      address.setCreateDate(new Date());
      address.setCreatedById(userId);
      // address.setProjectId(projectId);
      // address.setChangeCounter(0);
      address = addressRepo.save(address);
      Long addressId = address.getAddressId();
      logger.info(
          "Address details stored successfully User Id {}, Context Id {}, new Address Id : {}",
          userId, contextId, addressId);

      List<Role> existingRoles = null;
      if (applicant.getApplicantId() != null) {
        if (category.equals(PROPERTY_OWNER) || category.equals(PUBLIC)) {
          existingRoles = roleRepo.findByRoleTypeId(applicant.getApplicantId(), Arrays.asList(1, 6));
        } else if (category.equals(CONTACT_AGENT)) {
          existingRoles = roleRepo.findByRoleTypeId(applicant.getApplicantId(), Arrays.asList(2,3,4,5));
        }
      }
      List<Role> updatedRoles = transformationService.transformPropertyRelationshipToRoleEntity(userId,
          contextId, applicant, addressId, publicName, category, existingRoles);
      
//      List<OnlineUser> onlineUsers = onlineUserRepo.findByProjectIdAndEmailAddress(
//          publicName.getProjectId(), address.getEmailAddress());
//      
//      if (!CollectionUtils.isEmpty(onlineUsers)) {
//        publicName.setOnlineSubmitterInd(1);      
//      }
      if (CollectionUtils.isEmpty(existingPublics)) {
        logger.info("Adding new public entity and role details");
        publicName.setCreateDate(new Date());
        publicName.setCreatedById(userId);
//        publicName.setSelectedInEtrackInd(1);
        publicName = publicRepo.save(publicName);
        List<ProjectActivity> projectActivityList =
            projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId,
                ActivityTaskStatus.APPLICANT_INFO.getActivityStatus());

        ProjectActivity projectActivity = null;
        if (CollectionUtils.isEmpty(projectActivityList)) {
          logger.info("Preparing and storing the activity status for the project id {}", projectId);
          projectActivity = new ProjectActivity();
          projectActivity
              .setActivityStatusId(ActivityTaskStatus.APPLICANT_INFO.getActivityStatus());
          projectActivity.setProjectId(projectId);
          projectActivity.setStartDate(new Date());
          projectActivity.setCreateDate(new Date());
          projectActivity.setCreatedById(userId);
          if (PROPERTY_OWNER.equals(category) || (applicant.getPropertyRelationships() != null
              && applicant.getPropertyRelationships().contains(1))) {
            logger.info("Adding the permit/applicant owner will end the current "
                + "activity as completed for the project id {}", projectId);
            projectActivity.setCompletionDate(new Date());
          }
          projectActivityRepo.save(projectActivity);
        } else if ((projectActivity = projectActivityList.get(0)).getCompletionDate() == null) {
          projectActivity = projectActivityList.get(0);
          if (PROPERTY_OWNER.equals(category) || (applicant.getPropertyRelationships() != null
              && applicant.getPropertyRelationships().contains(1))) {
            projectActivity.setCompletionDate(new Date());
            projectActivity.setModifiedById(userId);
            projectActivity.setModifiedDate(new Date());
            logger.info("Received the permit/applicant owner will end the current "
                + "activity as completed for the project id {}", projectId);
            projectActivityRepo.save(projectActivity);
          }
        }
        // updateBusinessLegalName(applicant, publicName, userId);
        applicant.getAddress().setAddressId(addressId);
        applicant.setApplicantId(publicName.getPublicId());
        if (!CollectionUtils.isEmpty(updatedRoles)) {
          logger.info("Adding new role alone as the public entity is already exists");
          for (Role role : updatedRoles) {
            roleRepo.addRole(publicName.getPublicId(), role.getRoleTypeId(),
                role.getEmployeeRegionCode(), role.getLegallyResponsibleTypeCode(),
                role.getAddressId(), role.getEdbRoleId(), userId, new Date(),
                role.getPrimaryLrpInd(), 0, 1);
          }
        }
      } else {
        if (!CollectionUtils.isEmpty(updatedRoles)) {
          logger.info("Adding new role alone as the public entity is already exists");
          for (Role role : updatedRoles) {
            roleRepo.addRole(existingPublics.get(0).getPublicId(), role.getRoleTypeId(),
                role.getEmployeeRegionCode(), role.getLegallyResponsibleTypeCode(),
                role.getAddressId(), role.getEdbRoleId(), userId, new Date(),
                role.getPrimaryLrpInd(), 0, 1);
          }
        }
        applicant.getAddress().setAddressId(addressId);
        applicant.setApplicantId(existingPublics.get(0).getPublicId());
      }
      return applicant;
    } catch (DataExistException | BadRequestException de) {
      throw de;
    } catch (Exception e) {
      logger.error("Error while persisting public related details "
          + "into DB UserId {}, Context id {}. Error Message {}", userId, contextId, e);
      throw new ETrackPermitException("ADD_PUBLIC_DB_ERROR",
          "Error while adding new Public into Database", e);
    }
  }

  @Transactional
  @Override
  public Applicant addApplicant(final String userId, final String contextId, final Long projectId,
      Applicant applicant, final String category) {

    logger.info("Entering into add {} type Applicant userId {}, Context Id {}, projectId {}",
        category, userId, contextId, projectId);

    Long edbPublicId = applicant.getEdbApplicantId();

    if (edbPublicId != null && edbPublicId > 0) {
      List<Public> publicAvailable =
          publicRepo.findByEdbPublicIdAndProjectId(edbPublicId, projectId);
      if (!CollectionUtils.isEmpty(publicAvailable)) {
        throw new DataExistException("DATA_ALREADY_EXIST",
            "Public name is already exist " + edbPublicId);
      } else {
        logger.info(
            "Persisting the enterprise public and update with user keyed details User Id {}, Context Id {} ",
            userId, contextId);
        Long publicId = eTrackPermitDao.populateApplicantDetails(userId, contextId, projectId,
            applicant.getEdbApplicantId(), category);
        applicant.setApplicantId(publicId);
        Long addressId = addressRepo.findByPublicId(publicId);
        logger.info("Update the address id with passed address {}", addressId);
        applicant.getAddress().setAddressId(addressId);
        logger.info(
            "Enterprise public details added . Public Id {} and update with user keyed details User Id {}, Context Id {} ",
            publicId, userId, contextId);
        return updateApplicant(userId, contextId, projectId, applicant, category);
      }
    }
    return addNewApplicantDetail(userId, contextId, projectId, applicant, category);
  }

  @Override
  @Transactional
  public Applicant updateApplicant(final String userId, final String contextId,
      final Long projectId, Applicant applicant, final String category) {

    logger.info("Entering into update the applicant. User Id {}, Context Id {} ", userId,
        contextId);

    // Address address = updateAddress(userId, contextId, category, projectId, applicant);
    List<Long> addressIds = null;
    logger.info("Entering into update the address. User Id {}, Context Id {} ", userId, contextId);

    Optional<Public> publicEntityAvailability = publicRepo.findById(applicant.getApplicantId());
    if (publicEntityAvailability.isPresent()) {
      Public applicantEntity = publicEntityAvailability.get();

      if (applicantEntity.getEdbPublicId() == null && applicant.getApplicantId() != null
          && applicant.getEdbApplicantId() != null) {
        eTrackPermitDao.populatePublicDataIntoETrack(userId, contextId, applicant.getApplicantId(),
            applicant.getEdbApplicantId(), projectId, category);
      }
    } else {
      throw new DataNotFoundException("NO_PUBLIC_AVAIL",
          "No public is available for the Public Id " + applicant.getApplicantId());
    }
    switch (category) {
      case CONTACT_AGENT:
        addressIds = addressRepo.findAddressExistsForContact(applicant.getApplicantId(), projectId,
            applicant.getPublicTypeCode());
        break;
      case PROPERTY_OWNER:
        addressIds = addressRepo.findAddressExistsForOwner(applicant.getApplicantId(), projectId,
            applicant.getPublicTypeCode());
        break;
      case PUBLIC:
        addressIds = addressRepo.findAddressExistsForPublic(applicant.getApplicantId(), projectId,
            applicant.getPublicTypeCode());
        break;
    }

    if (!CollectionUtils.isEmpty(addressIds) 
        && applicant.getAddress().getAddressId() == null) {
      
      logger.error(
          "Address is already available for this public Id {} and address Id {} and project Id {}. UserId {}, Context Id {}",
          applicant.getApplicantId(), addressIds.get(0), projectId, userId, contextId);
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant.getAddress());
    }

    Address address = null;
    if (applicant.getAddress().getAddressId() == null) {
      logger.info(
          "This applicant {} doesn't have address earlier. Adding this as new address. User Id {}, Context Id {} ",
          applicant.getApplicantId(), userId, contextId);
      address = new Address();
      address.setCreateDate(new Date());
      address.setCreatedById(userId);
    } else {
      Optional<Address> adrResponse =
          addressRepo.findByAddressId(applicant.getAddress().getAddressId());

      if (!adrResponse.isPresent()) {
        logger.error(
            "No address is available for the input address Id {} and project Id {}. UserId {}, Context Id {}",
            applicant.getAddress().getAddressId(), projectId, userId, contextId);
        throw new BadRequestException("NO_ADDR_AVAIL",
            "There is no Address existing for the input Address id "
                + applicant.getAddress().getAddressId(),
            applicant.getAddress());
      }
      address = adrResponse.get();
      address.setModifiedById(userId);
      address.setModifiedDate(new Date());
      logger.info("Address change counter {} , User Id {}, Context Id {}",
          address.getChangeCounter(), userId, contextId);
      // address.setChangeCounter(address.getChangeCounter() + 1);
    }
    try {
      address = transformationService.transformInputAddressToEntityAddress(userId, contextId,
          projectId, applicant, address);
      address = addressRepo.save(address);// Upsert the address
      logger.info("Exiting from update the address. User Id {}, Context Id {} ", userId, contextId);
      
      Optional<Public> response = publicRepo.findById(applicant.getApplicantId());
      if (!response.isPresent()) {
        logger.error(
            "No applicant is available to update for the input applicant Id {}. UserId {}, Context Id {}",
            applicant.getApplicantId(), userId, contextId);
        throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant.getAddress());
      }

      Public publicName = response.get();
      publicName = transformationService.transformApplicantToPublicEntity(userId, contextId,
          projectId, applicant, publicName);
      
      List<Role> existingRoles = null;
      if (category.equals(PROPERTY_OWNER) || category.equals(PUBLIC)) {
        existingRoles = roleRepo.findByRoleTypeId(applicant.getApplicantId(), Arrays.asList(1, 6));
      } else if (category.equals(CONTACT_AGENT)) {
        existingRoles = roleRepo.findByRoleTypeId(applicant.getApplicantId(), Arrays.asList(2,3,4,5));
      }
      
      List<Role> publicRoles = transformationService.transformPropertyRelationshipToRoleEntity(userId,
          contextId, applicant, address.getAddressId(), publicName, category, existingRoles);
      
      logger.debug("Requesting to update the Roles. User id {}, Context Id {}", userId, contextId);
      roleRepo.saveAll(publicRoles);
      logger.debug("Completed to updating the Roles. User id {}, Context Id {}", userId, contextId);
      
//      if (StringUtils.hasLength(address.getEmailAddress())) {
//        List<OnlineUser> onlineUsersList = onlineUserRepo.findByProjectIdAndEmailAddress(projectId, address.getEmailAddress());
//        if (!CollectionUtils.isEmpty(onlineUsersList)) {
//          publicName.setOnlineSubmitterInd(1);
//        }
//      }
      publicName.setModifiedById(userId);
      publicName.setModifiedDate(new Date());
//      publicName.setSelectedInEtrackInd(1);
      publicRepo.save(publicName);
      logger.debug("Completed to updating the Public details. User id {}, Context Id {}", userId, contextId);
      List<ProjectActivity> projectActivityList = 
          projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId,
              ActivityTaskStatus.APPLICANT_INFO.getActivityStatus());
      ProjectActivity projectActivity = null;
      
      if (CollectionUtils.isEmpty(projectActivityList)) {
        logger.info("Creating new project Activity Project Id {} Context Id {}", projectId,
            contextId);
        projectActivity = new ProjectActivity();
        projectActivity.setActivityStatusId(ActivityTaskStatus.APPLICANT_INFO.getActivityStatus());
        projectActivity.setProjectId(projectId);
        projectActivity.setStartDate(new Date());
        projectActivity.setCreateDate(new Date());
        projectActivity.setCreatedById(userId);
        if (applicant.getPropertyRelationships() != null
            && applicant.getPropertyRelationships().contains(1)) {
          projectActivity.setCompletionDate(new Date());
        }
        logger.info("Update the project activity status Project Id {} Context Id {} {}", projectId,
            contextId, projectActivity);
        projectActivityRepo.save(projectActivity);
      } else {
        if (PROPERTY_OWNER.equals(category) || (applicant.getPropertyRelationships() != null
            && applicant.getPropertyRelationships().contains(1)
            && projectActivityList.get(0).getCompletionDate() == null)) {
          projectActivity = projectActivityList.get(0);
          projectActivity.setModifiedById(userId);
          projectActivity.setModifiedDate(new Date());
          projectActivity.setCompletionDate(new Date());
          logger.info("Update the project activity status Project Id {} Context Id {} {}",
              projectId, contextId, projectActivity);
          projectActivityRepo.save(projectActivity);
        }
      }
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("UPDATE_APPLICANT_ERROR",
          "Error while updating the Public details " + e.getMessage(), e);
    }
    logger.info("Completed to updating the applicant details. User id {}, Context Id {}", userId, contextId);
    return applicant;
  }

  @Override
  public Object getBusinessVerified(String userId, String contextId, String legalName) {
    try {
      String url = UriComponentsBuilder.newInstance().path("/getPublicData").build().toUriString();
      HttpEntity<Map<String, String>> requestEntity =
          akanaDOSServiceRequest("BEGINSWITH", legalName, contextId);

      ResponseEntity<BusinessLegalNameResponse> responseEntity = businessVerificationRestTemplate
          .postForEntity(url, requestEntity, BusinessLegalNameResponse.class);

      if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        List<String> legalNames = processDOSVerificationResponseMessage(responseEntity.getBody());
        logger.debug("List of legal names received for the BEGINSWITH match {}", legalNames);
        if (!CollectionUtils.isEmpty(legalNames) && legalNames.contains(legalName)) {
          return new ResponseEntity<>(Arrays.asList(legalName), HttpStatus.OK);
        }
        HttpEntity<Map<String, String>> containsRequestEntity =
            akanaDOSServiceRequest("CONTAINS", legalName, contextId);
        responseEntity = businessVerificationRestTemplate.postForEntity(url, containsRequestEntity,
            BusinessLegalNameResponse.class);
        legalNames = processDOSVerificationResponseMessage(responseEntity.getBody());
        logger.debug("List of legal names received for the BEGINSWITH match {}", legalNames);

        if (CollectionUtils.isEmpty(legalNames)) {
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(legalNames, HttpStatus.ACCEPTED);
      }
      return new ResponseEntity<>(responseEntity.getStatusCode());
    } catch (HttpClientErrorException e) {
      logger.error(
          "Error response for Business verification call. User Id {}, Context Id {}. Error {}",
          userId, contextId, e);
      if (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode())) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      } else {
        return new ResponseEntity<>(e.getStatusCode());
      }
    }
  }

  private HttpEntity<Map<String, String>> akanaDOSServiceRequest(final String typeOfMatch,
      final String legalName, final String contextId) {
    Map<String, String> verifyRequestEntity = new HashMap<>();
    verifyRequestEntity.put("custTransactionId", contextId);
    verifyRequestEntity.put("searchTypes", "LEGALNAME");
    verifyRequestEntity.put("legalEntityName", legalName);
    verifyRequestEntity.put("legalTypeOfMatch", typeOfMatch);
    verifyRequestEntity.put("legalCorpNameStatus", "ACTIVE");
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(akanaAuthToken);
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(verifyRequestEntity, headers);
    return requestEntity;
  }

  private List<String> processDOSVerificationResponseMessage(BusinessLegalNameResponse response) {
    List<String> legalNames = null;
    if (response.getResponseInfo() != null
        && "Success".equals(response.getResponseInfo().getResponseMessage())) {
      if (response.getLegalNameInformation() != null) {
        legalNames = new ArrayList<>();
        List<BusinessInformation> businessInformations =
            response.getLegalNameInformation().getBusinessInformation();
        for (BusinessInformation businessInfo : businessInformations) {
          legalNames.add(businessInfo.getLegalName());
        }
      }
    } else if (response.getResponseInfo() != null
        && response.getResponseInfo().getErrorMessage() != null) {
      throw new BadRequestException("INVALID_REQ", response.getResponseInfo().getErrorMessage(),
          response);
    }
    return legalNames;
  }

  @Transactional
  @Override
  public void deleteApplicant(final String userId, final String contextId, final Long projectId,
      Long edbPublicId, final Long applicantId, final String category) {

    Public applicant = null;
    List<Public> applicantList = publicRepo.findByPublicIdAndProjectId(applicantId, projectId);
    if (CollectionUtils.isEmpty(applicantList)) {
      logger.error(
          "Invalid Parameters are requested for deleting an applicant details applicant Id {} . UserId {}, Context Id {}",
          applicantId, userId, contextId);
      throw new BadRequestException("NO_PUBLIC_AVAIL",
          "There is no public associated with the public id " + applicantId, applicantId);
    }
    try {
      applicant = applicantList.get(0);
      edbPublicId = applicant.getEdbPublicId();
      List<Role> rolesList =  null;
      if (category.equals(PROPERTY_OWNER) || category.equals(PUBLIC)) {
        rolesList = roleRepo.findByRoleTypeId(applicant.getPublicId(), Arrays.asList(1, 6));
      } else if (category.equals(CONTACT_AGENT)) {
        rolesList = roleRepo.findByRoleTypeId(applicant.getPublicId(), Arrays.asList(2,3,4,5));
      }
      logger.debug("Roles {}", rolesList);
      logger.info("User has requested to the delete the Category {} "
          + "applicant(s). User Id : {}, Context Id {}", category, userId, contextId);

      if (ETrackPermitConstant.PUBLIC.equals(category)
          || ETrackPermitConstant.PROPERTY_OWNER.equals(category)
              && !CollectionUtils.isEmpty(rolesList)) {

        logger.info("Delete/Disassociate the Applicants from this Project "
            + "{}, User Id {}, Context Id {}", projectId, userId, contextId);

        Map<Integer, Role> roleMap = new HashMap<>();
        List<Long> nonOwnersRoleIds = new ArrayList<>();
        rolesList.forEach(role -> {
          if (!ETrackPermitConstant.OWNER_ROLE.equals(role.getRoleTypeId())) {
            nonOwnersRoleIds.add(role.getRoleId());
          }
          roleMap.put(role.getRoleTypeId(), role);
        });
        if (ETrackPermitConstant.PUBLIC.equals(category)) {
          if (rolesList.size() == 1 || roleMap.get(ETrackPermitConstant.OWNER_ROLE) == null) {
            logger.info("There is no Owner role associated or Only one relationship "
                + "associated to this Applicant. User Id {}, Context Id {}", userId, contextId);
            deleteApplicant(userId, contextId, rolesList, applicant, category);
          } else {
            logger.info("There is Owner role associated to this Applicant. "
                + "So, will exclude the Owner role and remove other roles. User Id {}, Context Id {}",
                userId, contextId);
            logger.debug("Non Owners roles list {}", nonOwnersRoleIds);
            nonOwnersRoleIds.forEach(nonOwnerRoleId -> {
              roleRepo.deleteRoleById(nonOwnerRoleId);
            });
          }
        } else if (ETrackPermitConstant.PROPERTY_OWNER.equals(category)) {
          if (roleMap.get(ETrackPermitConstant.LRP_ROLE) == null) {
            logger.info(
                "There is no LRP Role associated with this Applicant, "
                    + "So, this applicant can be deleted directly. User Id {}, Context Id {}",
                userId, contextId);
            deleteApplicant(userId, contextId, rolesList, applicant, category);
          } else {
            roleRepo.deleteRoleById(roleMap.get(ETrackPermitConstant.OWNER_ROLE).getRoleId());
          }
        }
      } else {
        logger.info("Requesting to delete the Contact/Agent associated "
            + "to this project id {}. User Id {}, Context Id {}", projectId, userId, contextId);
        deleteApplicant(userId, contextId, rolesList, applicant, category);
      }
    } catch (Exception e) {
      throw new ETrackPermitException("APLCT_DELETION_ERROR",
          "Error while deleting the Applicant and role associated with the applicant id "
              + applicantId,
          e);
    }
  }

  /**
   * This method is used to delete or un-associate the public/applicant from the Project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique transaction id to track this request.
   * @param rolesList - Roles associate to this public.
   * @param applicant - Applicant details.
   * @param category - Category of the public.
   */
  private void deleteApplicant(final String userId, final String contextId, List<Role> rolesList,
      Public applicant, final String category) {
    logger.info(
        "This applicant id is not associated as an Owner. Its a individual public associated. "
            + "So, this can be deleted directly. User Id {}, Context Id{} ",
        userId, contextId);
    if (applicant.getEdbPublicId() != null && applicant.getEdbPublicId() > 0) {
      logger.info(
          "Disassociate this Enterprise Applicant from the project. User Id {}, Context Id {}",
          userId, contextId);
      if (CONTACT_AGENT.equals(category)) {
        publicRepo.updateSeletedInETrackInd(applicant.getPublicId(), Arrays.asList(
            Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5))); // Logical delete
      } else {
        publicRepo.updateSeletedInETrackInd(applicant.getPublicId(),Arrays.asList(
            Integer.valueOf(1), Integer.valueOf(6))); // Logical delete
      }
    } else {
      // Physical delete
      logger.info("Delete this new Applicant from the Project. User Id {}, Context Id {}", userId,
          contextId);
      Set<Long> addressIds = new HashSet<>();
      if (!CollectionUtils.isEmpty(rolesList)) {
        rolesList.forEach(role -> {
          addressIds.add(role.getAddressId());
          roleRepo.deleteById(role.getRoleId());
        });
      }
      publicRepo.delete(applicant);
      if (addressIds != null) {
        addressIds.forEach(addressId -> {
          addressRepo.deleteById(addressId);
        });
      }
    }
  }


  @Transactional
  @Override
  public void deleteContacts(final String userId, final String contextId, final Long projectId,
      List<Long> contactIds) {
    logger.info("Entering into delete contacts/Agents User Id {}, Context Id {}", userId,
        contextId);

    List<Public> contactAgents = publicRepo.findAllPublicByIds(projectId, contactIds);

    if (CollectionUtils.isEmpty(contactAgents)) {
      logger.error(
          "There is no record associated with these input contact Ids. User Id {}, Context Id {}",
          userId, contextId);
      throw new BadRequestException("NO_CONTACT_AGENT_AVAIL",
          "There is no Contacts/Agents associated with these input contact Ids " + contactIds,
          contactIds);
    }

    if (contactIds.size() != contactAgents.size()) {
      logger.error(
          "Input list of request records are not matching with result. User Id {}, Context Id {}",
          userId, contextId);
      throw new BadRequestException("ONE_OR_MORE_CONTACT_MISSING",
          "One or More Contact/Agent is not avaialble for the input contact Ids " + contactIds,
          contactIds);
    }

    try {
      contactAgents.forEach(contact -> {
        List<Role> roles =  roleRepo.findByRoleTypeId(contact.getPublicId(), Arrays.asList(2,3,4,5));
        if (!CollectionUtils.isEmpty(roles)) {
          roles.forEach(role -> {
            applicationRepo.deleteByProjectIdAndRoleId(projectId, role.getRoleId());
          });
        }

        Long edbPublicId = contact.getEdbPublicId();
        if (edbPublicId != null && edbPublicId > 0) {
          publicRepo.updateSeletedInETrackInd(contact.getPublicId(), Arrays.asList(
              Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5))); // Logical delete
        } else {
          // Physical delete
          Set<Long> addressIds = new HashSet<>();
          if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> {
              addressIds.add(role.getAddressId());
            });
            roles.clear();
          }
          publicRepo.delete(contact);
          if (addressIds != null) {
            addressIds.forEach(addressId -> {
              addressRepo.deleteById(addressId);
            });
          }
        }
      });
    } catch (Exception e) {
      throw new ETrackPermitException("CONTACT_AGENT_DELETE_ERROR",
          "Error while deleting the Contact/Agents for the requested input ", e);
    }
    logger.info("Exiting from delete contacts/Agents User Id {}, Context Id {}", userId, contextId);
  }

  @Transactional(rollbackFor = {BadRequestException.class, ETrackPermitException.class})
  @Override
  public void addAcknowledgedApplicants(String userId, String contextId, final Long projectId,
      List<Long> signedApplicants) {
    logger.info("Entering into save Acknowledged Applicants User Id {}, Context Id {}", userId,
        contextId);
    try {
      int recordsUpdated = 0;
      if (!CollectionUtils.isEmpty(signedApplicants)) {
        List<Long> publicsList = publicRepo.findAllExcludedPublicIds(projectId, signedApplicants);
        if (!CollectionUtils.isEmpty(publicsList)) {
          publicRepo.updateAllAcknowledgedApplicants(userId, projectId, publicsList, 0);
        }
        recordsUpdated =
            publicRepo.updateAllAcknowledgedApplicants(userId, projectId, signedApplicants, 1);
      } else {
        List<Long> publicsList = publicRepo.findAllPublicByProjectId(projectId);
        if (!CollectionUtils.isEmpty(publicsList)) {
          publicRepo.updateAllAcknowledgedApplicants(userId, projectId, publicsList, 0);
        }
      }
      if (signedApplicants.size() != recordsUpdated) {
        throw new BadRequestException("PUBLIC_LIST_MISMATCH_ERROR",
            "There is no Publics to acknowledge or one or more Public is missing",
            signedApplicants);
      }
      List<ProjectActivity> projectActivities =
          projectActivityRepo.findAllByProjectIdAndActivityStatusId(projectId, 5);
      if (CollectionUtils.isEmpty(projectActivities)) {
        projectActivities = projectActivityRepo.findAllByProjectId(projectId);
        if (CollectionUtils.isEmpty(projectActivities) || projectActivities.size() != 4) {
          throw new BadRequestException("INVALID_REQ",
              "This project is incomplete or not available " + projectId, projectId);
        }
        ProjectActivity projectActivity = new ProjectActivity();
        projectActivity.setActivityStatusId(5);
        projectActivity.setProjectId(projectId);
        projectActivity.setCompletionDate(new Date());
        projectActivity.setCreatedById(userId);
        projectActivity.setCreateDate(new Date());
        projectActivityRepo.save(projectActivity);
      }
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("ACKNOWLEDGE_APLCT_ERROR",
          "Error while acknowledging the input publics. Error detail " + e.getMessage(), e);
    }
    logger.info("Exiting from save Acknowledged Applicants User Id {}, Context Id {}", userId,
        contextId);
  }
  
  @Transactional
  @Override
  public void updateOnlineSubmitter(String userId, String contextId, Long projectId,
      Long publicId, final Long publicIdTobeDeleted) {
    logger.info("Update the Public {} as Online Submitter for the Project Id {}. "
        + "User Id {}, Context id {}", publicId, projectId, userId, contextId);
    
//    try {
//      List<String> publicDetails = onlineUserRepo.findPublicDetailsByProjectIdAndPublicId(projectId, publicId);
//      if (CollectionUtils.isEmpty(publicDetails)) {
//        throw new BadRequestException("PUBLIC_NOT_EXIST", "This public is not available", projectId);
//      }
//      List<Public> applicantList = null;
//      if (publicIdTobeDeleted != null && publicIdTobeDeleted > 0) {
//        applicantList = publicRepo.findByPublicIdAndProjectId(publicIdTobeDeleted, projectId);
//        if (CollectionUtils.isEmpty(applicantList)) {
//          logger.error(
//              "Invalid Parameters are requested for deleting an applicant details applicant Id {} . UserId {}, Context Id {}",
//              publicIdTobeDeleted, userId, contextId);
//          throw new BadRequestException("PUBLIC_NOT_EXIST",
//              "There is no public associated with the public id to delete " + publicIdTobeDeleted, projectId);
//        }
//      }
//      
//      publicRepo.resetTheExistingOnlineSubmitter(projectId);
//      publicRepo.updatePublicAsOnlineSubmitter(projectId, publicId);
//      OnlineUser onlineUserDetail = onlineUserRepo.findByProjectIdAndOnlineUser(projectId);
//      if (onlineUserDetail == null) {
//        onlineUserDetail = new OnlineUser();
//        onlineUserDetail.setCreatedById(userId);
//        onlineUserDetail.setCreateDate(new Date());
//        onlineUserDetail.setProjectId(projectId);
//        onlineUserDetail.setOnlineUserTypeCode("O");    
//      } else {
//        onlineUserDetail.setModifiedById(userId);
//        onlineUserDetail.setModifiedDate(new Date());
//      }
//      String[] publicDetail = publicDetails.get(0).split(",");    
//      onlineUserDetail.setFirstName(publicDetail[0]);
//      onlineUserDetail.setLastName(publicDetail[1]);
//      onlineUserDetail.setEmailAddress(publicDetail[2]);
//      onlineUserRepo.save(onlineUserDetail);
//      
//      if (!CollectionUtils.isEmpty(applicantList)) {
//        logger.info("Delete the requested Public from the Project {}. "
//            + "User Id {}, Context Id {}", publicIdTobeDeleted, userId, contextId);
//        Public applicantTobeDeleted = applicantList.get(0);
//        List<Role> rolesList = applicantTobeDeleted.getRoles();
//        deleteApplicant(userId, contextId, rolesList, applicantTobeDeleted);
//      }
//      logger.info("Update the Public {} as Online Submitter for the Project Id {}. "
//          + "User Id {}, Context id {}", publicId, projectId, userId, contextId); 
//    } catch (BadRequestException bre) {
//      throw bre;
//    } catch (Exception e) {
//      logger.error("Error while adding or Updating the "
//          + "Online submitter details. User Id {}, Context Id {}", userId, contextId);
//      throw new ETrackPermitException("ONLINE_SUBMITTER_PERSIST_ERR", 
//          "Error while adding or Updating the Online submitter details.", e);
//    }
  }
}
