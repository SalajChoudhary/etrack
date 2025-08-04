package dec.ny.gov.etrack.permit.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.PermitTaskStatus;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackProjectController {

  @Autowired
  private ProjectService projectService;
  private static final Logger logger = LoggerFactory.getLogger(ETrackProjectController.class.getName());
  private static final String INVALID_PROJECT_ID_PASSED = "INVALID_PROJECT_ID";
  
  /**
   * This end point is to persist project details (new Facility and Existing facility) to being the project.
   * @param userId - User id who initiates this request.
   * @param contextId - Unique UUID for the transaction.
   * @param projectDetail - Project details.
   * 
   * @return - Project details with newly created Project Id.
   */
  @PostMapping("/project")
  @ApiOperation(value="Store the Project/Facility details into eTrack as an initial process of the permit process.")
  public ProjectDetail saveProjectDetails(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @RequestBody ProjectDetail projectDetail) {
    
    if (contextId == null) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info("Entering into store Project details. User Id {} , Context Id{} ", userId,
        contextId);
    return projectService.saveProject(userId, contextId, projectDetail);
  }

  /**
   * Update the Facility details from the Step 1.
   * 
   * @param userId - User id who initiates this request.
   * @param contextId - Unique Id UUID for the transaction.
   * @param jwtToken - JWT Token.
   * @param projectDetail - Project details.
   * 
   * @return - updated Project Detail.
   */
  @PutMapping("/project")
  @ApiOperation(value="Update the Project/Facility details with the information received.")
  public ProjectDetail updateProjectDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken, @RequestBody ProjectDetail projectDetail) {
    
    if (contextId == null) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info("Entering into update the Project details. User Id {} , Context Id{} ", userId,
        contextId);
    if (projectDetail.getProjectId() == null || projectDetail.getProjectId() <= 0) {
      throw new BadRequestException("PROJECT_ID_NOT_PASSED", "Project Id is not passed", projectDetail);
    }
    return projectService.updateProject(userId, contextId, jwtToken, projectDetail);
  }
  

  /**
   * Associate the input Geographical Inquiry into the Project..
   * 
   * @param userId - User id who initiates this request.
   * @param contextId - Unique Id UUID for the transaction.
   * @param projectId - Project Id.
   * @param inquiryId - Inquiry Id to be associated with this project.
   */
  @PutMapping("/associate-inquiry")
  @ApiOperation(value="Associate the Input Associate the Geographical Inquiry to this input project.")
  public void associateGeographicalInquiryToProject(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "1321", value="Project Id") final Long projectId,
      @RequestHeader @ApiParam(example = "1325", value="Inquiry Id") final Long inquiryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into associateGeographicalInquiryToProject . User Id {} , Context Id{} ", userId,
        contextId);
        
    if (!(projectId != null && projectId > 0 && inquiryId != null && inquiryId > 0)) {
      throw new BadRequestException("PROJ_INQ_ID_NOT_PASSED", 
          "Project Id and Inquiry id is Required to associate. Either one of them is missing.", projectId + " " + inquiryId);
    }
    projectService.associateGeographicalInquiryToProject(userId, contextId, projectId, inquiryId);
    logger.info("Exiting from associateGeographicalInquiryToProject . User Id {} , Context Id{} ", userId,
        contextId);

  }

  /**
   * Assign Project Manager/Analyst for the input project to take further action on this.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * @param assignmentNote - Assignment note.
   */
  @PutMapping("/assign-project")
  @ApiOperation(value="Assigning the Project to DEP Analyst to review")
  public void assignProjectToAnalyst(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody AssignmentNote assignmentNote) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into assignProjectToAnalyst. User Id {} , Context Id{} ", userId,
        contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException("PROJECT_ID_NOT_PASSED", "Project Id is not passed", projectId);
    }
    
    if (StringUtils.hasText(assignmentNote.getAnalystName()) 
        && StringUtils.hasText(assignmentNote.getAnalystId())) {
      projectService.updateProjectAssignment(userId, contextId, projectId, assignmentNote);
    } else {
      throw new BadRequestException("NO_ASSIGNED_USER_PASSED", "User Assigned details cannot be empty ", assignmentNote);
    }
    logger.info("Existing from assignProjectToAnalyst. User Id {} , Context Id{} ", userId,
        contextId);
  }
  
  
  /**
   * Retrieve the Project/Facility details for the input Project id. 
   * 
   * @param userId - User id who initiates this request.
   * @param contextId - Unique UUID for the transaction.
   * @param projectId - Project Id.
   * 
   * @return - Facility details.
   */
  @GetMapping("/project")
  @ApiOperation(value="Retrieve the Project/Facility details.")
  public ProjectDetail getProjectDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    if (contextId == null) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info("Entering into retrieve Project details. User Id {} , Context Id {} ", userId,
        contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException("PROJECT_ID_NOT_PASSED", "Project Id is not passed", projectId);
    }
    return projectService.retrieveProjectDetail(userId, contextId, projectId);
  }

  /**
   * To retrieve the status of the current project to display in the main navigation page.
   * @param userId - User id who initiates this request.
   * @param projectId - Project Id for the status to be retrieved
   * @param mode - Data Entry 0 and Validate Mode -1
   * 
   * @return - Project activity details
   */
  @GetMapping({"/project/status/{projectId}", "/project/status/{projectId}/{mode}"})
  @ApiOperation(value="Retrieve the status of the Project to display in the Main Navigation")
  public List<PermitTaskStatus> getProjectPermitStatus(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @PathVariable @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable(required = false) @ApiParam(example="1", value="0- Data Entry Mode, 1- Validate Mode") final Integer mode) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getProjectPermitStatus Project Id {} User Id {}, Context Id {}",
        projectId, userId, contextId);
    return projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
  }

  /**
   * Delete the Unsubmitted project requested by the user.
   * 
   * @param userId - Requested User id.
   * @param authorization - JWT Token
   * @param projectId - Project Id
   */
  @DeleteMapping("/project")
  @ApiOperation(value="Delete the Unsubmitted Project requested by the user.")
  public void deleteProject(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into delete the project {} User Id: {} Context Id: {}", projectId, userId,
        contextId);
    if (StringUtils.isEmpty(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    projectService.deleteProject(userId, contextId, authorization, projectId);
    logger.info("Exiting from delete the project {} User Id: {} Context Id: {}", projectId, userId,
        contextId);
  }

  /**
   * This end point is used to reject the project requested for validation.
   * 
   * @param userId - User who initiates this request.
   * @param projectId -Project Id.
   * @param rejectedReason - Rejected Reason.
   */
  @PutMapping("/reject-project")
  @ApiOperation(value="Reject the project by the DEC Staff if the project cannot be proceed further."
      + "This Project will be deleted from DEC Staff and moved to Requested user.")
  public void rejectProjectByValidator(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final String rejectedReason) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into rejectProjectByValidator(). User Id {}, Context Id {}", userId, contextId);
    if (!(StringUtils.hasLength(userId) 
        && projectId != null && projectId > 0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid value is passed", projectId);
    }
    if (!StringUtils.hasLength(rejectedReason) || rejectedReason.length() > 300) {
      throw new BadRequestException("INVALID_REJECT_REASON", "Rejeect reason is not provided", projectId);
    }
    projectService.rejectProjectValidation(userId, contextId, projectId, rejectedReason);
    logger.info("Exiting from rejectProjectByValidator(). User Id {}, Context Id {}", userId, contextId);
  }
}
