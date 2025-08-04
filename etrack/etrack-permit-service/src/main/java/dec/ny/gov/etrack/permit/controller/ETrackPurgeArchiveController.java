package dec.ny.gov.etrack.permit.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.DeletedPurgeArchiveDocument;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.PurgeArchive;
import dec.ny.gov.etrack.permit.service.ETrackPurgeArchiveService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/purge-archive")
public class ETrackPurgeArchiveController {

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackPurgeArchiveController.class.getName());

  @Autowired
  private ETrackPurgeArchiveService eTrackPurgeArchiveService;

  /**
   * Identify all the eligible documents based on the region id, Query (either Archive/Purge) and mark them for review.
   * 
   * @param userId - User who initiates this request.
   * @param purgeArchive - {@link PurgeArchive}
   * 
   * @return - {@link ResponseEntity}.
   */
  @PostMapping("/run-query")
  @ApiOperation(value = "Save Purge Archive Review Details.")
  public ResponseEntity<Long> savePurgeArchiveReviewDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody PurgeArchive purgeArchive) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into savePurgeArchiveReviewDetails. User Id {}, Context Id {}", userId,
        contextId);
    try {
      return ResponseEntity.ok(
          eTrackPurgeArchiveService.savePurgeArchiveReviewDetails(userId, contextId, purgeArchive));
    } catch (ETrackPermitException e) {
      return ResponseEntity.noContent().build();
    }
  }

  /**
   * Mark the review process for the input result id as completed by the analyst.
   * 
   * @param documentReview - {@link DocumentReview}
   */
  @PutMapping("/review-complete")
  @ApiOperation(value = "Analyst request the system to mark this review process is completed.")
  public void updateReviewCompletedIndicator(
      @RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId,
      @RequestBody DocumentReview documentReview) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updateReviewCompletedIndicator. User Id {}, Context Id {}", userId,
        contextId);

    if (Objects.isNull(documentReview) || documentReview.getResultId() == null
        || documentReview.getResultId() < 0) {
      throw new BadRequestException("INVALID_QUERY_RESULT_ID",
          "Invalid ResultId is passed in the request to update the status as validated.",
          documentReview);
    }
    eTrackPurgeArchiveService.updateAnalystIndicator(userId, contextId, documentReview);
    logger.info("Eixting from updateReviewCompletedIndicator. User Id {}, Context Id {}", userId,
        contextId);

  }

  /**
   * Mark all the requested Purge/Archive eligible documents as reviewed by the Analyst.
   * 
   * @param documentReview - {@link DocumentReview}i
   * 
   * @return - {@link List}
   */
  @PutMapping("/result")
  @ApiOperation(value = "Update Purge Archive Reviewed for All Documents.")
  public List<DeletedPurgeArchiveDocument> updatePurgeArchiveReviewDocumentsStatus(
      @RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId,
      @RequestBody DocumentReview documentReview) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updatePurgeArchiveReviewDocumentsStatus. User Id {}, Context Id {}", userId,
        contextId);
    
    if (Objects.isNull(documentReview) || CollectionUtils.isEmpty(documentReview.getDocuments())) {
      throw new BadRequestException("INVALID_DOCUMENT_IDs",
          "Invalid Document Ids are passed in the request to update the status as validated.",
          documentReview);
    }
    return eTrackPurgeArchiveService.updateArchivePurgeIndicator(userId, contextId, documentReview);
  }

  /**
   * Analyst request this document to remove from the review list as its not eligible for Archive/Purge.
   * If request is submitted by System Admin then document will be requested to remove from the Purge review list.
   * 
   * @param userId - User who initiates this request.
   * @param documentId - Document Id.
   */
  @DeleteMapping("/remove-document/{documentId}")
  @ApiOperation(value = "Remove/Mark the requested document from the review list. "
      + "So, the documents doesn't need to be either Archived or Purged."
      + "Also System Admin can use this method to Overwrite if Analyst marked for eligible and admin feels otherwise.")
  public void removeDocumentFromReviewList(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @PathVariable(value = "documentId") @ApiParam(example = "document Id",
          value = "Unique id of the document") final Long documentId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into removeDocumentFromReviewList. User Id {}, Context Id {}", userId,
        contextId);
    
    if (Objects.isNull(documentId) || documentId < 1) {
      throw new BadRequestException("INVALID_DOCUMENT_IDs",
          "Invalid DocumentId is passed in the request to remove the document from the resultset.",
          documentId);
    }
    eTrackPurgeArchiveService.deletePurgeArchiveDocument(userId, contextId, documentId);
    logger.info("Exiting from removeDocumentFromReviewList. User Id {}, Context Id {}", userId,
        contextId);
  }

  /**
   * nalyst/Admin requests all the requested review documents(Un-reviewed documents) to make it not eligible for Archive/Purge.
   * 
   * @param userId - User who initiates this request.
   * @param resultId - Query Result Id.
   * @param documentIds - List of document ids which needs to be marked as not eligible for review.
   */
  @PostMapping("/make-ineligible/{resultId}")
  @ApiOperation(value = "Remove/Mark the requested document from the review list. "
      + "So, the documents doesn't need to be either Archived or Purged."
      + "Also System Admin can use this method to Overwrite if Analyst marked for eligible and admin feels otherwise.")
  public void markAllRequestedDocumentAsNotEligible(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId, 
      @PathVariable(value = "resultId") @ApiParam(example = "Query result Id",
      value = "Query result id") final Long resultId, @RequestBody List<Long> documentIds) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into markAllRequestedDocumentAsNotEligible. User Id {}, Context Id {}", userId,
        contextId);
    eTrackPurgeArchiveService.markAllRequestedDocumentAsNotEligible(userId, contextId, resultId, documentIds);
    logger.info("Exiting from markAllRequestedDocumentAsNotEligible. User Id {}, Context Id {}", userId,
        contextId);

  }

  

  /**
   * Mark the Review Process as completed by the Admin who initiated this review process.
   * 
   * @param userId - User who initiates this request.
   * @param resultId - Query Result Id.
   */
  @PutMapping("/result/{resultId}")
  @ApiOperation(value = "Mark the review process as completed."
      + "1. Archive Review : Mark the process as completed."
      + "2. Purge Review : Mark the process as completed and Delete the documents from FileNet if its not purged earlier.")
  public void updatePurgeArchiveReviewProcessCompleted(
      @RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId,
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String jwtToken, 
      @PathVariable(value = "resultId") final Long resultId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updatePurgeArchiveReviewProcessCompleted. User Id {}, Context Id {}", userId,
        contextId);
    
    if (Objects.isNull(resultId) || resultId < 1) {
      throw new BadRequestException("INVALID_RESULT_ID",
          "Invalid ResultId is passed in the request to delete the query result.", resultId);
    }
    eTrackPurgeArchiveService.updateProcessCompletedIndicator(userId, contextId, jwtToken, resultId);
    logger.info("Exiting from updatePurgeArchiveReviewProcessCompleted. User Id {}, Context Id {}", userId,
        contextId);
  }

  /**
   * Purge all the documents reviewed by the Analyst and requested 
   * by Admin later to Purge them all associated with the result Id.
   * 
   * @param userId - User who initiates this request.
   * @param resultId - Query Result Id.
   * @param documentIds - List of document ID and Project Id as comma separated.
   */
  @PutMapping("/purge/{resultId}")
  @ApiOperation(value = "Purge all the reviewed documents associated with this result id.")
  public void purgeAllReviewedDocuments(
      @RequestHeader @ApiParam(example = "shortname",
      value = "User id of the logged in user") final String userId,
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String jwtToken, 
      @PathVariable(value = "resultId") final Long resultId, 
      @RequestBody List<String> documentIdAndProjectIds) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into purgeAllReviewedDocuments. User Id {}, Context Id {}", userId,
        contextId);
    
    if (Objects.isNull(resultId) || resultId < 1) {
      throw new BadRequestException("INVALID_RESULT_ID",
          "Invalid ResultId is passed in the request to purge the associated documents.", resultId);
    }
    eTrackPurgeArchiveService.purgeAllReviewedDocuments(userId, contextId, jwtToken, resultId, documentIdAndProjectIds);
    logger.info("Exiting from purgeAllReviewedDocuments. User Id {}, Context Id {}", userId,
        contextId);
  }

}
