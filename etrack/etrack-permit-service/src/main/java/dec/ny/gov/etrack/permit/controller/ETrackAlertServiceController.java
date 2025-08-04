package dec.ny.gov.etrack.permit.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.service.ETrackAlertService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackAlertServiceController {

  private static final Logger logger = LoggerFactory.getLogger(ETrackAlertServiceController.class.getName());
  
  @Autowired
  private ETrackAlertService eTrackAlertService;
  
  /**
   * Delete the requested alert id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param alertId - Alert id.
   */
  @DeleteMapping("/delete-alert/{alertId}")
  @ApiOperation(value="Delete the Alert requested by the user.")
  public void deleteAlert(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(required = false) @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestHeader(required = false) @ApiParam(example = "123213", value="Geographical Inquiry Id") final Long inquiryId,
      @PathVariable @ApiParam(example = "234234", value="Alert Id") final Long alertId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into deleteAlert for the project Id {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
    
    if (projectId == null && inquiryId == null) {
      throw new BadRequestException("PROJ_INQUIRY_NOT_PASSED", 
          "Either Project Id or Inquiry id is not passed", projectId + " "+inquiryId);
    }
    
    eTrackAlertService.deleteAlertMessage(userId, contextId, projectId, inquiryId, alertId);
    logger.info(
        "Exiting from deleteAlert for the project Id {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
  }

  /**
   * Mart the alert as read by the user.
   * 
   * @param userId - User who initiates this request.
   * @param projectId- Project Id.
   * @param alertId - Alert Id.
   */
  @PutMapping("/alert-read/{alertId}")
  @ApiOperation(value="Mark the input alert as read. so, it won't be displayed the Alert icon.")
  public void updateAlertAsRead(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "234234", value="Alert Id") final Long alertId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into updateAlertAsRead for the project Id {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
    
    eTrackAlertService.updateAlertMessageAsRead(userId, contextId, projectId, alertId);
    logger.info(
        "Entering into updateAlertAsRead for the project Id {} User Id: {} Context Id: {}",
        projectId, userId, contextId);
  }

}
