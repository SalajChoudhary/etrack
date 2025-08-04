package dec.ny.gov.etrack.gis.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.model.PolygonAction;
import dec.ny.gov.etrack.gis.model.ProjectPolygon;
import dec.ny.gov.etrack.gis.service.GISPolygonService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class GISPolygonServiceController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GISPolygonServiceController.class.getName());

  @Autowired
  private GISPolygonService gisService;
  private static final String MANTORY_VAL_MISSING_LOG_INFO = "Mandatory value is missing {} ";

  /**
   * Retrieve the DEC Polygon by Tax Id, County and Municipality details.
   * 
   * @param response - {@link HttpServletResponse}
   * @param taxParcelID - Tax Parcel Id.
   * @param countyName - Country name.
   * @param municipalName - Municipality name.
   * 
   * @return - DEC Polygon related details.
   */
  @GetMapping(value = "/DECPolygonByTaxId", produces = "application/json")
  @ApiOperation(value="Retrieve the DEC Polygon details for the input Tax Parcel Id, County and Municipality name.")
  public String getDECPolygonByTaxId(HttpServletResponse response,
      @RequestParam("taxParcelID") @ApiParam(example = "156.17-2-41", value="Tax Parcel Id") final String taxParcelID,
      @RequestParam("countyName") @ApiParam(example = "Albany", value="County Name") final String countyName,
      @RequestParam("municipalName") @ApiParam(example = "Albany", value="Municipality") final String municipalName) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getDECPolygonByTaxId. Context Id {}", contextId);
    if (!(StringUtils.hasLength(taxParcelID) && StringUtils.hasLength(countyName)
        && StringUtils.hasLength(municipalName))) {
      
      LOGGER.error(
          "One of the Mandatory value is missing Tax Parcel Id {} County Name {} Municipal {}",
          taxParcelID, countyName, municipalName);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getDECPolygonByTaxId(taxParcelID, countyName, municipalName, contextId);
  }

  /**
   * Retrieve the DEC Polygon details by the input address.
   * 
   * @param response - {@link HttpServletResponse}
   * @param street - Street address detail.
   * @param city - City name.
   * 
   * @return - DEC Polygon details.
   */
  @GetMapping(value = "/DECPolygonByAddress", produces = "application/json")
  @ApiOperation(value="Retrieve the DEC Facility Polygon details for the input street and city.")
  public String getDECPolygonByAddress(HttpServletResponse response,
      @RequestParam @ApiParam(example = "625 Broadway", value="Street/Address line 1") final String street, 
      @RequestParam @ApiParam(example = "Albany", value="City") final String city) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getDECPolygonByAddress. Context Id {}", contextId);

    if (!(StringUtils.hasLength(street) && StringUtils.hasLength(city))) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getDECPolygonByAddress(street, city, contextId);
  }

  /**
   * Retrieve the DEC Polygon details by the input DEC Id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param jwtToken - JWT Token.
   * @param decId - DEC Id.
   * 
   * @return - DEC Polygon detail for the input DEC ID.
   */
  @GetMapping(value = "/DECPolygonByDecId", produces = "application/json")
  @ApiOperation(value="Retrieve the DEC Facility Polygon details for the input DEC ID.")
  public String getDECPolygonByDecId(HttpServletResponse response,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestParam("decId") @ApiParam(example = "0-0000-12345", value="DEC ID") final String decId) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getDECPolygonByDecId Context Id {}", contextId);
    if (!StringUtils.hasLength(decId)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getDECPolygonByDecId(decId, contextId, jwtToken);
  }

  /**
   * Upload the polygon details into eFind.
   * 
   * @param uploadPolygonProjects - Polygon details for this project.
   * 
   * @return - Uploaded details.
   */
  @PostMapping("/upload-polygon-efind")
  @ApiOperation(value="Upload the Final approved/reviewed polygon by the DEC Staff in eTrack to eFind."
      + "This will also shares the details of if any change in the polygon made by the user/staff "
      + "in the exiting polygon for the existing facility which helps the staff to validate in eFind.")
  public Map<Long, String> uploadFinalApprovedPolygonToEFind(
      @RequestBody List<ProjectPolygon> uploadPolygonProjects) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into uploadFinalApprovedPolygonToEFind. Context Id {}", contextId);

    if (CollectionUtils.isEmpty(uploadPolygonProjects)) {
      return null;
    }
    return gisService.uploadApprovedPolygonToEFind(contextId, uploadPolygonProjects);
  }

  /**
   * Save/Amend the Applicant Polygon details.
   * 
   * @param featureMap - Feature details.
   * @param value - pjson value.
   * @param action - Save-S/Update-U.
   * 
   * @return - Updated Polygon after save/amend.
   */
  @PostMapping(value = "/applicantPolygon/{action}", produces = "application/json")
  @ApiOperation(value="Save/Update the Applicant/User scratch Polygon into eTrack.")
  public Object saveApplicantPolygon(
      final @RequestPart(value = "features") Object featureMap,
      final @RequestPart(value = "f") String value, 
      @PathVariable @ApiParam(example = "S", value="S-Save, U-Update") final PolygonAction action) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into applicantPolygon Context Id: {} ", contextId);
    return gisService.applicantPolygon(Arrays.asList(featureMap), value, action.name(), contextId);
  }


  /**
   * Save Or Update the Analyst Polygon based on the action requested by the user.
   * 
   * @param response - {@link HttpServletResponse}
   * @param featureMap - Feature details.
   * @param value - pjson value.
   * @param action - Save(S) or Update(U)
   * 
   * @return - Save or Update Analyst Polygon details.
   */
  @PostMapping(value = "/analystPolygon/{action}", produces = "application/json")
  @ApiOperation("Save the Polygon while the DEC Staff reviewing the details.")
  public Object saveAnalystPolygon(HttpServletResponse response,
      @RequestPart(value = "features") Object featureMap, @RequestPart(value = "f") String value,
      @PathVariable @ApiParam(example = "S", value="S-Save, U-Update") final PolygonAction action) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into analyst Polygon Context Id: {}", contextId);
    return gisService.analystPolygon(Arrays.asList(featureMap), value, action.name(), contextId);
  }

  /**
   * Save or Update the Submitted Polygon requested by the user.
   * 
   * @param response - {@link HttpServletResponse}
   * @param featureMap - Feature details.
   * @param value - pjson value.
   * @param action - Save(S) or Update(U)
   * 
   * @return - Save or Update Submitted Polygon details.
   */
  @PostMapping(value = "/submitedPolygon/{action}")
  @ApiOperation("Save the Submitted Polygon requested by the user/staff.")
  public Object saveSubmitedPolygon(HttpServletResponse response,
      @RequestPart(value = "features") Object featureMap, @RequestPart(value = "f") String value,
      @PathVariable @ApiParam(example = "S", value="S-Save, U-Update") final PolygonAction action) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into submitedPolygon Context Id: {}", contextId);
    return gisService.submittedPolygon(Arrays.asList(featureMap), value, action.name(), contextId);
  }

  /**
   * Retrieve the Applicant Polygon details for the input application id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param applicationId - Application Id.
   * 
   * @return - Applicant Polygon details.
   */
  @GetMapping(value = "/applicantPolygon", produces = "application/json")
  @ApiOperation("Retrieve the Applicant polygon for the input application id.")
  public String getApplicantPolygon(HttpServletResponse response,
      @RequestParam("applicationId") @ApiParam(example = "234892", value="Application Id") final String applicationId) {

    String contextId = UUID.randomUUID().toString();

    LOGGER.info("Entering into getApplicantPolygon. Context Id {}", contextId);
    if (!StringUtils.hasLength(applicationId)) {
      LOGGER.info(MANTORY_VAL_MISSING_LOG_INFO, applicationId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getApplicantPolygon(applicationId, contextId);
  }

  /**
   * Retrieve the Analyst Polygon details for the requested Analyst Id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param analystId - Analyst Id.
   * 
   * @return - Analyst Polygon details.
   */
  @GetMapping(value = "/analystPolygon", produces = "application/json")
  @ApiOperation("Retrieve the Analyst polygon for the input analyst polygon id.")
  public String getAnalystPolygon(HttpServletResponse response,
      @RequestParam("analystId") @ApiParam(example = "24324", value="Analyst Polygon Id") final String analystId) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getAnalystPolygon, Context Id {}", contextId);
    if (!StringUtils.hasLength(analystId)) {
      LOGGER.info(MANTORY_VAL_MISSING_LOG_INFO, analystId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getAnalystPolygon(analystId, contextId);
  }

  /**
   * Retrieve the Submitted Polygon details for the requested application submit id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param applSubId - Submitted Polygon Id.
   * 
   * @return - Submitted Polygon details.
   */
  @GetMapping(value = "/submitedPolygon", produces = "application/json")
  @ApiOperation("Retrieve the Submitted Polygon id for the input request application submitted polygon id.")
  public String getsubmitedPolygon(HttpServletResponse response,
      @RequestParam("applSubId") @ApiParam(example = "24283", value="Applicant/Analyst Submitted Polygon id") final String applSubId) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getsubmitedPolygon, Context Id {}", contextId);
    if (StringUtils.isEmpty(applSubId)) {
      LOGGER.info(MANTORY_VAL_MISSING_LOG_INFO, applSubId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getsubmitedPolygon(applSubId, contextId);
  }

  /**
   * Retrieve the DEC ID by the program Id and Type requested by the user.
   * 
   * @param userId - User who initiates this request.
   * @param programId - ProgramId.
   * @param programType - Program Type.
   * @param jwtToken - JWT Token.
   * 
   * @return - matched DEC Id if any.
   */
  @GetMapping("/decId")
  @ApiOperation("Retrieve the DEC ID for the input Program Id and Program Type.")
  public Map<String, Object> getDECIdByProgramType(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "0-0000-00012", value="Program Id") final String programId, 
      @RequestHeader @ApiParam(example = "DEC", value="Program Type") final String programType,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getDECIdByProgramType. User Id {} Context Id {}", userId, contextId);
    if (!StringUtils.hasLength(programId) || !StringUtils.hasLength(programType)) {
      LOGGER.error("Program Type or Id cannot be null");
      throw new BadRequestException("PROG_ID_BLANK", "Program Type or Id cannot be null",
          programType);
    }
    return gisService.getDECIdByProgramType(userId, contextId, jwtToken, programId, programType);
  }

  /**
   * Retrieve the DEC ID by the input Tax map number, County, Municipality.
   * 
   * @param userId - User who initiates this request.
   * @param txmap - Tax Map number.
   * @param county - County name.
   * @param municipality - Municipality name.
   * @param jwtToken - JWT Token
   * 
   * @return - Matched DEC Id.
   */
  @GetMapping("/decId/txmap")
  @ApiOperation("Retrieve the DEC ID for the input Tax Map, County and Municipality.")
  public ResponseEntity<String> getDECIdByTxmap(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestParam("txmap") @ApiParam(example = "156.17-2-41", value="Tax Map Number") final String txmap, 
      @RequestParam("county") @ApiParam(example = "Albany", value="County") final String county,
      @RequestParam(name = "municipality", required = false) @ApiParam(example = "Albany", value="Municipality") final String municipality,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getDECIDDetail. User Id {} Context Id{}", userId, contextId);
    if (StringUtils.hasLength(txmap) && StringUtils.hasLength(county)
        && StringUtils.hasLength(userId)) {
      return gisService.getDECIdByTxmap(userId, contextId, jwtToken, txmap, county, municipality);
    }
    throw new BadRequestException("INVALID_REQ",
        "One of them is missing a valid value. TaxMap, County, Municipality", txmap);
  }

  /**
   * Delete the requested Polygon Object id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param objectId - Object Id.
   * 
   * @return - Deleted Polygon details.
   */
  @PostMapping(value = "/deletePolygon/{objectId}")
  @ApiOperation(value="Delete the Applicant Polygon Object for the input Object id from GIS")
  public Object deletePolygonByObjId(HttpServletResponse response,
      @PathVariable @ApiParam(example = "23424", value="Polygon Object id") final String objectId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into deletePolygonByObjId Context Id: {}", contextId);
    return gisService.deletePolygonByObjId(objectId, contextId);
  }

  /**
   * Delete the requested Analyst Polygon by the requested Object Id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param userId - User who initiates this request.
   * @param objectId - Object id.
   * 
   * @return - Deleted Analyst Polygon details.
   */
  @PostMapping(value = "/delete-analyst-polygon/{objectId}")
  @ApiOperation(value="Delete the Analyst Polygon Object for the input Object id from "
      + "GIS which is created/modified during the review process")
  public Object deleteAnalystPolygonByObjId(HttpServletResponse response,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable @ApiParam(example = "23423", value="Polygon Object id") final String objectId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into deleteAnalystPolygonByObjId Context Id: {}", contextId);
    return gisService.deleteAnalystPolygonByObjId(userId, contextId, objectId);
  }

  /**
   * Delete the Applicant Submitted Polygon.
   * 
   * @param response - {@link HttpServletResponse}
   * @param userId - User who initiates this request.
   * @param objectId - Requested Object id to be deleted.
   * 
   * @return - Deleted Polygon Object id.
   */
  @PostMapping(value = "/delete-submittal-polygon/{objectId}")
  @ApiOperation(value="Delete the Applicant Submitted Polygon Object for the input Object id from GIS")
  public Object deleteApplicantSubmittalPolygonByObjId(HttpServletResponse response,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable @ApiParam(example = "23432", value="Delete the Applicant submitted Polygon") final String objectId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into deleteApplicantSubmittalPolygonByObjId Context Id: {}", contextId);
    return gisService.deleteApplicantSubmittalPolygonByObjId(userId, contextId, objectId);
  }

  /**
   * Upload the requested shape file.
   * 
   * @param userId - user who initiates this request.
   * @param filetype - File Type.
   * @param publishParameters - Published Parameters.
   * @param value - pjson value.
   * @param files - Files to be uploaded.
   * 
   * @return - Uploaded shape file details.
   */
  @PostMapping(value = "/upload")
  @ApiOperation(value="Upload the Shape file into GIS.")
  public Object uploadShapeFile(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      final @RequestPart("filetype") String filetype,
      final @RequestPart("publishParameters") String publishParameters,
      final @RequestPart("f") String value, final @RequestPart("file") MultipartFile files) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into uploadShapeFile User Id {} Context Id: {} ", userId, contextId);
    return gisService.uploadShapefile(userId, contextId, filetype, publishParameters, value,
        files);
  }

  /**
   * Save/Update the WorkArea Polygon.
   * @param featureMap - Feature Map details of the Polygon.
   * @param value - pjson value.
   * @param action - Save (S)/Update(U).
   * 
   * @return - Updated Work Area Polygon details.
   */
  @PostMapping(value = "/workarea-polygon/{action}", produces = "application/json")
  @ApiOperation(value="Save/Update the Work area polygon based action requested by the user.")
  public Object saveWorkAreaPolygon(final @RequestPart(value = "features") Object featureMap,
      final @RequestPart(value = "f") String value, 
      @PathVariable @ApiParam(example = "S", value="S-Save/U-Update") final PolygonAction action) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into saveWorkAreaPolygon Context Id: {} ", contextId);
    return gisService.saveOrUpdateWorkAreaPolygon(Arrays.asList(featureMap), value, action.name(),
        contextId);
  }

  /**
   * Delete the Work area polygon requested by the user.
   * 
   * @param userId - User who initiates this request.
   * @param objectId - Object id.
   * 
   * @return - Deleted WorkArea Polygon details.
   */
  @PostMapping(value = "/delete-workarea-polygon/{objectId}")
  @ApiOperation(value="Delete the Work Area Polygon requested by the user from GIS.")
  public Object deleteWorkAreaPolygonByObjId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable @ApiParam(example = "23424", value="Work area polygon object id") final String objectId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into deleteWorkAreaPolygonByObjId Context Id: {}", contextId);
    return gisService.deleteWorkAreaPolygonByObjId(userId, contextId, objectId);
  }

  /**
   * Retrieve the Work Area Polygon details for the input work area id.
   * 
   * @param response - {@link HttpServletResponse}
   * @param workareaId - Work Area Id.
   * 
   * @return - Work Area Polygon details.
   */
  @GetMapping(value = "/workarea-polygon", produces = "application/json")
  @ApiOperation(value="Retrieve the Work area Polygon from GIS.")
  public String getWorkAreaPolygon(HttpServletResponse response,
      @RequestParam("workareaId") @ApiParam(example = "23423", value="Work Area Id") final String workareaId) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getWorkAreaPolygon, Context Id {}", contextId);
    if (!StringUtils.hasLength(workareaId)) {
      LOGGER.info(MANTORY_VAL_MISSING_LOG_INFO, workareaId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisService.getWorkAreaPolygon(workareaId, contextId);
  }
}
