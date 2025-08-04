package gov.ny.dec.etrack.cache.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import gov.ny.dec.etrack.cache.entity.PermitType;
import gov.ny.dec.etrack.cache.entity.SICCodes;
import gov.ny.dec.etrack.cache.entity.SpatialInqCategory;
import gov.ny.dec.etrack.cache.entity.TransType;
import gov.ny.dec.etrack.cache.model.ConfigType;
import gov.ny.dec.etrack.cache.model.ETrackDocType;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeesDetail;
import gov.ny.dec.etrack.cache.model.SWFacilityType;
import gov.ny.dec.etrack.cache.service.ETrackConfigService;
import gov.ny.dec.etrack.cache.service.NAICSSICService;
import io.swagger.annotations.ApiOperation;

@RestController
public class ETrackConfigController {

  @Autowired
  private ETrackConfigService eTrackConfigService;

  @Autowired
  private NAICSSICService naicssicService;

  private static Logger logger = LoggerFactory.getLogger(ETrackConfigController.class.getName());
  private String contextId;
  private static final String userId = "cache-user";

  /**
   * Retrieve the DocTypes and list of document sub types for each doc type if any.
   * 
   * @return - Returns the {@link ResponseEntity}
   */
  @GetMapping("/docTypes")
  @ApiOperation(value="Retrieve the Document and Sub types")
  public ResponseEntity<Map<String, Map<Integer, ETrackDocType>>> getDocTypeAndSubTypes() {
    this.contextId = UUID.randomUUID().toString();
    logger.info("Received a request to receive Doc Type and Sub Types. User id: {} context id: {}",
        userId, contextId);
    return eTrackConfigService.getDocTypeAndSubTypes(userId, contextId);
  }

  /**
   * Retrieve all the messages. 
   * 
   * @return - Returns {@link ResponseEntity}
   */
  @GetMapping("/messages")
  @ApiOperation(value="Retrieve all the Messages (warning, error) to use in UI to display the message in the screen.")
  public ResponseEntity<Map<String, Map<String, String>>> getMessages() {
    this.contextId = UUID.randomUUID().toString();
    logger.info("Received a request to receive Messages. User id: {} context id: {}", userId,
        contextId);
    return eTrackConfigService.getMessages(userId, contextId);
  }

  
  /**
   * Retrieve the Configuration items from few configuration tables.
   * 
   * @return {@link ConfigType}
   */
  @GetMapping("/configTypes")
  @ApiOperation("Configuration details of applicant Types, Proposed use code, activity status, countries, states, action types etc.")
  public ConfigType getConfigTypes() {
    return eTrackConfigService.getConfigTypes();
  }

  /**
   * REtrieve the list permit types for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return {@link Map}
   */
  @GetMapping("/permitTypes/{projectId}")
  @ApiOperation(value="Retrieve the Permit Types associated with the Project id.")
  public Map<String, Map<String, List<PermitType>>> getPermitTypes(@RequestHeader final String userId, 
      @PathVariable final Long projectId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getPermitTypes. User Id {}. Context Id {}", userId, contextId);
    return eTrackConfigService.getAllPermitTypesByProjectId(userId, contextId, projectId);
  }

  /**
   * Retrieve the SIC code and description for each SIC Code. 
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of SIC Codes {@link List}
   */
  @GetMapping("/sic-naics")
  @ApiOperation(value="Returns the SIC Codes")
  public List<SICCodes> getSICCodes(@RequestHeader final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getSICCodes() User Id {}, Context ID{} ", userId, contextId);
    return naicssicService.getSICCodes(userId, contextId);
  }

