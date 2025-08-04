package dec.ny.gov.etrack.gis.controller;

import java.util.Arrays;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;
import dec.ny.gov.etrack.gis.model.PolygonAction;
import dec.ny.gov.etrack.gis.service.SpatialInquiryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class SpatialInquiryController {
  
  @Autowired
  private SpatialInquiryService spatialInquiryService;
  
  private static Logger LOGGER = LoggerFactory.getLogger(SpatialInquiryController.class.getName());
  private static final String MANTORY_VAL_MISSING_LOG_INFO = "Mandatory value is missing {} ";

  /**
   * Save/Amend the Spatial Polygon details.
   * 
   * @param featureMap - Feature map details.
   * @param value - pjson value.
   * @param action - Save/Update action.
   * 
   * @return - Store the Spatial Inquiry details.
   */
  @PostMapping(value = "/spatial-polygon/{action}", produces = "application/json")
  @ApiOperation(value="Save/Update the Applicant/User Spatial Inquiry Applicant Polygon into eTrack.")
  public Object saveSpatialInquiryApplicantPolygon(
      final @RequestPart(value = "features") Object featureMap,
      final @RequestPart(value = "f") String value, 
      @PathVariable @ApiParam(example = "S", value="S-Save, U-Update") final PolygonAction action) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into saveSpatialInquiryApplicantPolygon Context Id: {} ", contextId);
    return spatialInquiryService.spatialInquiryApplicantPolygon(Arrays.asList(featureMap), value, action.name(),
        contextId);
  }

  /**
   * Retrieve the Spatial Polygon details from GIS service for the Spatial Inquiry id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param spatialInquiryId - Spatial Inquiry Id.
   * 
   * @return - Spatial Polygon details.
   */
  @GetMapping(value = "/spatial-polygon/{spatialInquiryId}", produces = "application/json")
  @ApiOperation(value="Retrieve the Spatial Inquiry Polygon details for the input inquiry id.")
  public String getSpatialPolygonByInquiryId(HttpServletResponse response,
      @PathVariable @ApiParam(example = "12313", value="Sptial Inquiry id.") final String spatialInquiryId) {

    String contextId = UUID.randomUUID().toString();

    LOGGER.info("Entering into getSpatialPolygonByApplicationId. Context Id {}", contextId);
    if (!StringUtils.hasLength(spatialInquiryId)) {
      LOGGER.info(MANTORY_VAL_MISSING_LOG_INFO, spatialInquiryId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return spatialInquiryService.getSpatialPolygonByApplicationId(spatialInquiryId, contextId);
  }

  /**
   * Delete the Spatial Inquiry details for the input object id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param objectId - Object Id to be deleted.
   * 
   * @return - Deleted Spatial Inquiry Polygon details.
   */
  @PostMapping(value = "/delete-spatial-polygon/{objectId}")
  @ApiOperation(value="Delete the Spatial Inquiry Polygon details for the input inquiry id.")
  public Object deleteSpatialInquiryPolygonByObjId(HttpServletResponse response,
      @PathVariable @ApiParam(example = "222342", value="Spatial Inquiry Polygon Object Id") final String objectId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into deleteSpatialInquiryPolygonByObjId Context Id: {}", contextId);
    return spatialInquiryService.deleteSpatialInqPolygonByObjId(objectId, contextId);
  }

  /**
   * Persist the Spatial Inquiry details.
   * 
   * @param userId - User who initiates this request.
   * @param jwtToken - JWT Token
   * @param spatialInqPolygon - Spatial Inquiry Polygon details to be persisted.
   * 
   * @return - Persisted Spatial Inquiry Polygon.
   */
  @PostMapping(value = "/save-spatial-inquiry")
  @ApiOperation(value="Save the Spatial Inquiry details in eTrack requested by the user.")
  public Object saveSpatialInquiryDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestBody final JsonNode spatialInqPolygon) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into saveSpatialInquiryDetails Context Id: {}", contextId);
    return spatialInquiryService.saveSpatialInqDetails(userId, contextId, jwtToken, spatialInqPolygon);
  }

  /**
   * Retrieve the Spatial Inquiry associated with this inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param requestorName - Inquiry Requester name.
   * @param jwtToken - JWT token
   * @param inquiryId - Inquiry Id.
   * 
   * @return - Returns the Inquiry details.
   */
  @GetMapping(value = {"/spatial-inquiry", "/spatial-inquiry/{inquiryId}"})
  @ApiOperation(value="Retrievve the Spatial Inquiry details in eTrack requested by the user and/or inquiry id.")
  public Object getSpatialInquiryDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) @ApiParam(example = "NameString", value="Requestor name") final String requestorName,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @PathVariable(required = false) @ApiParam(example = "872934", value="Spatial Inquiry Id.") final Long inquiryId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getSpatialInquiryDetails Context Id: {}", contextId);
    return spatialInquiryService.getSpatialInquiryDetails(userId, contextId, jwtToken, inquiryId,
        requestorName);
  }

  /**
   * Store the Inquiry response details into GIS.
   * 
   * @param response - {@link HttpServletResponse}
   * @param userId - User who initiates this request.
   * @param jwtToken - JWT Token
   * @param spatialInqPolygonResponse - Spatial Inquiry Polygon Response details to be updated in GIS Layer.
   * 
   * @return - Persisted Spatial Inquiry Polygon.
   */
  @PostMapping(value = "/upload-inq-response")
  @ApiOperation(value="Upload the Inquiry Response details into GIS.")
  public void saveSpatialInquiryResponseDetails(HttpServletResponse response,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "276316e4-176c-4db0-bc87-dcaa486a5e8c", value="Unique UUID to track this request") final String contextId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestBody final JsonNode spatialInqPolygonResponse) {

    LOGGER.info("Entering into saveSpatialInquiryResponseDetails. User Id {}, Context Id: {}", userId, contextId);
    GISServiceResponse responseText = spatialInquiryService.saveSpatialInquiryResponseDetails(userId, contextId, jwtToken, spatialInqPolygonResponse);
    LOGGER.info("Exiting from saveSpatialInquiryResponseDetails. User Id {}, Context Id: {}. "
        + "Response Received {}", userId, contextId, responseText);
    response.setStatus(HttpStatus.OK.value());
  }
}
