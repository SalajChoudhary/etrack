package dec.ny.gov.etrack.dart.db.controller;

import static dec.ny.gov.etrack.dart.db.util.Message.INVALID_REQ;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.net.HttpHeaders;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.BridgeIdNumber;
import dec.ny.gov.etrack.dart.db.model.EmailContent;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.model.ProjectRejectDetail;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.EmailService;
import dec.ny.gov.etrack.dart.db.service.OnlineUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class DartDbServiceController {

  @Autowired
  private DartDbService dartDbService;

  @Autowired
  private OnlineUserService onlineUserService;

  @Autowired
  private EmailService emailService;

  private static final Logger logger =
      LoggerFactory.getLogger(DartDbServiceController.class.getName());

  @Value("${spring.env.profile.test}")
  private String environment;
  private static final String MANDATORY_PARAM_MISSING = "MANDATORY_PARAM_MISSING";
  private static final String USER_ID_MISSING = "USER_ID_NOT_PASSED";
  private static final String USER_ID_MISSING_ERR = "User Id is empty or blank";
  private static final String PROJECT_ID_MISSING = "PROJECT_ID_NOT_PASSED";

  /**
   * This end point is used to get the project details for the input project id.
   * 
   * @param userId - User's Unique Id who initiates this request.
   * @param projectId - Project id
   * 
   * @return - Project informations {@link ProjectInfo}
   */
  @GetMapping("/projectInfo")
  @ApiOperation(
      value = "Retrieve the Project details like description other details captured in the Step 3 in the permit process.")
  public ProjectInfo getProjectInfo(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the Project information details. User Id {} Context ID : {}", userId,
        contextId);
    if (projectId == null) {
      throw new BadRequestException(PROJECT_ID_MISSING,
          "There is no Project id is passed to retrieve the Project Details ", contextId);
    }
    return dartDbService.getProjectInformation(userId, contextId, projectId);
  }

  /**
   * This end point is use to return all the Facility's BIN numbers associated with this Project
   * Id..
   * 
   * @param userId - User's Unique Id who initiates this request.
   * @param projectId - Project Id
   * 
   * @return - Returns the Bridge Id Number(s) {@link List of BridgeIdNumber}
   */
  @GetMapping("/bridgeIds")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Retrieve the existing BIN numbers associated with the facility for the input project id.")
  public List<BridgeIdNumber> getFacilityBINs(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the Facility Bridge ID numbers User Id {} Context Id : {}", userId,
        contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(projectId)) {
      throw new BadRequestException(PROJECT_ID_MISSING,
          "There is no Project id is passed to get Facility BIN details", projectId);
    }
    return dartDbService.getFacilityBins(userId, contextId, projectId);
  }

  /**
   * This end point will return the list of applications created by the user and yet to submitted.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of pending applications.
   */
  @GetMapping("/pending-applications")
  @ApiOperation(value = "Retrieve all the applications created and not submitted to DEC to review.")
  public Object getPendingApplications(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getPendingApplications User Id {}, Context Id {}", userId,
        contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return dartDbService.getUnsubmittedApps(userId, contextId);
  }

  /**
   * This end point is used to retrieve all the project related details like 1. List of pending
   * projects 2. List of Projects pending with Validations 3. List of Projects active 4. List of
   * Projects has tasks Due 5. List of Projects has Applicant Response Due.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of pending applications
   */
  @GetMapping("/user-dashboard")
  @ApiOperation(
      value = "Retrieve the Unsubmitted applications and display in the user dashboard when the user logged into eTrack Portal.")
  public Object getUserDashboard(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getUserDashboard User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return dartDbService.getUserDashboardDetails(userId, contextId);
  }

  /**
   * This end point is to retrieve the details for all the regions or the region id passed, for the
   * Program Area Reviewer.
   * 
   * @param userId - User Id who initiates this request.
   * @param facilityRegionId - Region Id
   * 
   * @return - Program Area Reviewer Dashboard results.
   */
  @GetMapping(value = {"/reviewer-dashboard", "/reviewer-dashboard/{facilityRegionId}"},
      produces = "application/json")
  @ApiOperation(
      value = "Retrive all the Program reviewer details for logged in User and for the facility region id.")
  public Object getProgramReviewerDashboardDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @PathVariable(required = false) @ApiParam(example = "1",
          value = "Facility region id") final Integer facilityRegionId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getProgramReviewerDashboardDetails User Id {}, Context Id {}",
        userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return dartDbService.getProgramReviewerDashboardDetails(userId, contextId, facilityRegionId);
  }

  /**
   * This end point is to retrieve all the DEC Staff/Managers for the input region id.
   * 
   * @param userId - User who initiates this request.
   * @param regionId - Region Id
   * 
   * @return - List of matched analysts for the input parameter.
   */
  @GetMapping(value = {"/analysts/{regionId}", "/analysts"}, produces = "application/json")
  @ApiOperation(value = "Retrieve all the DEC Staff/Managers associated with the input region id.")
  public Object getAnalystsAssociatedWithRegion(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @PathVariable(required = false) @ApiParam(example = "1",
          value = "region id") final Integer regionId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getAnalystsAssociatedWithRegion User Id {}, Context Id {}", userId,
        contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return dartDbService.getUsersByRegionAndRoleTypeId(userId, contextId, regionId, 50);
  }

  /**
   * This end point will return the list of DEC Staff/Program Area Reviewer who has valid email
   * address in DART/eFind system.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of DEC Staff/Program Area Reviewer
   */
  @GetMapping(value = "/email/users/list", produces = "application/json")
  @ApiOperation(
      value = "Returns all the DEC Staff, Managers and Reviewer who has valid user id and email address in enterprise.")
  public Object getUsersWithValidEmailAddress(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getUsersWithValidEmailAddress User Id {}, Context Id {}", userId,
        contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return dartDbService.getUsersWithValidEmailAddress(userId, contextId);
  }

  /**
   * This end point will return all the active Program Staffs or Program Staff for the input region id.
   * 
   * @param userId - User who initiates this request.
   * @param regionId - Region Id.
   * 
   * @return - Program Area Reviewer
   */
  @GetMapping(value = {"/staff/{regionId}", "/staff"}, produces = "application/json")
  @ApiOperation(
      value = "Retrieves the Program Area Reviewers for the input region id if passed or from all the regions.")
  public Object getProgramAreaReviewersAssociatedWithRegion(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @PathVariable(required = false) @ApiParam(example = "1",
          value = "DEC Region id") final Integer regionId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into getProgramAreaReviewersAssociatedWithRegion. User Id {}, Context Id {}",
        userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return dartDbService.getUsersByRegionAndRoleTypeId(userId, contextId, regionId, 55);
  }



  /**
   * This end point will return the summary of the list of support documents uploaded by the user
   * for this project id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * 
   * @return - Summary of the list of supported document(s).
   */
  @GetMapping("/support-doc-summary")
  @ApiOperation(
      value = "Retrieves the list of all the support documents uploaded for this project.")
  public Object retrieveSupportDocuments(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Supporting documents Summary. User Id {}, Context Id {}",
        userId, contextId);
    if (!StringUtils.hasLength(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_OR_PROJECT_ID_MISSING",
          "User Id or Project Id is empty or blank", userId);
    }
    return dartDbService.retrieveSupportDocumentSummary(userId, contextId, projectId);
  }


  /**
   * This end point will return the list of required Signed applicants for this project.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * 
   * @return - Required Applicants to be signed
   */
  @GetMapping("/reqd-signed-applicants")
  @ApiOperation(
      value = "Retrieves the list of applicants which are eligible to sign the applications/projets")
  public Object retrieveRequiredApplicantsToSign(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Supporting documents Summary. User Id {}, Context Id {}",
        userId, contextId);
    if (StringUtils.isEmpty(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_ID_PROJECT_ID_EMPTY",
          "User Id and Project Id is Required.", projectId);
    }
    return dartDbService.retrieveRequiredApplicantsToSign(userId, contextId, projectId);
  }

  /**
   * This end point returns all the details will be displayed in the Virtual workspace screen.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param token - JWT Token
   * 
   * @return - All the required details for the Virtual Workspace.
   */
  @GetMapping("/virtual-workspace")
  @ApiOperation(value = "Retrieve the summary of the projects like Facility, Publics, Permits, "
      + "documents, invoices, notes etc.., asspciated with this project.")
  public Object retrieveProjectSummary(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveProjectSummary. User Id {}, Context Id {}", userId,
        contextId);
    if (StringUtils.isEmpty(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_ID_PROJECT_ID_EMPTY",
          "User Id and Project Id is Required.", projectId);
    }
    return dartDbService.retrieveProjectSummary(userId, contextId, token, projectId);
  }


  /**
   * This end point will return the assignment details for this user.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * 
   * @return - Assignment details
   */
  @GetMapping("/assignment")
  @ApiOperation(value = "Retrieve the Project assignment details.")
  public Object retrieveAssignmentDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveAssignmentDetails. User Id {}, Context Id {}", userId,
        contextId);
    return dartDbService.retrieveAssignmentDetails(userId, contextId, projectId);
  }

  /**
   * Retrieve count of new alerts for the logged in user.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of Alerts if anything for this user.
   */
  @GetMapping("/analyst/alerts")
  @ApiOperation(value = "Retrieve all the unread alerts associated with the logged in user.")
  public Object retrieveAnalystDashboardAlerts(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveAnalystDashboardAlerts. User Id {}, Context Id {}", userId,
        contextId);
    return dartDbService.retrieveAnalystsAlerts(userId, contextId);
  }

  /**
   * This end point will return the details of the alerts assigned to this user.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of Alert details.
   */
  @GetMapping("/analysts/view-alerts")
  @ApiOperation(value = "Retrieve the content of the alerts the user wants to see.")
  public Object viewAnalystDashboardAlerts(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into viewAnalystDashboardAlerts. User Id {}, Context Id {}", userId,
        contextId);
    return dartDbService.viewAnalystDashboardAlerts(userId, contextId);
  }

  /**
   * This end point is used to retrieve all the review eligible documents.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - List of review eligible documents.
   */
  @GetMapping("/review-documents")
  @ApiOperation(value = "Retrieve all the documents eligible to review.")
  public Object retrieveEligibleReviewDocuments(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveEligibleReviewDocuments. User Id {}, Context Id {}", userId,
        contextId);
    return dartDbService.retrieveEligibleReviewDocuments(userId, contextId, projectId);
  }

  /**
   * Retrieve the region id for the input userId.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - Region Id of the input user id associated with.
   */
  @GetMapping("/regionId")
  @ApiOperation(value = "Retrieve the region details for the input user id.")
  public Long retrieveRegionIdByUserId(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveRegionIdByUserId. User Id {}, Context Id {}", userId,
        contextId);
    return dartDbService.findRegionIdByUserId(userId, contextId);
  }

  /**
   * This end point will return the email correspondence to display in the dashboard envelop icon.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - Email Correspondence details.
   */
  @GetMapping("/email/dashboard")
  @ApiOperation(
      value = "Retrieve all the email notification(s)/correspondnece which is sent/received for the input user.")
  public Object retrieveEmailNotificationsIntoDashboard(@RequestHeader @ApiParam(
      example = "shortname", value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieveEmailNotificationsIntoDashboard for the. User Id {} , Context Id {}",
        userId, contextId);
    return emailService.retrieveEmailNotificationDetails(userId, contextId);
  }

  /**
   * Return the email correspondence associated with the input Project Id to display in the Virtual
   * Workspace envelop icon.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - List of Send and Received email Correspondence for the input Project id and User id.
   */
  @GetMapping("/email/virtual-workspace")
  @ApiOperation(value = "Retrieve the correpondence details for the input project id.")
  public Object retrieveEmailNotificationsIntoVirtualDesktop(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieveEmailNotificationsIntoVirtualDesktop for the. User Id {} , Context Id {}",
        userId, contextId);
    return emailService.retrieveEmailNotificationsInVirtualDesktop(userId, contextId, projectId);
  }

  /**
   * This end point is used to return the correspondence between the sender and receiver id for the
   * input project id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param emailSenderId - Email Sender id.
   * @param emailReceiverId - Email Receiver Id.
   * @param correspondenceType - Correspondence Type.
   * 
   * @return - Email Correspondence details.
   */
  @GetMapping("/email/virtual-workspace/{correspondenceType}")
  @ApiOperation(value = "Retrieve all the correspondences based on the correspondence type. "
      + "R-List of correspondences received. S- List of correspondences sent by this user.")
  public Object retrieveEmailCorrespondenceDetailsForRequestor(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @RequestHeader @ApiParam(example = "senderemailid",
          value = "Email Sender email id") final String emailSenderId,
      @RequestHeader @ApiParam(example = "emailreceiverid",
          value = "Email Receiver email id") final String emailReceiverId,
      @PathVariable @ApiParam(example = "R",
          value = "R-Received/S-Sent") final String correspondenceType) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieveEmailCorrespondenceDetailsForRequestor for the. User Id {} , Context Id {}",
        userId, contextId);
    if (!(projectId != null && projectId > 0)) {
      logger.error("Project Id is empty or invalid. User Id {}, Context Id {}", userId, contextId);
      throw new BadRequestException("INVALID_PROJ_ERR", "Project Id is empty or invalid",
          projectId);
    }
    if (!(StringUtils.hasLength(correspondenceType)
        && ("R".equals(correspondenceType) || "S".equals(correspondenceType)))) {
      logger.error("Invalid Correspondence type is not valid. User Id {}, Context Id {}", userId,
          contextId);
      throw new BadRequestException("INVALD_CORRESP_ERR",
          "Invalid Correspondence type is not valid", projectId);
    }
    return emailService.retrieveCorrespondencesForTheRequestor(userId, contextId, projectId,
        emailSenderId, emailReceiverId, correspondenceType);
  }

  /**
   * This end point is used to return correspondence(s) for the particular email
   * thread(correspondence Id).
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param correspondenceId - Email correspondence Id.
   * 
   * @return - Email Correspondences details.
   */
  @GetMapping({"/user/envelops/{correspondenceId}"})
  @ApiOperation(value = "Retrieve the correspondences for the input correspondence id/topic id.")
  public EmailContent retrieveEnvelopsDetailByCorrespondenceId(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "76234",
          value = "Email correspondence Id") final Long correspondenceId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieve the envelop communication details. User Id {} , Context Id {}",
        userId, contextId);
    return emailService.retrieveEmailCorrespondenceByCorrespondenceId(userId, contextId, projectId,
        correspondenceId, true);
  }

  /**
   * This method is used to retrieve the email correspondences for the assigned document to the
   * reviewer.
   * 
   * @param userId - User who initiates the request
   * @param reviewerId - Unique id for the reviewer name
   * @param projectId - Project id
   * @param documentId - Document id for the assigned
   * 
   * @return - Email Correspondence for the particular document Id requested for review and
   *         completes now.
   */
  @GetMapping({"/user/correspondence", "/user/correspondence/{documentId}"})
  @ApiOperation(
      value = "Retrieve all the email correspondences or correspondence associated with the document id.")
  public List<List<String>> retrieveCorrespondenceDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader final String reviewerId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @PathVariable(required = false) @ApiParam(example = "298432",
          value = "Document Id") final Long documentId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieve the Correspondence details for the assigned document. User Id {} , Context Id {}",
        userId, contextId);
    return emailService.retrieveEmailCorrespondenceByDocumentId(userId, contextId, reviewerId,
        projectId, documentId);
  }

  /**
   * This end point returns the DIMSR eligible permits for the input DEC ID/Facility Id.
   * 
   * @param userId - User who initiates this request.
   * @param decId - DEC ID.
   * 
   * @return - DIMSR Permit details.
   */
  @GetMapping({"/dmisr-detail/{decId}"})
  @ApiOperation(
      value = "Rerieve the DIMSR eligible permits for the input DEC ID which can help the user to apply as DIMSR application.")
  public Object retrieveDetailForDIMSR(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @PathVariable @ApiParam(example = "000-00-0000", value = "DEC ID") final String decId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieve the details for the DIMSR process. User Id {} , Context Id {}",
        userId, contextId);
    return dartDbService.retrieveSupportDetailsForDIMSR(userId, contextId, decId);
  }

  /**
   * This end point is used to retrieve all the project related details like 1. List of pending
   * projects 2. List of Projects pending with Validations 3. List of Projects active 4. List of
   * Projects has tasks Due 5. List of Projects has Applicant Response Due.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of pending applications
   */
  @GetMapping("/online-dashboard")
  @ApiOperation(value = "Retrieve all the Unsubmitted applications for the input online user.")
  public Object getOnlineUserDashboard(@RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getOnlineUserDashboard User Id {}, Context Id {}", userId,
        contextId);
    if (StringUtils.isEmpty(userId)) {
      throw new BadRequestException(USER_ID_MISSING, USER_ID_MISSING_ERR, userId);
    }
    return onlineUserService.getOnlineUserDashboardDetails(userId, contextId);
  }

  /**
   * This end point is used to retrieve all the Active Authorizations Permit for the input
   * Enterprise District Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique transaction id to track this request.
   * @param projectId - Project Id
   * @param edbDistrictId - Enterprise District Id.
   * 
   * @return - Active Authorization permits.
   */
  @GetMapping("/active-authorization-permit/{edbDistrictId}")
  @ApiOperation(
      value = "Retrieve all the Active Authorizations permits from the enterprise system.")
  public Object retrieveActiveAuthorizationPermits(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader final UUID contextId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "121012",
          value = "Enterprise district Id") final Long edbDistrictId) {

    logger.info("Entering into retrieveActiveAuthorizationPermit User Id {}, Context Id {}", userId,
        contextId);
    if (StringUtils.isEmpty(userId)) {
      throw new BadRequestException(INVALID_REQ, "Invalid/Blank value is passed in the header",
          userId);
    }
    return dartDbService.retrieveActiveAuthorizationPermits(userId, contextId.toString(), projectId,
        edbDistrictId);
  }

  /**
   * This end point will return the correspondence for the input document Ids and reviewer id.
   * 
   * @param projectId - Project Id.
   * @param userId - User who initiates this request.
   * @param reviewerId - Reviewer id.
   * @param documentIds - List of document id.
   * 
   * @return - Email correspondence associated with the requested review document(s).
   */
  @GetMapping("/reviewer-correspondence/{documentIds}")
  @ApiOperation(value = "Retrieve all the correspondence for the input reviewer id.")
  public Object retrieveCorrespondencesByReviewerId(
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "98324", value = "Reviewer Id") final String reviewerId,
      @PathVariable @ApiParam(example = "123213, 234324",
          value = "List of document Ids") final List<Long> documentIds) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveCorrespondencesByReviewerId. " + "User Id {}, Context Id {}",
        userId, contextId);
    if (!StringUtils.hasLength(userId) || !StringUtils.hasLength(reviewerId)
        || !(projectId != null && projectId > 0) || CollectionUtils.isEmpty(documentIds)) {
      throw new BadRequestException(MANDATORY_PARAM_MISSING,
          "One or more mandatory parameter is missing.", userId);
    }
    return emailService.retrieveCorrespondenceByReviewerAndDocumentIds(userId, contextId,
        reviewerId, projectId, documentIds);
  }


  /**
   * Generate the agreement and persist if it not, else return the form management details.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - Form Management details.
   */
  @GetMapping("/reject-detail")
  @ApiOperation(
      value = "Retrieves the project reject reason details if the input project is rejected.")
  public ProjectRejectDetail retrieveProjectRejectionDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve the project rejection details. User Id {}, Context Id {}",
        userId, contextId);
    if (!StringUtils.hasLength(userId) || !(projectId != null && projectId > 0)) {
      throw new BadRequestException(MANDATORY_PARAM_MISSING,
          "One or more mandatory parameter is missing to retrieve Project rejection details."
              + projectId,
          userId);
    }
    return dartDbService.retrieveProjectRejectionDetails(userId, contextId, projectId);
  }
}
