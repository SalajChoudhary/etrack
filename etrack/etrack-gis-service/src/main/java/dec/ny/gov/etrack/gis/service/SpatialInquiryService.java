package dec.ny.gov.etrack.gis.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;

@Service
public interface SpatialInquiryService {

  /**
   * Submit the Spatial Inquiry Applicant Polygon. 
   * 
   * @param featureMap - Feature Map.
   * @param value - pjson value.
   * @param action - Save or Update.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Submittal Polygon request.
   */
  Object spatialInquiryApplicantPolygon(List<Object> featureMap, String value, String action, final String contextId);
  
  /**
   * Delete the requested Polygon Object id.
   * 
   * @param objectId - Polygon object id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Deleted response.
   */
  Object deleteSpatialInqPolygonByObjId(String objectId, final String contextId);
  
  /**
   * Save/Persist the Spatial Inquiry details. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param spatialInqPolygon - Spatial Inquiry Polygon details.
   * 
   * @return - Persisted Spatial Inquiry detail.
   */
  Object saveSpatialInqDetails(String userId, String contextId, String jwtToken,
      JsonNode spatialInqPolygon);
  
  /**
   * Retrieve the Spatial Polygon for the input request Application id.
   * 
   * @param spatialInquiryId - Spatial Inquiry id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Spatial Polygon details in string format.
   */
  String getSpatialPolygonByApplicationId(String spatialInquiryId, String contextId);
  
  /**
   * Retrieve the Spatial Inquiry details for the inquiry id and requestor name.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param inquiryId - Inquiry id.
   * @param requestorName - Requestor name.
   * 
   * @return - Spatial Inquiry detail.
   */
  Object getSpatialInquiryDetails(String userId, String contextId, String jwtToken,
      Long inquiryId, String requestorName);

  
  /**
   * Requesting GIS Service to store the Inquiry response details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param spatialInqPolygonResponse - Spatial inquiry response details.
   * 
   * @return - Response from GIS service after the update.
   */
  GISServiceResponse saveSpatialInquiryResponseDetails(String userId, String contextId, String jwtToken,
      JsonNode spatialInqPolygonResponse);
}
