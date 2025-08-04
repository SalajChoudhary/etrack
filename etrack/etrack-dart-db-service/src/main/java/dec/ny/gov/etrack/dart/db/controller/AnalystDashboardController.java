package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.service.DashboardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/analyst")
public class AnalystDashboardController {

  private static Logger logger = LoggerFactory.getLogger(AnalystDashboardController.class.getName());
  
  @Autowired
  private DashboardService dashboardService;
 
  
  /**
   * Retrieve all the un-submitted projects.
   * @param userId - User who initiates this request.
   * 
   * @return - List of Resume entry applications.
   */
  @GetMapping("/user/resume-entry")
  @ApiOperation(value="Retrieve all the Projects submitted to display in the dashboard assigned for the input user.")
  public Object getResumeEntryProjects(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getResumeEntryProjects User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getResumeEntryPrjects(userId, contextId);
  }
  
  /**
   * Retrieve all the Pending Applications.
   * @param userId - User who initiates this request.
   *  
   * @return - List of pending applications.
   */
  @GetMapping("/user/all-active")
  @ApiOperation(value="Retrieve all active authorizations applications to display in the dashboard assigned for the input user.")
  public Object getAllActiveApplications(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getAllActiveApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getAllActiveProjects(userId, contextId);
  }

  /**
   * retrieve all the validation/review eligible applications. 
   * i.e. All the submitted projects should be reviewed before it gets uploaded into DART.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of validation applications.
   */
  @GetMapping("/user/validate")
  @ApiOperation(value="Retrieve all the projects review by the staff and not uploaded to DART to process.")
  public Object getAllValidateProjects(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getAllValidateProjects User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getValidateEligibleProjects(userId, contextId);
  }
  
  /**
   * Retrieve all the applications which are in Tasks Due status.
   * @param userId - User who initiates this request.
   * 
   * @return - List of Tasks Due applications.
   */
  @GetMapping("/user/tasks-due")
  @ApiOperation(value="Retrieve all the list of applications are in tasks due status assigned for the input user from enterprise.")
  public Object getTasksDueApplications(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getTasksDueApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getTasksDueApplications(userId, contextId);
  }
  
  /**
   * Retrieve all the applications which are  in Applicant Response Due status.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of Applicant Response Due applications.
   */
  @GetMapping("/user/aplct-response-due")
  @ApiOperation(value="Retrieve all the list of applications in applicants response due status assigned for the input user from enterprise.")
  public Object getApplicantResponseDueApplications(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getApplicantResponseDueApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getApplicantResponseDueApplications(userId, contextId);
  }
  
  /**
   * Retrieve all the applications which are in Suspended status.
   * @param userId - User who initiates this request.
   * 
   * @return - List of Suspended applications
   */
  @GetMapping("/user/suspended-apps")
  @ApiOperation(value="Retrieve all the list of applications which are suspended status assigned for the input user in enterprise.")
  public Object getSuspendedApplications(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getSuspendedApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getSuspendedApplications(userId, contextId);
  }
  
  /**
   * Retrieve all the applications which are Out for review status.
   * @param userId - User who initiates this request.
   * 
   * @return - List of Out for review applications.
   */
  @GetMapping("/user/out-for-review")
  @ApiOperation(value="Retrieve all the list of applications which are Out for review status assigned for the input user in enterprise.")
  public Object getOutForReviewApplications(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getOutForReviewApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getOutForReviewApplications(userId, contextId);
  }
  
  /**
   * Retrieve all the Emergency Authorization applications.
   * @param userId - User who initiates this request.
   * 
   * @return - List of Emergency Authorization applications.
   */
  @GetMapping("/user/emergency-apps")
  @ApiOperation(value="Retrieve all the list of Emergency applications assigned for the input user from enterprise .")
  public Object getEmergencyAuthorizationApplications(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getEmergencyAuthorizationApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getEmergencyAuthorizationApplications(userId, contextId);
  }

  /**
   * Retrieve all the Pending applications from all the regions  or requested facility region, 
   *  not limited with the logged in Staff associated applications.
   * 
   * @param userId - User who initiates this request.
   * @param facilityRegionId - Facility Region id.
   * 
   * @return - List of all the Pending applications.
   */
  @GetMapping(value={"/regional/active-apps", "/regional/active-apps/{facilityRegionId}"})
  @ApiOperation(value="Retrieve all the list of Active Authorizations applications are in tasks due status in enterprise.")
  public Object getRegionalAllActiveApplications(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Facility region id") final Integer facilityRegionId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getRegionalAllActiveApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getRegionalAllActiveApplications(userId, contextId, facilityRegionId);
  }

  /**
   * Retrieve all the validation eligible applications from all the .
   * 
   * @param userId - User who initiates this request.
   * @param facilityRegionId - Facility Region id.
   * 
   * @return - List of regional active applications
   */
  @GetMapping(value={"/regional/unvalidated-apps", "/regional/unvalidated-apps/{facilityRegionId}"})
  @ApiOperation(value="Retrieve all the list of projects currently under review by the staff in eTrack.")
  public Object getRegionalUnvalidatedApplications(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Facility region id") final Integer facilityRegionId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getRegionalUnvalidatedApplications User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getRegionalUnvalidatedApplications(userId, contextId, facilityRegionId);
  }

  /**
   * Retrieve all the list of applications requested for the PAR staff to be reviewed.
   * @param userId - User who initiates this request.
   * @param facilityRegionId - Facility Region id.
   * 
   * @return - List of applications assigned for the PAR staff. 
   */
  @GetMapping(value={"/regional/program-review-apps", "/regional/program-review-apps/{facilityRegionId}"})
  @ApiOperation(value="Retrieve all the list of program review eligible applications.")
  public Object getRegionalProgramReviewApplications(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Facility region id") final Integer facilityRegionId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getRegionalProgramReviewApplications. User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getRegionalProgramReviewApplications(userId, contextId, facilityRegionId);
  }
  
  /**
   * Retrieve all the applications which are in disposed status in all the regions or the requested region id.
   * @param userId - User who initiates this request.
   * @param facilityRegionId - Facility Region id.
   * 
   * @return - List of regional Disposed applications.
   */
  @GetMapping(value={"/regional/disposed-apps", "/regional/disposed-apps/{facilityRegionId}"})
  @ApiOperation(value="Retrieve all the list of Disposed applications from enterprise.")
  public Object getRegionalDisposedApplications(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Facility region id") final Integer facilityRegionId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getRegionalDisposedApplications. User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return dashboardService.getRegionalDisposedApplications(userId, contextId, facilityRegionId);
  }
  
}
