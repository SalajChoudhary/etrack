package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.net.HttpHeaders;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.service.DartPermitService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class DartPermitServiceController {

  private static final Logger logger = LoggerFactory.getLogger(DartPermitServiceController.class.getName());
  
  @Autowired
  private DartPermitService dartPermitService;
  
  /**
   * This end point will returns the Contact/Agent assigned to the Permit Application forms.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * 
   * @return - Contact/Agents associated with this Permit Application forms.
   */
  @GetMapping("/permit-assignment")
  @ApiOperation(value="Retrieve the List of permits assigned applicants for those.")
  public Object retrievePermitAssignedApplications(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve permit Applications. User Id {}, Context Id {}", userId,
        contextId);
    if (!StringUtils.hasLength(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_OR_PROJECT_ID_MISSING", "User Id or Project Id is empty or blank", userId);
    }
    return dartPermitService.retrievePermitsAssignment(userId, contextId, projectId);
  }

  /**
   * This end point is to retrieve all the eligible permits which user can apply for.
   * 
   * @param userId - User who initiates this request
   * @param projectId - Project id to pull the associated permit info.
   * 
   * @return - List of eligible Permits for the user can apply for.
   */
  @GetMapping("/permit-selection")
  @ApiOperation(value="Retrieve all the Permits Active Authorizations, Pending Applications if any for the facility associated with this project id and "
      + "List of other permits which user can apply via eTrack.")
  public Object retrievePermitDetailsToApply(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Permit details to display for the selection. "
        + "User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_OR_PROJECT_ID_MISSING", "User Id or Project Id is empty or blank", userId);
    }
    return dartPermitService.retrieveAllPermitApplications(userId, contextId, projectId, true);
  }

  /**
   * This end point will return the summary of the permits applied so far.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * @param jwtToken - JWT Token
   * 
   * @return - Summary of the permits applied so far.
   */
  @GetMapping("/permit-summary")
  @ApiOperation(value="Retrieve the summary of permits (both new and existing permits) applied so far for this project.")
  public Object retrievePermitSummaryDetail(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Permit Summary details. User Id {}, Context Id {}", userId,
        contextId);
    if (!StringUtils.hasLength(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_OR_PROJECT_ID_MISSING", "User Id or Project Id is empty or blank", userId);
    }
    return dartPermitService.retrieveAllPermitSummary(userId, contextId, jwtToken, projectId);
  }

  /**
   * This end point helps to retrieve the modification summary details for the project id.
   * 
   * @param userId - User who initiates this request
   * @param projectId - Project id to pull the associated permit info.
   * 
   * @return - Applied Permit(s) modification summary.
   */
  @GetMapping("/permit-mod-summary")
  @ApiOperation(value="Retrieves the Summary of the Modification eligible permits.")
  public Object retrievePermitModificationSummaryDetail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Permit Modification Summary details. User Id {}, Context Id {}", userId,
        contextId);
    if (StringUtils.hasLength(userId) || projectId == null || projectId <= 0) {
      throw new BadRequestException("USER_OR_PROJECT_ID_MISSING", "User Id or Project Id is empty or blank", userId);
    }
    return dartPermitService.retrievePermitModificationSummary(userId, contextId, projectId);
  }

  /**
   * This method is used to retrieve the list of eligible permits which can be added for the project as an additional permit.
   * @param projectId - Project Id
   * @param userId - User who initiates this request. 
   * 
   * @return - Eligible Permit details.
   */
  @GetMapping("/available-permits-to-add")
  @ApiOperation(value="Show all the list of permits not applied so far which user can apply as an additional permit from Virtual Workspace.")
  public Object retrieveAvailablePermitsAddAdditional(@RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveAvailablePermitsAddAdditional. "
        + "User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)|| !(projectId != null && projectId > 0)) {
      throw new BadRequestException("MANDATORY_PARAM_MISSING", 
          "One or more mandatory parameter is missing to retrieve available permits. " + projectId , userId);
    }
    return dartPermitService.retrieveAvailablePermitsAddAsAdditional(userId, contextId, projectId);
  }
  
}
