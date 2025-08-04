package gov.ny.dec.etrack.cache.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import gov.ny.dec.etrack.cache.entity.PermitType;
import gov.ny.dec.etrack.cache.entity.SpatialInqCategory;
import gov.ny.dec.etrack.cache.entity.TransType;
import gov.ny.dec.etrack.cache.model.ConfigType;
import gov.ny.dec.etrack.cache.model.ETrackDocType;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeesDetail;
import gov.ny.dec.etrack.cache.model.SWFacilityType;

public interface ETrackConfigService {
  /**
   * 
   * @param userId - User who initiates this request. - User who initiates this request.
   * @param contextId - Unique UUID to track this request. - Unique UUID to track this request.
   * 
   * @return - Doc Type and Sub Type mapping {@link ResponseEntity}
   */
  ResponseEntity<Map<String, Map<Integer, ETrackDocType>>> getDocTypeAndSubTypes(final String userId, final String contextId);
//  ResponseEntity<Map<Integer, ETrackDocType>> getDocTypeAndSubTypesByLangCode(final String langCode, final String userId, final String contextId);
  /**
   * Retrieve the configured message the UI to use and display for the appropriate action.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns {@link ResponseEntity}
   */
  ResponseEntity<Map<String, Map<String, String>>> getMessages(String userId, String contextId);
//  ResponseEntity <Map<String, String>> getMessagesByLangCode(final String langCode, final String userId, final String contextId);
  /**
   * Configuration details.
   * 
   * @return {@link ConfigType}
   */
  ConfigType getConfigTypes();
  
  /**
   * Retrieve all the Permits for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Permit type mapping {@link Map}
   */
  Map<String, Map<String, List<PermitType>>> getAllPermitTypesByProjectId(final String userId, final String contextId, final Long projectId);
  
//  Iterator<SupportDocConfig> findAllSupportConfig(String userId);
  /**
   * Retrieve all the DEC regions.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link List} regions.
   */
  List<String> findAppRegions(String userId, String contextId);
  
  /**
   * Retrieve Solid Waste Facility Type.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link List}
   */
  List<SWFacilityType> findSWFacilityType(String userId, String contextId);
  
  /**
   * Retrieve the Invoice Fee Configuration.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link Map}
   */
  Map<String, List<InvoiceFeesDetail>> getInvoiceFeesConfig(String userId, String contextId);
  
  /**
   * Retrieve all the Transaction types.
   * 
   * @return {@link Iterable}
   */
  Iterable<TransType> getTransTypes();
  
  /**
   * Retrieve the list of Spatial Inquiry Categories.
   * 
   * @return Category Available to as Key and List of Inquiry values{@link Iterable}
   */
  Map<String, List<SpatialInqCategory>> getSpatialInqCategories();
  
  /**
   * Retrieve the all the URLs and other details which will be used by UI.
   * 
   * @return {@link Map}
   */
  Map<String, String> getSystemParameters();
  
  /**
   * Retrieve all the XTRA IDs, Program IDs and Special Attention Codes configuration.
   * 
   * @return - all the configurations
   */
  Map<String, Object> getXtraIdProgIdAndSplAttneCodes();
    
  /**	
   * Retrieve all the active GIS Layers.
   * 
   * @return - LIst of GIS Layers.
   */
  List<GISLayerConfigView> getGISLayers();
  
  /**
   * Retrieve all the Permits by Sub category like Construction, Operating and General Permits.
   * 
   * @return - Key and Value of Sub Category and Permits.
   */
  Map<String, List<String>> retrievePermitsGroupBySubCategory();

    Map<String, Map<String, List<PermitType>>> getActivePermitTypesByProjectId(
            String userId, String contextId, Long projectId);
}
