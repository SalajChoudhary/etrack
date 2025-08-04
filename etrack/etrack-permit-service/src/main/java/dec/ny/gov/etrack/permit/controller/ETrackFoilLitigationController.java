package dec.ny.gov.etrack.permit.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.model.FoilRequest;
import dec.ny.gov.etrack.permit.model.LitigationRequest;
import dec.ny.gov.etrack.permit.service.ETrackFoilLigitationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackFoilLitigationController {

  @Autowired
  private ETrackFoilLigitationService eTrackFoilLigitationService;
  private static final Logger logger = LoggerFactory.getLogger(ETrackApplicantController.class.getName());
  
  /**
   * Add/Update the Litigation details for the input project id.
   *  
   * @param userId - User who initiates this request.
   * @param projectId - Project Id 
   * @param litigationRequest - Litigation Request data {@link LitigationRequest} 
   * 
   * @return - return list of Litigation details and hitory mapping.
   */
  @PostMapping("/add-litigation")
  @ApiOperation(value="Store the Litigation details to the input project.")
  public Map<String, Object> addLitigationRequest(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,  
      @RequestBody final LitigationRequest litigationRequest) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into addLitigationRequest method . User Id {}, Context Id {}", userId, contextId);
    return eTrackFoilLigitationService.saveOrUpdateLitigationRequest(userId, contextId, projectId, litigationRequest);
  }
  
  /**
   * This method is used to update the changes made in the Project's Virtual workspace 
   * details like FOIL, Litigation etc.,
   *  
   * @param userId - User who initiates this request.
   * @param projectId - Project Id 
   * @param foilRequest - Foil Request data.
   * 
   * @return - List of Foil Request numbers.
   */
  @PostMapping("/add-foil")
  @ApiOperation(value="Store the FOIL details to the input project.")
  public List<String> addFoilRequestDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,  
      @RequestBody final FoilRequest foilRequest) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into addFoilRequestDetails method . User Id {}, Context Id {}", userId, contextId);
    return eTrackFoilLigitationService.saveOrUpdateFoilRequest(userId, contextId, projectId, foilRequest);
  }

}
