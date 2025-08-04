package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.model.GIReviewerDashboardDetail;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryCategory;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryRequest;

@Service
public interface SpatialInquiryService {
  
  /**
   * Retrieve the Spatial Inquiry details for the input inquiry type and user is.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryType - Inquiry type.
   * 
   * @return - Matched Spatial Inquiry details in a list.
   */
  List<SpatialInquiryRequest> retrieveSpatialInquiryServiceByInquiryType(
      final String userId, final String contextId, final SpatialInquiryCategory inquiryType);
  
  /**
   * Retrieve the Spatial Inquiry details for the requested inquiry id and the requestor.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry id.
   * @param requestorName - Requestor name.
   * 
   * @return - Spatial Inquiry details if any for the input parameters
   */
  Object retrieveSpatialInqDetail(final String userId, final String contextId,
      Long inquiryId, String requestorName);

  /**
   * Retrieve all the Spatial inquiries submitted by all the users for all the regions or input region.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryType - Spatial Inquiry type.
   * @param facilityRegionId - Facility Region id.
   * 
   * @return - List of inquiry details.
   */
  Object retrieveRegionalSpatialInquiryServiceByInquiryType(String userId, String contextId,
      SpatialInquiryCategory inquiryType, Integer facilityRegionId);

  /**
   * Retrieve the Spatial Document Summary for the input inquiry Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Spatial Inquiry document details.
   */
  Object retrieveSpatialDocumentSummary(String userId, String contextId, Long inquiryId);

  /**
   * Retrieve the status of the Spatial Inquiry for the input inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Key and Value of the inquiry status.
   */
  Map<String, Long> retrieveSpatialInquiryStatus(String userId, String contextId, Long inquiryId);

  /**
   * Retrieve all the Geographical Inquiry details to display in the Virtual Workspace for the input inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Virtual Workspace inquiry details.
   */
  Object retrieveGeographicalInquiryForVW(String userId, String contextId, Long inquiryId);

  /**
   * Retrieve the Geographical Inquiry note configuration details.
   * 
   * @param userId - User who initiates this request.  
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Spatial Inquiry notes configuration.
   */
  Object retrieveGeographicalNoteConfig(String userId, String contextId);

  /**
   * Retrieve the Geographical Inquiry note details.
   * 
   * @param userId - User who initiates this request.  
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry id.
   * @param noteId - Geographical inquiry note id.
   * 
   * @return - Geographical Inquriry note details.
   */
  Object getNote(String userId, String contextId, Long inquiryId, Long noteId);

  /**
   * Retrieve Project details for the input reviewer to review.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Geographical Inquiry details requested to review for the input user.
   */
  List<GIReviewerDashboardDetail> getProgramReviewerDashboardDetails(final String userId, final String contextId);

  /**
   * Retrieve all the Geographical Inquiry reviewer eligible documents.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Inquiry Id.
   * 
   * @return - List of review documents.
   */
  Object retrieveEligibleReviewDocuments(String userId, String contextId, Long inquiryId);

  /**
   * Retrieve all the Spatial Inquiry details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param region - Region id.
   * 
   * @return - all the Spatial Inquiry details.
   */
  Object retrieveAllSpatialInquiryDetails(String userId, String contextId, Integer region);
}
