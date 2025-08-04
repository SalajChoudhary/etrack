package dec.ny.gov.etrack.gis.controller;

import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.model.ProjectDetail;
import dec.ny.gov.etrack.gis.service.GISFacilityService;
import dec.ny.gov.etrack.gis.util.Validator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class GISFacilityController {

  @Autowired
  private GISFacilityService gisFacilityService;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(GISFacilityController.class.getName());
  
  /**
   * Persist the requested Facility details by the user.
   * 
   * @param userId - user who initiates this request.
   * @param jwtToken - JWT Token.
   * @param projectDetail - Project/Facility details.
   * 
   * @return - Updated Project details includes newly created project id.
   */
  @PostMapping("/facility")
  @ApiOperation(value="Save the Facility details into eTrack.")
  public ProjectDetail saveFacilityDetail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestBody ProjectDetail projectDetail) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into saveFacilityDetail. User Id {} Context Id {}", userId, contextId);
    Validator.isProjectValid(userId, contextId, projectDetail);
    return gisFacilityService.saveFacilityDetail(userId, contextId, jwtToken, projectDetail);
  }

  /**
   * Update the amended Facility details by the user.
   * 
   * @param userId - user who initiates this request.
   * @param jwtToken - JWT Token.
   * @param projectDetail - Project/Facility details.
   * 
   * @return - Updated Project details.
   */
  @PutMapping("/facility")
  @ApiOperation(value="Update the Facility details into eTrack requested by the user/staff during data entry or review process.")
  public ProjectDetail updateFacilityDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestBody ProjectDetail projectDetail) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into update Facility details. User Id {} Context Id {}", userId,
        contextId);
    Validator.isProjectValid(userId, contextId, projectDetail);
    if (projectDetail.getProjectId() == null || projectDetail.getProjectId() <= 0) {
      throw new BadRequestException("PROJECT_ID_EMPTY", "Project Id cannot be empty",
          projectDetail);
    }
    return gisFacilityService.updateFacilityDetail(userId, contextId, jwtToken, projectDetail);
  }

  /**
   * Retrieve the Project/Facility details for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param jwtToken - JWT Token.
   * @param projectId - Project Id.
   * 
   * @return - Project details.
   */
  @GetMapping("/facility")
  @ApiOperation(value="Retrieve the facility details for the input project to display in the step 1.")
  public ResponseEntity<ProjectDetail> getFacilityDetail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getFacilityDetail. User Id {} Context Id {}", userId, contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException("PROJECT_ID_EMPTY", "Project Id cannot be empty", projectId);
    }
    return gisFacilityService.retrieveFacilityInfo(userId, contextId, jwtToken, projectId);
  }
  
  /**
   * Retrieve the history of the Facility details i.e Data from enterprise and changes made during
   * the submission..
   * 
   * @param response - {@link HttpServletResponse}
   * @param jwtToken - JWT Token.
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - History of the facility details.
   */
  @GetMapping("/facility/view")
  @ApiOperation(value="Retrieve the initial details stored in the enterprise which helps the "
      + "reviewer to compare if any changes made by the user while submitting the project.")
  public ResponseEntity<JsonNode> retrieveFacilityHistory(HttpServletResponse response,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into retrieveFacilityHistory User Id {} Context Id: {} ", userId,
        contextId);
    return gisFacilityService.retrieveFacilityHistory(userId, contextId, jwtToken, projectId);
  }
}
