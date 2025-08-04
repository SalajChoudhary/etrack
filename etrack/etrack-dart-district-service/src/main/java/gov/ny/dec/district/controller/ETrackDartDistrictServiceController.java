package gov.ny.dec.district.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.district.dart.entity.ApplicationNarrativeDetail;
import gov.ny.dec.district.dart.entity.District;
import gov.ny.dec.district.service.DARTDistrictService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackDartDistrictServiceController {

  @Autowired
  private DARTDistrictService dartDistrictService;
  
  private Logger logger = LoggerFactory.getLogger(ETrackDartDistrictServiceController.class.getName());

  /**
   * Retrieve District details for the requested DEC ID.
   * 
   * @param userId - User who initiates this request.
   * @param decId - DEC ID.
   * 
   * @return - List of matched district(s) for the input DEC ID.
   */
  @GetMapping(value = "/retrieveDistrictByDecId/{decId}", produces = "application/json")
  @ApiOperation(value="Retrieve the District details for the input DEC ID from enterprise.")
  public ResponseEntity<List<District>> retrieveDistrictsByDecId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @PathVariable @ApiParam(example = "0-0000-12312", value="DEC ID") final String decId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering retrieveDistrictByDecId(). DecId {}. User Id {}, Context Id {} ", decId, userId, contextId);
    return dartDistrictService.getDistrictDetailsByDecId(userId, contextId, decId);
  }

  /**
   * Retrieve the District details for the input Facility name and search type.
   * 
   * @param userId - User who initiates this request.
   * @param facilityName - Facility Name.
   * @param searchType - Search type. S-starts with, E- Exact and C- Contains.
   * 
   * @return - List of matched district(s).
   */
  @GetMapping(value = "/retrieveDistrictByFacilityName/{facilityName}/{searchType}", produces = "application/json")
  @ApiOperation(value="Retrieve the District details for the input Facility name and Search type from enterprise.")
  public ResponseEntity<List<District>> retrieveDistrictsByFacilityName(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @PathVariable @ApiParam(example = "New York State Department", value="Facility Nmae")  String facilityName, 
      @PathVariable @ApiParam(example = "S", value="Search Type") String searchType) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering retrieveDistrictByFacilityName(). User Id {}, Context Id {}, "
        + "facilityName {}, searchType {}", userId, contextId, facilityName, searchType);
    return dartDistrictService.getDistrictDetailsByFacilityName(userId, contextId, facilityName, searchType);
  }
  
  /**
   * Retrieve District details for the requested District ID.
   * 
   * @param userId - User who initiates this request.
   * @param districtId - District Id.
   * 
   * @return - List of matched district(s).
   */
  @GetMapping(value = "/retrieveDistrictDetails/{districtId}", produces = "application/json")
  @ApiOperation(value="Retrieve the District details for the input District Id from enterprise.")
  public ResponseEntity<DistrictDetail> getDistrictDetailsByDistrictId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @PathVariable @ApiParam(example = "234234", value="Enterprise District Id") Long districtId) {
     
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering getDistrictDetailsByDistrictId(). "
        + "User Id: {}, Context Id : {} District Id : {} ", userId, contextId, districtId);
    return dartDistrictService.getDistrictDetails(userId, contextId, districtId);
  }

  /**
   * Retrieve the Application Narrative Descriptions for the input District Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique Id to track the transaction.
   * @param districtId - District Id.
   * 
   * @return - Application Description details.
   */
  @GetMapping(value = "/retrieveNarrativeDesc/{districtId}", produces = "application/json")
  @ApiOperation(value="Retrieve the Application Narartive HTml text for the input district id from enterprise.")
  public List<ApplicationNarrativeDetail> retrieveApplNarrativeDescriptionDetail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId,
      @PathVariable @ApiParam(example = "234234", value="Enterprise District Id") final Long districtId) {
    
    if (!StringUtils.hasLength(contextId)) {
      contextId= UUID.randomUUID().toString();
    }
    logger.info("Entering retrieveApplNarrativeDescriptionDetail(). "
        + "User Id {}, Context Id: {} District Id: {} ", userId, contextId, districtId);
    return dartDistrictService.getApplicationNarrativeDescription(userId, contextId, districtId);
  }
  
  /**
   * Upload the DIMSR application details into DART database.
   * 
   * @param userId - User Id who initiates this request.
   * @param guid - GUID of the requested user.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   */
  @PostMapping(value = "/upload-dimsr-appl-dart/{projectId}", produces = "application/json")
  @ApiOperation(value="Upload the DIMSR application details into DART/enterprise system by calling the Stored Procedure created in enterprise.")
  public void uploadDIMSRApplication(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "24337e61-71a2-4765-91fa-8f4dc3d59c09", value="DEC Staff Unique PPID/GUID assigned in the AD System") final String guid,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId,
      @PathVariable @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    if (!StringUtils.hasLength(contextId)) {
      contextId= UUID.randomUUID().toString();
    }
    logger.info("Entering uploadDIMSRApplication(). "
        + "User Id {}, Context Id : {} Project Id: {} ", userId, contextId, projectId);
    dartDistrictService.uploadDIMSRApplication(userId, contextId, projectId, guid);
  }
  
  /**
   * Upload the eTrack Project Data into Dart database.
   * 
   * @param userId - User Id who initiates this request.
   * @param guid - GUID of the requested user.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   */
  @PostMapping(value = "/upload-etrack-appl-dart/{projectId}", produces = "application/json")
  @ApiOperation(value="Upload the eTrack project details into DART/enterprise system by calling the Stored Procedure created in enterprise.")
  public void uploadETrackApplicationDetailsToDart(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "24337e61-71a2-4765-91fa-8f4dc3d59c09", value="DEC Staff Unique PPID/GUID assigned in the AD System") final String guid,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @PathVariable @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    if (!StringUtils.hasLength(contextId)) {
      contextId= UUID.randomUUID().toString();
    }
    logger.info("Entering into uploadETrackApplicationDetailsToDart(). "
        + "User Id: {}, Context Id : {} Project Id: {} ", userId, contextId, projectId);
    dartDistrictService.uploadETrackApplicationDetailsToDart(userId, contextId, projectId, guid);
    logger.info("Exiting from uploadETrackApplicationDetailsToDart(). "
        + "User Id: {}, Context Id : {} Project Id: {} ", userId, contextId, projectId);

  }

  /**
   * Add additional permit for the existing project into Dart database.
   * 
   * @param userId - User Id who initiates this request.
   * @param projectId - Project Id.
   * @param contextId -  Unique Id to track the transaction.
   * @param guid - Unique UUID for the user.
   * @param applId - Application Id.
   */
  @PostMapping(value = "/upload-addl-permit/{applId}", produces = "application/json")
  @ApiOperation(value="Upload the Additional application details created from Virtual Workspace in the existing batch to enterprise.")
  public void uploadAdditionalPermitDetailToDart(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId, 
      @RequestHeader @ApiParam(example = "24337e61-71a2-4765-91fa-8f4dc3d59c09", value="DEC Staff Unique PPID/GUID assigned in the AD System") final String guid,
      @PathVariable @ApiParam(example = "1231231", value="eTrack application Id") final Long applId) {
    
    if (!StringUtils.hasLength(contextId)) {
      contextId= UUID.randomUUID().toString();
    }
    logger.info("Entering into uploadAdditionalPermitDetailToDart(). "
        + "User Id: {}, Context Id : {} Project Id: {} ", userId, contextId, applId);
    dartDistrictService.addAdditionalPermitToDart(userId, contextId, projectId, applId, guid);
    logger.info("Exiting from uploadAdditionalPermitDetailToDart(). "
        + "User Id: {}, Context Id : {} Project Id: {} ", userId, contextId, applId);
  }
  
  /**
   * Retrieve the Milestone related details from enterprise and upload into eTrack.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param contextId - Unique UUID to track this request.
   */
  @PostMapping(value="/refresh-project-milestone")
  @ApiOperation(value="Used to retrieve the most updated milestone details in DART/Enterprise. for the input project id and update to eTrack.")
  public void refreshProjectMilestone(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestHeader @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId) {
    dartDistrictService.refreshMilestoneStatusByProjectId(userId, contextId, projectId);
  }
}
