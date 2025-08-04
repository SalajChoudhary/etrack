package dec.ny.gov.etrack.permit.controller;

import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ;
import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ_MSG;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.PublicCategory;
import dec.ny.gov.etrack.permit.service.ETrackApplicantService;
import dec.ny.gov.etrack.permit.util.Validator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackApplicantController {

  private static final Logger logger = LoggerFactory.getLogger(ETrackApplicantController.class.getName());
  
  @Autowired
  private ETrackApplicantService eTrackApplicantService;
  
  private static final String INVALID_PROJECT_ID_PASSED = "INVALID_PROJECT_ID";
  
  /**
   * To add and amend the Public detail passed for the input Project id. 
   * 
   * @param userId - User who initiates this request
   * @param projectId - Project id;
   * @param applicant - Applicant information details
   * @param category - Applicant category C- Contact/Agent, P - Public, O - Owner.
   * 
   * @return - updated applicant details after persist.
   */
  @PostMapping("/applicant/{category}")
  @ApiOperation(value="Store the Public details into eTrack for the input project.")
  public Applicant saveApplicant(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody Applicant applicant,
      @PathVariable @ApiParam(example = "P", value="Public Category") final PublicCategory category) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Save the Applicant details started. User Id {} Context ID : {}", userId, contextId);

    if (Validator.isValid(userId, contextId, applicant, category.name())) {
      if (applicant.getApplicantId() != null  && applicant.getApplicantId() > 0) {
        logger.info("Input Applicant is already available for this Project. "
            + "So, updating the existing applicant dteails. User Id {}, Context Id {}", userId, contextId);
        return eTrackApplicantService.updateApplicant(userId, contextId, projectId, applicant,
            category.name());
      } else {
        logger.info("Input Applicant is not available available for this Project. "
            + "So, persisting as new applicant dteails. User Id {}, Context Id {}", userId, contextId);
        return eTrackApplicantService.addApplicant(userId, contextId, projectId, applicant, category.name());
      }
    }
    return applicant;
  }

  /**
   * Amend the Applicant/Public requested to update.
   * 
   * @param userId - User id who initiates this request
   * @param projectId - input project id details
   * @param applicant - Applicant details received from the input request
   * @param category - Category C- Contact/Agent, P- Public, O - Owner
   * 
   * @return - Updated applicant details.
   */
  @PutMapping("/applicant/{category}")
  @ApiOperation(value="Update the Public with the informations shared in this call.")
  public Applicant updateApplicant(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody Applicant applicant,
      @PathVariable @ApiParam(example = "P", value="Public Category") final PublicCategory category) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Update the Applicant details started. User Id: {}, Context Id : {}", userId, contextId);
    if (applicant != null && applicant.getApplicantId() == null) {
      logger.error(
          "Applicant Id is passed as blank in the input request field {} . UserId {}, Context Id {}",
          applicant.getApplicantId(), userId, contextId);
      throw new BadRequestException("NO_EXISTING_PUBLIC", 
          "There is no reference is passed (applicant id) in the input request", applicant);
    }
    if (Validator.isValid(userId, contextId, applicant, category.name())) {
      applicant =
          eTrackApplicantService.updateApplicant(userId, contextId, projectId, applicant, category.name());
    }
    logger.info("Update the Applicant details completed. User Id: {}, Context Id : {}", userId, contextId);
    return applicant;
  }

  /**
   * To add the Applicant to this project..
   * 
   * @param userId - User id who initiates this request
   * @param projectId - input project id details
   * @param applicant - Applicant details needs to be associated.
   * 
   * @return - Applicant details with newly applicant id.
   */
  @PostMapping("/applicant")
  @ApiOperation(value="Save the LRP details into eTrack.")
  public Applicant saveApplicantDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody Applicant applicant) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Save the Applicant details started Context ID : {}", contextId);
    if (applicant == null || applicant.getApplicantId() != null) {
      throw new BadRequestException("NO_EXISTING_PUBLIC", 
          "There is a reference is passed (applicant id) in the input request, "
          + "Cannot be treated as New Public to be associated to this project", applicant);
    }
    if (Validator.isValid(userId, contextId, applicant, "P")) {
      applicant = eTrackApplicantService.saveApplicant(userId, contextId, projectId, applicant);
    }
    return applicant;
  }

  /**
   * Update the applicant (applicant/owner/contact/agent) details.
   * 
   * @param userId - User id who initiates this request.
   * @param projectId - input project id details.
   * @param applicant - Applicant details received from the input request.
   * 
   * @return - Updated applicant details.
   */
  @PutMapping("/applicant")
  @ApiOperation(value="Update/Amend the LRP details into eTrack.")
  public Applicant updateApplicantDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody Applicant applicant) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Update the Applicant details started Context ID : {}", contextId);
    if (applicant != null && applicant.getApplicantId() == null) {
      logger.error(
          "Applicant Id is passed as blank in the input request field {} . UserId {}, Context Id {}",
          applicant.getApplicantId(), userId, contextId);
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
    }
    if (Validator.isValid(userId, contextId, applicant, "P")) {
      applicant = eTrackApplicantService.updateApplicant(userId, contextId, projectId, applicant, "P");
    }
    return applicant;
  }

  /**
   * To delete the public if its added as new from eTrack or non associate 
   * the applicant details from the input project id if its existing public.
   * 
   * @param userId - User id who initiates this request. 
   * @param projectId - Project Id
   * @param edbPublicId - Public Id associated with Enterprise.
   * @param applicantId - Public Id associated with eTrack.
   * @param category - Category P- Public, O-Owner, C- Contact/Agent.
   * 
   * @return - Status of this delete request.
   */
  @DeleteMapping("/applicant/{applicantId}/{category}")
  @ApiOperation(value="Delete the Public requested as an input.")
  public ResponseEntity<Void> deleteApplicantDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestHeader(required = false) final Long edbPublicId,
      @PathVariable @ApiParam(example="1312321", value="Public Id to be deleted") final Long applicantId, 
      @PathVariable  @ApiParam(example="P", value="Public Category") final PublicCategory category) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Delete the applicant information, Category {}, "
        + "User Id {} Context Id: {}", category, userId, contextId);

    if (!(applicantId != null && applicantId > 0)) {
      logger.error("Invalid applicant Id or not passed {} . UserId {}, Context Id {}",
          applicantId, userId, contextId);
      throw new BadRequestException("INVALID_APLCT_ID", "Invalid Applicant Id is passed", applicantId);
    }
    
