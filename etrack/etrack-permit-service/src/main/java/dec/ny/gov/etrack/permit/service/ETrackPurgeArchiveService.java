package dec.ny.gov.etrack.permit.service;

import java.util.List;

import dec.ny.gov.etrack.permit.model.DeletedPurgeArchiveDocument;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.PurgeArchive;

public interface ETrackPurgeArchiveService {

  /**
   * Identify all the eligible documents based on the region id, Query (either Archive/Purge) and
   * mark them for review.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param purgeArchive - {@link PurgeArchive}
   * 
   * @return - Return the Query result id.
   */
  Long savePurgeArchiveReviewDetails(final String userId, final String contextId,
      final PurgeArchive purgeArchive);

  /**
   * Update all the documents reviewed completed for the input Query result.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @param documentReview {@link DocumentReview}
   */
  void updateAnalystIndicator(final String userId, final String contextId,
      DocumentReview documentReview);

  /**
   * Mark all the requested documents(either Archive/Purge) as reviewed
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param documentReview - {@link DocumentReview}
   * 
   * @return - {@link List}
   */
  List<DeletedPurgeArchiveDocument> updateArchivePurgeIndicator(final String userId,
      final String contextId, DocumentReview documentReview);

  /**
   * Update the report as completed.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param resutId - Query Result Id.
   */
  void updateProcessCompletedIndicator(final String userId, final String contextId, final String jwtToken,
      Long resutId);

  /**
   * Analyst request this document to remove from the review list as its not eligible for
   * Archive/Purge. If request is submitted by System Admin then document will be requested to
   * remove from the Purge review list.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param documentId - Document Id.
   */
  void deletePurgeArchiveDocument(final String userId, final String contextId, Long documentId);

  /**
   * Purge/Delete all the documents from DMS as requested by Admin.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param resultId - Query result id.
   * @param documentIds - List of document Ids to be deleted.
   */
  void purgeAllReviewedDocuments(final String userId, final String contextId, final String jwtToken,
      final Long resultId, List<String> documentIds);

  /**
   * Mark all the requested documents to review for the Archive/Purge for the input Query Result id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param resultId - Query result id.
   * @param documentIds  - List of document Ids.
   */
  void markAllRequestedDocumentAsNotEligible(final String userId, final String contextId,
      final Long resultId, List<Long> documentIds);
}
