package dec.ny.gov.etrack.permit.service;

import java.util.List;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.GIInquiryPolygonResponse;
import dec.ny.gov.etrack.permit.model.GIReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.GeographicalInquiryNoteView;
import dec.ny.gov.etrack.permit.model.GeographicalInquirySubmittalResponse;
import dec.ny.gov.etrack.permit.model.SpatialInquiryRequest;

@Service
public interface SpatialInquiryService {

  /**
   * Store the Spatial Inquiry details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param spatialInquiryRequest - Spatial Inquiry polygon details to be stored.
   * 
   * @return - Updated Spatial Inquiry details wjth the newly created inquiry id.
   */
  SpatialInquiryRequest saveSpatialInquiryDetail(final String userId, final String contextId,
      SpatialInquiryRequest spatialInquiryRequest);
  
  /**
   * Retrieve the Map from GIS service using the User's requested URL and upload into DMS>
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param inquiryId - Geographical Inquiry Id.
   * @param spatialInquiryMapUrl - Spatial Inquiry Map URL.
   */
  void uploadSpatialInquiryMap(final String userId, final String contextId, final String jwtToken, final Long inquiryId,
      final String spatialInquiryMapUrl);

  /**
   * Submit the Spatial Inquiry details. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken  - JWT Token.
   * @param inquiryId - Geographical Inquiry Id.
   */
  void submitSpatialInquiry(
      final String userId, final String contextId, String jwtToken, final Long inquiryId);

  /**
   * Submit the Geographical Inquiry response details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   * @param jwtToken = JWT Token.
   * @param submittalResponse - Submitted Response details {@link GeographicalInquirySubmittalResponse}
   */
  void submitSpatialInquiryResponse(final String userId, final String contextId, 
      final Long inquiryId, final String jwtToken,
      final GeographicalInquirySubmittalResponse submittalResponse);

  /**
   * Update the Geographical Inquiry assignment details with the requested DEP Analyst details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   * @param assignmentNote - Assignment details.
   */
  void updateGeographicalInquiryAssignment(final String userId, final String contextId, final Long inquiryId,
      AssignmentNote assignmentNote);

  /**
   * Add/Amend Geographical Inquiry note details. 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   * @param geographicalInquiryNote - Geographical Inquiry request details {@link GeographicalInquiryNoteView}
   * 
   * @return - Updated notes.
   */
  GeographicalInquiryNoteView addNotes(final String userId, final String contextId, final Long inquiryId,
      GeographicalInquiryNoteView geographicalInquiryNote);
  
  /**
   * Delete the requested Geographical Inquiry note details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   * @param noteId - Geographical Inquiry note id to be deleted.
   */
  void deleteNote(final String userId, final String contextId, final Long inquiryId, final Long noteId);

  /**
   * Update the Submitted Document reviewer details for the PAR to review.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   * @param documentReview - Document Reviewer details.
   */
  void updateDocumentReviewerDetails(final String userId, final String contextId, final Long inquiryId,
      DocumentReview documentReview);

  /**
   * Mark the requested Geographical Inquiry details as completed/incompleted based on the request.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   * @param reviewCompletionDetail - Document review completion details.
   */
  void updateDocumentReviewerCompletionDetails(final String userId, final String contextId, 
      final Long inquiryId, GIReviewCompletionDetail reviewCompletionDetail);

  /**
   * Mark the inquiry document upload process is requested for skip.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Geographical Inquiry Id.
   */
  void skipRequiredDocumentUploadForInquiries(String userId, String contextId, Long inquiryId);
}