//    if (!(StringUtils.hasLength(category) 
//        && ETrackPermitConstant.PUBLIC.equals(category) 
//        || ETrackPermitConstant.PROPERTY_OWNER.equals(category) 
//        || ETrackPermitConstant.CONTACT_AGENT.equals(category))) {
//      
//      logger.error("Invalid applicant Id or not passed {} . UserId {}, Context Id {}",
//          applicantId, userId, contextId);
//      throw new BadRequestException("INVALID_CATEGORY", "Invalid Category indicator is passed", category);
//    }
    eTrackApplicantService.deleteApplicant(userId, contextId, projectId, edbPublicId, applicantId, category.name());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Delete more than one Contact/Agent details from the eTrack Project 
   * if its new public created via eTrack or non-associate them from this project if they are existing DART public.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param contactIds - List of Contact Ids to delete.
   */
  @DeleteMapping("/contacts/{contactIds}")
  @ResponseStatus(value = HttpStatus.OK)
  @ApiOperation(value="Delete all the Contact/Agents requested.")
  public void deleteContactAgentByIds(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "1231,12321,2432", value="Contact/Agent Ids") final List<Long> contactIds) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into delete the list of Contacts requested by User Id {} Context Id : {}",
        userId, contextId);

    if (CollectionUtils.isEmpty(contactIds) || !(projectId != null && projectId > 0)) {
      logger.error("No Contact Ids/ProjectId are passed in this request. User Id {}, Context Id {}", userId,
          contextId);
      throw new BadRequestException("CONTACT_AGENT_NOT_AVAILABLE", "There is no Contact/Agents passed to delete", contactIds);
    }
    eTrackApplicantService.deleteContacts(userId, contextId, projectId, contactIds);
  }

  
  /**
   * Save as an new Contact/Agent if its new or associate with this project if its existing in DART . 
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param applicant - Applicant information.
   * 
   * @return - Updated Applicant details with newly created applicant id.
   */
  @PostMapping("/contactAgent")
  @ApiOperation(value="Persist the Contact/Agent details requested by the user.")
  public Applicant savecontactAgentDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody Applicant applicant) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Save the Contact or Agent details started. User Id {}, Context ID : {}", userId, contextId);
    if (Validator.isValid(userId, contextId, applicant, PublicCategory.C.name())) {
      applicant = eTrackApplicantService.saveApplicant(userId, contextId, projectId, applicant);
    }
    return applicant;
  }

  /**
   * Amend the Contact/Agent details.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param applicant - Applicant details.
   * 
   * @return - Updated Applicant information.
   */
  @PutMapping("/contactAgent")
  @ApiOperation(value="Update the Contact/Agent details requested details.")
  public Applicant updatecontactAgentDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody Applicant applicant) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Update the Contact or Agent details started Context ID : {}", contextId);
    if (Validator.isValid(userId, contextId, applicant, "C")) {
      applicant = eTrackApplicantService.updateApplicant(userId, contextId, projectId, applicant, "C");
    }
    return applicant;
  }
  


  /**
   * Verify the business name is Legal name or not by making a call NYS DOS.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param legalName - Business Legal Name which needs to be verified.
   * 
   * @return - List of Business Legal Names contains Legal name.
   */
  @GetMapping("/verify/business")
  @ApiOperation(value="Verify the input Business/Organization name is verified one or not.")
  public Object verifyBusiness(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestHeader @ApiParam(example = "NYS DEC", value="Business/Organization name") final String legalName) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Verify the business User Id {} Context Id {}", userId, contextId);
    if (StringUtils.isEmpty(legalName) || legalName.length() < 3) {
      throw new BadRequestException("INVALID_LEGAL_NAME", 
          "Legal name is not passed or Less than 3 characters Passed", legalName);
    }
    return eTrackApplicantService.getBusinessVerified(userId, contextId, legalName);
  }

  /**
   * Mark the input public as Online Submitter for the input project Id.
   * 
   * @param userId - User who initiates this request.
   * @param publicIdTobeDeleted - Public Id to deleted physically if new public or unassociate if existing public.
   * @param projectId - Project Id.
   * @param publicId - Public Id.
   */
  @PutMapping("/online-submitter/{publicId}")
  @ApiOperation(value="Mark the requested Public as a new Online submitter for the project.")
  public void uploadOnlineSubmitter(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(required = false) @ApiParam(example = "24324", 
      value="Existing Online submitter Public Id which needs to be removed from this project") final Long publicIdTobeDeleted, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "24324", value="New Online submitter Public Id")  final Long publicId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into uploadOnlineSubmitter(). User Id {}, Context Id {}", userId, contextId);
    if (!(StringUtils.hasLength(userId) 
        && projectId != null && projectId > 0 && publicId != null && publicId >0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid value is passed", projectId);
    }
    eTrackApplicantService.updateOnlineSubmitter(userId, contextId, projectId, publicId, publicIdTobeDeleted);
    logger.info("Exiting from uploadOnlineSubmitter(). User Id {}, Context Id {}", userId, contextId);
  }

  
  /**
   * Update the Applicant status as acknowledged/signed in Step 5 for the project id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param applicants - List of Applicant Ids.
   */
  @PostMapping("/acknowledge-applicants")
  @ApiOperation(value="Store the LRPs, Owners and/or Contact Agent acknowledged details.")
  public void saveAcknowledgedApplicants(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final List<Long> applicants) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into adding acknowledged applicants User Id: {} Context Id: {}", userId,
        contextId);
    if (StringUtils.isEmpty(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    eTrackApplicantService.addAcknowledgedApplicants(userId, contextId, projectId, applicants);
    logger.info("Exiting from adding acknowledged applicants User Id: {} Context Id: {}", userId,
        contextId);
  }
}
