package dec.ny.gov.etrack.permit.controller;

import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ;
import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ_MSG;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.ApplicationPermit;
import dec.ny.gov.etrack.permit.model.ApplicationPermitDetail;
import dec.ny.gov.etrack.permit.model.DIMSRRequest;
import dec.ny.gov.etrack.permit.model.DartUploadDetail;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.EmailContent;
import dec.ny.gov.etrack.permit.model.ProjectInfo;
import dec.ny.gov.etrack.permit.model.ReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;
import dec.ny.gov.etrack.permit.service.ETrackPermitService;
import dec.ny.gov.etrack.permit.service.EmailService;
import dec.ny.gov.etrack.permit.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackPermitController {

  @Autowired
  private ETrackPermitService eTrackPermitService;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private EmailService emailService;
  
  private static Logger logger = LoggerFactory.getLogger(ETrackPermitController.class.getName());
  private static final List<Integer> CONSTN_TYPES = Arrays.asList(1, 2, 3);
  private static final List<String> STEP_CATEGORIES =
      Arrays.asList("PROJ", "PROJ-INFO", "SUPPORT-DOC", "SIGN-SUBMIT");
  private static final String INVALID_PROJECT_ID_PASSED = "INVALID_PROJECT_ID";


  /**
   * To update the validate status of each step and/or sub step.
   * 
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param category - eTrack main steps.
   * @param activityId - Activity Status id.
   * @param indicator - Validated Indicator. Possible values are 0, 1.
   */
  @PostMapping("/validator/{category}/{activityId}/{indicator}")
  @ApiOperation(value="Capture and persist the validated status.")
  public void saveValidatorStepDetail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "P", value="Permit Process Step category") final String category,
      @PathVariable @ApiParam(example = "PROJ", value="Activity Id assigend for each step/Sub Step."
          + "PROJ\n"
          + "PROJ-INFO\n"
          + "SUPPORT-DOC\n"
          + "SIGN-SUBMIT") final Integer activityId, 
      @PathVariable @ApiParam(example = "1", value="Indicates whether the step/sub-step in the permit process is validated or not. 1 - Validated, 0 - Unvalidated") Integer indicator) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Save applicant validate details User id {}, Context Id {}", userId, contextId);

    if (StringUtils.isEmpty(category) || !STEP_CATEGORIES.contains(category.toUpperCase())) {
      throw new BadRequestException("INVALID_STEP_CATG", 
          "Invalid Step category is passed in the request to update the status as validated.", category);
    }
    eTrackPermitService.storeValidatorForStep(userId, contextId, projectId, category, activityId,
        indicator);
  }

  /**
   * Update the Project details in the permit application process especially Step 3.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param projectInfo - Project information details.
   * 
   * @return - Updated Project information details.
   */
  @PostMapping("/projectInfo")
  @ApiOperation(value="Persist the Project details like description, SIC, NAICS, "
      + "BIN etc.., captured in the Project Decription screen in the eTrack Portal.")
  public ProjectInfo storeProjectInfo(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final ProjectInfo projectInfo) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Store/Update the Project information User Id {}, Context Id {}", userId,
        contextId);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    
    try {
      if (StringUtils.isEmpty(projectId) || (projectInfo.getConstrnType() != null
          && (!CONSTN_TYPES.contains(projectInfo.getConstrnType())))) {
        throw new BadRequestException("INVALID_PROJ_INFO_PASSED", "One or more invalid Project information is passed", projectInfo);
      }

      if (StringUtils.hasLength(projectInfo.getProposedStartDate())) {
        projectInfo.setProposedStartDateVal(sdf.parse(projectInfo.getProposedStartDate()));
      }
      if (StringUtils.hasLength(projectInfo.getEstmtdCompletionDate())) {
        projectInfo.setEstmtdCompletionDateVal(sdf.parse(projectInfo.getEstmtdCompletionDate()));
      }
      
    } catch (ParseException e) {
      logger.error(
          "Unexpected Date format is received in the input. User Id: {} "
          + "Context Id : {} Start Date {} , Estimated Completion Date {}",
          userId, contextId, projectInfo.getProposedStartDate(),
          projectInfo.getEstmtdCompletionDate());
      throw new BadRequestException("INVALID_DATE_FORMAT", "Invalid format of Date value is passed", projectInfo);
    }
    return projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
  }


  /**
   * Persist the requested Application Permits for the input Project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param applicationPermit - Application Permits
   */
  @PostMapping("/application-permit")
  @ApiOperation(value="Store the list of Permits requested by the user.")
  public void saveApplicationPermits(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final ApplicationPermit applicationPermit) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into save application permits User Id: {}, Context Id {}:", userId,
        contextId);

    if (!(applicationPermit != null && StringUtils.hasLength(userId)
        && projectId != null && projectId > 0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    Integer constrnType = applicationPermit.getConstrnType();
    if (constrnType != null) {
      if (!CONSTN_TYPES.contains(constrnType)) {
        throw new BadRequestException("INVALID_CONSTN_TYPE", 
            "Invalid Construction type is passed", applicationPermit);
      }
    }
    eTrackPermitService.saveApplicationPermits(userId, contextId, projectId, applicationPermit);
    logger.info("Exiting from save application permits User Id: {}, Context Id {}:", userId,
        contextId);
  }

  /**
   * Update the Application Transaction/Application Type 
   * when the user wants to change the MOD questions from the summary. 
   * 
   * @param userId - User id who initiates this request.
   * 
   * @param projectId - Project Id
   * @param applicationPermit - Modified Application details.
   */
  @PutMapping("/application-permit")
  @ApiOperation(value="Update the Application Transaction/Auth type staff wants to update during the review process.")
  public void updateApplicationAppType(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final ApplicationPermit applicationPermit) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into amend Applications Trans/App Type for the permits User Id: {}, Context Id {}:", userId,
        contextId);

    if (!(applicationPermit != null && StringUtils.hasLength(userId)
        && projectId != null && projectId > 0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    eTrackPermitService.updateAmendedApplicationTransTypes(userId, contextId, projectId, applicationPermit);
    logger.info("Entering into amend Applications Trans/App Type for the permits User Id: {}, Context Id {}:", userId,
        contextId);
  }

  /**
   * Add additional permits for the input Project id. While processing the permits in DART, 
   * user might have requested to add additional permits or the project might need additional permits.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param ppid - Unique GUID allocated by AD/ASMS system for this user.
   * @param jwtToken - JWT Token.
   * @param applicationPermit - Additional Application.
   */
  @PostMapping("/addl-application")
  @ApiOperation(value="Add additional application/permit for the project created in eTrack and uploaded to DART. "
      + "Staff can add additional permits from Virtual Workspace.")
  public void saveAdditionalApplicationPermit(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestHeader @ApiParam(example = "24337e61-71a2-4765-91fa-8f4dc3d59c09", 
      value="DEC Staff Unique PPID assigned in the AD System") final String ppid, 
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestBody final ApplicationPermit applicationPermit) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into save additional application permit User Id: {}, Context Id {}:", userId,
        contextId);

    if (!(applicationPermit != null && StringUtils.hasLength(userId)
        && projectId != null && projectId > 0)) {
      throw new BadRequestException("ADDL_PERMIT_REQD", 
        "One or more mandatory detail is missing for the project "+ projectId, applicationPermit);
    }
    eTrackPermitService.saveAdditionalApplicationPermit(
        userId, contextId, jwtToken, ppid, projectId, applicationPermit);
    logger.info("Exiting from save additional application permit User Id: {}, Context Id {}:", userId,
        contextId);
  }
  
  /**
   * Assign Contact/Agents to the application(s) form.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id
   * @param assignContacts - Assigned Contacts for each permit.
   */
  @PostMapping("/permit-contacts")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Assign Contact/Agent to the Permit form.")
  public void assignContacts(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final List<ApplicationPermitDetail> assignContacts) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Save the associated Contacts to the Permits User Id {} Context ID : {}", userId, contextId);
    if (CollectionUtils.isEmpty(assignContacts) || StringUtils.isEmpty(userId)
        || StringUtils.isEmpty(projectId)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    eTrackPermitService.assignContacts(userId, contextId, projectId, assignContacts);
  }
  
  /**
   * Delete the requested application by the user for the input project.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param applicationId - Application Id. 
   * @param permitType - Permit Type.
   */
  @DeleteMapping("/application-permit/{applicationId}/{permitType}")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Delete the Application/Permit requested by the user.")
  public void deleteApplicationPermit(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable  @ApiParam(example = "993243", value="Application Id") final Long applicationId, 
      @PathVariable @ApiParam(example = "TW", value="Permit Type") final String permitType) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Delete the application permit requested User Id: {} Context ID: {}",
        userId, contextId);
    if (StringUtils.hasLength(userId) && projectId != null && projectId > 0
        && StringUtils.hasLength(permitType) && applicationId != null && applicationId > 0) {

      eTrackPermitService.removeApplicationPermit(userId, contextId, projectId, applicationId,
          permitType);
      logger.info("Exiting from Delete the application permit requested User Id: {} Context ID: {}",
          userId, contextId);
      return;
    }
    throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
  }

  /**
   * Delete the requested the list of applications by the user.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param applicationIds - Application Ids to be deleted. 
   */
  @PostMapping("/del-appl-permit")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Delete all the Applications/Permits requested by the user.")
  public void deleteApplicationPermits(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody List<Long> applicationIds) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Delete the List of application permits requested User Id: {} Context ID: {}",
        userId, contextId);
    if (StringUtils.hasLength(userId) 
        && projectId != null && projectId > 0 && !CollectionUtils.isEmpty(applicationIds)) {

      eTrackPermitService.removeApplicationPermits(userId, contextId, projectId, applicationIds);
      logger.info("Exiting from Delete the List of application permits requested User Id: {} Context ID: {}",
          userId, contextId);
      return;
    }
    throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
  }

  /**
   * Retrieve the list of Permit Types for the input Project Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id. 
   * @return - List of Permit Types for the input project id.
   */
  @GetMapping("/permitTypes")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Retrieve all the Permit Types associated with this project.")
  public List<String> getApplicationPermits(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Save the application permits User Id {} Context ID : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(projectId)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    return eTrackPermitService.getPermitTypes(userId, contextId, projectId);
  }

  /**
   * Mark the support document upload step as completed.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id. 
   */
  @PostMapping("/support-document")
  @ApiOperation(value="Mark the Support document step as completed.")
  public void supportDocument(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into support document User Id: {} Context Id: {}", userId, contextId);
    projectService.addSupportDocument(userId, contextId, projectId);
  }
  
  /**
   * Submit the project in the Step 5 in the project submission flow.
   * 
   * @param userId - User Id who initiates this request.
   * @param acknowledgedInd - Acknowledged indicator.
   * @param projectId - Project Id.
   */
  @PostMapping("/submit-project")
  @ApiOperation(value="Mark the project as Submitted.")
  public void projectSubmission(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(required = false) @ApiParam(example = "Y", value="Project acknowledged indicator") final String acknowledgedInd,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into project final User submission User Id: {} Context Id: {}", userId,
        contextId);
    if (StringUtils.isEmpty(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    projectService.submitProject(userId, contextId, projectId);
    logger.info("Exiting from project final User submission User Id: {} Context Id: {}", userId,
        contextId);
  }

  /**
   * Update the applicant signature received details.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param publicIdList - List of Public Ids to be marked as acknowledged.
   */
  @PostMapping("/sign-received")
  @ApiOperation(value="Update all the list of publics as signature received.")
  public void updateSignatureReceived(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody List<Long> publicIdList) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into update the signature received for the project {} User Id: {} Context Id: {}",
        projectId, userId, contextId);

    if (CollectionUtils.isEmpty(publicIdList)) {
      throw new BadRequestException("PUBLIC_IDs_NOT_PASSES", 
          "There is no public Ids passed to update the Signature ", projectId);
    }
    projectService.updateSignatureReceived(userId, contextId, projectId, publicIdList);
    logger.info(
        "Exiting from update the signature received for the project {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
  }
  
  
  /**
   * Assign the requested reviewer for the document to review.
   * 
   * @param userId - User id who initiates this request.
   * @param projectId - Project Id.
   * @param documentReview- Document Review details.
   * 
   * @return - Returns the content in the email correspondence format 
   *            as the sequence call should trigger an email from the UI.
   */
  @PostMapping("/assign-doc-reviewer")
  @ApiOperation(value="Assign the DEC Program Area Reviewer to review the document")
  public EmailContent assignProjectReviewerToDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody DocumentReview documentReview) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into assignProjectReviewerToDocument. User Id {} , Context Id{} ", userId,
        contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project id is blank or invalid passed", projectId);
    }
    if (StringUtils.hasText(documentReview.getReviewerName()) 
        && StringUtils.hasText(documentReview.getDateAssigned())
        && StringUtils.hasText(documentReview.getDueDate())) {
      return projectService.updateDocumentReviewerDetails(userId, contextId, projectId, documentReview);
    } else {
      throw new BadRequestException("INVALID_REVIEW_DETAIL", "Review request details should not be empty ", documentReview);
    }
  }
  
  /**
   * Mark the status as Review Complete for the requested review documents.
   * 
   * @param token - JWT Token.
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param reviewCompletionDetail - Review completion details.
   */
  @PostMapping("/review-complete")
  @ApiOperation(value="Mark the review as completed in eTrack "
      + "and upload all the correspondences related to this review into DMS.")
  public void updateDocReviewCompletion(
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String token,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody ReviewCompletionDetail reviewCompletionDetail) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updateDocReviewCompletion. User Id {} , Context Id{} ", userId,
        contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project id is blank or invalid passed", projectId);
    }
    if (reviewCompletionDetail == null) {
      throw new BadRequestException("INVALID_REVIEW_DETAIL", "Review request details should not be empty", reviewCompletionDetail);
    }
    projectService.updateDocumentReviewerCompletionDetails(userId, contextId, token, projectId, reviewCompletionDetail);
    logger.info("Existing from updateDocReviewCompletion. User Id {} , Context Id{} ", userId,
        contextId);
  }
  
  /**
   * Send the email to the list of correspondences requested in this request and store the correspondence details too.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId  - Project Id
   * @param emailContent - Email Content
   * @param attachments - Documents to be attached in the email.
   */
  @PostMapping(value = "/send-email", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  @ApiOperation(value="Send the email to the requested list of emails along with attachments if any. "
      + "These attachments will not be stored in eTrack.")
  public void sendEmail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestPart EmailContent emailContent, @RequestParam(required = false) MultipartFile[] attachments) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into sendEmail() method . User Id {}, Context Id {}", userId, contextId);
    emailService.sendEmail(userId, contextId, projectId, emailContent, attachments);
    logger.info("Exiting from sendEmail() method . User Id {}, Context Id {}", userId, contextId);
  }
  
  /**
   * Mark the email as read by the user for the input correspondence id.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param correspondenceId - Correspondence id.
   */
  @PostMapping(value = "/email-read/{correspondenceId}")
  @ApiOperation(value="Mark the requested email correspondence id as read. "
      + "So, this email won't be displayed as a new in the User dashboard.")
  public void updateEmailReadStatus(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "12313", value="Email Correspondence Id") final Long correspondenceId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updateEmailReadStatus() method . User Id {}, Context Id{}", userId, contextId);
    emailService.updateEmailReadStatus(userId, contextId, projectId, correspondenceId);
    logger.info("Exiting from updateEmailReadStatus() method . User Id {}, Context Id{}", userId, contextId);
  }
  
  
  /**
   * Delete all the correspondences associated with this Correspondence id.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param correspondenceId - Correspondence Id.
   */
  @DeleteMapping(value = "/delete-email/{correspondenceId}")
  @ApiOperation(value="Delete the email correspondence requested by the user.")
  public void deleteEmailCorrespondence(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "12324", value="Email Correspondence Id") final Long correspondenceId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into deleteEmailCorrespondence() method . User Id {}, Context Id{}", userId, contextId);
    emailService.deleteEmailCorrespondence(userId, contextId, projectId, correspondenceId);
    logger.info("Exiting from deleteEmailCorrespondence() method . User Id {}, Context Id{}", userId, contextId);
  }
  
  /**
   * Update the BIG MOD question and answer details.
   * .
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * 
   * @param existingPermits - Existing Permit details.
   */
  @PutMapping("/submit-permit-form")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Update the Existing Pwermit form submission details "
      + "like BIG MOD question when the user applies for Modification/Extension/Transfer.")
  public void updateExistingPermitAmendRequest(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final List<ApplicationPermitDetail> existingPermits) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Save the Existing Permit modification/extension request details User Id {} Context Id : {}", userId, contextId);
    if (CollectionUtils.isEmpty(existingPermits) || StringUtils.isEmpty(userId)
        || StringUtils.isEmpty(projectId)) {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, existingPermits);
    }
    eTrackPermitService.updateExistingPermitAmendDetails(userId, contextId, projectId, existingPermits);
  }
  
  /**
   * Retrieve all the permit details associated with the requested batch.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param batchId - Batch Id.
   * 
   * @return - Permit details.
   */
  @GetMapping("/permit-form/{batchId}")
  @ApiOperation(value="Retrieve all the Permit forms for the input enterprise batch id.")
  public List<ApplicationPermitDetail> retrievePermitFormDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "45645", value="Enterprise batch id") final Long batchId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrive the Existing Permit modification/extension request "
        + "details for the batch Id {} User Id {} Context Id : {}", batchId, userId, contextId);
    return eTrackPermitService.retrievePermitDetails(userId, contextId, projectId, batchId);
  }

  /**
   * Store the DIMSR details both eTrack and enterprise.
   * 
   * @param userId - User Id who initiates this request.
   * @param jwtToken - JWT Token.
   * @param ppid - Unique PPID assigned for the user in AD system.
   * @param dimsrRequest - DIMSR Request details. 
   * 
   * @return - Uploaded DIMSR details.
   */
  @PostMapping("/dimsr-application")
  @ApiOperation(value="Store the DIMSR application into eTrack and Upload to DART.")
  public DIMSRRequest storeDIMSRDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,  
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestHeader @ApiParam(example = "24337e61-71a2-4765-91fa-8f4dc3d59c09", 
          value="Unique Id of the DEP Staff assigned for this project") final String ppid, 
      @RequestBody final DIMSRRequest dimsrRequest) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into storeDIMSRDetails() method . User Id {}, Context Id {}", userId, contextId);
    return projectService.saveDIMSRDetails(userId, contextId, jwtToken, ppid, dimsrRequest);
  }
  
  /**
   * Store the reviewed Permits into eTrack. 
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param reviewedPermits - Reviewed Permits.
   * 
   * @return - Processed reviewed permits.
   */
  @PostMapping("/reviewed-permits")
  @ApiOperation(value="Store the reviewed permits.")
  public Object storeReviewedPermits(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody final Map<String, List<ReviewedPermit>> reviewedPermits) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into storeReviewedPermits() method . User Id {}, Context Id {}", userId, contextId);
    
    if (!(StringUtils.hasLength(userId) 
        && projectId != null && projectId > 0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    if (CollectionUtils.isEmpty(reviewedPermits)) {
      throw new BadRequestException("INVALID_REVIEW_PERMITS", "There is no reviewed Permits requested by the user to Persist", projectId);
    }
    return eTrackPermitService.storeReviewedPermits(userId, contextId, reviewedPermits);
  }

  /**
   * Upload the project details into Enterprise database and update the status in eTrack.
   * 
   * @param userId - User Id who initiates this request
   * @param token - JWT Token.
   * @param ppid - Unique GUID assigned to the logged in user by AD/ASMS.
   * @param projectId - Project Id.
   * @param dartUploadDetail - DART Upload details.
   */
  @PostMapping("/upload-to-dart")
  @ApiOperation(value="Upload the reviewed eTrack Project into DART Permit system.")
  public void uploadProjectDetailsToDART(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String token,
      @RequestHeader @ApiParam(example = "24337e61-71a2-4765-91fa-8f4dc3d59c09", 
      value="Unique Id of the DEP Staff assigned for this project") final String ppid, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody final DartUploadDetail dartUploadDetail) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into uploadProjectDetailsToDART(). User Id {}, Context Id {}", userId, contextId);
    
    if (!(StringUtils.hasLength(userId) 
        && projectId != null && projectId > 0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid value is passed", projectId);
    }
    
    if (!(dartUploadDetail != null && StringUtils.hasLength(dartUploadDetail.getReceivedDate())
        &&  !CollectionUtils.isEmpty(dartUploadDetail.getReviewedPermits()))) {
      throw new BadRequestException("INVALID_REVIEW_PERMITS", "There is no reviewed Permits "
          + "or received date requested by the user to Persist", projectId);
    }
    projectService.uploadProjectDetailsToEnterprise(userId, contextId, token, ppid, projectId, dartUploadDetail);
  }
}