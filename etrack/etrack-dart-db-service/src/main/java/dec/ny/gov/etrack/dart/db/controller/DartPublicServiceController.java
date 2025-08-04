package dec.ny.gov.etrack.dart.db.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.ApplicantType;
import dec.ny.gov.etrack.dart.db.model.PublicType;
import dec.ny.gov.etrack.dart.db.model.SearchPatternEnum;
import dec.ny.gov.etrack.dart.db.service.DartPublicService;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class DartPublicServiceController {

  private static final Logger logger = LoggerFactory.getLogger(DartPublicServiceController.class.getName());
  private static final List<Integer> VALID_NUMERICS = Arrays.asList(0, 1);
  
  @Autowired
  private DartPublicService dartPublicService;
  
  /**
   * This end point is to return the LRP, Owners or Contact/Agents summary 
   * details based on the category the user is requesting.
   * 
   * @param userId - User who initiated this request
   * @param projectId - Project id
   * @param category - Applicant Category . P- Publics, O- Owners, C- Contacts/Agents
   * @param associatedInd - Project associated indicator . 
   * 0 - Facility Publics yet to be associated
   * 1 = Facility Publics associated
   *        
   * @return - Returns the status of the request along with Publics details.
   */
  @GetMapping(value = "/applicants/{category}/{associatedInd}")
  @ApiOperation(value="This endpoint is used to retrieve all the publics associated with this project based on the category passed. "
      + "Caterogry can be LRPs, Property Owners and Contact Agents.")
  public ResponseEntity<Object> getApplicants(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "P", value="Applicant Category LRP, OWNER or CONTACT/AGENT") final ApplicantType category,
      @PathVariable @ApiParam(example = "1", value="Possible values of 0 or 1.") Integer associatedInd) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into getApplicants to retrieve all the applicants based on category {}. User Id {}. Context Id {}",
        category, userId, contextId);

    if (StringUtils.hasLength(userId) && projectId != null
        && projectId > 0 && VALID_NUMERICS.contains(associatedInd)) {
      return dartPublicService.retrieveApplicants(userId, contextId, category.name(), projectId,
          associatedInd);
    }
    throw new BadRequestException("PUBLIC_SUMMARY_PARAM_MISSING", "One or more mandatory Parameters "
        + "(User Id, Project Id, Category and/or associatedInd) are missing", projectId);
  }

  /**
   * Returns the Summary of all the publics which can be Applicants, Owners and Contact/Agent associated with this project.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project id.
   * 
   * @return - List of Publics by category.
   */
  @GetMapping("/applicant-summary")
  @ApiOperation(value="Returns the summary of the list of publics associated with this project. "
      + "The public can be LRPs, Property Owners and Contact Agents.")
  public ResponseEntity<Object> getPublicsSummary(@RequestHeader 
      @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into getPublicsSummary for the Project Id {}. User Id {}. Context Id {}", projectId, userId, contextId);
    if (StringUtils.hasLength(userId) && projectId != null && projectId > 0) {
      return dartPublicService.retrieveAllPublicsAssociatedWithThisProject(userId, contextId, projectId);
    }
    throw new BadRequestException("PUBLIC_SUMMARY_PARAM_MISSING", "One or more mandatory Parameters "
        + "(User Id, Project Id) are missing to retrieve the Public Summary", projectId);
  }
  
  /**
   * This end point is to do the Publics Search in DART based on the input Search Parameters.
   * 
   * @param userId - User initiates the request id
   * @param firstName - First name search parameter for Individual LRPs. Also the same field name is 
   *                        used for other sections like Business/Partner name search etc.., 
   * @param fType - Search Pattern for the first name. S- StartsWith, C-Contains and E- for Exact
   * @param lastName - Last name Search Parameter for Individual LRPs.
   * @param lType - Search Pattern for the last name . S- StartsWith, C-Contains and E- for Exact
   * @param publicType - Public Type..
   * 
   * @return - Search result of the Publics.
   */
  @GetMapping(value = "/search/{publicType}", produces = "application/json")
  @ApiOperation(value="Returns the publics from the enterprise which are matched with the input search criteria. "
      + "The public can be LRPs, Property Owners and Contact Agents.")
  public ResponseEntity<Object> getMatchedApplicantsFromEnterprise(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "FirstName", value="First Name search text")final String firstName,
      @RequestHeader @ApiParam(example = "S", value="S-Starts With, C- Contains, E-Exact") final SearchPatternEnum fType, 
      @RequestHeader(required = false) @ApiParam(example = "LastName", value="Last Name Search Text") final String lastName,
      @RequestHeader(required = false) @ApiParam(example = "shortname", 
      value="S-Starts With, C- Contains, E-Exact") final SearchPatternEnum lType, 
      @PathVariable @ApiParam(example = "I", value="I-individual") final PublicType publicType) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getMatchedApplicantsFromEnterpriseForSearch() - Public Search details "
        + "for the for the input name details. User Id {} Context Id {}", userId, contextId);

    if (StringUtils.hasLength(firstName)) {
      if (!DartDBServiceUtility.isValidStrings(userId, firstName, fType.name())) {
        throw new BadRequestException("PUBLIC_SEARCH_PARAM_MISSING", "One or more mandatory Parameters "
            + "(User Id, Project Id, firstName, Search Type are missing", publicType);
      }
    }
    return dartPublicService.getAllMatchedApplicants(userId, contextId, publicType, firstName, fType,
        lastName, lType);
  }
  
  /**
   * This end point is used to retrieve the applicant information for the input eTrack public/applicant id
   * 
   * @param userId - User's Unique Id who initiates this request
   * @param projectId - Project Id 
   * @param publicId - Public/Applicant id.
   * 
   * @return - Applicant information if exists
   */
  @GetMapping(value = "/applicant/{publicId}", produces = "application/json")
  @ApiOperation(value="Retrieve the Public information for the input public Id")
  public ResponseEntity<Object> getApplicantInfo(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "34334", value="Public Id") final Long publicId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getApplicantInfo() details for the project id {} Context Id {}",
        projectId, contextId);
    if (publicId == null || publicId <= 0) {
      throw new BadRequestException("PUBLIC_ID_EMPTY", "Public Id cannot be blank or zero", publicId);
    }
    return dartPublicService.getApplicantInfo(userId, contextId, projectId, publicId, null);
  }

  /**
   * This end point is used to retrieve the applicant information for the existing DART public Id.
   * 
   * @param userId - User's Unique Id who initiates this request.
   * @param projectId - Project Id
   * @param edbPublicId - Enterprise/DART public Id.
   * @param aplctType - Public Type.
   * 
   * @return - Applicant information.
   */
  @GetMapping(value = "/applicant/{edbPublicId}/{aplctType}", produces = "application/json")
  @ApiOperation(value="This endpoint is used to retrieve all the publics associated with this project. "
      + "The public can be LRPs, Property Owners and Contact Agents.")
  public ResponseEntity<Object> getEdbApplicantInfo(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "34334", value="Public Id already exist in the enterprise system") final Long edbPublicId,
      @PathVariable @ApiParam(example = "I", value="Applicant/Public Type code Individual, Agency, Organization etc.") final PublicType aplctType) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getEdbApplicantInfo() details for the project id {} Context Id {}",
        projectId, contextId);
    return dartPublicService.getEdbApplicantInfo(userId, contextId, projectId, edbPublicId, aplctType.name());
  }

  /**
   * This end point is used to retrieve the existing eTrack applicants details with history of changes if any.
   * 
   * @param userId - User's Unique Id who initiates this request.
   * @param projectId - Project Id.
   * @param applicantId - eTrack Public/Applicant id.
   * 
   * @return - Applicant information.
   */
  @GetMapping(value = "/applicant/view/{applicantId}", produces = "application/json")
  @ApiOperation(value="Retrieve the Publics Initial data and modified during the process.")
  public ResponseEntity<Object> applicantHistory(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "28347", value="Public Id") final Long applicantId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into applicantHistory() details for the project id {} Context Id {}",
        projectId, contextId);
    return dartPublicService.retrieveApplicantHistory(userId, contextId, projectId, applicantId);
  }

  /**
   * This end point is used to retrieve the Contact/Agent information for the input public id.
   * 
   * @param userId - User's Unique Id who initiates this request.
   * @param projectId - Project Id.
   * @param contactId - Contact/Agent Unique id.
   * @param aplctType - ApplicantType.
   * 
   * @return - Contact/Agent information
   */
  @GetMapping(value = {"/contact/{contactId}", "/contact/{contactId}/{aplctType}"},
      produces = "application/json")
  @ApiOperation(value="Retrieve the matched Contact/Agent details for the input contact id and/or applicant type passed as an input parameter")
  public ResponseEntity<Object> getContactInfo(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable  @ApiParam(example = "24923", value="Public Id") final Long contactId,
      @PathVariable(required = false) @ApiParam(example = "I", 
      value="Applicant/Public Type code Individual, Agency, Organization etc.") final PublicType aplctType) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getContactInfo() details for the project id {} Context Id {}",
        projectId, contextId);
    String searchPublicType = aplctType != null ? aplctType.name() : null;
    return dartPublicService.getApplicantInfo(userId, contextId, projectId, contactId, searchPublicType);
  }

  /**
   * Check whether the input public id is valid or not.
   * 
   * @param userId - User who initiates this request.
   * @param publicId - eTrack Public Id
   * @param projectId - Project Id
   * @param edbPublicId - Enterprise Public Id which will be tagged to the eTrack Public Id.
   * 
   * @return - Returns HttpStatus.Ok if its valid.
   */
  @GetMapping("/validate/dart-public/{edbPublicId}")
  @ApiOperation(value="Validate the enterprise Public id which the user/staff wants to associate with new eTrack public.")
  public Object validateEdbPublicId(@RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "24923", value="eTrack Public Id") final Long publicId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "2492123", value="Enterprise Public Id") Long edbPublicId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into validateEdbPublicId. User Id {}, Context Id {}", userId, contextId);
    return dartPublicService.validateEdbPublicId(userId, contextId, projectId, publicId, edbPublicId);
  }

}
