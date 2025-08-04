package dec.ny.gov.etrack.dart.db.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.GIReviewerDashboardDetail;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryCategory;
import dec.ny.gov.etrack.dart.db.service.SpatialInquiryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/spatial-inq")
public class SpatialInquiryController {

  private static final Logger logger = LoggerFactory.getLogger(SpatialInquiryController.class.getName());
     
  @Autowired
  private SpatialInquiryService geographicalInquiryService;
  
  /**
   * Retrieve the Spatial Inquiry details for the input inquiry type to display in the User's dashboard.
   * @param userId - User who initiates this request.
   * @param inquiryType - Spatial Inquiry type.
   * 
   * @return - List of Spatial Inquiry associated with this inquiry type.
   */
  @GetMapping("/{inquiryType}")
  @ApiOperation(value="Retrieve the Spatial Inquiry details for the input inquiry type to display in the User's dashboard.")
  public Object getSpatialInquiryDetailsByInquiryType(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable @ApiParam(example = "BOROUGH_DETERMINATION", value="Spatial Inquiry category") final SpatialInquiryCategory inquiryType) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getResumeEntryProjects User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveSpatialInquiryServiceByInquiryType(userId, contextId, inquiryType);
  }
 
  /**
   * Retrieve all the Spatial Inquiry details for all the region or only foe region passed to display in the Analyst's dashboard.
   * @param userId - User who initiates this request.
   * @param region - region id.
   * 
   * @return - List of all the Spatial Inquiries associated.
   */
  @GetMapping({"/all-inquiries", "/all-inquiries/{region}"})
  public Object retrieveAllSpatialInquiryDetailsByRegion(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Inquiry region") final Integer region) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getResumeEntryProjects User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveAllSpatialInquiryDetails(userId, contextId, region);
  }
  /**
   * Retrieve the Geographical Inquiry Polygon inquiry details for the requestor name.
   * 
   * @param userId - User who initiates this request.
   * @param contextId -Unique UUID to track this request.
   * @param requestorName - Requestor name.
   * @param inquiryId - Geographical Inquiry id.
   * 
   * @return - Spatial Polygon Inquiry details.
   */
  @GetMapping(value = {"/spatial-inquiry", "/spatial-inquiry/{inquiryId}"})
  public Object retrieveSpatialPolygonInquiryDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader final String contextId,
      @RequestHeader(required = false) @ApiParam(example = "requestorName", value="Inquiry Requestor name")final String requestorName, 
      @PathVariable(required = false) @ApiParam(example = "1232", value="Geographical Inquiry id") final Long inquiryId) {
    
    logger.info("Entering into retrieveSpatialPolygonInquiryDetails() method . User Id {}, Context Id {}", userId, contextId);
    return geographicalInquiryService.retrieveSpatialInqDetail(userId, contextId, inquiryId, requestorName);
  }

  /**
   * Retrieve all the Spatial Inquiries details for the input inquiry type to display in the regional projects. 
   * for all the users and region or all the users and individual region.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryType - Spatial Inquiry Type.
   * @param facilityRegionId - Facility Region Id.
   * 
   * @return - List of all the inquiries.
   */
  @GetMapping(value ={"/regional/{inquiryType}", "/regional/{inquiryType}/{facilityRegionId}"})
  public Object getRegionalSpatialInquiryDetailsByInquiryTypeAndRegionId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable @ApiParam(example = "BOROUGH_DETERMINATION", value="Spatial Inquiry category") final SpatialInquiryCategory inquiryType, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Facility Region id") final Integer facilityRegionId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getResumeEntryProjects User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveRegionalSpatialInquiryServiceByInquiryType(
        userId, contextId, inquiryType, facilityRegionId);
  }
  

  /**
   * Retrieve the list of required document for this inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Spatial Inquiry document details.
   */
  @GetMapping("/doc-summary")
  @ApiOperation(value="Retrieve the list of Spatial Inquiry document for the input inquiry category.")
  public Object getSpatialInquiryDocuments(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "142131", value="Spatial Inquiry Id.") final Long inquiryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getSpatialInquiryDocuments User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveSpatialDocumentSummary(userId, contextId, inquiryId);
  }
  
  /**
   * Retrieve the current status of the Geographical Inquiry for the requested inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Spatial Inquiry document details.
   */
  @GetMapping("/status")
  @ApiOperation(value="Retrieve the current status of the Geographical Inquiry for the requested inquiry id")
  public Object retrieveSpatialInquiryStatus(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "142131", value="Geographical Inquiry Id.") final Long inquiryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveSpatialInquiryStatus User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveSpatialInquiryStatus(userId, contextId, inquiryId);
  }

  /**
   * Retrieve the Geographical Inquiry details for the input inquiry id to show in the Virtual Workspace.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Geographical Inquiry details.
   */
  @GetMapping("/virtual-workspace")
  @ApiOperation(value="Retrieve the Geographical Inquiry details for the requested inquiry id")
  public Object retriveGeographicalInquiryDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "142131", value="Spatial Inquiry Id.") final Long inquiryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retriveGeographicalInquiryDetails User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveGeographicalInquiryForVW(userId, contextId, inquiryId);
  }


  /**
   * Retrieve the Geographical Inquiry notes configurations.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Geographical Inquiry details.
   */
  @GetMapping("/note-config")
  @ApiOperation(value="Retrieve the Geographical Inquiry details for the requested inquiry id")
  public Object retriveGeographicalNoteConfigurationDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retriveGeographicalNoteConfigurationDetails User Id {}, Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(userId)) {
      throw new BadRequestException("USER_ID_MISSING", "User Id is empty or blank", userId);
    }
    return geographicalInquiryService.retrieveGeographicalNoteConfig(userId, contextId);
  }

  
  /**
   * Retrieve the Geographical Inquiry note details for the input note id.
   * 
   * @param userId - User initiates this request.
   * @param projectquiryId - Inquiry Id.
   * @param noteId - Geographical Inquiry note Id.
   * 
   * @return - Returns Geographical Inquiry note details.
   */
  @GetMapping("/notes/{noteId}")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Note details for the input note id.")
  public Object getNoteByNoteId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Inquiry Id") final Long inquiryId, 
      @PathVariable @ApiParam(example = "23423", value="Note Id") final Long noteId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the Project Notes details User Id {} Context ID : {}", userId, contextId);
    if (!StringUtils.hasLength(userId) || inquiryId == null 
        || inquiryId <= 0 || noteId == null || noteId <= 0) {
      
      throw new BadRequestException("INVALID_INQUIRY_ID_PASSED", 
          "Geographical Inquiry and/or User id is blank or invalid passed", inquiryId);
    }
    return geographicalInquiryService.getNote(userId, contextId, inquiryId, noteId);
  }
  
  /**
   * Retrieve the list of Geographical Inquiry reviews requested for the logged in Program Area Reviewer to review. 
   * 
   * @param userId - User Id who initiates this request.
   * 
   * @return - Geographical inquiry review details.
   */
  @GetMapping(value = {"/reviewer-dashboard"}, produces = "application/json")
  @ApiOperation(
      value = "Retrieve the list of Geographical Inquiry reviews requested "
          + "for the logged in Program Area Reviewer to review for the input inquiry type.")
  public List<GIReviewerDashboardDetail> retrieveGeographicalProgramReviewerDashboardDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveGeographicalProgramReviewerDashboardDetails User Id {}, Context Id {}",
        userId, contextId);
    return geographicalInquiryService.getProgramReviewerDashboardDetails(userId, contextId);
  }
  
  /**
   * Retrieve all the review eligible Goegraphical inquiry documents.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Geographical Inquiry Id.
   * 
   * @return - List of review eligible documents.
   */
  @GetMapping("/review-documents")
  @ApiOperation(value = "Retrieve all the documents eligible to review.")
  public Object retrieveEligibleReviewDocuments(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Inquiry Id") final Long inquiryId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveEligibleReviewDocuments. User Id {}, Context Id {}", userId,
        contextId);
    return geographicalInquiryService.retrieveEligibleReviewDocuments(userId, contextId, inquiryId);
  }
}
