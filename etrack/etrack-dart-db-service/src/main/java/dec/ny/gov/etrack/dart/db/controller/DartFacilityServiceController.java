package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.service.DartFacilityService;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class DartFacilityServiceController {

  private static final Logger logger = LoggerFactory.getLogger(DartPermitServiceController.class.getName());
  
  @Autowired
  private DartFacilityService dartFacilityService;


  /**
   * This end point will return the DEC Id for the input Program Type and Program Id.
   * 
   * @param userId - User who initiates the request
   * @param contextId - Unique UUID to track the transaction
   * @param programId - Program id
   * @param programType - Program type
   * 
   * @return - return the DEC ID details for the input Program Type.
   */
  @GetMapping("/decid/programType/{programType}")
  @ApiOperation(value="Returns the matched DEC ID for the input Program Type if any. ")  
  public Object getDECIDByProgramType(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) 
      @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @RequestHeader @ApiParam(example="234287234", value="Program ID based on the Program Type preferred by the user.") final String programId,
      @PathVariable @ApiParam(example="DEC", value="Program Type") final String programType) {

    if (StringUtils.isEmpty(contextId)) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info("Entering into DECIDByProgramType User Id {}, " + "Context ID {} Program Type {}",
        userId, contextId, programType);
    return dartFacilityService.getDECIDByProgramType(userId, contextId, programId, programType);
  }

  /**
   * This end point is used to get the DEC ID for the input Tax map number.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this transaction,
   * @param txmap - Tax Map number for the search. 
   * @param county - County detail
   * @param municipality - Municipality detail
   * 
   * @return - matched DEC ID detail.
   */
  @GetMapping("/decid/txmap")
  @ApiOperation(value="Returns the matched DEC ID for the input Tax Map number if any.")  
  public Object getDECIDByTaxMapNumber(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @RequestParam("txmap") @ApiParam(example="156.17-2-41", value="Tax map number") final String txmap,
      @RequestParam("county") @ApiParam(example="Steuben", value="County details") final String county,
      @RequestParam(name = "municipality", required = false) 
      @ApiParam(example="Rosendale", value="Municipality details") final String municipality) {

    if (StringUtils.isEmpty(contextId)) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info("Entering into getDECIDByTaxMapNumber User Id {}, Context ID {}  Tax Map {} ",
        userId, contextId, txmap);
    if (!DartDBServiceUtility.isValidStrings(userId, txmap, county)) {
      throw new BadRequestException("TAXMAP_SEARCH_PARAM_MISSING", 
          "One or more Tax Map search parameters is missing", txmap + " " + municipality);
    }
    return dartFacilityService.getDECIDByTaxMap(userId, contextId, txmap, county, municipality);
  }

  /**
   * This end point is used to return the Facility details for the input Project Id.
   * 
   * @param userId -  User who initiates this request
   * @param projectId - Project Id
   * 
   * @return - Returns the {@link FacilityDetail} details
   */ 
  @GetMapping("/facility")
  @ApiOperation(value="Endpoint is used to retrieve the facility detaikls for the input project id.")
  public FacilityDetail getETrackFacility(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the facility details. User Id {} Context ID : {}", userId, contextId);
    if (projectId == null) {
      throw new BadRequestException("PROJECT_ID_MISSING", "There is no Project Id is passed", contextId);
    }
    return dartFacilityService.getEtrackFacilityDetails(userId, contextId, projectId);
  }

  /**
   * Retrieve all the matching facilities for the input parameter address Line1 and City.
   * 
   * @param userId - Unique Id who initiates this request.
   * @param addrLine1 - Address Line #1.
   * @param city - City
   * 
   * @return - Returns the matched Facilities.
   */
  @GetMapping("/facilities")
  @ApiOperation(value="Retrieve all the matching facilities for the input parameter address Line1 and City.")
  public Object getMatchingFacilities(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestParam  @ApiParam(example = "625 Broadway", value="Address Line 1")  final String addrLine1, 
      @RequestParam  @ApiParam(example = "HAMMONDSPORT", value="City name")  final String city) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the all the matching facility details "
        + "for the requested address. User Id {} Context Id : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(addrLine1)
        || StringUtils.isEmpty(city)) {
      throw new BadRequestException("FACILITY_SEARCH_PARAM_MISSING", "One or more mandatory Facility search parameter is missing",
          "Address Line 1 " + addrLine1 + " City " + city);
    }
    return dartFacilityService.getAllMatchedFacilities(userId, contextId, addrLine1, city);
  }

  /**
   * This end point is used to retrieve the existing eTrack facility details with history changes if any.
   * 
   * @param userId - User's Unique Id who initiates this request.
   * @param projectId - Project Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return -Facility history detail.s
   */
  @GetMapping(value = "/facility/view", produces = "application/json")
  @ApiOperation(value="Retrieve all the facility details both initial data from enterprise and recent data captured in eTrack system.")
  public ResponseEntity<Object> facilityHistory(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = false) String contextId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    if (contextId == null) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info("Entering into facility history () details for the project id {}. User Id {}, Context Id {}",
        projectId, userId, contextId);
    return dartFacilityService.retrieveFacilityHistory(userId, contextId, projectId);
  }
}