  /**
   * Retrieve the NAICS codes and description for the input SIC Code.
   * 
   * @param userId - User who initiates this request.
   * @param sicCode - SIC Code.
   * 
   * @return - Returns the Code and Description mapping {@link Map}
   */
  @GetMapping("/naics/{sicCode}")
  @ApiOperation(value="Returns the NAICS Code for the input SIC Code")
  public Map<String, String> getNAICSCodes(@RequestHeader final String userId,
      @PathVariable final String sicCode) {
    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into getNAICSCodes() for the input SIC Code" + " User Id {}, Context ID{} ",
        userId, contextId);
    return naicssicService.getNAICSCodes(userId, contextId, sicCode);
  }
  
  /**
   * Retrieve all the available DEC regions.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return Returns the {@link List}
   */
  @GetMapping("/region")
  @ApiOperation(value="Returns all the regions configured in the E_REGION table.")
  public List<String> getRegionDetails(@RequestHeader final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getRegionDetails() User Id {}, Context Id {} ", userId, contextId);
    return eTrackConfigService.findAppRegions(userId, contextId);
  }

  /**
   * Returns the Solid Waste Type and Sub type.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - Returns the {@link List}
   */
  @GetMapping("/sw-faclity-type")
  @ApiOperation(value="Retrieve the Solid Waste Facility Types.")
  public List<SWFacilityType> getSWFacilityType(@RequestHeader final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getSWFacilityType() User Id {}, Context Id {}", userId, contextId);
    return eTrackConfigService.findSWFacilityType(userId, contextId);
  }
  
  /**
   * Returns the invoice fee configuration details.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - Invoice Fee configuration {@link Map}
   */
  @GetMapping("/invoice-fees")
  @ApiOperation(value="Retrived the invoice Fee configurations. Fee Type and Fee details.")
  public Map<String, List<InvoiceFeesDetail>> getInvoiceFeesConfig(@RequestHeader final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into getInvoiceFeesConfig() User Id {}, Context Id {}", userId, contextId);
    return eTrackConfigService.getInvoiceFeesConfig(userId, contextId);
  }
  
  /**
   * Return the Transaction type configuration. both code and description.
   * 
   * @return - Returns the list of {@link TransType}
   */
  @GetMapping("/transTypes")
  @ApiOperation(value="Returns the Tranaction Types")
  public Iterable<TransType> getTransTypes() {
    return eTrackConfigService.getTransTypes();
  }

  /**
   * Returns the list of Spatial Inquiry Categories.
   * 
   * @return - Returns the list of {@link SpatialInqCategory} for each category available to the user.
   */
  @GetMapping("/spatial-inq-category")
  @ApiOperation(value="Returns the Active Spatial Inquiry Categories")
  public Map<String, List<SpatialInqCategory>> getSpatialInqCategory() {
    return eTrackConfigService.getSpatialInqCategories();
  }
  
  /**
   * Returns the URLs configured to display in the Web site.
   * 
   * @return Code and Description Key and Value mapping of {@link String}
   */
  @GetMapping("/system-parameters")
  @ApiOperation(value="Returns the System Parameters")
  public Map<String, String> getSystemParameters() {
    return eTrackConfigService.getSystemParameters();
  }

  /**
   * Returns the XTRA IDs, Program Ids and Special Attention Codes.
   * 
   * @return Code and Description mapping of {@link Map}
   */
  @GetMapping("/xtra-prog-id-spl-attn")
  public Map<String, Object> getXtraIdProgIdAndSplAttneCodes() {
    return eTrackConfigService.getXtraIdProgIdAndSplAttneCodes();
  }
  
  /**
   * Returns all the configured GIS layers.
   * 
   * @return - List of {@link GISLayerConfigView}
   */
  @GetMapping("/gis-layers")
  @ApiOperation(value="Returns all the GIS Layers configured in the E_GIS_LAYER")
  public List<GISLayerConfigView> getGisLayers() {
    return eTrackConfigService.getGISLayers();
  }
  
  @GetMapping("/permits-by-sub-catg")
  @ApiOperation(value="Retrieve all the Permit Sub Categorized details. Sub Categories are Construction, Operating and General Permit")
  public Map<String, List<String>> retriveListOfPermitsGroupBySubCategory() {
    return eTrackConfigService.retrievePermitsGroupBySubCategory();
  }
}
